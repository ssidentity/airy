load("@io_bazel_rules_go//go:def.bzl", "go_library")

go_library(
    name = "config",
    srcs = ["config.go"],
    importmap = "infrastructure/cmd/config",
    importpath = "cli/cmd/config",
    visibility = ["//visibility:public"],
    deps = ["@com_github_spf13_cobra//:cobra"],
)
