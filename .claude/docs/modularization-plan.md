# GymPlannerService — Modularization Plan

Gradual migration from a single-module Spring Boot WebFlux monolith to a
multi-module Gradle build. Each phase leaves the app **fully building and
testable** — work can stop between any two phases.

---

## Background & analysis

### Starting point
- The root project *was* the Spring Boot app (all code in `src/main/kotlin/com/ianarbuckle/gymplannerservice/`).
- An `app` module existed but was an **orphan standalone Spring Initializr project** (Java, own `../../settings.gradle.kts`/`gradlew`, Spring Boot pinned by hand).

### Cross-package dependency graph
```
authentication   -> booking, common, userProfile      ⚠ cycle
booking          -> authentication, availability, common, fcm, trainers, userProfile, utils
availability     -> booking, trainers                  ⚠ cycle
checkin          -> trainers
fcm              -> authentication
fitnessclass     -> authentication, fcm
userProfile      -> authentication, booking            ⚠ cycle
facilityStatus   -> common
trainers         -> common
exercises, faultReporting, gymlocations, messages,
common, utils, validation, configuration               -> (no feature deps)
```

### Key insights
- **Three circular dependencies** exist — `authentication ↔ userProfile`,
  `authentication ↔ booking`, `availability ↔ booking`. Gradle modules cannot
  have cycles, so these must be broken before those packages become modules.
- **Authentication is a poor *first* extraction** despite being the natural
  starting point: it sits in two cycles and is imported by 4 other packages.
  It splits cleanly into:
  - **`security` (cross-cutting infra)** — `SecurityConfig`, `JwtUtils`,
    `JwtAuthenticationManager`, `AuthEntryPointJwt`. No domain coupling;
    everything depends *on* it → perfect early module.
  - **`authentication` (domain)** — `AuthenticationController`, `User`,
    `UserRepository`, `AuthenticationService`. Tangled with `userProfile`/
    `booking` → extract *after* cycles are broken.
- **Leaf packages** (`common`, `utils`, `validation`, `configuration`,
  `exercises`, `faultReporting`, `gymlocations`, `messages`) have no outgoing
  feature dependencies → safest to extract first.

### Target end-state
```
:app             entrypoint, wiring, properties, main()
:authentication  :booking :availability … (feature modules)
:security        JWT / filter chain
:core            common, utils, validation, shared exceptions
build-logic/     convention plugins (composite build)
```

---

## Phase 0 — Fix the build skeleton ✅ DONE

Move the whole monolith into `:app`; make the root a pure aggregator.

- [x] Root `../../settings.gradle.kts` includes `:app`; root `build.gradle.kts` holds
  only shared plugin declarations (`apply false`) + `allprojects` config.
- [x] Deleted orphan `../../app` scaffolding (`settings.gradle.kts`, `gradlew`,
      `gradle/wrapper`, `.gitignore`, `.gitattributes`, Java `AppApplication`,
      `app.iml`, duplicate `application*.properties`).
- [x] Moved `src/main/kotlin`, `src/test/kotlin`, `src/main/resources`
      (incl. `firebase-service-account.json`) into `../../app`.
- [x] `../../app/build.gradle.kts` = the real Kotlin Spring Boot app, using the
  version catalog; detekt config resolved via `rootProject.file(...)`.
- [x] Updated `../../.gitignore` firebase path to `app/src/main/resources/...`.

**Exit criteria (all met):** `:app:test`, `:app:spotlessCheck`, `:app:detekt`
green; git recorded moves as renames (history preserved).

**At this point `:app` *is* the monolith — one module, everything green.**

---

## Phase 1 — Shared build logic ✅ DONE

Introduce a `build-logic/` composite build with precompiled **convention
plugins** so future modules inherit config without copy-paste.

- [x] Created `build-logic/` with its own `settings.gradle.kts` (re-declares the
      `libs` catalog from `../gradle/libs.versions.toml`) + `build.gradle.kts`
      (`kotlin-dsl`, plugin-artifact deps, the `LibrariesForLibs` accessor hack).
- [x] `pluginManagement { includeBuild("build-logic") }` in the root
      `settings.gradle.kts`.
- [x] Authored convention plugins:
  - `gymplanner.kotlin-library` — Kotlin JVM, toolchain 22, `-Xjsr305=strict`
    (+ `-Xannotation-default-target=param-property`), Spotless (ktlint), Detekt
    (root config/baseline), base test stack (kotlin-test, mockk, turbine, truth,
    coroutines-test, junit-launcher).
  - `gymplanner.spring-conventions` — applies kotlin-library + kotlin-spring +
    Spring dependency management (imports the Spring Boot BOM) + shared
    Kotlin/Jackson/coroutines runtime.
