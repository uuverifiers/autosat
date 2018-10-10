#!/bin/bash

RUN_DIR="to_run"

if [ "$1" = "-h" ] ; then
	echo "usage: $0 [arguments]"
	echo "arguments:"
	echo "  -h          show this text"
	echo "  -t timeout  set timeout for every benchmark"
	echo "  --silent    do not print diagnostic messages"
	exit 0
fi

TMPFILE=$(mktemp /tmp/bench.XXXXXXXXXXX)
echo "tmpfile = ${TMPFILE}"

echo "date_time;	benchmark;	status;	total_wall_time;	sat_time;	sat_queries;	log_file;" \
	| tee ${TMPFILE}

for i in ${RUN_DIR}/* ; do
	./runSingleBenchmark.sh ${i} $@
done | tee -a ${TMPFILE}

echo "=========================== OUTPUT ==========================="
column -t -s '	' ${TMPFILE}
