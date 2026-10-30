# AGENTS.md

Base of the coldis Java libraries: helpers, exceptions, shared model contracts and local caches. Every other coldis library depends on it.

Part of the coldis Java libraries (`org.coldis.library`, GitHub `coldis-org/library-java-core`). Each
library is a single Maven module whose parent is `org.coldis:configuration` (sibling checkout
`../coldis-configuration`); Java version, AspectJ, Hibernate enhancement and release settings live
there.

## Packages

All under `src/main/java/org/coldis/library/`.

| Package | Holds |
|---------|-------|
| `helper` | `DateTimeHelper`, `ObjectHelper`, `ReflectionHelper`, `StringHelper`, `BufferedReducer`, … |
| `exception` | `BusinessException`, `IntegrationException` and the message model they carry |
| `model` | Shared contracts (`Typable`, `Identifiable`, `Expirable`, `Timestampable`, `RetryBackoff`) and `view` (`ModelView` for `@JsonView`), `statistics`, `verification`, `science` |
| `cache` | Local periodic-job and statistics-summary caches |
| `thread` | Dynamic thread pools and thread-bound context |

Depends on: no other coldis library. Used by: `dto`, `pdf`, `persistence`, `serialization`, `service`, `service-client`, `test`, and the services built on them.

## Commands

```bash
mvn clean install -Dnet.bytebuddy.experimental=true > build.log 2>&1
mvn test -Dnet.bytebuddy.experimental=true -Dtest=<Class> > test.log 2>&1
```

Send Maven output to a file and read it afterwards; don't pipe it through `grep`/`tail`.

## Gotchas

- **`-Dnet.bytebuddy.experimental=true`** is needed on a JVM newer than Byte Buddy supports (Java 25
  locally): `hibernate-enhance-maven-plugin` fails without it. Don't work around it by switching
  `JAVA_HOME`. The release plugin already passes the flag.
- **AspectJ compile-time weaving** (from the parent pom): `@Transactional` and the other aspects
  work on private methods and self-calls. Run through `test`/`install`, not a bare
  `surefire:test` after `test-compile`.
- **This is a library.** Every change ships to every consumer on their next version bump. Keep
  public APIs and defaults backward compatible, or name the break in the PR description.
- **Releases are local:** `mvn release:prepare release:perform`. The release plugin skips tests,
  so run the full suite first and never release with failing tests. Release upstream libraries
  before the ones that depend on them, then bump the version in the consumers.

## Conventions

Code:
- No `var`, and no single-letter, abbreviated or dummy names, including lambda parameters,
  exceptions and loop counters.
- One exit point per method. Only a trivial guard clause may return early; no `break`/`continue`.
- Order methods callees before callers.
- Business logic goes in `*ServiceComponent`. A `*Service` only holds endpoint (HTTP/JMS) logic.
- Use `DateTimeHelper.getCurrentLocalDate[Time]()`, never `LocalDate[Time].now()`. Use
  `BigDecimal` for money, rates and percentages.
- Don't swallow exceptions to fail open.
- JSONB-persisted classes: removing a getter or setter, renaming one, or changing a field's type
  silently loses stored data. Keep the old name with `@JsonProperty` or a compatible setter, and
  give new fields a default that applies when old records lack them.
- Put JPA and Jackson annotations (`@Id`, `@Column`, `@JsonView`, `@Transient`, …) on the getter,
  not the field.
- Log with SLF4J (`LOGGER`), never `System.out`. Keep diagnostic logs at `debug`.
- Never hand-edit generated code. Change the source and regenerate.

Tests:
- No Mockito. Build real objects and go through real Spring beans and containers.
- Don't split the Spring test context (`@TestPropertySource`, `@MockBean`, `@DirtiesContext`, …).

Git:
- Branch names start with `feature/`, `bugfix/` or `hotfix/`.
- Stage explicit paths, not `-A`.

## Workspace

This repository expects `supersim-infrastructure-code-reviewer` checked out next to it
(`../supersim-infrastructure-code-reviewer`; clone
`https://ci.gov.tech.supersim.com.br/supersim/infrastructure-code-reviewer.git` there if it's
missing). The review gates and the local review both live in it.

## Review

The gates are those of `src/agents/review/review.toml` when this repository has one, otherwise
the reviewer's defaults (service family plus `security` and `infrastructure`). What each gate
stops: `../supersim-infrastructure-code-reviewer/local-review/GATES.md`. The service gates were
written for application services: rules about client/common/test subprojects, generated clients,
integration services, service tiers or migrations apply here only where this library has that
concept. The PR description is what the gates read as intent: name every default, limit or
behaviour you change there.

Before pushing, run the local review: follow
`../supersim-infrastructure-code-reviewer/local-review/REVIEW.md`. It ends with
`local-check attest`. Push only when the developer asks.
