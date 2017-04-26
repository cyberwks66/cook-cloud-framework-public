#!/bin/bash
set -e
set -x

VENDOR="$(pwd)/src/vendor/"
TMP_VENDOR="/tmp/vendor/"
GOPATH="${TMP_VENDOR}":$(pwd):${GOPATH}

go get -u github.com/docker/docker/client
#go get -u github.com/getlantern/systray
#go get -u github.com/shurcooL/trayhost
# go get -u ./...

rm -rf "${VENDOR}"
mkdir -p "${VENDOR}"
mv "${TMP_VENDOR}"/src/* "${VENDOR}"
rm -rf "${TMP_VENDOR}"
