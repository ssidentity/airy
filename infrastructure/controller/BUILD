# gazelle:prefix controller
# gazelle:importmap_prefix infrastructure
load("@io_bazel_rules_go//go:def.bzl", "go_binary", "go_library")

go_library(
    name = "controller_lib",
    srcs = ["main.go"],
    importmap = "infrastructure",
    importpath = "controller",
    visibility = ["//visibility:private"],
    deps = [
        "//infrastructure/controller/pkg/configmap-controller",
        "@io_k8s_client_go//kubernetes",
        "@io_k8s_client_go//tools/clientcmd",
        "@io_k8s_klog//:klog",
    ],
)

go_binary(
    name = "controller",
    out = "controller",
    embed = [":controller_lib"],
    visibility = ["//visibility:public"],
)
