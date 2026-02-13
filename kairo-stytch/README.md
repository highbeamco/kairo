# Stytch

Interface for [Stytch](https://stytch.com/),
letting you easily manage identity.

## Installation

Install `kairo-stytch-feature`.
You don't need to install the Stytch SDK separately —
it's included by default.

```kotlin
// build.gradle.kts

dependencies {
  implementation("com.highbeam.kairo:kairo-stytch-feature")
}
```

## Usage

First, add the Feature to your Server.

```kotlin
val features = listOf(
  StytchFeature(config.stytch),
)
```

We recommend using [kairo-config](../kairo-config/README.md) to configure the Feature.

```hocon
stytch {
  projectId = "project-live-00000000-abcd-1234-wxyz-000000000000"
  secret = ${STYTCH_SECRET}
}
```

Now you can interact with Stytch in your code.

```kotlin
val stytch: Stytch // Inject this.

stytch.users.create(...)
```

The `Stytch` class exposes lazy properties for each Stytch API surface:
`users`, `sessions`, `passwords`, `magicLinks`, `oauth`, `otps`, `totps`,
`m2m`, `rbac`, and more.
Each property is only initialized when first accessed.

### Handling results

Stytch SDK methods return `StytchResult<T>`.
Use the `get()` extension to unwrap the result,
returning the value on success or throwing on error.

```kotlin
val response = stytch.users.create(CreateRequest(...)).get()
```

Errors throw `StytchException`, which has two variants:

- `StytchException.Response`: The API returned an error response.
- `StytchException.Critical`: A network or serialization failure occurred.
