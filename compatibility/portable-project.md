# Portable Scratch for Java project, version 1

A project ZIP contains ordinary UTF-8 Java source files and real asset files.
Paths use `/`, are relative to the project root, and preserve folders and case.
A single enclosing project folder is accepted. Build output, caches and library
JARs are excluded from browser exports. Binary workspace data URLs are decoded
to files; shaders remain UTF-8 text. No source is rewritten by the ZIP codec.

Optional metadata reuses Studio's `.scratch4j/project.json`:

```json
{
  "version": 1,
  "portableVersion": 1,
  "startStage": "Main",
  "startFile": "Main.java",
  "flavour": "standard",
  "libraryVersion": "5.7.0",
  "browserFeatures": ["Sprite/clone"],
  "lesson": "spielwerkstatt",
  "checkpoint": "ef-02-variablen",
  "externalDependencies": [],
  "desktopFiles": ["List.java"],
  "sourceEnvironment": "browser"
}
```

`startStage` names a Stage, Window, or Java main class. `startFile` chooses the
browser's entry file. `flavour` is `standard` or `nrw`; it maps to browser
libraries `["scratch"]` or `["scratch", "nrw"]`. Other Studio settings and
unknown optional metadata are retained by browser round trips. Java runs without
the metadata. NRW desktop projects still need their course's Abiturklassen.

Browser workspace JSON remains an import adapter. Its module names, folder IDs,
settings, source text and data URLs become the same project files. Compact Java
25 source with `void main()` stays a compact file; Studio's launcher supports
instance and static main methods. Studio adds the browser's implicit imports
when adapting workspace JSON or a ZIP marked `sourceEnvironment: "browser"`,
leaving explicit imports intact. `desktopFiles` lists course-provided Java
classes retained as files while the browser uses its built-in NRW classes.
A Studio project without a main method receives a separate generated browser
entry file; existing source files remain intact. Empty folders retain their IDs.

Archives with unsafe paths, conflicting names, invalid metadata, unsupported
schema versions, corrupt UTF-8, more than 2,000 files, files over 16 MiB, or total
uncompressed contents over 64 MiB fail before replacing the current workspace.
Studio also accepts older shared ZIPs containing bundled JARs, with a separate
128 MiB per-file / 256 MiB total limit. These legacy ZIPs are larger than the
portable browser limit and should use Studio's browser export before transfer.

Unsupported browser features, newer library requirements, external dependencies
and legacy spritesheets produce actionable messages. No adapter downloads or
executes dependencies. Legacy spritesheet archives require extraction into real
image files before portable export.

The ZIP stores source and assets; it does not certify that arbitrary Java code
works in both compilers. Review `/compatibility` and the importing compiler's
diagnostics. Keep a saved copy when moving between environments.
