#!/bin/sh

RUN=../run

ulimit -t 8000

for name in Israeli-Jalfon.txt \
            Lehmann-Rabin.txt \
            herman-linear.txt \
            bakery.txt \
            firewire.txt \
            szymanski.txt \
            LR-philo.txt \
            take-away.txt \
            nim.txt \
            rap.txt; do

    echo Benchmark: $name
    
    # Using incremental procedure and pre-computed invariants
    /usr/bin/time -f "Runtime (Incr+Inv+Symm): %e" $RUN $name

    # Using incremental procedure w/o pre-computed invariants
    cp $name tempinput.txt
    echo 'noPrecomputedInvariant;' >>tempinput.txt
    /usr/bin/time -f "Runtime (Incr+Symm): %e" $RUN tempinput.txt

    # Using monolithic procedure (w/o pre-computed invariants)
    echo 'monolithicWitness;' >>tempinput.txt
    /usr/bin/time -f "Runtime (Mono): %e" $RUN tempinput.txt

    # Experiments without symmetries
    grep -v '^symmetries' <$name >tempinput.txt
    /usr/bin/time -f "Runtime (Incr+Inv): %e" $RUN tempinput.txt 

    echo 'noPrecomputedInvariant;' >>tempinput.txt
    /usr/bin/time -f "Runtime (Incr): %e" $RUN tempinput.txt

done >benchmark.log 2>&1
