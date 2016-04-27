# Liveness Checking for Randomised Parameterised Systems

The tool in this directory implements a solver for parameterised
reachability games. Liveness (almost sure termination) of randomised
parameterised systems can be reduced to such games, by considering 
process transitions as one of the players ("Process", usually called
Player 2 in our setting), and the "Scheduler" (Player 1). The tool
uses a variety of techniques to automatically compute well-founded
relations and invariants representing winning strategies of Player 2,
including Angluin's L* algorithm and SAT solving.

## Input format

We illustrate the input format of the tool using a simple example,
a linear version of the Herman protocol (i.e., instead of ring topology,
as normally assumed for Herman, we just arrange processes as a linear
array). The example is also available in the file
benchmarks/herman-linear.txt

```
/**
 * The finite automaton defining initial configurations.
 *
 * Here, letter "N" represents processes without token, and
 * "T" processes with token. Initial configurations are words
 * with at least one token.
 */
I0 {
    init: s0;      // initial state of the automaton

    s0 -> s0 N;    // transition labelled with letter "N"
    s0 -> s0 T;

    s0 -> s1 T;
    
    s1 -> s1 N;
    s1 -> s1 T;

    accepting: s1; // comma-separated list of accepting states
}

/**
 * The following flag specifies that the set I0 of initial configurations
 * has to be closed under Player 1 and Player 2 transitions. This is
 * usually necessary when analysing randomised systems.
 */
closedUnderTransitions;

/**
 * The set of winning states of Player 2.
 * (here: configurations with precisely one token)
 */
F {
    init: s0;
    s0 -> s0 N;
    s0 -> s1 T;
    s1 -> s1 N;
    accepting: s1;
}

/**
 * The length-preserving transducer defining Player 1 transitions.
 * This uses the same format as the finite automata I0, F, only that
 * each transition is labelled with a pair of letters.
 *
 * Here, Player 1 represents the scheduler of a randomised system, and
 * selects processes by changing their letter X to X_.
 */
P1 {
    init: sinit;

    sinit -> s0;         // epsilon-transitions
    sinit -> sN;

    s0 -> s0 N/N;        // transitions labelled with a pair of letters
    s0 -> s0 T/T;

    s0 -> s1 T/T_;       // this process is activated

    s1 -> s2 T/T;
    s1 -> s2 N/N;
    
    s2 -> s2 N/N;
    s2 -> s2 T/T;

    sN -> sN N/N;

    accepting: s2, sN;
}

/**
 * The length-preserving transducer defining Player 2 transitions.
 *
 * Here, Player 2 has to implement the possible transitions of the
 * process selected by the scheduler (Player 1).
 */
P2 {
    init: s0;

    s0 -> s0 N/N;
    s0 -> s0 T/T;

    s0 -> s1 T_/T;          // (1) selected process keeps token
    s0 -> s2 T_/N;          // (2) selected process passes token to right
    
    // Merge tokens
    s2 -> s1 T/T;           // merge tokens
    s2 -> s1 N/T;           // pass token to right from s2

    s1 -> s1 N/N;
    s1 -> s1 T/T;

    accepting: s1;
}

/**
 * The rest of the file gives verification options.
 */
 
// Number of states considered for the progress relation T
transducerStateGuessing: 1 .. 10;

// Number of states considered for the regular set A
automatonStateGuessing: 0 .. 4;
```
