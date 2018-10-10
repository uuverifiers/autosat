#!/usr/bin/env bash

RFFSCRIPT="./runFairFancy"

# Check the number of command-line arguments
if [ \( "$#" -eq 0 \) ] ; then
	echo "usage: ${0} <file> [arguments]"
	echo "where [arguments] are passed to the script \"${RFFSCRIPT}\" (see the script for the possible arguments)"
	exit 1
fi

INPUT="${1}"
shift

RUNCMD="${RFFSCRIPT}"

TMPFILE=$(mktemp /tmp/bench.XXXXXXXXXXX)
OUTFILE=$(mktemp /tmp/bench.XXXXXXXXXXX)
LOGFILE=$(mktemp /tmp/bench.XXXXXXXXXXX)
cat ${INPUT} \
	| grep -v '^logLevel' \
	| grep -v '^explicitChecksUntilLength' \
	> ${TMPFILE}
# echo "logLevel: 1;" >> ${TMPFILE}
echo "logLevel: 2;" >> ${TMPFILE}

echo -n "$(date "+%y-%m-%d %H:%M:%S");	"
echo -n "${INPUT};	"
echo "Running  ${RUNCMD} ${TMPFILE} $@" >> ${LOGFILE}
STARTTIME=$(date +%s.%N)
${RUNCMD} ${TMPFILE} $@ >> ${OUTFILE} 2>> ${LOGFILE}
ENDTIME=$(date +%s.%N)
DIFFTIME=$(echo "$ENDTIME - $STARTTIME" | bc)

grep -q "VERDICT: Player 2 can win from every" ${OUTFILE}
ret=$?

if [ "$ret" -eq 0 ] ; then
	echo -e -n "..\e[1;32mCORRECT\e[0m..;	"
else
	echo -e -n "--\e[1;31mFAILURE\e[0m--;	"
fi

# print the measured time
echo -n "${DIFFTIME};	"

# compute the time taken by the SAT solver and the number of queries
SAT_TIME_ARRAY=$(cat ${OUTFILE} \
	| grep "encoding.SatSolver - Solving time" \
	| sed 's/^.*Solving time//' \
	| sed 's/ms*$//')
SAT_TIME_MS=$(echo "${SAT_TIME_ARRAY}" \
	| paste -sd+ \
	| bc)
SAT_TIME=$(echo "scale=3; ${SAT_TIME_MS} / 1000" | bc)
SAT_QUERIES=$(echo "${SAT_TIME_ARRAY}" | wc -l)
echo -n "${SAT_TIME};	"
echo -n "${SAT_QUERIES};	"

# merge out and log file
echo "======== OUTPUT FILE ========" >> ${LOGFILE}
cat ${OUTFILE} >> ${LOGFILE}

echo "${LOGFILE};"
