# `kairo-config`

Home of `ConfigLoader`, which loads configs from YAML files,
with support for config extension and application.

- **Config extension:** Configs can extend other configs
  by specifying `extends: other-config-name` as a top-level YAML property.
- **Config application:** Configs can apply other configs
  by specifying `apply: [other-config-name-0, other-config-name-1]` as a top-level YAML property

If no config name is provided to `ConfigLoader`,
it will use the `KAIRO_CONFIG` envirnoment variable to identify the config name.

## Usage

### Step 1: Include the dependency

```kotlin
// build.gradle.kts

dependencies {
  api("kairo:kairo-config:0.4.0")
}
```

### Step 2: Try reading a basic config

Your YAML file **must** be in `config/*`.

```kotlin
// src/main/kotlin/yourPackage/server/monolith/MonolithServerConfig.kt

data class MonolithServerConfig(
  val message: String,
)
```

```yaml
# src/main/resources/config/basic-config.yaml

message: "Hello, World!"
```

```kotlin
// src/main/kotlin/yourPackage/server/monolith/MonolithServer.kt

ConfigLoader.load<MonolithServerConfig>("basic-config")
```

### Step 3: Try reading a complex config

This is a "complex" config in the sense that it utilizes both _config extension_ and _config application_.
The large number of config properties is needed to demonstrate by example how these features work.

```kotlin
data class MonolithServerConfig(
  val message: String,
  val name: String,
  val port: Int,
  val height: Sizes,
  val width: Sizes,
  val depth: Sizes,
) {
  data class Sizes(
    val min: Int,
    val max: Int,
    val average: Int,
  )
}
```

```yaml
# src/main/resources/config/base-config.yaml

extraRootProperty: "This breaks the config."
name: "Base config"
port: 3000
height: { min: 2, max: 9, other: 9 }
width: { min: 3, max: 19, average: 15 }
```

```yaml
# src/main/resources/config/applied-config-0.yaml

extraRootProperty: { remove: {} }
message: "Applied config 0"
name: { remove: {} }
height:
  merge: { max: 10, average: 8, other: { remove: {} } }
depth:
  replace: { min: 6, max: 30, average: 24 }
```

```yaml
# src/main/resources/config/config-with-extension-and-application.yaml

extends: base-config

apply:
  - applied-config-0

message: "Hello, World!"
name: "My config"
port: 8080
width:
  replace: { min: 4, max: 20, average: 16 }

```

```kotlin
// src/main/kotlin/yourPackage/server/monolith/MonolithServer.kt

ConfigLoader.load<MonolithServerConfig>("config-with-extension-and-application")
```