# Browser compatibility artifacts

`browser-policy.json` records the reviewed desktop version, browser revision,
library flavors, intentional desktop limitations and known behavioral gaps.
`browser-api.json` is a snapshot of the browser compiler's declarations for both
flavors. The Javadoc doclet adds structured owners and overloads to generated
reference pages; `scripts/browser-compatibility.py` combines them into
`contract.json`, the table in `browser-compatibility.md` and browser reference fixtures.

The contract covers documented public constructors and method groups. Overload
matching uses simple erased parameter names and static/constructor kinds. The
full desktop types remain in the artifact. Compilation and declaration coverage
do not establish complete behavioral or binary compatibility.

## Updating both repositories

Use an explicit checkout path; the repositories need not be siblings:

```bash
# In online-ide: refresh the inventory after a declaration change.
UPDATE_SCRATCH_INVENTORY=1 npm run test:ci -- src/test/ScratchCompatibilityTest.test.ts -t "inventory matches"

# In scratch-for-java: generate reference metadata and synchronize consumers.
mvn -q -DskipTests prepare-package
python3 scripts/browser-compatibility.py --online-ide /path/to/online-ide
python3 scripts/parity-fixture.py --check --strict --online-ide /path/to/online-ide

# In online-ide: validate the newly generated examples and contract.
npm run test:ci -- src/test/ScratchCompatibilityTest.test.ts

# In scratch-for-java: read-only freshness check.
python3 scripts/browser-compatibility.py --check --online-ide /path/to/online-ide
```

When changing the desktop version, review the policy and advance the browser
revision explicitly. For a browser behavioral change, advance its revision even
if declarations stay the same. Remove resolved policy overrides and diagnostic
exceptions after the corresponding behavior is implemented and tested.

`browser-reference-diagnostics.json` contains the 13 known unsupported examples'
exact diagnostic IDs and ranges. It is reviewed input, not an automatically
updated failure baseline. All examples are compiled. Unexpected failures and
unexpected fixes both fail: a fix should remove its exception and update its
availability. Compiling clone examples remain read-only until lifecycle behavior
is implemented. Supported overload examples can remain runnable on a page that
also documents missing overloads.

## CI and publishing

The browser suite validates its own snapshot and fixtures without a library
checkout. Library tests and documentation publishing additionally require an
explicit browser checkout, artifact freshness and strict parity checks. The
shared CI action uses `openpatch/online-ide`'s default branch. Coordinate changes
across repositories: merge a synchronized browser consumer before library CI
expects it, or test both working copies locally while preparing the changes.

`build.sh` generates availability annotations and publishes `/compatibility.json`.
Browser builds publish `/scratch-compatibility.json`; `Window.getLibraryVersion()`
reports its browser revision, currently `5.8.0-browser.1`.
