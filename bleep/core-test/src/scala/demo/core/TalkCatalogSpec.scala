package demo.core

final class TalkCatalogSpec extends munit.FunSuite {
  test("creates a talk with a stable slug") {
    val catalog = new TalkCatalog
    val result = catalog.create(TalkDraft("Hello, sbt 2!", "Ada", 45, Set("scala")))
    assertEquals(result.map(_.id), Right("hello-sbt-2"))
  }

  test("rejects invalid durations") {
    val catalog = new TalkCatalog
    val result = catalog.create(TalkDraft("Too long", "Ada", 241, Set.empty))
    assertEquals(result, Left(InvalidTalk("Duration must be between 5 and 240 minutes")))
  }

  test("filters talks by tag") {
    val catalog = TalkCatalog.withSampleData()
    assertEquals(catalog.list(Some("performance")).map(_.id), List("cache-all-the-things"))
  }
}
