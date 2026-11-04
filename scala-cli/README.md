# Scala CLI 1.18.0

Установите [Scala CLI](https://scala-cli.virtuslab.org/install) и JDK 17+. Это намеренно не монорепо-модель: `core` и `server` — наборы входов, а не самостоятельный граф модулей с публикацией артефактов.

```bash
scala-cli test core
scala-cli test server core
scala-cli run server core
scala-cli --power package server core --assembly -o dist/talks-server.jar
java -jar dist/talks-server.jar
```

Scala-версия и библиотеки объявлены директивами `//> using` в `project.scala`; тестовые зависимости отделены через `test.dep`. Bloop даёт инкрементальную компиляцию, но нет аналога sbt task graph, project query или универсального task cache.
