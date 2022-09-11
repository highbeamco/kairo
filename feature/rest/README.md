# REST Feature

Adding `RestFeature` to a Server allows for HTTP endpoints to be bound.
This is necessary for all Servers, even if the Server is not inherently RESTful
(such as Servers that handle asynchronous event processing)
because a health check GET endpoint is necessary.

## Usage

To expose a REST endpoint on the Server,
first implement `RestEndpoint` to define the endpoints' identities.
This goes in the interface module of a Feature.

```kotlin
public object CelebrityApi {
  public object List : RestEndpoint() {
    override val method: HttpMethod = HttpMethod.Get
    override val path: String = "/celebrities"
  }

  public data class Get(val celebrityGuid: UUID) : RestEndpoint() {
    override val method: HttpMethod = HttpMethod.Get
    override val path: String = "/celebrities/$celebrityGuid"
  }
}
```

Then implement `RestEndpointHandler` to define how the Server should handle requests.
This goes in the implementation module of the same Feature.

```kotlin
import CelebrityApi as Api
import CelebrityRep as Rep

public class ListCelebrities
@Inject constructor() : RestEndpointHandler<Api.List, List<Rep>>(Api.List::class) {
  override suspend fun handle(endpoint: Api.List): List<Rep> {
    // In real usage, this list would probably come from a database.
    return listOf(
      Rep(name = "Johnny Depp", age = 59),
      Rep(name = "Morgan Freeman", age = 85),
      Rep(name = "Taylor Swift", age = 32),
      Rep(name = "Tom Cruise", age = 60),
    )
  }
}

public class GetCelebrity
@Inject constructor() : RestEndpointHandler<Api.Get, Rep?>(Api.Get::class) {
  override suspend fun handle(endpoint: Api.Get): Rep? {
    // In real usage, this value would probably come from a database.
    return Rep(name = "Johnny Depp", age = 59)
  }
}
```

Now, bind this endpoint handlers in the respective Feature.

```kotlin
bindRestEndpoint(ListCelebrities::class)
bindRestEndpoint(GetCelebrity::class)
```

## Implementation notes and limitations

[Ktor](https://ktor.io/) is used as the underlying server implementation.

In order to tell Ktor what endpoint template to use
(what method, parameterized path, and other endpoint metadata to look for in request mapping),
an instance of the `RestEndpoint` implementation (`CelebrityApi` in the example above)
is created during server startup, using randomized parameters.
**One important limitation of this approach is that
`RestEndpoint` implementations must plainly represent endpoints**.
They must not apply transformations to parameters,
nor implement `method` or `path` in any non-deterministic way.