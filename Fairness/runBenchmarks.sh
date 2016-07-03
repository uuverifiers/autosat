#!/bin/bash

BENCHMARKS="\
	benchmarks/moran/moran-linear-fair-AB.txt \
	benchmarks/herman/herman-ring-selfloops.txt \
	"

RUNCMD="./runFairFancy --silent"

for i in ${BENCHMARKS} ; do
	echo ${i}
	TMPFILE=`mktemp /tmp/bench.XXXXXXXXXXX`
	cat ${i} | grep -v '^logLevel' > ${TMPFILE}

	${RUNCMD} ${TMPFILE}
done
