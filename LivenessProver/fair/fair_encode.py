#!/usr/bin/python3

import argparse
import re
import sys

from enum import Enum

##############################################################################
class Problem:
    '''Represents a liveness problem'''
    def __init__(self):
        self.alphabet = set()
        self.options = []
        self.uses_delim = False


# global instance of a problem
problem = Problem()


##############################################################################
class Automaton:
    '''Represents an automaton'''

    def __init__(self):
        '''__init__(self)

Constructor.
'''
        self.startState = None
        self.acceptStates = None
        self.transitions = []
        self.epsTransitions = []


    ###########################################
    def processStartState(self, line):
        '''processStartState(line)

Parses a line with the starting state of an automaton.
'''
        match = reStartState.match(line)
        assert match is not None
        if self.startState is not None:
            raise Exception("Defining starting state multiple times: " + line)
        self.startState = match.group('state')


    ###########################################
    def processAcceptStates(self, line):
        '''processAcceptStates(line)

Parses a line with accepting states of an automaton.
'''
        match = reAcceptStates.match(line)
        assert match is not None
        if self.acceptStates is not None:
            raise Exception("Defining accepting states multiple times: " + line)
        self.acceptStates = match.group('states')


    ###########################################
    def processEpsTrans(self, line):
        '''processEpsTrans(line)

Processes an epsilon transition on line.
'''
        assert reEpsTrans.match(line)
        self.epsTransitions.append(line)

    ###########################################
    def processTrans(self, line):
        '''processTrans(line)

Processes automaton transition from line.
'''
        if reAutTrans.match(line):
            match = reAutTrans.match(line)

            problem.alphabet.add(match.group('symbol'))

            self.transitions.append(
                (
                    match.group('src'),
                    match.group('tgt'),
                    match.group('symbol')
                ))
        elif reTransdTrans.match(line):
            match = reTransdTrans.match(line)

            problem.alphabet.add(match.group('fst_symbol'))
            problem.alphabet.add(match.group('snd_symbol'))

            self.transitions.append(
                (
                    match.group('src'),
                    match.group('tgt'),
                    match.group('fst_symbol'),
                    match.group('snd_symbol')
                ))
        else:
            raise Exception("Invalid transition syntax: " + line)


    def __str__(self):
        '''__str__(self)

Transforms automaton into a string.
'''
        output = ""
        output += "init: " + self.startState + ";\n"

        for trans in self.epsTransitions:
            output += trans + "\n"

        for trans in self.transitions:
            if len(trans) == 3:
                output += trans[0] + " -> " + trans[1] + " " + trans[2] + ";\n"
            elif len(trans) == 4:
                output += trans[0] + " -> " + trans[1] + " " + trans[2] + "/" + trans[3] + ";\n"
            else:
                raise Exception("Internal error")

        output += "accepting: " + self.acceptStates + ";\n"

        return output


##############################################################################

# regex for matching start states
reStartState = re.compile(r'^init:\ *(?P<state>[a-zA-Z0-9_]+);$')

# regex for matching accepting states
reAcceptStates = re.compile(r'^accepting:\ *(?P<states>.+);$')

# regex for matching automaton transitions
reAutTrans = re.compile(r'^(?P<src>[a-zA-Z0-9_]+)\ *->\ *(?P<tgt>[a-zA-Z0-9_]+)\ +(?P<symbol>[a-zA-Z_]+)\ *;$');

# regex for matching transducer transitions
reTransdTrans = re.compile(r'^(?P<src>[a-zA-Z0-9_]+)\ *->\ *(?P<tgt>[a-zA-Z0-9_]+)\ +(?P<fst_symbol>[a-zA-Z_]+)/(?P<snd_symbol>[a-zA-Z_]+)\ *;$');

# regex for matching epsilon transitions
reEpsTrans = re.compile(r'^(?P<src>[a-zA-Z0-9_]+)\ *->\ *(?P<tgt>[a-zA-Z0-9_]+)\ *;$');

