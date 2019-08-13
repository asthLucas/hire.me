#!/bin/bash

./mvnw package -DskipTests
docker build -t short_url:1.0 .
docker run -p 8080:8080 --name short_url short_url:1.0
