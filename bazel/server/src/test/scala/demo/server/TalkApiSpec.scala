package demo.server

import demo.core._

final class TalkApiSpec extends munit.FunSuite {
  test("returns a useful error for an unknown talk") {
    val api = new TalkApi(TalkCatalog.withSampleData())
    assertEquals(api.find("missing"), Left(NotFound("missing")))
  }

  test("creates and then finds a talk") {
    val api = new TalkApi(new TalkCatalog)
    val draft = TalkDraft("Fast builds", "Grace", 30, Set("builds"))
    val created = api.create(draft)
    assertEquals(created.map(_.id), Right("fast-builds"))
    assertEquals(api.find("fast-builds"), created)
  }
}