- [x] Refactored `app/build.gradle.kts` to `apply` the conventions + the Spring
      Boot plugin; only app-specific deps remain (starters, jjwt, firebase,
      springdoc, devtools, flapdoodle).
- [x] Added plugin-artifact entries to the version catalog for the convention
      plugins to consume.

**Gotcha resolved:** Detekt fails if its embedded Kotlin differs from the
version it was built against. Spring's `io.spring.dependency-management`
force-manages Kotlin across *all* configurations, overriding the pin — so the
pin must be re-asserted *after* that plugin is applied. Extracted into
`Project.pinDetektKotlinVersion()` (`build-logic/.../DetektConventions.kt`) and
called from both conventions (last-registered rule wins).

**Exit criteria (all met):** `./gradlew clean test spotlessCheck detekt` green;
`:app:bootJar` produces the runnable fat jar; app config now comes entirely from
convention plugins.

---

## Phase 2 — Extract the leaves (`:core-utils`) ✅ DONE

Move zero-dependency shared code into a `:core-utils` module. Pure de-risking; proves
the convention plugins on a real extraction.

- [x] Created `:core-utils` module. It applies `gymplanner.spring-conventions`
      (not just kotlin-library) because `GlobalExceptionHandler` is a WebFlux
      `@RestControllerAdvice` and `@FutureDate` uses Jakarta Validation; the
      module adds `spring-boot-starter-webflux` + `spring-boot-starter-validation`.
- [x] Moved `common` (GymLocation), `utils` (LocalDateTimeKtx, ExtensionsKt),
      and `validation` (GlobalExceptionHandler, FutureDate/FutureDateValidator)
      into `:core-utils`, keeping identical package names → **zero import churn**
      for the ~19 consumers (they resolve via the new project dependency).
- [x] Fixed a latent bug: `FutureDateValidator.kt` had **no package declaration**
      (default package), so `Booking.kt` used `import FutureDate` — which cannot
      cross a module boundary. Gave it `package …validation` and updated the
      import to `com.ianarbuckle.gymplannerservice.validation.FutureDate`.
- [x] `include(":core-utils")` in settings; `:app` depends on
      `implementation(project(":core-utils"))`.
- [x] Fixed a second latent bug: the `@FutureDate` annotation was missing its
      spec-mandated `groups`/`payload` members, which throws
      `ConstraintDefinitionException` whenever a booking date is validated (no
      test covered it). Restored the members and added
      `FutureDateValidatorTest` in `:core-utils` (past date → violation, future
      date → none) as a regression guard.

**Gotcha resolved (build-wide):** `:core-utils:compileKotlin` failed with
`getPluginClasspaths() is null` from the Kotlin Build Tools API. Root cause:
`io.spring.dependency-management` applies managed versions to *every*
configuration — including `kotlinCompilerPluginClasspath` — which corrupts the
kotlin-spring (all-open) compiler-plugin classpath. **Fix:** dropped that plugin
from `gymplanner.spring-conventions` entirely and switched to Gradle's native
`platform("org.springframework.boot:spring-boot-dependencies:<ver>")` BOM, which
only constrains dependency configurations. Consequences:
  - The Phase 1 `pinDetektKotlinVersion()` re-assertion in spring-conventions is
    no longer needed (no dependency-management to override it); the pin in
    `gymplanner.kotlin-library` now suffices.
  - `developmentOnly` doesn't extend `implementation`, so `:app` adds the
    `platform(...)` BOM to that configuration directly for `spring-boot-devtools`.
  - Added `gradle.properties` with larger daemon heap/metaspace (the default
    384 MiB metaspace was crashing the daemon and triggering the buggy fallback).

**Exit criteria (all met):** `./gradlew clean test spotlessCheck detekt` green;
`:app:bootJar` still produces the runnable fat jar; `:core-utils` depends only on
Spring/Jakarta libs (no dependency on `:app`).

---

## Phase 3 — Extract `:security` ✅ DONE

Move the cross-cutting JWT / filter-chain infrastructure into `:security`. This
is the safe "authentication starting point."

- [x] Created `:security` module (applies `gymplanner.spring-conventions` +
      spring-security + webflux + jjwt). It depends on **no** project — nothing
      it moved uses `:core-utils`, so it's a true leaf (not `:core-utils` as
      originally sketched).
