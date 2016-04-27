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
array.

```
// Configurations with at least one token
I0 {
    init: s0;

    s0 -> s0 N;
    s0 -> s0 T;

    s0 -> s1 T;
    
    s1 -> s1 N;
    s1 -> s1 T;

    accepting: s1;
}

closedUnderTransitions;

// Configurations with precisely one token
F {
    init: s0;
    s0 -> s0 N;
    s0 -> s1 T;
    s1 -> s1 N;
    accepting: s1;
}

P1 {
    init: sinit;

    sinit -> s0;
    sinit -> sN;

    s0 -> s0 N/N;
    s0 -> s0 T/T;

    s0 -> s1 T/T_;

    // To make sure the chosen process is not last
    s1 -> s2 T/T;
    s1 -> s2 N/N;

    s2 -> s2 N/N;
    s2 -> s2 T/T;

    // Player 1 always needs to be able to make a move
    sN -> sN N/N;

    accepting: s2, sN;
}

P2 {
    init: s0;

    s0 -> s0 N/N;
    s0 -> s0 T/T;

    // keep
    s0 -> s1 T_/T;
    // pass to right
    s0 -> s2 T_/N;
    
    // Merge tokens
    s2 -> s1 T/T;
    // Pass token to right from s2
    s2 -> s1 N/T;

    s1 -> s1 N/N;
    s1 -> s1 T/T;

    accepting: s1;
}

transducerStateGuessing: 1 .. 10;
automatonStateGuessing: 0 .. 4;
```
