# gazelle:prefix handler
# gazelle:importmap_prefix infrastructure/lib/go/k8s
load("@io_bazel_rules_go//go:def.bzl", "go_library")

go_library(
    name = "handler",
    srcs = [
        "config.go",
        "configmaps.go",
        "containers.go",
        "deployments.go",
    ],
    importmap = "infrastructure/lib/go/k8s",
    importpath = "handler",
    visibility = ["//visibility:public"],
    deps = [
        "//infrastructure/lib/go/k8s/callbacks",
        "//infrastructure/lib/go/k8s/util",
        "@io_k8s_api//core/v1:core",
        "@io_k8s_apimachinery//pkg/apis/meta/v1:meta",
        "@io_k8s_client_go//kubernetes",
        "@io_k8s_client_go//util/retry",
        "@io_k8s_klog//:klog",
    ],
)
