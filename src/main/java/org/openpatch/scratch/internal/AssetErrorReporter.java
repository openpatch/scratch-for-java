package org.openpatch.scratch.internal;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that builds beginner-friendly error messages when an asset file
 * cannot be loaded. It shows the resolved absolute path, checks whether the
 * parent folder exists, and lists nearby files to help spot typos.
 */
public final class AssetErrorReporter {

  private AssetErrorReporter() {}

  private static final int MAX_LISTED_FILES = 6;

  /**
   * Prints a detailed, beginner-friendly error for a failed asset load and
   * then exits.
   *
   * @param assetType       Human-readable type, e.g. "image", "sound", "font"
   * @param originalPath    The path string the user supplied
   * @param supportedFormats Short list of accepted extensions, e.g. "PNG, JPG, GIF"
   * @param fileExtensions  Extensions used to filter similar files in the same dir,
   *                        e.g. new String[]{".png",".jpg",".gif"}
   */
  public static void reportAndExit(
      String assetType,
      String originalPath,
      String supportedFormats,
      String[] fileExtensions) {

    String resolvedPath = Applet.getPath(originalPath);
    File resolved = new File(resolvedPath).getAbsoluteFile();
    File parentDir = resolved.getParentFile();

    // If the parent dir wasn't found via getPath(), try finding it on the classpath.
    // This handles cases where the file doesn't exist (typo) but the folder does.
    if (parentDir == null || !parentDir.exists()) {
      String parentRelPath = new File(originalPath).getParent();
      if (parentRelPath != null) {
        URL parentResource = Thread.currentThread().getContextClassLoader().getResource(parentRelPath);
        if (parentResource != null && "file".equals(parentResource.getProtocol())) {
          try {
            parentDir = new File(parentResource.toURI());
          } catch (URISyntaxException ignored) {}
        }
      }
    }

    System.err.println("\n==============================================");
    System.err.println("ERROR: Could not load " + assetType + " file!");
    System.err.println("==============================================");
    System.err.println("You wrote:      " + originalPath);
    System.err.println("Java looked at: " + resolvedPath);

    if (parentDir == null || !parentDir.exists()) {
      String folderPath = parentDir != null ? parentDir.getPath() : "(unknown)";
      System.err.println("\nThe folder does not exist: " + folderPath);
      System.err.println("\nTips:");
      System.err.println("  - Check the folder name for typos");
      System.err.println("  - Make sure the folder is inside your assets directory");
    } else {
      // Collect files with relevant extensions
      File[] siblings = parentDir.listFiles(f -> {
        if (!f.isFile()) return false;
        String lower = f.getName().toLowerCase();
        for (String ext : fileExtensions) {
          if (lower.endsWith(ext)) return true;
        }
        return false;
      });

      if (siblings == null || siblings.length == 0) {
        System.err.println("\nNo " + assetType + " files found in: " + parentDir.getPath());
        System.err.println("  Supported formats: " + supportedFormats);
        System.err.println("\nTip: Add a " + assetType + " file to that folder first.");
      } else {
        // Look for a case-insensitive match
        String targetName = resolved.getName();
        List<String> suggestions = new ArrayList<>();
        for (File f : siblings) {
          if (f.getName().equalsIgnoreCase(targetName)) {
            suggestions.add(0, f.getName()); // exact case-insensitive match — top of list
          } else {
            suggestions.add(f.getName());
          }
        }

        if (suggestions.get(0).equalsIgnoreCase(targetName)
            && !suggestions.get(0).equals(targetName)) {
          System.err.println("\n*** Did you mean: '" + suggestions.get(0) + "'? (case mismatch)");
        } else {
          System.err.println("\nFile '" + targetName + "' was not found in: " + parentDir.getPath());
        }

        System.err.println("\n" + capitalize(assetType) + " files in that folder:");
        int shown = 0;
        for (String name : suggestions) {
          if (shown >= MAX_LISTED_FILES) {
            System.err.println("  ... and " + (suggestions.size() - shown) + " more");
            break;
          }
          System.err.println("  - " + name);
          shown++;
        }
        System.err.println("\nTip: Copy the exact filename from the list above into your code.");
      }
    }

    System.err.println("==============================================\n");
    System.exit(1);
  }

  /**
   * Prints a beginner-friendly error for a name that was meant to be an asset
   * bundled with Scratch for Java but does not exist, and then exits.
   *
   * @param name      the name the user supplied
   * @param assetType either "sprite" or "sound"
   */
  public static void reportUnknownBuiltinAndExit(String name, String assetType) {
    boolean isSound = "sound".equals(assetType);

    System.err.println("\n==============================================");
    System.err.println("ERROR: Unknown built-in " + assetType + "!");
    System.err.println("==============================================");
    System.err.println("You wrote: " + name);
    System.err.println("\nThere is no built-in " + assetType + " with that name.");

    List<String> suggestions = isSound
        ? BuiltinSounds.suggest(name, MAX_LISTED_FILES)
        : BuiltinAssets.suggest(name, MAX_LISTED_FILES);
    if (!suggestions.isEmpty()) {
      System.err.println("\n*** Did you mean:");
      for (String suggestion : suggestions) {
        System.err.println("  - " + suggestion);
      }
    }

    if (name.indexOf('/') >= 0) {
      System.err.println("\nTip: If you meant a file of your own, add the file");
      System.err.println("     extension, for example \"" + name
          + (isSound ? ".wav" : ".png") + "\".");
    }
    System.err.println("\nTip: All built-in " + assetType + "s are listed at");
    System.err.println("     https://scratch4j.openpatch.org/" + assetType + "s");
    System.err.println("==============================================\n");
    System.exit(1);
  }

  private static String capitalize(String s) {
    if (s == null || s.isEmpty()) return s;
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }
}
