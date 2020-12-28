package main

import (
	cm "controller/pkg/configmap-controller"
	"flag"

	"k8s.io/klog"

	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
)

func main() {
	var kubeconfig string
	var master string

	// Check if kubernetes configuration is provided, otherwise use serviceAccount
	flag.StringVar(&kubeconfig, "kubeconfig", "", "absolute path to the kubeconfig file")
	flag.StringVar(&master, "master", "", "master url")
	flag.Parse()

	// Create connection
	config, err := clientcmd.BuildConfigFromFlags(master, kubeconfig)
	if err != nil {
		klog.Fatal(err)
	}

	// Create clientset client
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		klog.Fatal(err)
	}

	// Create configMap controller
	configMapController := cm.ConfigMapController(clientset)
	stop := make(chan struct{})
	defer close(stop)
	go configMapController.Run(1, stop)

	// Wait forever
	select {}
}