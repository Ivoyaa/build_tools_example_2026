# Mill 1.3.1

Bootstrap-скрипт `./mill` уже включён. Он скачает Mill и JDK 17, указанную в заголовке `build.mill`.

```bash
./mill __.compile
./mill __.test
./mill resolve __.test
./mill inspect 'core[3.8.4].compile'
./mill show 'core[3.8.4].compile'
./mill 'server[3.8.4].run'
./mill 'server[3.8.4].assembly'
```

`CrossScalaModule` создаёт варианты Scala 2.13.18 и 3.8.4 для обоих модулей; `moduleDeps = Seq(core())` связывает соответствующие строки матрицы. Используется родной layout Mill: `src` и `test/src`. Wildcards `_` и `__` выбирают несколько модулей, а `out/<module>/<scala-version>/<task>` делает кеш удобным для показа. `run` — команда, поэтому выполняется каждый раз; `compile` и `test` кешируются по входам.
