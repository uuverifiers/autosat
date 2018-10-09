#!/usr/bin/env bash
# prepares a directory of benchmarks for experiments

RUN_DIR="to_run"
BENCH_LIST="list_benchmarks.txt"

FAIR_ENCODE="fair/fair_encode.py"

####################################
proc_bench() {
	in_file=${1}
	for enc in $(${FAIR_ENCODE} -e) ; do
		uniq_str="${in_file}-${enc}"
		uniq_str_hash=$(echo ${uniq_str} | sha256sum | head -c 10)
		in_file_base=$(basename ${in_file})
		out_file="${RUN_DIR}/${uniq_str_hash}-${in_file_base}-${enc}"
		echo "creating ${out_file}"
		cat ${in_file} \
			| grep -v '^encoding' \
			> ${out_file}
		echo "encoding: ${enc};" >> ${out_file}
	done
}
####################################

########################## ENTRY POINT ##########################
rm -r ${RUN_DIR}
mkdir ${RUN_DIR}

cat ${BENCH_LIST} | \
	while read line ; do
		proc_bench ${line}
	done
