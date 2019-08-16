#!/bin/bash

if [[ $1 = "build" ]]; then
	if [[ $2 = "skip-test" ]]; then
		./mvnw -q package -DskipTests
	else 
		./mvnw -q package
	fi

    docker volume create database
    docker build -t short_url:latest .
    exit 0
fi
if [[ $1 = "clean" ]]; then
    docker rm short_url
    docker rmi short_url:latest
    exit 0
fi
if [ -n $1 ]; then
    docker run -p 8080:8080 -v database:/tmp --name short_url short_url:latest
    exit 0
fi 


