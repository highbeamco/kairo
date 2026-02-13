# bom (Bill of Materials)

Aligns versions of all Kairo modules so consumers only need to declare the BOM once. Contains no code -- only Gradle dependency constraints.

## Key files
- `build.gradle.kts` -- auto-generates constraints for all publishable subprojects

## Patterns and conventions
- Consumers add `implementation(platform("software.airborne.kairo:bom:VERSION"))` to align Kairo module versions
- The BOM iterates over all subprojects with `maven-publish` plugin and adds `api` constraints for each

## Related modules
- `bom-full` -- extends this BOM with version alignment for external dependencies
