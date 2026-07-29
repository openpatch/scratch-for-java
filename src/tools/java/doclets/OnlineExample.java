package doclets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rewrites a reference example into a program the Online IDE can run, so that
 * the documentation can embed the example itself rather than a recording of it.
 *
 * <p>
 * The examples under {@code src/examples/java/reference} are ordinary desktop
 * programs. The Online IDE has no packages and no imports - every class is in
 * scope - and the program itself is a {@code void main()} beside the classes,
 * the Java 25 form, which is also what a reader can copy into a file and run.
 * Two shapes of example have to be turned into that:
 *
 * <ul>
 * <li>A single file whose class extends nothing is a wrapper: it exists only to
 * give the example a {@code main}. Its constructor body <em>is</em> the program,
 * so it becomes the body of that {@code main}, and any class nested inside it is
 * lifted out to the top level.</li>
 * <li>A folder of files - {@code MyStage}, {@code MySprite}, {@code MyWindow} -
 * is already a set of top-level classes. They are kept as they are, the window
 * is dropped, and the program is the one line that builds the stage.</li>
 * </ul>
 *
 * <p>
 * In both cases the recording scaffolding goes: the {@code GifRecorder}, the
 * {@code exit()}, the {@code main}, and the {@code while (...forMillis(3000))}
 * loop that only ran for as long as the GIF needed. Where that loop had a body
 * it becomes the endless loop the example always meant; where it had none it
 * disappears, because in the browser the program keeps running by itself.
 *
 * <p>
 * Nothing here parses Java. The examples all share these two shapes, and a
 * transformation that only understands them fails loudly - by producing
 * something that does not compile - rather than quietly producing a program that
 * does the wrong thing. {@code DocumentationSnippetsTest} compiles what comes
 * out of it.
 */
final class OnlineExample {

    private OnlineExample() {
    }

    private static final Path REFERENCE = findReference();

    /**
     * Where the examples live. Javadoc runs in its own process and does not
     * promise to start in the project directory, so the folder is looked for from
     * wherever it did start, upwards, rather than assumed to be right here.
     */
    private static Path findReference() {
        Path relative = Path.of("src", "examples", "java", "reference");
        for (Path dir = Path.of("").toAbsolutePath(); dir != null; dir = dir.getParent()) {
            Path candidate = dir.resolve(relative);
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }
        return relative;
    }

    /**
     * Lines the Online IDE has no use for. It has no packages and no imports -
     * every class is already in scope.
     */
    private static final Pattern SCAFFOLDING = Pattern.compile(
            "^\\s*(?:package\\b|import\\b).*");

    private static final Pattern TYPE_DECLARATION = Pattern.compile(
            "^\\s*(?:(?:public|private|protected|static|abstract|final)\\s+)*"
                    + "(?:class|interface|enum|record)\\s+(\\w+)(\\s+extends\\s+(\\w+))?");

    private static final Pattern MAIN_METHOD = Pattern.compile(
            "^\\s*public static void main\\s*\\(");

    /**
     * The example as one Online IDE program, or {@code null} when it cannot be
     * expressed as one - a Tiled map or a mouse cursor needs files the browser
     * has no way to reach.
     */
    static String of(String folder, List<String> fileNames) {
        if (fileNames.isEmpty()) {
            return null;
        }

        var sources = new ArrayList<String>();
        for (String fileName : fileNames) {
            Path path = folder.isEmpty()
                    ? REFERENCE.resolve(fileName)
                    : REFERENCE.resolve(folder).resolve(fileName);
            try {
                sources.add(Files.readString(path));
            } catch (IOException e) {
                return null; // no source, no interactive example
            }
        }
        for (String source : sources) {
            if (source.contains("assets/")) {
                return null; // needs a file the browser cannot load
            }
        }

        // A folder example is a Stage subclass plus a Window that only exists to
        // put it on screen; the browser builds the stage itself, so that window
        // goes. An example about the Window class subclasses it too, but brings
        // no Stage subclass along - which is what tells the two apart.
        boolean windowIsAWrapper = sources.stream().anyMatch(OnlineExample::declaresAStage);

        var types = new ArrayList<String>();
        var statements = new ArrayList<String>();
        var stages = new ArrayList<String>();
        for (String source : sources) {
            unwrap(source, types, statements, stages, windowIsAWrapper);
        }

        // A folder example has no loose statements of its own: the window built
        // the stage, and here that one line is the whole program.
        if (statements.isEmpty() && !stages.isEmpty()) {
            statements.add("new " + stages.get(0) + "();");
        }
        if (statements.isEmpty() && types.isEmpty()) {
            return null;
        }

        // The program goes in a `void main()` beside the classes, which is a
        // whole program in Java 25 - a compact source file. Copied into a file
        // of the reader's own it stays one; the loose statements it used to be
        // were only ever a program in the browser.
        var out = new StringBuilder();
        if (!statements.isEmpty()) {
            out.append("void main() {\n");
            for (String statement : statements) {
                out.append(statement.isBlank() ? statement : "  " + statement).append("\n");
            }
            out.append("}\n");
        }
        for (String type : types) {
            out.append("\n").append(type).append("\n");
        }
        return out.toString().strip() + "\n";
    }

