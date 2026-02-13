# GCP Secret Supplier

`kairo-gcp-secret-supplier` is a lightweight wrapper around
[Google Secret Manager](https://cloud.google.com/security/products/secret-manager),
making access through Kotlin **idiomatic and coroutine-friendly**.
No more blocking calls on event loops!

```kotlin
gcpSecretSupplier["projects/012345678900/secrets/example/versions/1"]
```

## Installation

Install `kairo-gcp-secret-supplier`.
You can also install `kairo-gcp-secret-supplier-testing` for testing.

```kotlin
// build.gradle.kts

dependencies {
  implementation("com.highbeam.kairo:kairo-gcp-secret-supplier")
  testImplementation("com.highbeam.kairo:kairo-gcp-secret-supplier-testing")
}
```

## Usage

Use `DefaultGcpSecretSupplier` in production.
It uses [Application Default Credentials](https://cloud.google.com/docs/authentication/provide-credentials-adc)
to authenticate with Google Cloud.

```kotlin
val gcpSecretSupplier: GcpSecretSupplier = DefaultGcpSecretSupplier()
val value = gcpSecretSupplier["projects/012345678900/secrets/example/versions/1"]
```

The result is a `ProtectedString` containing the secret value,
or `null` if the secret does not exist.
The secret ID must be a fully-qualified resource name
including the project, secret name, and version.

## Testing

Use alternative implementations to simplify testing.

- `NoopGcpSecretSupplier`: Always throws `NotImplementedError`.
- `FakeGcpSecretSupplier`: Allows you to set GCP secret values manually.
- Custom implementations: Implement `GcpSecretSupplier`.

## Logging config

We recommend excluding logs below the `INFO` level for this library.

```xml
<Logger name="kairo.gcpSecretSupplier" level="info"/>
```
