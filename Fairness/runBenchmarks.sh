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

OUTFILE_NAME="output-$(date "+%y-%m-%d_%H-%M-%S").csv"

TMPFILE=$(mktemp /tmp/bench.XXXXXXXXXXX)
# echo "output file = ${TMPFILE}"
echo "output file = ${OUTFILE_NAME}"

echo "date_time;	benchmark;	status;	total_wall_time;	sat_time;	sat_queries;	log_file;" \
	| tee ${OUTFILE_NAME}

for i in ${RUN_DIR}/* ; do
	./runSingleBenchmark.sh ${i} $@
done | tee -a ${OUTFILE_NAME}

echo "=========================== OUTPUT ==========================="
column -t -s '	' ${OUTFILE_NAME}
echo "Output written to ${OUTFILE_NAME}"