    /** Whether a file declares a class extending {@code Stage}. */
    private static boolean declaresAStage(String source) {
        for (String line : source.split("\n", -1)) {
            Matcher type = TYPE_DECLARATION.matcher(line);
            if (type.find() && type.group(3) != null && isStage(type.group(3))) {
                return true;
            }
        }
        return false;
    }

    /** Splits one example file into the classes it declares and the program itself. */
    private static void unwrap(String source, List<String> types, List<String> statements,
            List<String> stages, boolean windowIsAWrapper) {
        var lines = List.of(source.split("\n", -1));

        int depth = 0;
        boolean inWrapper = false;
        boolean inConstructor = false;
        String wrapper = null;
        var keptBody = new ArrayList<String>();
        int keptDepth = -1;
        int skipDepth = -1;
        var body = new ArrayList<String>();

        for (String raw : lines) {
            String line = raw.stripTrailing();
            int before = depth;
            depth += count(line, '{') - count(line, '}');

            // a class being dropped whole (the Window wrapper of a folder example)
            if (skipDepth >= 0) {
                if (depth <= skipDepth) {
                    skipDepth = -1;
                }
                continue;
            }

            // a class being kept whole - its own body needs the same clean-up
            if (keptDepth >= 0) {
                if (!isScaffolding(line)) {
                    keptBody.add(dedent(line, keptDepth));
                }
                if (depth <= keptDepth) {
                    types.add(String.join("\n", cleanUp(keptBody)).stripTrailing());
                    keptBody.clear();
                    keptDepth = -1;
                }
                continue;
            }

            if (isScaffolding(line)) {
                continue;
            }

            Matcher type = TYPE_DECLARATION.matcher(line);
            if (type.find()) {
                String name = type.group(1);
                String base = type.group(3);

                if ("Window".equals(base) && windowIsAWrapper) {
                    skipDepth = before; // it only existed to show the stage
                    continue;
                }
                if (base == null && !inWrapper) {
                    inWrapper = true; // the class that only holds a main
                    wrapper = name;
                    continue;
                }
                if (base != null && isStage(base)) {
                    stages.add(name);
                }
                keptDepth = before;
                keptBody.add(dedent(stripModifiers(line), before));
                continue;
            }

            if (!inWrapper) {
                continue;
            }
            if (MAIN_METHOD.matcher(line).find()) {
                inConstructor = false;
                skipDepth = before;
                continue;
            }
            if (line.matches("^\\s*(?:public\\s+)?" + wrapper + "\\(\\s*\\)\\s*\\{\\s*$")) {
                inConstructor = true;
                continue;
            }
            if (inConstructor) {
                if (before == 2 && line.strip().equals("}")) {
                    inConstructor = false;
                    continue;
                }
                body.add(dedent(line, 2));
            }
        }

        statements.addAll(cleanUp(body));
    }

    private static boolean isScaffolding(String line) {
        return SCAFFOLDING.matcher(line).matches() || line.endsWith("// @ignore");
    }

    private static List<String> cleanUp(List<String> lines) {
        var out = new ArrayList<>(lines);
        trimTrailingBlanks(out);
        return out;
    }

    /** Stage subclasses are what a folder example's program has to build. */
    private static boolean isStage(String base) {
        return base.equals("Stage") || base.endsWith("Stage");
    }

    private static void trimTrailingBlanks(List<String> lines) {
        while (!lines.isEmpty() && lines.get(lines.size() - 1).isBlank()) {
            lines.remove(lines.size() - 1);
        }
    }

    private static String stripModifiers(String line) {
        return line.replaceFirst("^(\\s*)(?:public|private|protected|static|final)\\s+", "$1");
    }

    private static String dedent(String line, int levels) {
        String indent = "  ".repeat(levels);
        return line.startsWith(indent) ? line.substring(indent.length()) : line;
    }

    private static int count(String line, char c) {
        int n = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == c) {
                n++;
            }
        }
        return n;
    }
}
