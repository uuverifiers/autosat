#!/bin/bash

BENCHMARKS="\
	benchmarks/currency_games/flip-game.txt \
	benchmarks/moran/moran-linear-fair-AB.txt \
	benchmarks/herman/herman-ring-selfloops.txt \
	"

RUNCMD="./runFairFancy --silent"

for i in ${BENCHMARKS} ; do
	TMPFILE=`mktemp /tmp/bench.XXXXXXXXXXX`
	OUTFILE=`mktemp /tmp/bench.XXXXXXXXXXX`
	cat ${i} \
		| grep -v '^logLevel' \
		| grep -v '^explicitChecksUntilLength' \
		> ${TMPFILE}

	echo -n "Running ${i}:	"
	STARTTIME=$(date +%s.%N)
	${RUNCMD} ${TMPFILE} > ${OUTFILE}
	ENDTIME=$(date +%s.%N)
	DIFFTIME=$(echo "$ENDTIME - $STARTTIME" | bc)

	grep -q "VERDICT: Player 2 can win from every initial configuration" ${OUTFILE}
	ret=$?

	if [ "$ret" -eq 0 ] ; then
		echo -n "CORRECT:	"
	else
		echo -n "--FAILURE--:	"
	fi

	echo "${DIFFTIME}"
done
