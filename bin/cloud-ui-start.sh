#!/bin/bash
if [ "$(docker ps -q -f name=node-js-server)" ]; then
    if [ "$(docker ps -aq -f status=exited -f name=node-js-server)" ]; then
        docker rm node-js-server
    else
	docker stop node-js-server
	docker rm "$(echo $(docker ps -aqf name=node-js-server))"
    fi
    docker build -t node-js-server ./cloud-ui/node-js-server
    docker run -d --name node-js-server -p 4500:4500 node-js-server
else
    docker build -t node-js-server ./cloud-ui/node-js-server
    docker run -d --name node-js-server -p 4500:4500 node-js-server
fi

if [ "$(docker ps -q -f name=cloud-ui)" ]; then
    if [ "$(docker ps -aq -f status=exited -f name=cloud-ui)" ]; then
        docker rm cloud-ui
    else
	docker stop cloud-ui
	docker rm "$(echo $(docker ps -aqf name=cloud-ui))"
    fi
    docker build -t cloud-ui ./cloud-ui/stat-interface
    docker run -d --name cloud-ui -p 4200:4200 cloud-ui
else
    docker build -t cloud-ui ./cloud-ui/stat-interface
    docker run -d --name cloud-ui -p 4200:4200 cloud-ui
fi
