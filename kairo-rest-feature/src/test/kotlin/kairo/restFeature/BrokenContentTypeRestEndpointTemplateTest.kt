package kairo.restFeature

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import org.junit.jupiter.api.Test

/**
 * This test uses [BrokenContentTypeLibraryBookApi]
 * to test cases where the [RestEndpoint.ContentType] annotation
 * is used in unexpected or unsupported ways.
 */
internal class BrokenContentTypeRestEndpointTemplateTest {
  @Test
  fun contentTypePresentOnGet() {
    shouldThrow<IllegalArgumentException> {
      RestEndpointTemplate.parse(BrokenContentTypeLibraryBookApi.ContentTypePresentOnGet::class)
    }.shouldHaveMessage(
      "REST endpoint kairo.restFeature.BrokenContentTypeLibraryBookApi.ContentTypePresentOnGet" +
        " may not have @ContentType since it does not have a body.",
    )
  }

  @Test
  fun contentTypeNotPresentOnPost() {
    shouldThrow<IllegalArgumentException> {
      RestEndpointTemplate.parse(BrokenContentTypeLibraryBookApi.ContentTypeNotPresentOnPost::class)
    }.shouldHaveMessage(
      "REST endpoint kairo.restFeature.BrokenContentTypeLibraryBookApi.ContentTypeNotPresentOnPost" +
        " requires @ContentType since it has a body.",
    )
  }

  /**
   * This is actually valid; an empty string means "Any" content type.
   */
  @Test
  fun emptyContentType() {
    RestEndpointTemplate.parse(BrokenContentTypeLibraryBookApi.EmptyContentType::class)
      .shouldBe(
        RestEndpointTemplate(
          method = HttpMethod.Post,
          path = RestEndpointPath.of(
            RestEndpointPath.Component.Constant("library"),
            RestEndpointPath.Component.Constant("library-books"),
          ),
          query = RestEndpointQuery.of(),
          contentType = ContentType.Any,
          accept = ContentType.Application.Json,
        ),
      )
  }

  @Test
  fun malformedContentType() {
    shouldThrow<IllegalArgumentException> {
      RestEndpointTemplate.parse(BrokenContentTypeLibraryBookApi.MalformedContentType::class)
    }.shouldHaveMessage(
      "REST endpoint kairo.restFeature.BrokenContentTypeLibraryBookApi.MalformedContentType" +
        " content type is invalid. Bad Content-Type format: application.",
    )
  }
}