package demo.core

import java.text.Normalizer
import java.util.Locale

final class TalkCatalog(initial: Seq[Talk] = Seq.empty) {
  private var talks = initial.map(talk => talk.id -> talk).toMap

  def list(tag: Option[String] = None): List[Talk] = synchronized {
    talks.values
      .filter(talk => tag.forall(talk.tags.contains))
      .toList
      .sortBy(_.id)
  }

  def find(id: String): Either[NotFound, Talk] = synchronized {
    talks.get(id).toRight(NotFound(id))
  }

  def create(draft: TalkDraft): Either[ApiError, Talk] = synchronized {
    validate(draft).flatMap { _ =>
      val id = TalkCatalog.slugify(draft.title)
      if (talks.contains(id)) Left(Conflict(id))
      else {
        val talk = Talk(id, draft.title.trim, draft.speaker.trim, draft.durationMinutes, draft.tags)
        talks += id -> talk
        Right(talk)
      }
    }
  }

  private def validate(draft: TalkDraft): Either[InvalidTalk, Unit] =
    if (draft.title.isBlank) Left(InvalidTalk("Title must not be blank"))
    else if (draft.speaker.isBlank) Left(InvalidTalk("Speaker must not be blank"))
    else if (!(5 to 240).contains(draft.durationMinutes))
      Left(InvalidTalk("Duration must be between 5 and 240 minutes"))
    else Right(())
}

object TalkCatalog {
  def withSampleData(): TalkCatalog = new TalkCatalog(
    Seq(
      Talk("sbt-2-in-practice", "sbt 2 in practice", "Ada", 45, Set("scala", "builds")),
      Talk("cache-all-the-things", "Cache all the things", "Grace", 30, Set("builds", "performance"))
    )
  )

  private def slugify(value: String): String =
    Normalizer.normalize(value, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase(Locale.ROOT)
      .replaceAll("[^a-z0-9]+", "-")
      .stripPrefix("-")
      .stripSuffix("-")
}
