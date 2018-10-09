#!/bin/bash

RUN_DIR="to_run"

TMPFILE=$(mktemp /tmp/bench.XXXXXXXXXXX)
for i in ${RUN_DIR}/* ; do
	./runSingleBenchmark.sh ${i}
done | tee ${TMPFILE}

echo "=========================== OUTPUT ==========================="
column -t -s '	' ${TMPFILE}
