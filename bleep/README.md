# Bleep 1.0.0-M15

Установите Bleep по [официальной инструкции](https://bleep.build/docs/installation/). Даже старый launcher загрузит версию из `bleep.yaml`; сама сборка закреплена на M15.

```bash
bleep projects
bleep compile
bleep test
bleep test core-test
bleep run server
bleep dist server
```

В `bleep.yaml` четыре проекта: `core`, `server`, `core-test`, `server-test`. Общие настройки вынесены в `template-common`; тесты являются отдельными проектами с `isTestProject: true`. Bleep использует BSP server и предоставляет отдельные команды для инспекции build-конфигурации и remote cache.

Полезно для демо:

```bash
bleep build show core
bleep build invalidated
bleep remote-cache --help
```
