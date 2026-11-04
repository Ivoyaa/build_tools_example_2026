package demo.server

import demo.core._

final class TalkApi(catalog: TalkCatalog) {
  def list(tag: Option[String]): List[Talk] = catalog.list(tag)
  def find(id: String): Either[ApiError, Talk] = catalog.find(id)
  def create(draft: TalkDraft): Either[ApiError, Talk] = catalog.create(draft)
}
