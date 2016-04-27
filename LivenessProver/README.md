# Liveness Checking for Randomised Parameterised Systems

The tool in this directory implements a solver for parameterised
reachability games. Liveness (almost sure termination) of randomised
parameterised systems can be reduced to such games, by considering 
process transitions as one of the players ("Process", usually called
Player 2 in our setting), and the "Scheduler" (Player 1). The tool
uses a variety of techniques to automatically compute well-founded
relations and invariants representing winning strategies of Player 2,
including Angluin's L* algorithm and SAT solving.

