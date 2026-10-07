# Portable behavioral checks, schema 1

Keep checks as ordinary Java alongside the project. Separate logic from the
window: use explicit frame calls, real headless costume hitboxes and
`org.openpatch.scratch.Random.randomSeed(seed)`. A seed makes a run reproducible
within a runtime; random algorithms need not produce identical numbers across runtimes.

Shared check bodies use normal Java and throw when a condition is false. The
canonical `checks/PortableBehaviorChecks.java` runs unchanged in desktop JUnit
and Chromium. Desktop needs no window; the browser wrapper creates a Stage for
WebGL, runs the same body and pauses. Clock/lifecycle probes separately exercise
actual stepping and asynchronous rendering.

Students can keep the browser teaching test format:

```java
@Test
class ScoreTest {
    @Test
    void startsAtZero() {
        assertEquals(0, new Score().getPoints(), "Initial score");
    }
}
```

Studio and the curriculum desktop checker remove only the class annotation in
memory and inject JUnit Test/assertion imports. Line breaks and original source
files are preserved. Test methods retain their annotations. JUnit 5.11.4 /
console 1.11.4 is bundled in Studio; the curriculum checker downloads that exact
console artifact once and verifies its SHA-256, or uses its matching local cache.
Other Java IDEs need explicit JUnit imports and no class annotation.

Optional `.scratch4j/checks.json` selects tests:

```json
{"schemaVersion":1,"mode":"logic","classes":["ScoreTest"]}
```

`logic` runs in a headless child JVM. `graphics` permits a window and needs a
display. Studio/CLI applies a 30-second limit. Without a manifest, Studio discovers
Java files containing `@Test`. Compilation errors, assertion failures, zero tests
and timeouts fail the run. The browser uses its Testrunner. ZIP/JSON adapters
preserve sources and the check manifest, excluding caches.

Run using Studio's Run > Run behavior checks command, or the
`org.openpatch.scratch4j.runner.TeachingTestRunner <project-folder>` CLI.
The curriculum gate compiles all archives, including test classes, and runs
PunkteregelTest on desktop. Interactive curriculum checks compile it in the browser.
Canonical behavioral checks run in both runtime CI gates.
