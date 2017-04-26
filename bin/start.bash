#!/bin/bash

java -Dspring.cloud.config.server.git.uri=file://tmp/asdf -jar configServer/build/libs/configServer-0.1.0.jar