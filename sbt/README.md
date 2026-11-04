# sbt 2.0.12

Нужны JDK 17+ и свежий sbt launcher. Версия самой сборки закреплена в `project/build.properties`.

```bash
sbt test                 # инкрементальные тесты, обе строки project matrix
sbt testFull             # принудительно полный запуск
sbt 'core.../test'       # query по префиксу проекта
sbt '...@scalaBinaryVersion=3/test'
sbt 'show core/buildSignature'
sbt 'show core2_13/buildSignature'
sbt server/run
```

`build.sbt` сам написан на Scala 3. Не scoped `scalaVersion` и `organization` применяются ко всем модулям — это новые common settings sbt 2.

`buildSignature` показывает автоматический кеш задач: повторите `show` в одном sbt shell или из двух CLI-вызовов и смотрите на `onsite task` / `disk cache hit` в итоговой статистике. `server/run` использует client-side run через sbtn. BOM-пример — `jackson-bom` и `jackson-core` без версии в `build.sbt`.

Полный сценарий находится в локальном `../DEMO.md` (файл намеренно не хранится в Git).
