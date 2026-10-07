# Course pack sources

The teacher material that goes into `scratch-to-java-course.zip`:
`teacher-notes.md`, `assessment.md` and `pilot.md`. The lessons themselves are
the tutorials under `docs/book/tutorials`, and the projects are built from the
`*-100` archives under `docs/archives`.

## Building the pack

From the library repository:

```sh
python3 scripts/build-course-pack.py --output target/scratch-to-java-course.zip
```

To include the four selected Spielwerkstatt checkpoints, pass an explicit
checkout of Hyperbook Informatik:

```sh
python3 scripts/build-course-pack.py --curriculum /path/to/hyperbook-informatik \
  --output target/scratch-to-java-course.zip
```

A local documentation build generates the seven-tutorial pack and publishes it
as `/scratch-to-java-course.zip`. Release automation attaches the complete pack,
with the Spielwerkstatt checkpoints, to the GitHub release. Markdown copies of
the lessons keep their Hyperbook directives; the prepared Java projects and
assets work offline as they are.

## Classroom pilot

The pilot has not been carried out with students or teachers yet. `pilot.md` is
the form for recording it: installation, first run, save and reopen, moving a
project between computers and recovering from a lost save, with OS and
IDE/library versions. Feed what you observe into the next revision.
