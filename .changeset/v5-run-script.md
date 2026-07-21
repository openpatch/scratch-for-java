---
type: patch
---

Add `scripts/run.sh` for picking something to run.

```
./scripts/run.sh
```

Lists every demo, finished tutorial project and reference example — 153 of them
— in an [fzf](https://github.com/junegunn/fzf) picker with a preview of the
source, and runs whatever you choose.

```
./scripts/run.sh cat          # start with the list filtered
./scripts/run.sh --demos      # one kind only
```

A query matching exactly one thing runs it straight away. Demos and reference
examples run from `target/classes`; the tutorial projects are not part of the
build, so they are compiled on the fly with their own folder on the classpath so
their assets are found.
