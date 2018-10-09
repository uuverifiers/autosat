#!/usr/bin/env bash

RFFSCRIPT="./runFairFancy"

# Check the number of command-line arguments
if [ \( "$#" -ne 1 \) ] ; then
	echo "usage: ${0} <file> [arguments]"
	echo "where [arguments] are passed to the script \"${RFFSCRIPT}\" (see the script for the possible arguments)"
	exit 1
fi

INPUT="${1}"

RUNCMD="${RFFSCRIPT} $@"

TMPFILE=$(mktemp /tmp/bench.XXXXXXXXXXX)
OUTFILE=$(mktemp /tmp/bench.XXXXXXXXXXX)
LOGFILE=$(mktemp /tmp/bench.XXXXXXXXXXX)
cat ${INPUT} \
	| grep -v '^logLevel' \
	| grep -v '^explicitChecksUntilLength' \
	> ${TMPFILE}
echo "logLevel: 1;" >> ${TMPFILE}

echo -n "$(date "+%y-%m-%d %H:%M:%S");	"
echo -n "${INPUT};	"
echo "Running  ${RUNCMD} ${TMPFILE}" >> ${LOGFILE}
STARTTIME=$(date +%s.%N)
${RUNCMD} ${TMPFILE} >> ${OUTFILE} 2>> ${LOGFILE}
ENDTIME=$(date +%s.%N)
DIFFTIME=$(echo "$ENDTIME - $STARTTIME" | bc)

grep -q "VERDICT: Player 2 can win from every" ${OUTFILE}
ret=$?

if [ "$ret" -eq 0 ] ; then
	echo -e -n "..\e[1;32mCORRECT\e[0m..;	"
else
	echo -e -n "--\e[1;31mFAILURE\e[0m--;	"
fi

# merge out and log file
echo "======== OUT FILE ========" >> ${LOGFILE}
cat ${OUTFILE} >> ${LOGFILE}

echo -n "${DIFFTIME};	"
echo "(log: ${LOGFILE} )"
