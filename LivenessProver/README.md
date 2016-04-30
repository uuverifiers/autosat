# SLRP: Solver for Liveness of Randomised Parameterised systems

The tool SLRP (pronounced slurp) implements a solver for (almost sure) liveness 
of randomised parameterised systems. The tool reduces this problem to
parameterised reachability games between the "Process" player (Player 2 in
our setting) and the "Scheduler" (Player 1 in our setting). The tool
uses a variety of CEGAR techniques to automatically compute well-founded
relations and invariants representing winning strategies of Player 2 (which
we call "advice bits" in the paper). The basic framework is to guess the advice
bits (either using Angluin's L* algorithm or SAT solving), and verify the guess
using an automata method. 

## Usage

On a Linux/UNIX system, SLRP can be invoked using the provided script:

```
> ./slrp benchmarks/herman-linear.txt 
VERDICT: Player 2 can win from every reachable configuration
[...]
```

A collection of examples is provided in the "benchmarks" directory.

## Input format

A parameterised reachability game is defined as a tuple (I0, F, P1, P2) of 
automata, where:
* I0 represents the set of initial states
* F represents the set of final states
* P1 represents the transition relation of Player 1 (Scheduler)
* P2 represents the transition relation of Player 2 (Process)

The objective is for Player 2 to be able to reach F (i.e. win), regardless of 
the moves of Player 1. The set of initial states that Player 2 should be able 
to win from depends on some flags, which can be enabled/disabled according
to the verification problem. If almost sure liveness for randomised 
parameterised systems is considered, then the flag `ClosedUnderTransition`
should be inserted in the file (see below). There are also other flags that
can be enabled/disabled depending on the different CEGAR techniques employed
(see below).

We illustrate this input format of the tool using a simple example,
a linear version of the Herman randomised self-stabilising protocol (i.e., 
instead of ring topology, as normally assumed for Herman, we just arrange 
processes as a linear array). The example is also available in the file
benchmarks/herman-linear.txt. Loosely speaking, the processes holding a token
in Herman protocol will toss a coin, when chosen by the Scheduler, and keep the 
token with probability 1/2 and pass the token to its *right* with probability
1/2. Two tokens held by the same process are *merged*. A stable configuration 
is one in which precisely one process holding a 
token. The initial configurations are those in which more than one processes
are holding a token. The liveness property to prove is reaching a stable
configuration with probability 1.

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
 * necessary when analysing liveness for randomised systems.
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

/////////////////////////////////////////////////////////////////////////////
// The rest of the file contains verification options
 
/**
 * Lower/Upper bound (inclusive) on the number of states considered for the 
 * progress relations T
 */
transducerStateGuessing: 1 .. 10;

/**
 * Lower/Upper bound (inclusive) on the number of states considered for the regular sets A, B of
 * configurations
 */
automatonStateGuessing: 0 .. 4;

/**
 * Only search for progress relations that represent lexicographic
 * ranking functions (not used here).
 */
// useRankingFunctions;

/**
 * Disable disjunctive advice bits (not used here).
 */
// monolithicWitness;

/**
 * Accelerate using symmetries of the game (not used here).
 */
// symmetries: rotation;

/**
 * Run sanity checks before parameterised analysis: solve instances
 * of the game up to length 6 using explicit-state model checking.
 * This is often useful for finding bugs in models (not used here).
 */
// explicitChecksUntilLength: 6;

/**
 * Parallelise the search for strategies to make use of multiple
 * available cores; currently on a scale from
 * 0 (fully sequential) to 2.
 * (not used here)
 */
// parallel: 1;

/**
 * How much log output to produce, currently on a scale from
 * 0 (quiet) to 2 (full log).
 * (not used here)
 */
// logLevel: 1;

```
