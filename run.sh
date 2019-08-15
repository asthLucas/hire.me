#!/bin/bash

if [[ $1 = "build" ]]; then
    ./mvnw -q package
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
    docker run -p 8080:8080 -v database:/home --name short_url short_url:latest
    exit 0
fi 


