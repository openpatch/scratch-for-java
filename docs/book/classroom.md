---
name: Classroom course
---

# Scratch to Java course

Start with the seven [introductory tutorials](/tutorials), then choose a
[Spielwerkstatt](https://informatik.openpatch.org/projekte/spielwerkstatt) mechanic.
Each tutorial has a starter based on the previous lesson and a finished checkpoint.
Keep the reflection questions and Entwicklertagebuch alongside the code.

The course pack contains ordinary Java projects, assets, Markdown lessons,
teacher notes, assessment criteria and a classroom pilot form. Studio's
**Project > Import course pack** command copies its bundled library into each
project. Prepare both standard and NRW flavors before disconnecting the school
computer from the internet.

The implementation targets desktop library **5.8.0**, browser **5.8.0-browser.1**,
Java **17** for named library projects and Java **25** for Studio/compact files.
ZIP transfer, test adaptation and course import require the coordinated IDE
update. Check [compatibility](/compatibility) and the downloaded IDE's release notes;
these capabilities are not present in every older build.

## Prepare a class

1. Import the pack into a writable course folder. Each student works on a copy.
2. Run the first rabbit project and an NRW checkpoint while offline.
3. Save and reopen the folder, then export a project ZIP.
4. Try that ZIP in the class's browser revision. Download the edited ZIP or
   workspace JSON and reopen it in Studio.
5. In the score checkpoint, select **Run > Run behavior checks** in Studio or
   the browser's **Testrunner**. Checks travel with the project.

Use checkpoints to recover a working starting point. Assess the explanation,
evidence from testing and a student's own change. Keep the reflection activities.

## Build an offline pack

From the library repository:

```sh
python3 scripts/build-course-pack.py --output target/scratch-to-java-course.zip
```

Include the four selected Spielwerkstatt checkpoints using an explicit checkout:

```sh
python3 scripts/build-course-pack.py --curriculum /path/to/hyperbook-informatik \
  --output target/scratch-to-java-course.zip
```

[Download the tutorial pack](/scratch-to-java-course.zip) generated with this documentation.
Release automation attaches the complete `scratch-to-java-course.zip` after the coordinated
changes are released. Local documentation builds generate the seven-tutorial pack.
The source teacher material is in `course/`. Markdown copies retain Hyperbook
directives; prepared Java and assets are directly usable offline.

## Classroom pilot

The pilot has not yet been carried out with students or teachers. Use `pilot.md`
from the pack to record installation, first run, save/reopen, device transfer and
recovery observations, with OS and IDE/library versions. Feed observed friction
back into the next revision.
