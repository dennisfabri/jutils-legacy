# AGENTS Guide

jutils-legacy is the collection of technical libraries behind JAuswertung (plugin framework, Swing, print and I/O
utilities, i18n, wizard, font chooser). It is a Maven multi-module project (Java 25, root `pom.xml`, groupId
`de.dennisfabri.jutils`, one shared version). Code lives under `de.df.jutils.*`; some packages are split across modules
(e.g. `de.df.jutils.gui` in `jutils-swing`, `-wizard` and `-fontchooser`). There is no JPMS. The workspace guide
(`../AGENTS.md`) describes how this project relates to the others.

## A Library, Not an Application

- The main consumer is JAuswertung (`../JAuswertung-Project`); AlphaServer uses `jutils-print`. Their code
  must keep compiling against what you change here.
- Public classes and methods are API. Before renaming, moving or removing one, search every project in the workspace
  for its use; prefer adding over changing. A breaking change needs matching commits in the consumers (one commit per
  repository, see the workspace guide).
- Keep the code generic: no JAuswertung domain concepts (competitions, swimmers, rulebooks) in here.

## Modules

Listed bottom-up; the order in `<modules>` of the root `pom.xml` does not follow it, the dependencies do.

| Module | Purpose | Depends on |
| --- | --- | --- |
| `jutils-common` | Reflection, functional, util, data and IO helpers; holds `jutils.properties` (shared UI texts) | — |
| `jutils-i18n` | Resource bundle helpers (`JUtilsI18n`, `SafeTextProcessor`, `IdentityResourceBundle`, …) | — |
| `jutils-swing` | Swing components and utilities (`de.df.jutils.gui`, `graphics`, `swing`), vendored SwingX helpers | common, i18n |
| `jutils-io` | File and stream utilities | common, swing |
| `jutils-print` | Printing infrastructure (`PrintManager`, `PrintExecutor`) | swing, io |
| `jutils-plugin` | Plugin framework (`IPlugin`, `APlugin`, `AFeature`, `PluginManager`, `UpdateEvent`) | swing |
| `jutils-wizard` | Wizard dialogs | swing, io |
| `jutils-fontchooser` | `JFontChooser`: filterable font list, styles, size, preview; German/English | i18n |
| `jutils-demo` | Demo placeholder; currently without sources or POM | — |

- Lower modules never depend on higher ones; avoid cycles. When two modules need to share something, move it down to
  the lowest module both already depend on.
- External dependencies are declared with versions in the root `pom.xml` (`dependencyManagement`); modules reference
  them without versions.

## Swing Threading

- Swing components are only accessed on the EDT. Use `EDTUtils` (`jutils-swing`) or `SwingUtilities.invokeLater` to get
  there; follow what the surrounding code uses.
- Long-running work runs off the EDT and must not touch Swing components. Components in this library must not start such
  work behind the caller's back; expose the hook and let the application decide.

## Internationalisation

- Shared UI texts are in `jutils-common/src/main/resources/jutils.properties` (English) and `jutils_de.properties`
  (German), looked up with `JUtilsI18n.get(key, args…)`. Missing keys fall back to the key itself, so check that
  every new key exists in both files.
- A self-contained component may carry its own bundle next to its code, as `jutils-fontchooser` does
  (`fontchooser.properties` / `fontchooser_de.properties`). Both languages must have identical keys.
- Never hardcode user-visible text in Java code. Properties files are UTF-8.

## Exceptions

- Throw specific exceptions named after the concept and the problem rather than generic `RuntimeException`.
- Exceptions carry data only; text the user sees is built by the dialog or wizard that shows them.
- Do not swallow exceptions or end with `printStackTrace()`; handle the failure or propagate it. Do not add
  `System.out`/`System.err` output.

## Build, Test, Run

