#!/usr/bin/env bash

mkdir microservices
cd microservices

spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--type=gradle-project \
--java-version=1.8 \
--packaging=jar \
--name=game-service \
--package-name=vladimir.microservices.core.game \
--groupId=vladimir.microservices.core.game \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
game-service

spring init \
--boot-version=2.1.0.RELEASE \
--build=gradle \
--type=gradle-project \
--java-version=1.8 \
--packaging=jar \
--name=review-service \
--package-name=vladimir.microservices.core.review \
--groupId=vladimir.microservices.core.review \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
review-service

spring init \
--boot-version=2.1.0.RELEASE \
--build=gradle \
--type=gradle-project \
--java-version=1.8 \
--packaging=jar \
--name=dlc-service \
--package-name=vladimir.microservices.core.dlc \
--groupId=vladimir.microservices.core.dlc \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
dlc-service

spring init \
--boot-version=2.1.0.RELEASE \
--build=gradle \
--type=gradle-project \
--java-version=1.8 \
--packaging=jar \
--name=game-event-service \
--package-name=vladimir.microservices.core.event \
--groupId=vladimir.microservices.core.event \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
game-event-service

spring init \
--boot-version=2.1.0.RELEASE \
--build=gradle \
--type=gradle-project \
--java-version=1.8 \
--packaging=jar \
--name=game-composite-service \
--package-name=vladimir.microservices.composite.game \
--groupId=vladimir.microservices.composite.game \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
game-composite-service

cd ..