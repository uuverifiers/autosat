#!/bin/bash

BENCHMARKS="\
	benchmarks/wolf-sheep/wolf-sheep-linear-nograss.txt \
	benchmarks/clustering/clustering-line.txt \
	benchmarks/currency_games/flip-game.txt \
	benchmarks/herman/herman-linear-selfloops.txt \
	benchmarks/herman/herman-ring-selfloops.txt \
	benchmarks/herman/herman-odd-linear-selfloops.txt \
	benchmarks/herman/herman-odd-ring-selfloops.txt \
	benchmarks/cell-cycle-switch/cell-cycle-switch-n2-randomswitch-linear.txt \
	benchmarks/moran/moran-linear-fair-AB.txt \
	benchmarks/moran/moran-linear-fair-ABC.txt \
	benchmarks/cell-cycle-switch/cell-cycle-switch-n2-linear.txt \
	benchmarks/cell-cycle-switch/cell-cycle-switch-linear.txt \
	"

for i in ${BENCHMARKS} ; do
	./runSingleBenchmark.sh ${i}
done