- Build and install a module (and what it needs) into `~/.m2`: `mvn -pl <module> -am install`. The consumers
  resolve this project only through `~/.m2`, so install after every change they rely on
  (`../build-dependencies.sh` builds all sibling projects in order; extra arguments go to Maven).
- Tests only: `mvn -pl <module> -am test`; one class / method: `-Dtest=ClassName` / `-Dtest=ClassName#method`
  (add `-Dsurefire.failIfNoSpecifiedTests=false` when `-am` pulls in modules without that test).
- Full build with tests: `mvn clean verify`. When finishing a branch, a call to `mvn verify` is required.
- Swing tests that need a display run under `xvfb-run -a mvn …`; prefer tests that work headless.
- Version updates: `versions-maven-plugin` with `version-rules.xml`. After a version change in this project, the
  consumers (`jutils.version` in JAuswertung and AlphaServer) must follow.

## Tests

- JUnit 6 (Jupiter). Name test classes `*Test`; older ones are `*Tests`, keep the name when extending those.
- Many modules have few or no tests (`jutils-swing`, `jutils-i18n`, `jutils-wizard`); new code comes with tests.
  Keep logic out of Swing classes where it can live in a plain class that is testable without a display.

## Documentation and Issues

- `docs/issues/` tracks open findings from code work: one self-contained file per issue. When the issue is fixed and
  verified, delete its file — the commit that fixes it is the record.
- A problem outside the scope of the current task becomes an issue there instead of being fixed in the same change.

## Coding Standards

- **Comments**: do not write a comment unless it provides benefit on its own — standing alone, with no other context,
  it must tell the reader something they could not already get from the code around it (names, types, structure, a
  nearby signature). A comment that restates what the code does, restates a parameter's name/type/default in prose, or
  explains something inferable in a few seconds of reading does not clear this bar. The only comments that clear it: a
  hidden constraint, a subtle invariant, or a workaround for a specific bug that isn't obvious from the code itself.
  When in doubt, don't add the comment. This is a hard rule, not a default that yields to matching a file's existing
  style — a comment already present in a file is not evidence it belongs there. Whenever you touch a file or area of
  code, also remove any existing comment in that area that fails this test.
- **PTDD (Predictive TDD)**: follow Red-Green-Refactor, but make two predictions explicit. Before writing a test, state
  precisely what the code should do and how you'll observe it (e.g. Given-When-Then). Before running a newly written or
  adjusted test, predict exactly how it will fail; if the actual failure differs, or the test passes unexpectedly, stop
  and investigate before writing any implementation — the mismatch means a wrong assumption, a broken test setup, or a
  missing dependency. Once the failure matches the prediction, implement the minimal change that turns it green, then
  tighten assertions in small increments across further red-green cycles. Source: https://ted.dev/tag/ptdd/.
- **Working approach**: keep changes localized to the requested feature or defect — no unrelated refactorings in the
  same change. Follow the dominant pattern already present in the code being touched. Preserve public APIs used by
  other modules or sibling projects unless the task explicitly requires changing them. When in doubt, make the
  smallest change that satisfies the requirement.
- **Language**: code, identifiers and documentation are English here; JAuswertung's German domain vocabulary does not
  belong in this project.

## Git Workflow

- While developing a feature, work on a feature branch and stay on it until the feature is finished. Do not merge into
  `main` in between, not even to hand a library change to another project early; build and install from the branch
  instead (`mvn install`).
- Start a branch before the first commit when `main` is checked out. Follow-up work goes on the branch that is checked
  out; do not create further branches unasked.
- Merge into `main` only when the feature is finished and the user asks for it, with a real merge commit
  (`git merge --no-ff`), never a fast-forward.
- **Commit messages**: write them in German, matching the existing history. The body is a brief bullet list of at most
  3–4 one-line bullets. Focus on intention — why the change was made and what behavior it affects — not mechanics
  visible in the diff. Don't mention tests that accompany a code change; do state it when a commit only adds or changes
  tests.

