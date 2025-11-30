package doclets;

import com.sun.source.doctree.*;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Javadoc Doclet that generates JSON files for each method and class.
 * 
 * File structure (base package: org.openpatch.scratch):
 * - /ClassName/methodName.md.json for classes in base package
 * - /extensions/animations/ClassName/methodName.md.json for
 * org.openpatch.scratch.extensions.animations
 * - /ClassName/index.md.json for class documentation
 * - /extensions/animations/index.md.json for package documentation
 * 
 * Usage:
 * javadoc -doclet org.openpatch.doclets.Scratch4JDoclet -docletpath
 * /path/to/doclet.jar -sourcepath src -subpackages com.yourpackage -d output
 */
public class Scratch4JDoclet implements Doclet {
    private Reporter reporter;
    private String outputDir = "json-docs";
    private static final String BASE_PACKAGE = "org.openpatch.scratch";

    // Track packages and their classes
    private Map<String, List<TypeElement>> packageClasses = new HashMap<>();

    @Override
    public void init(Locale locale, Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public String getName() {
        return "Scratch4JDoclet";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return Set.of(new Option() {
            @Override
            public int getArgumentCount() {
                return 1;
            }

            @Override
            public String getDescription() {
                return "Output directory for JSON files";
            }

            @Override
            public Kind getKind() {
                return Kind.STANDARD;
            }

            @Override
            public List<String> getNames() {
                return List.of("-d", "--output-dir");
            }

            @Override
            public String getParameters() {
                return "directory";
            }

            @Override
            public boolean process(String option, List<String> arguments) {
                outputDir = arguments.get(0);
                return true;
            }
        });
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        try {
            Path outputPath = Paths.get(outputDir);
            outputPath.toFile().mkdirs();

            // First pass: collect all classes by package
            for (Element element : environment.getIncludedElements()) {
                if (element.getKind() == ElementKind.CLASS ||
                        element.getKind() == ElementKind.INTERFACE) {
                    TypeElement typeElement = (TypeElement) element;
                    String packageName = getPackageName(typeElement);

                    // Skip if class or its package has @ignore-in-docs tag
                    if (!shouldIgnore(typeElement, environment) &&
                            !isPackageIgnored(packageName, environment)) {
                        packageClasses.computeIfAbsent(packageName, k -> new ArrayList<>()).add(typeElement);
                    }
                }
            }

            // Second pass: generate class documentation
            for (Element element : environment.getIncludedElements()) {
                if (element.getKind() == ElementKind.CLASS ||
                        element.getKind() == ElementKind.INTERFACE) {
                    TypeElement typeElement = (TypeElement) element;
                    String packageName = getPackageName(typeElement);
                    // Skip if class or package has @ignore-in-docs tag
                    if (!shouldIgnore(typeElement, environment) &&
                            !isPackageIgnored(packageName, environment)) {
                        processClass(typeElement, environment, outputPath);
                    }
                }
            }

            // --- NEW: Collect all packages, not just those with classes ---
            Set<String> allPackages = new HashSet<>(packageClasses.keySet());
            for (Element element : environment.getIncludedElements()) {
                if (element.getKind() == ElementKind.PACKAGE) {
                    PackageElement pkg = (PackageElement) element;
                    String pkgName = pkg.getQualifiedName().toString();
                    allPackages.add(pkgName);
                }
            }

            // Third pass: generate package index files for all packages
            for (String pkgName : allPackages) {
                // Skip if package has @ignore-in-docs tag
                if (!isPackageIgnored(pkgName, environment)) {
                    List<TypeElement> classes = packageClasses.getOrDefault(pkgName, Collections.emptyList());
                    generatePackageIndex(pkgName, classes, environment, outputPath);
                }
            }

            return true;
        } catch (Exception e) {
            reporter.print(Diagnostic.Kind.ERROR, "Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String getPackageName(TypeElement element) {
        Element enclosing = element.getEnclosingElement();
        while (enclosing != null && enclosing.getKind() != ElementKind.PACKAGE) {
            enclosing = enclosing.getEnclosingElement();
        }
        if (enclosing != null && enclosing.getKind() == ElementKind.PACKAGE) {
            return ((PackageElement) enclosing).getQualifiedName().toString();
        }
        return ""; // Default package
    }

    private Path getRelativePackagePath(String packageName) {
        if (packageName.equals(BASE_PACKAGE)) {
            return Paths.get("");
        } else if (packageName.startsWith(BASE_PACKAGE + ".")) {
            String relativePkg = packageName.substring(BASE_PACKAGE.length() + 1);
            return Paths.get(relativePkg.replace('.', '/'));
        }
        return Paths.get(packageName.replace('.', '/'));
    }

    private String getCustomPackageName(String packageName, DocletEnvironment env) {
        for (Element element : env.getIncludedElements()) {
            if (element.getKind() == ElementKind.PACKAGE) {
                PackageElement pkg = (PackageElement) element;
                if (pkg.getQualifiedName().toString().equals(packageName)) {
                    DocCommentTree docComment = env.getDocTrees().getDocCommentTree(pkg);
                    if (docComment != null) {
                        Map<String, String> customTags = extractCustomTags(docComment);
                        if (customTags.containsKey("name-in-docs")) {
                            return customTags.get("name-in-docs");
                        }
                    }
                }
            }
        }
        return packageName; // Return original if no custom name found
    }

    private void generatePackageIndex(String packageName, List<TypeElement> classes,
            DocletEnvironment env, Path outputPath) throws IOException {
        Path packagePath = outputPath.resolve(getRelativePackagePath(packageName));
        packagePath.toFile().mkdirs();

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("template", "package");

        // Use custom package name if specified, otherwise use original
        String displayName = getCustomPackageName(packageName, env);
        json.put("name", displayName);
        json.put("related", new ArrayList<>());

        // Extract package description from package-info.java if available
        String description = "";
        Integer indexInDocs = null;
        for (Element element : env.getIncludedElements()) {
            if (element.getKind() == ElementKind.PACKAGE) {
                PackageElement pkg = (PackageElement) element;
                if (pkg.getQualifiedName().toString().equals(packageName)) {
                    DocCommentTree docComment = env.getDocTrees().getDocCommentTree(pkg);
                    if (docComment != null) {
                        description = extractDescription(docComment, packageName);
                        Map<String, String> customTags = extractCustomTags(docComment);
                        if (customTags.containsKey("index-in-docs")) {
                            try {
                                indexInDocs = Integer.parseInt(customTags.get("index-in-docs").trim());
                            } catch (NumberFormatException e) {
                                // ignore invalid numbers
                            }
                        }
                    }
                    break;
                }
            }
        }
        json.put("description", convertToMarkdown(description, packageName).trim());
        if (indexInDocs != null) {
            json.put("index", indexInDocs);
        }

        // List of classes in this package
        List<String> classList = classes.stream()
                .map(c -> c.getSimpleName().toString())
                .sorted()
                .collect(Collectors.toList());
        json.put("classes", classList);

        writeJsonToFile(json, packagePath.resolve("index.md.json"));
    }

    private void processClass(TypeElement classElement, DocletEnvironment env, Path outputPath) {
        String className = classElement.getSimpleName().toString();
        String packageName = getPackageName(classElement);

        // Create class directory in the appropriate package path
        Path packagePath = outputPath.resolve(getRelativePackagePath(packageName));
        Path classDir = packagePath.resolve(className);
        classDir.toFile().mkdirs();

        // Group methods by name to handle overloading
        Map<String, List<ExecutableElement>> methodGroups = new HashMap<>();
        List<ExecutableElement> constructors = new ArrayList<>();

        for (Element member : classElement.getEnclosedElements()) {
            if (member.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) member;
                // Skip if method has @ignore tag
                if (!shouldIgnore(method, env)) {
                    String methodName = method.getSimpleName().toString();
                    methodGroups.computeIfAbsent(methodName, k -> new ArrayList<>()).add(method);
                }
            } else if (member.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructor = (ExecutableElement) member;
                // Skip if constructor has @ignore tag
                if (!shouldIgnore(constructor, env)) {
                    constructors.add(constructor);
                }
            }
        }

        // Generate class index
        try {
            generateClassIndex(classElement, constructors, methodGroups, env, classDir);
        } catch (IOException e) {
            reporter.print(Diagnostic.Kind.ERROR,
                    "Failed to generate class index for: " + className);
        }

        // Generate one JSON per method name (combining overloads)
        for (Map.Entry<String, List<ExecutableElement>> entry : methodGroups.entrySet()) {
            try {
                generateMethodJson(entry.getValue(), className, env, classDir);
            } catch (IOException e) {
                reporter.print(Diagnostic.Kind.ERROR,
                        "Failed to generate JSON for method: " + entry.getKey());
            }
        }
    }

    private void generateClassIndex(TypeElement classElement, List<ExecutableElement> constructors,
            Map<String, List<ExecutableElement>> methodGroups,
            DocletEnvironment env, Path classDir) throws IOException {
        Map<String, Object> json = new LinkedHashMap<>();

        String className = classElement.getSimpleName().toString();
        String currentPackage = getPackageName(classElement);

        json.put("template", "class");
        json.put("related", new ArrayList<>());
        json.put("name", className);

        // Parse class documentation
        DocCommentTree docComment = env.getDocTrees().getDocCommentTree(classElement);
        String description = "";
        Map<String, String> customTags = new HashMap<>();
        Integer indexInDocs = null;

        if (docComment != null) {
            description = extractDescription(docComment, currentPackage);
            customTags = extractCustomTags(docComment);
            if (customTags.containsKey("index-in-docs")) {
                try {
                    indexInDocs = Integer.parseInt(customTags.get("index-in-docs").trim());
                } catch (NumberFormatException e) {
                    // ignore invalid numbers
                }
            }
        }

        json.put("scratchblock", customTags.getOrDefault("scratchblock", ""));
        json.put("description", convertToMarkdown(description, currentPackage).trim());
        if (indexInDocs != null) {
            json.put("index", indexInDocs);
        }

        // List of constructors
        List<String> constructorList = new ArrayList<>();
        for (ExecutableElement constructor : constructors) {
            constructorList.add(buildConstructorSyntax(constructor, className));
        }
        json.put("constructor", constructorList);

        // Examples - support multiple examples
        json.put("examples", buildExamples(customTags));

        // --- Add public static fields under "fields" ---
        List<Map<String, Object>> fields = new ArrayList<>();
        for (Element member : classElement.getEnclosedElements()) {
            if (member.getKind() == ElementKind.FIELD) {
                Set<Modifier> modifiers = member.getModifiers();
                if (modifiers.contains(Modifier.PUBLIC) && modifiers.contains(Modifier.STATIC)) {
                    Map<String, Object> fieldJson = new LinkedHashMap<>();
                    fieldJson.put("name", member.getSimpleName().toString());
                    fieldJson.put("type", simplifyType(member.asType().toString()));
                    // Add field Javadoc if present
                    DocCommentTree fieldDoc = env.getDocTrees().getDocCommentTree(member);
                    String fieldDesc = "";
                    if (fieldDoc != null) {
                        fieldDesc = convertToMarkdown(extractDescription(fieldDoc, currentPackage), currentPackage)
                            .replace("\n", " ").replace("\r", " ").trim();
                    }
                    fieldJson.put("description", fieldDesc);
                    fields.add(fieldJson);
                }
            }
        }
        if (!fields.isEmpty()) {
            json.put("fields", fields);
        }

        writeJsonToFile(json, classDir.resolve("index.md.json"));
    }

    private void generateMethodJson(List<ExecutableElement> methods, String className,
            DocletEnvironment env, Path classDir) throws IOException {
        if (methods.isEmpty())
            return;

        ExecutableElement primaryMethod = methods.get(0);
        Map<String, Object> json = new LinkedHashMap<>();

        String currentPackage = getPackageName((TypeElement) primaryMethod.getEnclosingElement());

        json.put("template", "class-method");
        json.put("related", new ArrayList<>());
        json.put("name", primaryMethod.getSimpleName().toString() + "()");

        // --- Combine descriptions for overloaded methods ---
        StringBuilder combinedDescription = new StringBuilder();
        String commonHeading = "";
        boolean headingSet = false;
        List<String> overloadBodies = new ArrayList<>();
        Integer indexInDocs = null;
        Map<String, String> customTags = new HashMap<>();

        for (int i = 0; i < methods.size(); i++) {
            ExecutableElement method = methods.get(i);
            DocCommentTree docComment = env.getDocTrees().getDocCommentTree(method);
            String description = "";
            Map<String, String> tags = new HashMap<>();
            if (docComment != null) {
                description = extractDescriptionRaw(docComment);
                tags = extractCustomTags(docComment);
                if (tags.containsKey("index-in-docs")) {
                    try {
                        indexInDocs = Integer.parseInt(tags.get("index-in-docs").trim());
                    } catch (NumberFormatException e) {
                        // ignore invalid numbers
                    }
                }
            }
            if (i == 0) {
                customTags = tags;
            }
            // Split at first line break (empty line or \n\n)
            int headingEnd = description.indexOf("\n\n");
            if (headingEnd == -1) headingEnd = description.indexOf("\r\n\r\n");
            if (headingEnd == -1) headingEnd = description.indexOf("<p>");
            if (headingEnd == -1) headingEnd = description.length();

            String heading = description.substring(0, headingEnd).trim();
            String body = description.substring(Math.min(headingEnd + 2, description.length())).trim();

            if (!headingSet) {
                commonHeading = heading;
                headingSet = true;
            }
            if (!body.isEmpty()) {
                overloadBodies.add(body);
            }
        }
        combinedDescription.append(commonHeading);
        if (!overloadBodies.isEmpty()) {
            combinedDescription.append("\n\n");
            for (String body : overloadBodies) {
                combinedDescription.append(body).append("\n\n");
            }
        }
        String finalDescription = convertToMarkdown(combinedDescription.toString().trim(), currentPackage);

        // Parse custom tags from primary method
        DocCommentTree docComment = env.getDocTrees().getDocCommentTree(primaryMethod);
        if (docComment != null) {
            customTags = extractCustomTags(docComment);
        }
        json.put("scratchblock", customTags.getOrDefault("scratchblock", ""));
        json.put("class", className);
        json.put("description", finalDescription.trim());
        if (indexInDocs != null) {
            json.put("index", indexInDocs);
        }

        // Collect all syntax variations from overloaded methods
        List<String> syntaxList = new ArrayList<>();
        for (ExecutableElement method : methods) {
            syntaxList.add(buildMethodSyntax(method));
        }
        json.put("syntax", syntaxList);

        // Return type (from primary method)
        String returnType = primaryMethod.getReturnType().toString();
        json.put("returns", simplifyType(returnType));

        // Examples - support multiple examples
        json.put("examples", buildExamples(customTags));

        // Collect parameters from ALL overloaded methods
        Map<String, Map<String, String>> allParameters = new LinkedHashMap<>();

        for (ExecutableElement method : methods) {
            DocCommentTree methodDoc = env.getDocTrees().getDocCommentTree(method);

            for (VariableElement param : method.getParameters()) {
                String paramName = param.getSimpleName().toString();

                if (!allParameters.containsKey(paramName)) {
                    Map<String, String> paramMap = new LinkedHashMap<>();
                    paramMap.put("name", paramName);

                    String paramDesc = extractParamDescription(methodDoc, paramName);
                    paramDesc = convertToMarkdown(paramDesc, currentPackage);
                    // remove newlines from param description
                    paramDesc = paramDesc.replace("\n", " ").replace("\r", " ");
                    // remove duplicate spaces
                    paramDesc = paramDesc.replaceAll(" +", " ");
                    paramMap.put("description", paramDesc);
                    paramMap.put("type", simplifyType(param.asType().toString()));

                    allParameters.put(paramName, paramMap);
                }
            }
        }

        json.put("parameters", new ArrayList<>(allParameters.values()));

        // Write to file: ClassName/methodName.md.json
        String filename = primaryMethod.getSimpleName() + ".md.json";
        writeJsonToFile(json, classDir.resolve(filename));
    }

    private List<Map<String, Object>> buildExamples(Map<String, String> customTags) {
        List<Map<String, Object>> examples = new ArrayList<>();

        // Look for example.N.preview, example.N.folder, example.N.files tags
        Map<Integer, Map<String, String>> exampleMap = new HashMap<>();

        for (Map.Entry<String, String> entry : customTags.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("example.")) {
                String[] parts = key.split("\\.", 3);
                if (parts.length == 3) {
                    // Format: example.N.property
                    try {
                        int index = Integer.parseInt(parts[1]);
                        String property = parts[2];
                        exampleMap.computeIfAbsent(index, k -> new HashMap<>())
                                .put(property, entry.getValue());
                    } catch (NumberFormatException e) {
                        // Not a numbered example, skip
                    }
                } else if (parts.length == 2) {
                    // Format: example.property (default to index 0)
                    String property = parts[1];
                    exampleMap.computeIfAbsent(0, k -> new HashMap<>())
                            .put(property, entry.getValue());
                }
            }
        }

        // Build examples in order
        for (int i = 0; i < 100; i++) { // Support up to 100 examples
            if (exampleMap.containsKey(i)) {
                Map<String, String> exampleData = exampleMap.get(i);
                if (exampleData.containsKey("preview")) {
                    Map<String, Object> example = new LinkedHashMap<>();
                    example.put("preview", exampleData.get("preview"));
                    example.put("folder", exampleData.getOrDefault("folder", ""));

                    // Parse example files with static pattern
                    List<Map<String, String>> files = new ArrayList<>();
                    String exampleFiles = exampleData.getOrDefault("files", "");
                    String examplePattern = exampleData.getOrDefault("lines", "reg:([Rr]ecorder|package|@ignore)");

                    if (!exampleFiles.isEmpty()) {
                        for (String fileName : exampleFiles.split(";")) {
                            Map<String, String> fileMap = new LinkedHashMap<>();
                            fileMap.put("src", fileName.trim());
                            fileMap.put("lines", examplePattern);
                            files.add(fileMap);
                        }
                    }
                    example.put("files", files);
                    examples.add(example);
                }
            }
        }

        return examples;
    }

    private String buildConstructorSyntax(ExecutableElement constructor, String className) {
        StringBuilder syntax = new StringBuilder();
        syntax.append("new ").append(className).append("(");

        List<String> params = constructor.getParameters().stream()
                .map(p -> p.getSimpleName().toString())
                .collect(Collectors.toList());

        syntax.append(String.join(", ", params));
        syntax.append(")");

        return syntax.toString();
    }

    private String buildMethodSyntax(ExecutableElement method) {
        StringBuilder syntax = new StringBuilder();
        syntax.append(".").append(method.getSimpleName()).append("(");

        List<String> params = method.getParameters().stream()
                .map(p -> p.getSimpleName().toString())
                .collect(Collectors.toList());

        syntax.append(String.join(", ", params));
        syntax.append(")");

        return syntax.toString();
    }

    private String extractDescription(DocCommentTree docComment, String currentPackage) {
        StringBuilder desc = new StringBuilder();
        for (DocTree tree : docComment.getFullBody()) {
            desc.append(tree.toString());
        }
        String description = desc.toString().trim();
        return convertToMarkdown(description, currentPackage);
    }

    // Helper to extract raw description (without markdown conversion)
    private String extractDescriptionRaw(DocCommentTree docComment) {
        StringBuilder desc = new StringBuilder();
        for (DocTree tree : docComment.getFullBody()) {
            desc.append(tree.toString());
        }
        return desc.toString().trim();
    }

    /**
     * Converts Javadoc HTML to Markdown format.
     * Specifically handles {@code} blocks within
     * 
     * <pre>
     * tags and {@link} tags.
     * 
     * @param html           The HTML content to convert
     * @param currentPackage The package of the current element for relative link
     *                       calculation
     */
    private String convertToMarkdown(String html, String currentPackage) {
        // Convert <pre>{@code ...}</pre> to markdown code blocks
        // Pattern matches: <pre>\s*\\{@code\s* ... }\s*</pre>
        String result = html.replaceAll(
                "(?s)<pre>\\s*\\{@code\\s*(.*?)\\s*\\}\\s*</pre>",
                "\n```java\n$1\n```\n");

        // Also handle <pre><code> ... </code></pre>
        result = result.replaceAll(
                "(?s)<pre>\\s*<code>\\s*(.*?)\\s*</code>\\s*</pre>",
                "\n```java\n$1\n```\n");

        // Handle standalone {@code ...} for inline code
        result = result.replaceAll("\\{@code\\s+(.*?)\\}", "`$1`");

        // Convert <p> tags to markdown paragraph breaks (double newline)
        result = result.replaceAll("(?i)<p>\\s*", "\n\n");
        result = result.replaceAll("(?i)</p>\\s*", "\n\n");

        // Clean up multiple consecutive newlines (more than 2)
        result = result.replaceAll("\n{3,}", "\n\n");

        // Handle {@link} tags - convert to relative markdown links
        result = convertLinks(result, currentPackage);

        return result.trim();
    }

    /**
     * Converts {@link} tags to relative markdown links.
     * Examples:
     * {@link org.openpatch.scratch.Window} -> [Window](../Window/index.md)
     * {@link Window} -> [Window](../Window/index.md)
     * {@link Window#show()} -> [Window.show()](../Window/show.md)
     * {@link #move(int)} -> [move()](move.md)
     * 
     * @param text           The text containing {@link} tags
     * @param currentPackage The package of the current element
     */
    private String convertLinks(String text, String currentPackage) {
        StringBuilder result = new StringBuilder();
        int lastIndex = 0;

        // Find all {@link ...} patterns
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{@link\\s+([^}]+)\\}");
        java.util.regex.Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            // Append text before the match
            result.append(text.substring(lastIndex, matcher.start()));

            // Convert the link
            String linkContent = matcher.group(1).trim();
            result.append(formatLink(linkContent, currentPackage));

            lastIndex = matcher.end();
        }

