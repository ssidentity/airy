# gazelle:prefix redis-worker

load("@io_bazel_rules_go//go:def.bzl", "go_binary", "go_library")
load("@io_bazel_rules_docker//go:image.bzl", "go_image")
load("//tools/build:container_push.bzl", "container_push")

go_binary(
    name = "redis-worker_binary",
    embed = [":redis-worker_lib"],
    visibility = ["//visibility:public"],
)

go_image(
    name = "image",
    embed = [":redis-worker_lib"],
)

container_push(
    registry = "ghcr.io/airyhq/webhook",
    repository = "consumer",
)

go_library(
    name = "redis-worker_lib",
    srcs = ["main.go"],
    importpath = "redis-worker",
    visibility = ["//visibility:private"],
    deps = ["//backend/webhook/redis-worker/pkg/scheduler"],
)
