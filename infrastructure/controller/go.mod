module controller

go 1.15

require (
	callbacks v0.0.0
	handler v0.0.0
	util v0.0.0
	github.com/googleapis/gnostic v0.4.1
	github.com/imdario/mergo v0.3.11 // indirect
	golang.org/x/time v0.0.0-20201208040808-7e3f01d25324 // indirect
	k8s.io/api v0.20.0
	k8s.io/apimachinery v0.20.0
	k8s.io/client-go v0.20.0
	k8s.io/klog v1.0.0
	k8s.io/utils v0.0.0-20201110183641-67b214c5f920 // indirect
	sigs.k8s.io/structured-merge-diff/v3 v3.0.0 // indirect
)

replace callbacks => ../lib/go/k8s/callbacks
replace handler => ../lib/go/k8s/handler
replace util => ../lib/go/k8s/util

