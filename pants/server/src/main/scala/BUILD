scala_sources(
    name="server",
    sources=["**/*.scala"],
    dependencies=[
        "//:tapir-core",
        "//:tapir-json-circe",
        "//:tapir-jdkhttp-server",
    ],
)

deploy_jar(
    name="talks-server",
    main="demo.server.Main",
    dependencies=[":server"],
)
