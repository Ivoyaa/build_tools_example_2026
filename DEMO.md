# Сценарий живого выступления

Рассчитано примерно на 25–30 минут. Перед докладом один раз выполните тесты во всех проектах: первый запуск скачивает JDK, компиляторы и зависимости, поэтому не должен попасть на сцену.

## 1. Общая задача — 3 минуты

Покажите `core` и `server` в любом проекте. Запустите сервер, например:

```bash
cd sbt
sbt server/run
```

В другом терминале:

```bash
curl http://localhost:8080/health
curl 'http://localhost:8080/api/talks?tag=builds'
curl -i -X POST http://localhost:8080/api/talks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Live demo","speaker":"Ada","durationMinutes":30,"tags":["scala"]}'
curl -i -X POST http://localhost:8080/api/talks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Too long","speaker":"Ada","durationMinutes":500,"tags":[]}'
```

Первый POST возвращает 201, второй — 400. Повтор первого даст 409.

## 2. Scala 3 и common settings — 3 минуты

Откройте `sbt/build.sbt`:

- синтаксис `build.sbt` компилируется Scala 3;
- `scalaVersion := scala3` без `ThisBuild` применяется ко всем строкам матрицы;
- `rootProject.autoAggregate` убирает ручной `.aggregate(...)`;
- встроенный `projectMatrix` создаёт `core2_13`, `core`, `server2_13`, `server`.

В sbt shell:

```text
projects
show core/scalaVersion
show core2_13/scalaVersion
```

## 3. Кеш задач — 4 минуты

В том же shell:

```text
show core/buildSignature
show core/buildSignature
```

В итоговой статистике первый запуск может быть `onsite task`, второй — `disk cache hit`. Затем покажите `server/update` или `test`: кеш работает не только для игрушечной задачи.

Смена значения `version` в `build.sbt` меняет вход `buildSignature` и снова отправляет задачу на выполнение. Не меняйте файл прямо на сцене, если нет запасного времени: auto reload сам перечитает build definition, что тоже является частью демо.

## 4. Инкрементальные тесты — 3 минуты

```text
core/test
core/test
core/testFull
```

Повторный `test` использует сохранённый результат, `testFull` принудительно запускает тесты. Для сравнения в отдельных терминалах:

```bash
cd mill && ./mill 'core[3.8.4].test'
cd bazel && bazelisk test //core:core-test
cd ../pants && pants test core/src/test/scala:tests
cd ../scala-cli && scala-cli test core
```

На повторных вызовах смотрите не только время, но и `(cached)` у Bazel и cache сообщения Pants. Scala CLI повторно запускает тесты, хотя компиляция остаётся инкрементальной.

## 5. Query и cross-build — 3 минуты

В sbt:

```text
core.../test
...@scalaBinaryVersion=3/test
```

Сравните с:

```bash
cd mill && ./mill resolve __.test
cd ../bazel && bazelisk query 'kind(scala_junit_test, //...)'
cd ../pants && pants --changed-since=HEAD test
```

Последняя команда зависит от git working tree и на нетронутом checkout может ничего не выбрать — это ожидаемо.

## 6. Клиент-сервер и run — 2 минуты

Держите открытым sbt shell, а из системного терминала выполните:

```bash
sbt 'show core/buildSignature'
sbt server/run
```

sbtn подключается к существующему серверу без повторной загрузки сборки. `run` выполняется на клиентской стороне и не блокирует sbt server. Для контраста покажите Mill daemon, BSP server Bleep, Bazel server и `pantsd` — внешне эффект похож, но архитектура и границы кешей разные.

## 7. BOM — 2 минуты

В `sbt/build.sbt` покажите:

```scala
("com.fasterxml.jackson" % "jackson-bom" % "2.21.0").pomOnly()
"com.fasterxml.jackson.core" % "jackson-core" % "*"
```

Затем сравните с `boms = [...]` и `maven_install.json` в Bazel. Подчеркните: BOM управляет constraints, lockfile фиксирует resolution.

## 8. Финальное сравнение — 3 минуты

Откройте `COMPARISON.md`. Главный тезис: sbt 2 не просто переносит build definition на Scala 3, а сближает привычную sbt-модель с cached build systems. При этом Bazel и Pants всё ещё строже по герметичности и графу действий, Mill проще программируется, Bleep декларативнее, а Scala CLI выигрывает на маленьких задачах за счёт минимальной конфигурации.
