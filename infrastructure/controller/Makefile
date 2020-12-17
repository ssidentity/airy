build:
	CGO_ENABLED=0 GOOS=linux go build -o /tmp/controller -a main.go

image: build
	cp /tmp/controller ./controller
	docker build -t ghcr.io/airyhq/infrastructure/controller:beta .
	rm -f ./controller

push: image
	docker push ghcr.io/airyhq/infrastructure/controller:beta
