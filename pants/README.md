# Pants 2.33.0

Установите официальный нативный launcher по [инструкции Pants](https://www.pantsbuild.org/stable/docs/getting-started/installing-pants). Внешний Python не нужен. Версия сборки находится в `pants.toml`.

```bash
pants generate-lockfiles   # нужно только после изменения зависимостей
pants list ::
pants check ::
pants test ::
pants test core/src/test/scala:tests
pants run server/src/main/scala:talks-server
pants package server/src/main/scala:talks-server
```

`scala_artifact` сам выбирает суффикс Scala-версии. Зависимости между `core` и `server` выводятся из imports; внешние зависимости сервиса здесь указаны явно для наглядности. Lockfile — `3rdparty/jvm/default.lock`.

Повторный `pants test ::` использует process cache. Полезно сравнить `pants test ::` с `pants --no-local-cache test ::`, а также посмотреть `pants peek server/src/main/scala:server`.
