# gazelle:prefix util
# gazelle:importmap_prefix infrastructure/lib/go/k8s
# gazelle:prefix k8s.io/kubernetes
# gazelle:proto disable_global

load("@io_bazel_rules_go//go:def.bzl", "go_library")

go_library(
    name = "util",
    srcs = ["util.go"],
    importmap = "infrastructure/lib/go/k8s",
    importpath = "util",
    visibility = ["//visibility:public"],
    deps = [
        "@io_k8s_api//core/v1:core",
        "@io_k8s_apimachinery//pkg/apis/meta/v1:meta",
        "@io_k8s_klog//:klog",
    ],
)
