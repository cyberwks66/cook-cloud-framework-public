#!/bin/bash

function setup()
{
	echo "Running main test scenarios for Framework"
	INFILE="fw-main-test-scenarios.txt"
	rm -f failure.log
	rm -f success.log
}

function splitScenarios()
{
	CNT=$(grep -c "^DESC" $INFILE)
	CNT=`expr $CNT - 1`
	csplit  -f scenario ${INFILE} "/^DESC/" {$CNT}
}

function readStanzas()
{
	scenarioFiles=$(ls -1 scenario*)
	for file in ${scenarioFiles[@]}
	do
   		echo ">>$file<<"
		readScenario
	done
}

function readScenario()
{
	PAT="^(DESC|CMD|RESP)"
	while read line
	do 
		if [[ "$line" =~ $PAT ]];then
    		#echo ">$line<"
			runLine
		fi
	done < $file
}

function runLine()
{
	PAT_DESC="^DESC:"
	PAT_CMD="^CMD :"
	PAT_RESP="^RESP:"
	echo "running scenario line $line"
	if [[ "$line" =~ $PAT_DESC ]];then
		DESC=$line
	fi
	if [[ "$line" =~ $PAT_CMD ]];then
		CMD=$( echo $line |sed -e 's/CMD : //' )
	fi
	if [[ "$line" =~ $PAT_RESP ]];then
		RESP=$(echo $line |sed -e 's/^RESP: //')
		runCmd
	fi
echo "DESC is $DESC"
echo "CMD is $CMD"
echo "RESP is $RESP"
}

function runCmd()
{
	echo "#${CMD}"
	ACTUAL_RESP=$(eval $CMD)
	echo "$ACTUAL_RESP"
	PAT=$(echo $RESP)
	if [[ "$ACTUAL_RESP" =~ $PAT ]];then
		reportSuccess
	else
		reportFailure
	fi
	echo "################################################################################"
}

function reportFailure()
{
	echo "FAILURE put this into slack channel"
	echo "$DESC" >>failure.log
	echo "$CMD " >>failure.log
	echo "$RESP" >>failure.log
	echo "$ACTUAL_RESP" >>failure.log
	echo "" >>failure.log
}

function reportSuccess()
{
	echo "SUCCESS put this into log file"
	echo "$DESC" >>success.log
	echo "$CMD " >>success.log
	echo "$RESP" >>success.log
	echo "" >>success.log
}

function teardown()
{
	echo "All Done Running main test scenarios for Framework"
}

function main()
{
	setup
	splitScenarios
	readStanzas
	teardown
}

declare stanzas=()
main $*

