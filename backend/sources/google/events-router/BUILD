load("//tools/build:springboot.bzl", "springboot")
load("//tools/build:junit5.bzl", "junit5")
load("//tools/build:container_push.bzl", "container_push")

app_deps = [
    "//backend:base_app",
    "//backend/model/channel:channel",
    "//backend/model/message:message",
    "//backend/model/metadata:metadata",
    "//lib/java/uuid",
    "//lib/java/kafka/schema:source-google-events",
    "//lib/java/spring/kafka/core:spring-kafka-core",
    "//lib/java/spring/kafka/streams:spring-kafka-streams",
]

springboot(
    name = "google-events-router",
    srcs = glob(["src/main/java/**/*.java"]),
    main_class = "co.airy.spring.core.AirySpringBootApplication",
    deps = app_deps,
)

[
    junit5(
        size = "medium",
        file = file,
        resources = glob(["src/test/resources/**/*"]),
        deps = [
            ":app",
            "//backend:base_test",
            "//lib/java/kafka/test:kafka-test",
        ] + app_deps,
    )
    for file in glob(["src/test/java/**/*Test.java"])
]

container_push(
    registry = "ghcr.io/airyhq/sources",
    repository = "google-events-router",
)
