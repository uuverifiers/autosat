#!/bin/bash

BENCHMARKS="\
	benchmarks/wolf-sheep/wolf-sheep-linear-nograss.txt \
	benchmarks/currency_games/flip-game.txt \
	benchmarks/herman/herman-linear-selfloops.txt \
	benchmarks/herman/herman-ring-selfloops.txt \
	benchmarks/herman/herman-odd-linear-selfloops.txt \
	benchmarks/herman/herman-odd-ring-selfloops.txt \
	benchmarks/agents-opinions/agents_opinions_n2_randomswitch_linear.txt \
	benchmarks/moran/moran-linear-fair-AB.txt \
	benchmarks/moran/moran-linear-fair-ABC.txt \
	benchmarks/agents-opinions/agents_opinions_n2_linear.txt \
	benchmarks/agents-opinions/agents_opinions_linear.txt \
	"

RUNCMD="./runFairFancy --silent"

for i in ${BENCHMARKS} ; do
	TMPFILE=`mktemp /tmp/bench.XXXXXXXXXXX`
	OUTFILE=`mktemp /tmp/bench.XXXXXXXXXXX`
	cat ${i} \
		| grep -v '^logLevel' \
		| grep -v '^explicitChecksUntilLength' \
		> ${TMPFILE}
	echo "logLevel: 1;" >> ${TMPFILE}

	echo -n "$(date "+%y-%m-%d %H:%M:%S");	"
	echo -n "${i};	"
	STARTTIME=$(date +%s.%N)
	${RUNCMD} ${TMPFILE} > ${OUTFILE}
	ENDTIME=$(date +%s.%N)
	DIFFTIME=$(echo "$ENDTIME - $STARTTIME" | bc)

	grep -q "VERDICT: Player 2 can win from every" ${OUTFILE}
	ret=$?

	if [ "$ret" -eq 0 ] ; then
		echo -e -n "\e[1;32mCORRECT\e[0m;	"
	else
		echo -e -n "--\e[1;31mFAILURE\e[0m--;	"
	fi

	echo -n "${DIFFTIME};	"
	echo "( ${OUTFILE} )"
done