# regex for start of automaton definition
reAutDefStart = re.compile(r'^(?P<autname>[a-zA-Z0-9]+)\ +\{$')

# regex for an option without a parameter
reOptionNoParam = re.compile(r'^(?P<option>[a-zA-Z0-9]+)\ *;$')

# regex for an option with a parameter
reOptionWithParam = re.compile(r'^(?P<option>[a-zA-Z0-9]+:.+)\ *;$')

# symbols for encoding counters
ZERO = '0'
ONE = '1'

# the delimiter character
DELIM = "delim"

# special states
FINAL_NEW_PRE_INIT = "XX'pre_init"
FINAL_NEW_INIT = "XX'init"
FINAL_NEW_ZERO = "XX'zero"
FINAL_NEW_ACCEPT = "XX'accept"


def parseOptions():
    '''parseOptions() -> options

Parses command-line options and returns them as an object with the following
attributes:

  filename - the name of the input file as a String
'''
    parser = argparse.ArgumentParser(description="Encodes (weak) fairness"
        " into a two-player transition system")
    parser.add_argument("filename", metavar="file")
    args = parser.parse_args()
    return args


def autInitToFair(aut):
    '''autInitToFair(aut) -> Automaton

Encodes fairness into an automaton representing initial configurations of
a system.
'''
    output = Automaton()
    output.startState = aut.startState
    output.acceptStates = aut.acceptStates
    output.epsTransitions = aut.epsTransitions

    for trans in aut.transitions:
        assert len(trans) == 3
        src = trans[0]
        tgt = trans[1]
        symbol = trans[2]
        oneState = tgt + "'one"
        zeroState = tgt + "'zero"

        if problem.contains_delim:
            if symbol == DELIM:
                output.transitions.append((src, src, ONE))
                output.transitions.append((src, zeroState, ONE))
                output.transitions.append((zeroState, zeroState, ZERO))
                output.transitions.append((zeroState, tgt, ZERO))
            else:
                output.transitions.append(trans)
        else:
            assert not problem.contains_delim

            output.transitions.append((src, oneState, symbol))
            output.transitions.append((oneState, oneState, ONE))
            output.transitions.append((oneState, zeroState, ONE))
            output.transitions.append((zeroState, zeroState, ZERO))
            output.transitions.append((zeroState, tgt, ZERO))

    output.transitions = list(set(output.transitions)) # kill duplicates
    return output


###############################################################################
def autFinalToFair(aut):
    '''autFinalToFair(aut) -> Automaton

Encodes fairness into an automaton representing final configurations of
a system.
'''
    output = Automaton()
    output.startState = FINAL_NEW_PRE_INIT
    output.acceptStates = aut.acceptStates + ", " + FINAL_NEW_ACCEPT
    output.epsTransitions = aut.epsTransitions
    output.epsTransitions.append(FINAL_NEW_PRE_INIT + " -> " + aut.startState + ";")
    output.epsTransitions.append(FINAL_NEW_PRE_INIT + " -> " + FINAL_NEW_INIT + ";")

    for trans in aut.transitions:
        assert len(trans) == 3
        src = trans[0]
        tgt = trans[1]
        symbol = trans[2]
        oneState = tgt + "'one"
        zeroState = tgt + "'zero"

        if problem.contains_delim:
            if symbol == DELIM:
                output.transitions.append((src, src, ONE))
                output.transitions.append((src, zeroState, ONE))
                output.transitions.append((zeroState, zeroState, ZERO))
                output.transitions.append((zeroState, tgt, ZERO))
            else:
                output.transitions.append(trans)
        else:
            assert not problem.contains_delim

            output.transitions.append((src, oneState, symbol))
            output.transitions.append((oneState, oneState, ONE))
            output.transitions.append((oneState, zeroState, ONE))
            output.transitions.append((zeroState, zeroState, ZERO))
            output.transitions.append((zeroState, tgt, ZERO))

    # transitions in FINAL_NEW_INIT
    for symb in (problem.alphabet - {DELIM}) | {ZERO, ONE}:
        output.transitions.append((FINAL_NEW_INIT, FINAL_NEW_INIT, symb))
    for symb in problem.alphabet - {DELIM}:
        output.transitions.append((FINAL_NEW_INIT, FINAL_NEW_ZERO, symb))

    # transitions in FINAL_NEW_ZERO
    output.transitions.append((FINAL_NEW_ZERO, FINAL_NEW_ACCEPT, ZERO))

    # transitions in FINAL_NEW_ACCEPT
    for symb in (problem.alphabet - {DELIM}) | {ZERO, ONE}:
        output.transitions.append((FINAL_NEW_ACCEPT, FINAL_NEW_ACCEPT, symb))

    output.transitions = list(set(output.transitions)) # kill duplicates
    return output


