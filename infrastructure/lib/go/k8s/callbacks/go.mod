module callbacks

go 1.15

require (
	util v0.0.0
	github.com/sirupsen/logrus v1.7.0
	golang.org/x/time v0.0.0-20201208040808-7e3f01d25324 // indirect
	k8s.io/api v0.20.0
	k8s.io/apimachinery v0.20.0
	k8s.io/client-go v0.20.0
	k8s.io/klog v1.0.0
	k8s.io/utils v0.0.0-20201110183641-67b214c5f920 // indirect
)

replace util => ../util
