# Bazel 8.4.2 + rules_scala 7.4.1

Установите [Bazelisk](https://github.com/bazelbuild/bazelisk): он прочитает `.bazelversion` и скачает нужный Bazel.

```bash
bazelisk test //...
bazelisk test //core:core-test //server:server-test
bazelisk run //server:talks-server
bazelisk query 'kind(scala_.*, //...)'
bazelisk cquery '//server:server'
```

Maven-зависимости и SHA-256 находятся в `maven_install.json`. После изменения координат в `MODULE.bazel` обновите его:

```bash
bazelisk run @maven//:pin
```

`MODULE.bazel` также показывает BOM через `boms = [...]` и версионно-неполную координату `jackson-core`. Повторный `bazelisk test //...` должен показать `(cached) PASSED`; отключить кеш результатов можно флагом `--nocache_test_results`.