###############################################################################
def autPlay1ToFair(aut):
    '''autPlay1ToFair(aut) -> Automaton

Encodes fairness into aut for Player 1.
'''
    output = Automaton()
    output.startState = aut.startState
    output.acceptStates = aut.acceptStates
    output.epsTransitions = aut.epsTransitions

    for trans in aut.transitions:
        assert len(trans) == 4
        src = trans[0]
        tgt = trans[1]
        fstSymbol = trans[2]
        sndSymbol = trans[3]
        copyState = tgt + "'copy"
        oneState = tgt + "'one"
        zeroState = tgt + "'zero"

        if (fstSymbol == sndSymbol): # no rewrite
            output.transitions.append((src, copyState, fstSymbol, sndSymbol))
            output.transitions.append((copyState, copyState, ONE, ONE))
            output.transitions.append((copyState, copyState, ZERO, ZERO))
            output.transitions.append((copyState, tgt, ZERO, ZERO))
        else: # Player 1 selects a process
            assert fstSymbol != sndSymbol
            output.transitions.append((src, oneState, fstSymbol, sndSymbol))

            # create 1's
            output.transitions.append((oneState, oneState, ZERO, ONE))
            output.transitions.append((oneState, oneState, ONE, ONE))
            output.transitions.append((oneState, tgt, ZERO, ZERO))
            ## the following lines were changed to reduce the state space
            # output.transitions.append((oneState, zeroState, ZERO, ONE))
            # output.transitions.append((oneState, zeroState, ONE, ONE))

            # create 0's
            ## the following block was removed to reduce the state space
            # output.transitions.append((zeroState, zeroState, ZERO, ZERO))
            # output.transitions.append((zeroState, zeroState, ONE, ZERO))
            # output.transitions.append((zeroState, tgt, ZERO, ZERO))
            # output.transitions.append((zeroState, tgt, ONE, ZERO))

    output.transitions = list(set(output.transitions)) # kill duplicates
    return output


###############################################################################
def autPlay2ToFair(aut):
    '''autPlay2ToFair(aut) -> Automaton

Encodes fairness into aut for Player 2.
'''
    output = Automaton()
    output.startState = aut.startState
    output.acceptStates = aut.acceptStates
    output.epsTransitions = aut.epsTransitions

    for trans in aut.transitions:
        assert len(trans) == 4
        src = trans[0]
        tgt = trans[1]
        fstSymbol = trans[2]
        sndSymbol = trans[3]
        oneState = tgt + "'one"
        zeroState = tgt + "'zero"

        output.transitions.append((src, oneState, fstSymbol, sndSymbol))

        # decrement!
        output.transitions.append((oneState, oneState, ONE, ONE))
        output.transitions.append((oneState, zeroState, ONE, ZERO))
        output.transitions.append((zeroState, zeroState, ZERO, ZERO))
        output.transitions.append((zeroState, tgt, ZERO, ZERO))

    output.transitions = list(set(output.transitions)) # kill duplicates
    return output


