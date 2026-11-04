# Scala build tools: интерактивное сравнение

Один и тот же сервис **Talks API** собирается шестью инструментами. Это не benchmark: проект нужен для живого сравнения модели сборки, кеширования, тестирования, cross-build и работы из CLI.

## Что собираем

Двухмодульный HTTP-сервер на Scala 2.13/3 и Tapir:

- `core` — модель выступления, валидация, slug-генератор и in-memory каталог;
- `server` — Tapir endpoints, JSON через Circe и JDK HTTP server;
- тесты на MUnit в обоих модулях;
- sbt project matrix и Mill `CrossScalaModule` собирают код на Scala 2.13.18 и 3.8.4.

Исходники намеренно продублированы: каждую директорию можно открыть и запустить независимо.

| Инструмент | Зафиксированная версия | Тесты | Запуск |
|---|---|---|---|
| [sbt 2](sbt/) | 2.0.12 | `sbt test` | `sbt server/run` |
| [Mill](mill/) | 1.3.1 | `./mill __.test` | `./mill 'server[3.8.4].run'` |
| [Bleep](bleep/) | 1.0.0-M15 | `bleep test` | `bleep run server` |
| [Bazel](bazel/) | 8.4.2, rules_scala 7.4.1 | `bazelisk test //...` | `bazelisk run //server:talks-server` |
| [Pants](pants/) | 2.33.0 | `pants test ::` | `pants run server/src/main/scala:talks-server` |
| [Scala CLI](scala-cli/) | 1.18.0 | `scala-cli test core && scala-cli test server core` | `scala-cli run server core` |

Требуется JDK 17+. Mill и Pants скачивают выбранную JDK сами; для sbt и Scala CLI её нужно иметь в `JAVA_HOME`/`PATH`. Mill уже содержит bootstrap-скрипт. Для остальных инструментов установка описана ниже.

## Установка инструментов

### macOS с Homebrew

```bash
# JDK 17 для sbt и Scala CLI
brew install --cask temurin@17

# sbt и Bazelisk
brew install sbt bazelisk

# Coursier нужен для установки Bleep
brew install coursier/formulas/coursier
cs install bleep

# Pants с нативным launcher
brew install pantsbuild/tap/pants

# Scala CLI
brew install VirtusLab/scala-cli/scala-cli
```

Mill устанавливать глобально не нужно: команды запускаются через `./mill` из директории `mill/`. Bleep загрузит версию, указанную в `bleep.yaml`, Bazelisk — Bazel из `.bazelversion`, а Pants — версию из `pants.toml`.

Если JDK уже управляется через Coursier, для текущего терминала можно использовать:

```bash
eval "$(cs java --jvm temurin:17 --env)"
java -version
```

### Другие ОС

- sbt: [официальная установка](https://www.scala-sbt.org/2.x/docs/en/setup.html).
- Mill: не требуется, используйте включённый `mill/mill` (macOS/Linux) или скачайте `mill.bat` по [инструкции](https://mill-build.org/mill/cli/installation-ide.html).
- Bleep: [нативные пакеты и Coursier](https://bleep.build/docs/installation/).
- Bazel: установите [Bazelisk](https://github.com/bazelbuild/bazelisk), не обязательно ставить Bazel отдельно.
- Pants: [нативный launcher](https://www.pantsbuild.org/stable/docs/getting-started/installing-pants); внешний Python не нужен.
- Scala CLI: [пакеты для macOS, Linux и Windows](https://scala-cli.virtuslab.org/install).

### Проверка установки

```bash
java -version
sbt --script-version
bleep --help
bazelisk version
scala-cli version

(cd mill && ./mill --version)
(cd pants && pants --version)
```

После этого переходите в директорию нужного инструмента и используйте команды из таблицы выше.

## HTTP-сценарий

После запуска на порту 8080:

```bash
curl http://localhost:8080/health
curl 'http://localhost:8080/api/talks?tag=builds'
curl http://localhost:8080/api/talks/sbt-2-in-practice

curl -i -X POST http://localhost:8080/api/talks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Build tools live","speaker":"You","durationMinutes":45,"tags":["scala","builds"]}'

curl http://localhost:8080/api/talks/build-tools-live
```

Ожидаются `200 OK` для чтения, `201 Created` для создания, `400` для невалидной длительности, `404` для отсутствующего id и `409` при повторном создании.

## Навигация

- [Сравнение фич sbt 2 с остальными инструментами](COMPARISON.md)
- `DEMO.md` — пошаговый сценарий выступления; хранится только локально и не попадает в Git.
- Локальный README в каждой директории — команды и особенности конкретной сборки.
