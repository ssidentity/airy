# gazelle:prefix callbacks
# gazelle:importmap_prefix infrastructure/lib/go/k8s
load("@io_bazel_rules_go//go:def.bzl", "go_library")

go_library(
    name = "callbacks",
    srcs = ["callbacks.go"],
    importmap = "infrastructure/lib/go/k8s",
    importpath = "callbacks",
    visibility = ["//visibility:public"],
    deps = [
        "//infrastructure/lib/go/k8s/util",
        "@io_k8s_api//apps/v1:apps",
        "@io_k8s_api//core/v1:core",
        "@io_k8s_apimachinery//pkg/apis/meta/v1:meta",
        "@io_k8s_client_go//kubernetes",
        "@io_k8s_klog//:klog",
    ],
)