- [x] Moved `SecurityConfig`, `JwtUtils`, `JwtAuthenticationManager` (incl.
      `BearerToken` + `JwtServerAuthenticationConverter`) into a **new package**
      `com.ianarbuckle.gymplannerservice.security` (kept under the app base
      package so `@SpringBootApplication` still component-scans them; avoids
      splitting `authentication.*` across two modules).
- [x] Moved `TokenExpiredException` into `:security` (only `JwtUtils` used it);
      removed it from the auth domain's `Exceptions.kt`.
- [x] **Broke the coupling back into the auth domain with a port:** added
      `SecurityUserLookup` + `AuthenticatedUser` interfaces in `:security`;
      `JWTAuthenticationManager` depends on the port instead of `UserRepository`.
      The app provides `UserSecurityLookup` (a `@Component` adapter backed by
      `UserRepository`, mapping `User.roles` → authority-name strings). This
      inverts the dependency: `:app` → `:security`, never the reverse.
- [x] `SecurityConfig` no longer imports `ERole`; role authorities are local
      `ROLE_MODERATOR`/`ROLE_ADMIN` string constants (must match the domain
      enum's names).
- [x] `:app` depends on `implementation(project(":security"))`;
      `AuthenticationService` updated to import `JwtUtils` from `:security`.
- [x] Tests: `JwtUtilsTests` + `JWTAuthenticationManagerTests` moved to
      `:security` (the latter rewired to mock the `SecurityUserLookup` port
      instead of `UserRepository`/`User`/`ERole`). `SecurityConfigTests` (a
      `@WebFluxTest` bound to app controllers) and `AuthenticationServiceTests`
      stayed in `:app` with updated imports.

**Deviations from the sketch:**
  - `AuthEntryPointJwt` was **not** moved — it's dead code (unreferenced, and a
    servlet `AuthenticationEntryPoint` in a WebFlux app). Left in `:app`'s
    `authentication.data.security` package; delete or reconcile in Phase 4 rather
    than drag servlet-api into the clean `:security` module.
  - `:security` depends on no project (not `:core-utils`).

**Verification note:** the repo has **no full-context `@SpringBootTest`** (slice
tests only, likely to avoid Firebase `@PostConstruct` init), so the port→adapter
wiring isn't exercised at runtime by the suite. It's sound by construction:
single `SecurityUserLookup` impl, both beans under the scanned base package,
`UserRepository` is a Spring Data bean. A context smoke test could close this gap
if desired.

**Exit criteria (all met):** `./gradlew clean test spotlessCheck detekt` green
across all three modules; `:app:bootJar` still produces the runnable fat jar;
`:security` has no dependency on `:app` (no cycle).

---

## Phase 4 — Break cycles, then extract `:authentication`

- [ ] Relocate misplaced `booking.exception.UserNotFoundException` into `:core-utils`.
- [ ] Invert `authentication → userProfile` / `authentication → booking` using
      **ports** (interfaces owned by auth, implemented by consumers) so arrows
      point one way.
- [ ] Extract the auth *domain* as `:authentication` (depends on `:core`,
      `:security`): `AuthenticationController`, `User`, `UserRepository`,
      `AuthenticationService`, auth `domain`/`exception`.

**Exit criteria:** no cycles involving authentication; full suite green.

---

## Phase 5 — Extract remaining features (leaf-first)

Order by fewest dependencies so each extraction stays acyclic:

1. [ ] `exercises`, `faultReporting`, `gymlocations`, `messages` (no feature deps)
2. [ ] `facilityStatus`, `trainers`, `checkin` (depend on `common`/`trainers`)
3. [ ] `fcm`, `fitnessclass` (depend on `authentication`/`fcm`)
4. [ ] `availability`, `booking` — **break the `availability ↔ booking` cycle**
       (ports) when reached.
5. [ ] `userProfile` **last** (most entangled).

Each feature module: applies convention plugins, depends on `:core`/`:security`
(+ specific features via ports), moves its `data`/`exception` subpackages,
keeps tests alongside.

**Exit criteria:** `:app` contains only wiring/config/`main()`; every feature is
its own module; full suite green; no cycles.

---

## Conventions during migration

- One phase (or one module within Phase 5) per PR — keep diffs reviewable.
- After every move: `./gradlew clean test spotlessCheck detekt` must pass.
- Prefer `git mv` / plain move + rename detection to preserve history.
- Break dependency cycles with **ports (interfaces)**, not by merging modules.
- Keep the version catalog (`../../gradle/libs.versions.toml`) as the single source of
  dependency versions.