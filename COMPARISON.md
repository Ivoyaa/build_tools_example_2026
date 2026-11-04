# Что из sbt 2 есть в других инструментах

Сравниваются версии из этого репозитория: sbt 2.0.12, Mill 1.3.1, Bleep 1.0.0-M15, Bazel 8.4.2 + rules_scala 7.4.1, Pants 2.33.0 и Scala CLI 1.18.0.

**Да** означает близкий встроенный механизм. **Аналог** означает, что задача решается, но модель заметно другая. **Нет** означает отсутствие самостоятельной возможности на уровне build tool; плагин или ручная интеграция всё равно могут быть возможны.

| Возможность sbt 2 | Mill | Bleep | Bazel | Pants | Scala CLI |
|---|---|---|---|---|---|
| Scala 3 в мета-сборке | Да: `build.mill` на Scala 3 | Нет: YAML | Нет: Starlark | Нет: TOML, плагины на Python | Нет: директивы |
| Общие настройки без `ThisBuild` | Аналог: базовый trait | Аналог: templates | Аналог: Starlark macros | Аналог: глобальные options и `__defaults__` | Частично: общие входные файлы с директивами |
| Клиент-сервер по умолчанию | Аналог: Mill daemon | Аналог: shared BSP server | Да: Bazel server | Аналог: `pantsd` | Частично: Bloop для компиляции |
| Автоматический локальный task cache | Да: tasks в `out/` | Аналог: BSP/incremental build | Да: action cache | Да: process cache | Нет общего task cache |
| Bazel-compatible remote cache | Нет встроенного аналога | Да: `bleep remote-cache` | Да | Да, свой remote API | Нет |
| Инкрементальный `test`, кеш результата | Да для cached test task | Частично: прежде всего incremental compile | Да: test results cache | Да: process cache | Нет: тесты запускаются снова |
| Query нескольких проектов | Да: `_`, `__`, braces | Частично: выбор проектов CLI | Да: target patterns и `query` | Да: address specs, `--changed-since` | Нет: выбираются входные пути |
| Встроенная project matrix | Да: `Cross` / `CrossScalaModule` | Аналог: шаблоны и отдельные проекты | Аналог: targets и toolchains | Аналог: `parametrize` и resolves | Нет полноценной матрицы проектов |
| Maven BOM как version constraints | Нет отдельного DSL; доступен Coursier API | Нет отдельного поля в `bleep.yaml` | Да: `rules_jvm_external.boms` | Нет прямого аналога; фиксирует lockfile | Нет отдельного аналога в этом примере |
| `rootProject.autoAggregate` | Аналог: `__` и явный граф модулей | Аналог: команда над всеми проектами | Аналог: `//...` | Аналог: `::` | Нет |

## Важные различия, которые стоит проговорить

### Кеш задачи — не то же самое, что инкрементальный компилятор

sbt 1 умел инкрементально компилировать через Zinc, но повторный `test` обычно запускал тесты заново. sbt 2 делает задачи кешируемыми по объявленным входам, включая результат `test`. Mill, Bazel и Pants давно строят работу вокруг похожей идеи, хотя гранулярность различается: Mill кеширует task, Bazel — action/test, Pants — процесс с digest входов.

У sbt 2 локальный дисковый кеш машинно-общий и задачи автоматически готовы к remote cache. Для файловых side effects нужно объявить выход через `Def.declareOutput`; просто прочитать transient `sources` внутри собственной задачи недостаточно для корректного cache key.

### Query — это не только wildcard

- sbt 2: `core.../test` выбирает по префиксу, `...@scalaBinaryVersion=3/test` — по значению setting.
- Mill: `__.test` решает структурный wildcard, но не запрос по произвольному setting.
- Bazel: `//...` выбирает пакеты, а отдельный `query` умеет граф и атрибуты targets.
- Pants: `::`, путь к файлу и `--changed-since` тесно связаны с монорепо-workflow.
- Bleep в первую очередь оперирует именами проектов из декларативной модели.

### Cross-build имеет разную цену конфигурации

В sbt 2 `projectMatrix` встроен и создаёт независимые project axes; в примере одновременно есть Scala 2.13.18 и 3.8.4. Mill предоставляет собственную типизированную `Cross`-модель. Pants может параметризовать targets разными resolves. Bazel обычно выражает вариации через отдельные targets, toolchains и transitions — это мощнее как общая модель, но менее специализировано для Scala cross-build.

### BOM и lockfile отвечают на разные вопросы

BOM задаёт согласованные version constraints, а lockfile фиксирует конкретный результат resolution. sbt 2 и Bazel/rules_jvm_external показывают BOM прямо в конфигурации. Pants, Bazel и Bleep также используют фиксацию/инспекцию зависимостей, но не стоит называть lockfile заменой BOM в публикуемой библиотеке.

### Scala CLI полезен именно как другая крайность

Scala CLI быстро запускает и тестирует набор исходников без тяжёлой build definition. Это отличный developer experience для scripts и небольших приложений, но не прямой конкурент sbt 2 в настраиваемом task graph, remote build cache или большом монорепо.

## Первичные источники

- [sbt 2.0 changes](https://www.scala-sbt.org/2.x/docs/en/changes/sbt-2.0-change-summary.html)
- [sbt caching](https://www.scala-sbt.org/2.x/docs/en/concepts/caching.html)
- [Mill: Building Scala](https://mill-build.org/mill/scalalib/intro.html)
- [Bleep CLI reference](https://bleep.build/docs/reference/cli/build/)
- [rules_scala](https://github.com/bazel-contrib/rules_scala)
- [Pants: Java and Scala](https://www.pantsbuild.org/stable/docs/jvm/java-and-scala)
