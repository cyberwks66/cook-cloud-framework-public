#!/bin/bash
echo "testing CSCN Framework"

function setup()
{
	_HOST=$1
	MAX_TRIES=3
	if [[ -z "$_HOST" ]];then
		echo "Where do you want to test?"
		echo "TEST, TEST2, LOCAL"
		while [[ -z "$_HOST" ]];do
			read -t 3 _HOST
			((TRIES++))
			if [[ "$TRIES" == "$MAX_TRIES" ]];then
				_HOST="localhost"
				BANNER_MSG="Using Default "
				return
			fi
			sleep 2
		done
	fi
	if [[ "$_HOST" == "TEST" ]];then
		_HOST="test.internal.cooksys.com"
	fi
	if [[ "$_HOST" == "TEST2" ]];then
		_HOST="test2.internal.cooksys.com"
	fi
	if [[ "$_HOST" == "LOCAL" ]];then
		_HOST="localhost"
	fi
}

function testConfigDefault()
{
while [ true ];
do
	RESP=$(curl http://${_HOST}:8888/application/default)
	RC=$?
	echo ""
	if [[ "$RC" == "0" ]];then
		echo "Discovery status (default) is $RC, all is good!"
		return
	fi
	echo "still waiting on Discovery ..."
	sleep 10
done
}

function testConfigServer()
{
while [ true ];
do
	curl http://${_HOST}:8888/config-server/default
	RC=$?
	echo ""
	echo "Discovery status for config-server is $RC"
	if [[ "$RC" == "0" ]];then
		return
	fi
	sleep 10
done
}

function testDiscovery()
{
while [ true ];
do
	curl http://${_HOST}:8761/apps
	RC=$?
	echo ""
	echo "Status for discovery is $RC"
	if [[ "$RC" == "0" ]];then
		return
	fi
	sleep 10
done
}

function testRouter()
{
while [ true ];
do
	curl http://${_HOST}:8764/routes
	RC=$?
	echo ""
	echo "Status for routes is $RC"
	if [[ "$RC" == "0" ]];then
		return
	fi
	sleep 10
done
}

function testHelloWorld()
{
while [ true ];
do
	curl http://${_HOST}:8764/hello-world/hello
	RC=$?
	echo ""
	echo "Status for hello-world is $RC"
	if [[ "$RC" == "0" ]];then
		return
	fi
	sleep 10
done
}

function testMetrics()
{
while [ true ];
do
	curl http://${_HOST}:8764/metrics
	RC=$?
	echo ""
	echo "Status for zuul metrics is $RC"
	if [[ "$RC" == "0" ]];then
		return
	fi
	sleep 10
done
}

function launchFWComponentsAsJars()
{
	echo "launching FW Components as Jars"
	java -jar ./configServer/build/libs/configServer-0.1.0.jar >/dev/null 2>&1 &
	configPID=$!
	echo "config PID is $configPID"
	sleep 5
	java -jar ./framework/discovery/build/libs/discovery-0.1.0.jar >/dev/null 2>&1 &
	discoveryPID=$!
	echo "discovery PID is $discoveryPID"
	sleep 5
	java -jar ./framework/router/build/libs/router-0.1.0.jar >/dev/null 2>&1 &
	routerPID=$!
	echo "router PID is $routerPID"
	sleep 5
	java -jar ./hello-world/build/libs/hello-world-0.1.0.jar >/dev/null 2>&1 &
	helloWorldPID=$!
	echo "helloWorld PID is $helloWorldPID"
	sleep 5
}

function killFramework()
{
	FW_PIDS=$(ps -ef |grep java |grep 'configServer\|discovery\|router\|hello-world' |awk '{print $2}')
	for myPID in ${FW_PIDS[@]}
	do
		echo "killing PID : $myPID"
		kill -9 $myPID
	done
}

function writeBanner()
{
	echo "########################################################################################################################"
	echo "### Running test against $BANNER_MSG $_HOST"
	echo "########################################################################################################################"
	echo ""
}

function main()
{
	setup $*
	writeBanner

	#launchFWComponentsAsJars

	testConfigDefault
	testConfigServer
	testDiscovery
	testRouter
	testHelloWorld
	testMetrics

	#killFramework
}

main $*