###############################################################################
def parseAut(it):
    '''parseAut(it) -> Automaton

Parses an automaton (resp. transducer) representation into an instance of the
Automaton class.  Modifies it.
'''
    # initialize
    output = Automaton()

    for line in it:
        if (line == "}"): # end of automaton
            return output
        elif (line[0:2] == "//"): # comments
            pass
        elif (line == ""): # empty string
            pass
        elif (reStartState.match(line)): # start states
            output.processStartState(line)
        elif (reAcceptStates.match(line)): # accepting states
            output.processAcceptStates(line)
        elif (reEpsTrans.match(line)): # epsilon transition
            output.processEpsTrans(line)
        elif (reAutTrans.match(line)) or (reTransdTrans.match(line)):
            output.processTrans(line)
        else:
            raise Exception("Syntax error: " + line)


def processTopFile(it):
    '''processTopFile(it)

Processes top file structures in a file.  Modifies it.
'''
    for line in it:
        if (line[0:2] == "//"): # comments
            pass
        elif (line == ""): # empty string
            pass
        elif (reOptionNoParam.match(line)): # option
            match = reOptionNoParam.match(line)
            assert match is not None
            problem.options.append(match.group('option'))
        elif (reOptionWithParam.match(line)): # option with parameter
            match = reOptionWithParam.match(line)
            assert match is not None
            problem.options.append(match.group('option'))
        elif (reAutDefStart.match(line)): # beginning of an automaton
            name = reAutDefStart.match(line).group('autname')
            if name == "I0": # aut for initial configurations
                problem.autInit = parseAut(it)
            elif name == "F": # aut for final configurations
                problem.autFinal = parseAut(it)
            elif name == "P1": # aut for Player 1
                problem.autPlay1 = parseAut(it)
            elif name == "P2": # aut for Player 2
                problem.autPlay2 = parseAut(it)
            else:
                raise Exception("Invalid automaton name: " + name)
        else:
            raise Exception("Syntax error: " + line)


###############################################################################
def processLines(inlines):
    '''processLines([inline]) -> [outline]

Process input lines into output lines, adding fairness into the system
'''
    outlines = []
    it = iter(inlines)

    # we need to load the whole file first to collect all symbols in the
    # alphabet!
    processTopFile(it)

    # sanity checks
    assert ONE not in problem.alphabet
    assert ZERO not in problem.alphabet

    # if there is DELIM, encode fairness in a different way
    if DELIM in problem.alphabet:
        problem.contains_delim = True
    else:
        problem.contains_delim = False

    problem.fairInit = autInitToFair(problem.autInit)
    problem.fairFinal = autFinalToFair(problem.autFinal)
    problem.fairPlay1 = autPlay1ToFair(problem.autPlay1)
    problem.fairPlay2 = autPlay2ToFair(problem.autPlay2)

    outlines.append("I0 {\n")
    outlines.append(str(problem.fairInit))
    outlines.append("}\n\n")

    CLOSED_UNDER_TRANSITIONS = "closedUnderTransitions"
    if CLOSED_UNDER_TRANSITIONS in problem.options:
        outlines.append(CLOSED_UNDER_TRANSITIONS + ";\n")
        problem.options.remove(CLOSED_UNDER_TRANSITIONS)

    # output all other automata
    for (name, aut) in [
      ("F", problem.fairFinal),
      ("P1", problem.fairPlay1),
      ("P2", problem.fairPlay2)]:
        outlines.append(name + " {\n")
        outlines.append(str(aut))
        outlines.append("}\n\n")

    for option in problem.options:
        outlines.append(option + ";\n")

    return outlines


###############################################################################
if __name__ == '__main__':
    options = parseOptions()
    # input
    inlines = [line.strip() for line in open(options.filename)]
    # output
    outlines = processLines(inlines)

    for line in outlines:
        print(line)
