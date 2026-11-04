package demo.core

final case class Talk(
    id: String,
    title: String,
    speaker: String,
    durationMinutes: Int,
    tags: Set[String]
)

final case class TalkDraft(
    title: String,
    speaker: String,
    durationMinutes: Int,
    tags: Set[String]
)

sealed abstract class ApiError(val message: String)
final case class NotFound(id: String) extends ApiError(s"Talk '$id' does not exist")
final case class InvalidTalk(override val message: String) extends ApiError(message)
final case class Conflict(id: String) extends ApiError(s"Talk '$id' already exists")
