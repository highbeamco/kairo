package io.limberapp.api.post

import io.ktor.http.HttpMethod
import io.limberapp.rep.post.PostRep
import io.limberapp.restInterface.Endpoint

internal object PostApi {
  internal data class Post(
      val rep: PostRep.Creation,
  ) : Endpoint(HttpMethod.Post, "/posts", body = rep)

  internal data class Get(
      val postId: Int,
  ) : Endpoint(HttpMethod.Get, "/posts/$postId")

  internal object Conflict : Endpoint(HttpMethod.Get, "/conflict")
}