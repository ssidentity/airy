package handler

import (
	"util"

	v1 "k8s.io/api/core/v1"
)

// GetConfigmapConfig provides utility config for configmap
func GetConfigmapConfig(configmap *v1.ConfigMap) Config {
	return Config{
		Namespace: configmap.Namespace,
		Name:      configmap.Name,
		SHAValue:  util.GetSHAfromConfigmap(configmap),
		Type:      "CONFIGMAP",
	}
}
