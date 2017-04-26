#!/bin/bash
GOPATH=$(pwd):${GOPATH}

echo $GOPATH
go build src/cmd/cloudctl.go