        // Append remaining text
        result.append(text.substring(lastIndex));

        return result.toString();
    }

    private String formatLink(String linkContent, String currentPackage) {
        // Split into class reference and member reference (method/field)
        String[] parts = linkContent.split("#", 2);
        String classRef = parts[0].trim();
        String memberRef = parts.length > 1 ? parts[1].trim() : null;

        // Handle same-class method reference (#method())
        if (classRef.isEmpty() && memberRef != null) {
            String methodName = memberRef.replaceAll("\\(.*\\)", "");
            return "[" + memberRef + "](" + methodName + ")";
        }

        // Determine the target package
        String targetPackage;
        String simpleClassName;

        if (classRef.contains(".")) {
            // Full package path provided
            int lastDot = classRef.lastIndexOf('.');
            targetPackage = classRef.substring(0, lastDot);
            simpleClassName = classRef.substring(lastDot + 1);
        } else {
            // Simple class name - assume same package
            targetPackage = currentPackage;
            simpleClassName = classRef;
        }

        // Calculate relative path between packages
        String relativePath = calculateRelativePath(currentPackage, targetPackage, simpleClassName);

        // Format link text and URL
        String linkText;
        String linkUrl;

        if (memberRef != null) {
            // Link to method: [ClassName.method()](../ClassName/method)
            String methodName = memberRef.replaceAll("\\(.*\\)", "");
            linkText = simpleClassName + "." + memberRef;
            linkUrl = relativePath + "/" + methodName;
        } else {
            // Link to class: [ClassName](../ClassName/index)
            linkText = simpleClassName;
            linkUrl = relativePath + "/index";
        }

        return "[" + linkText + "](" + linkUrl + ")";
    }

    /**
     * Calculates the relative path from current package to target class.
     * 
     * @param currentPackage  The package we're linking from
     * @param targetPackage   The package of the target class
     * @param targetClassName The simple name of the target class
     * @return Relative path like "../Window" or
     *         "../extensions/animation/AnimatedSprite"
     */
    private String calculateRelativePath(String currentPackage, String targetPackage, String targetClassName) {
        // Remove base package from both
        String currentRelative = removeBasePackage(currentPackage);
        String targetRelative = removeBasePackage(targetPackage);

        // Same package - just reference the class in parent directory
        if (currentRelative.equals(targetRelative)) {
            return "../" + targetClassName;
        }

        // Different packages - build full relative path
        if (targetRelative.isEmpty()) {
            // Target is in base package
            return "../" + targetClassName;
        } else {
            // Target is in a sub-package
            String targetPath = targetRelative.replace('.', '/');
            return "../" + targetPath + "/" + targetClassName;
        }
    }

    private String removeBasePackage(String packageName) {
        if (packageName.equals(BASE_PACKAGE)) {
            return "";
        } else if (packageName.startsWith(BASE_PACKAGE + ".")) {
            return packageName.substring(BASE_PACKAGE.length() + 1);
        }
        return packageName;
    }

    private Map<String, String> extractCustomTags(DocCommentTree docComment) {
        Map<String, String> tags = new HashMap<>();
        for (DocTree tree : docComment.getBlockTags()) {
            if (tree.getKind() == DocTree.Kind.UNKNOWN_BLOCK_TAG) {
                UnknownBlockTagTree tag = (UnknownBlockTagTree) tree;
                String tagName = tag.getTagName();
                String tagValue = tag.getContent().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining())
                        .trim();
                tags.put(tagName, tagValue);
            }
        }
        return tags;
    }

    private String extractParamDescription(DocCommentTree docComment, String paramName) {
        if (docComment == null)
            return "";

        for (DocTree tree : docComment.getBlockTags()) {
            if (tree.getKind() == DocTree.Kind.PARAM) {
                ParamTree param = (ParamTree) tree;
                if (param.getName().toString().equals(paramName)) {
                    return param.getDescription().stream()
                            .map(Object::toString)
                            .collect(Collectors.joining())
                            .trim();
                }
            }
        }
        return "";
    }

    private String simplifyType(String type) {
        // Handle all generics recursively, e.g. java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>
        int genericStart = type.indexOf('<');
        int genericEnd = type.lastIndexOf('>');
        if (genericStart != -1 && genericEnd != -1 && genericEnd > genericStart) {
            String rawType = type.substring(0, genericStart);
            String generics = type.substring(genericStart + 1, genericEnd);

            // Remove package from raw type
            int lastDot = rawType.lastIndexOf('.');
            String simpleRawType = lastDot >= 0 ? rawType.substring(lastDot + 1) : rawType;

            // Recursively simplify generic parameters, split by comma not inside <>
            List<String> params = new ArrayList<>();
            int depth = 0;
            StringBuilder current = new StringBuilder();
            for (int i = 0; i < generics.length(); i++) {
                char c = generics.charAt(i);
                if (c == '<') depth++;
                if (c == '>') depth--;
                if (c == ',' && depth == 0) {
                    params.add(simplifyType(current.toString().trim()));
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
            if (current.length() > 0) {
                params.add(simplifyType(current.toString().trim()));
            }
            return simpleRawType + "<" + String.join(", ", params) + ">";
        }
        // Remove package names, keep only simple class name
        int lastDot = type.lastIndexOf('.');
        if (lastDot > 0) {
            type = type.substring(lastDot + 1);
        }
        return type;
    }

    private void writeJsonToFile(Map<String, Object> json, Path file) throws IOException {
        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write(toJson(json, 0));
        }
    }

    private String toJson(Object obj, int indent) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof String) {
            return "\"" + escapeJson((String) obj) + "\"";
        } else if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof List) {
            return listToJson((List<?>) obj, indent);
        } else if (obj instanceof Map) {
            return mapToJson((Map<?, ?>) obj, indent);
        }
        return "\"" + obj.toString() + "\"";
    }

    private String listToJson(List<?> list, int indent) {
        if (list.isEmpty())
            return "[]";

        StringBuilder sb = new StringBuilder("[\n");
        String indentStr = "  ".repeat(indent + 1);

        for (int i = 0; i < list.size(); i++) {
            sb.append(indentStr).append(toJson(list.get(i), indent + 1));
            if (i < list.size() - 1)
                sb.append(",");
            sb.append("\n");
        }

        sb.append("  ".repeat(indent)).append("]");
        return sb.toString();
    }

    private String mapToJson(Map<?, ?> map, int indent) {
        if (map.isEmpty())
            return "{}";

        StringBuilder sb = new StringBuilder("{\n");
        String indentStr = "  ".repeat(indent + 1);

        List<? extends Map.Entry<?, ?>> entries = new ArrayList<>(map.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<?, ?> entry = entries.get(i);
            sb.append(indentStr)
                    .append("\"").append(entry.getKey()).append("\": ")
                    .append(toJson(entry.getValue(), indent + 1));
            if (i < entries.size() - 1)
                sb.append(",");
            sb.append("\n");
        }

        sb.append("  ".repeat(indent)).append("}");
        return sb.toString();
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Checks if an element should be ignored based on @ignore-in-docs tag.
     * 
     * @param element The element to check
     * @param env     The doclet environment
     * @return true if the element should be ignored
     */
    private boolean shouldIgnore(Element element, DocletEnvironment env) {
        DocCommentTree docComment = env.getDocTrees().getDocCommentTree(element);
        if (docComment == null) {
            return false;
        }

        for (DocTree tree : docComment.getBlockTags()) {
            if (tree.getKind() == DocTree.Kind.UNKNOWN_BLOCK_TAG) {
                UnknownBlockTagTree tag = (UnknownBlockTagTree) tree;
                if ("ignore-in-docs".equals(tag.getTagName())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if a package should be ignored based on @ignore-in-docs tag in
     * package-info.java.
     * 
     * @param packageName The package name to check
     * @param env         The doclet environment
     * @return true if the package should be ignored
     */
    private boolean isPackageIgnored(String packageName, DocletEnvironment env) {
        for (Element element : env.getIncludedElements()) {
            if (element.getKind() == ElementKind.PACKAGE) {
                PackageElement pkg = (PackageElement) element;
                if (pkg.getQualifiedName().toString().equals(packageName)) {
                    return shouldIgnore(pkg, env);
                }
            }
        }
        return false;
    }
}