package demo.server

import demo.core._
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe._

object TalkEndpoints {
  implicit private val talkSchema: Schema[Talk] = Schema.any
  implicit private val draftSchema: Schema[TalkDraft] = Schema.any
  implicit private val notFoundSchema: Schema[NotFound] = Schema.any
  implicit private val invalidSchema: Schema[InvalidTalk] = Schema.any
  implicit private val conflictSchema: Schema[Conflict] = Schema.any
  private val apiError = oneOf[ApiError](
    oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[NotFound])),
    oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[InvalidTalk])),
    oneOfVariant(statusCode(StatusCode.Conflict).and(jsonBody[Conflict]))
  )

  val health: PublicEndpoint[Unit, Unit, String, Any] =
    endpoint.get.in("health").out(stringBody)

  val list: PublicEndpoint[Option[String], ApiError, List[Talk], Any] =
    endpoint.get
      .in("api" / "talks")
      .in(query[Option[String]]("tag"))
      .out(jsonBody[List[Talk]])
      .errorOut(apiError)

  val find: PublicEndpoint[String, ApiError, Talk, Any] =
    endpoint.get
      .in("api" / "talks" / path[String]("talkId"))
      .out(jsonBody[Talk])
      .errorOut(apiError)

  val create: PublicEndpoint[TalkDraft, ApiError, Talk, Any] =
    endpoint.post
      .in("api" / "talks")
      .in(jsonBody[TalkDraft])
      .out(statusCode(StatusCode.Created).and(jsonBody[Talk]))
      .errorOut(apiError)
}
