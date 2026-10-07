# Release catalogs

The doclet supplies public overloads, parameter names and documentation.
Availability comes from the reviewed compatibility contract. Translation
overrides use stable identifiers: documentation group plus erased parameters.
Do not overwrite translated descriptions when regenerating English Javadoc.

Generate after the doclet and compatibility artifacts. Pass explicit consumer
paths to synchronize their committed snapshots:

    mvn -q -DskipTests prepare-package
    python3 scripts/browser-compatibility.py --online-ide /path/to/online-ide
    python3 scripts/release-catalogs.py --online-ide /path/to/online-ide --studio /path/to/studio
    python3 scripts/release-catalogs.py --check --online-ide /path/to/online-ide --studio /path/to/studio

The same JSON is distributed in the library JAR, documentation, Studio and
browser assets. Example entries include checksums for source and asset files.
The --archive option packages those ordinary files:

    python3 scripts/release-catalogs.py --archive target/scratch-catalogs.zip

Studio can update templates from the release artifact without a sibling checkout:

    python3 scripts/sync-templates.py --artifact /path/to/scratch-catalogs.zip --check

Older libraries can use local source JARs. Without source metadata, Studio filters
the current descriptions against the project's actual public binary signatures.
No network request or class initialization is needed to select help.

CI checks consumer snapshots against the library's default branch. Coordinated
changes must merge their generated snapshots together before these gates pass.
