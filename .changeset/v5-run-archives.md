---
type: patch
---

Two ways to run the finished tutorial projects from a checkout.

**Without building a jar**, using the library exactly as it stands in your
working copy:

```
mvn -q compile exec:exec -Parchive -Darchive=make-it-walk-100 -Dmain=WalkStage
```

**With BlueJ or VS Code**, which need a real jar in each project's `+libs`:

```
mvn package -Pall -DskipTests
./scripts/link-jar.sh
```

The linked jars are for local testing only. They are git-ignored, and `build.sh`
removes them before zipping, because a jar left behind would put 19 MB inside
every project download. Both are documented in the README.
