#!/bin/bash

set -e
set -x

java -Dspring.cloud.config.failFast=true -jar -Dspring.cloud.config.discovery.enabled=true internal/discovery/build/libs/discovery-*.jar
