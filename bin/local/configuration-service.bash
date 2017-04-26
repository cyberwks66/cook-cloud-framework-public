#!/bin/bash

set -e
set -x

COOK_DEV_CONFIG="${HOME}/.cook/configuration-service/git/"

if [ ! -d "${COOK_DEV_CONFIG}" ] ; then
    mkdir -p "${COOK_DEV_CONFIG}"
    pushd "${COOK_DEV_CONFIG}"
    git init -q
    echo "This is the placeholder for your local dev configuration" > README
    git add README
    git commit -am "Initial commit"
    popd
fi

java -Dspring.cloud.config.server.git.uri=file://${COOK_DEV_CONFIG} -jar internal/configuration/build/libs/configuration-*.jar
