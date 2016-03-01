#!/usr/bin/python3

import argparse
import re
import sys

from enum import Enum

##############################################################################
class Problem:
    '''Represents a liveness problem'''
    def __init__(self):
        self.alphabet = []


# global instance of a problem
problem = Problem()


##############################################################################
class Automaton:
    '''Represents an automaton'''

    def __init__(self):
        '''__init__(self)

Constructor.
'''
        self.startStates = None
        self.acceptStates = None
        self.transitions = []
        self.epsTransitions = []


    ###########################################
    def processStartStates(self, line):
        '''processStartStates(line)

Parses a line with starting states of an automaton.
'''
        match = reStartStates.match(line)
        assert match is not None
        if self.startStates is not None:
            raise Exception("Defining starting states multiple times: " + line)
        self.startStates = match.group('states')


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
        '''processAutTrans(line)

Processes an automaton transition on line.
'''
        if reAutTrans.match(line):
            match = reAutTrans.match(line)

            self.transitions.append(
                (
                    match.group('src'),
                    match.group('tgt'),
                    match.group('symbol')
                ))
        elif reTransdTrans.match(line):
            match = reTransdTrans.match(line)

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
        output += "init: " + self.startStates + ";\n"

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
reStartStates = re.compile(r'^init:(?P<states>.+);$')

# regex for matching accepting states
reAcceptStates = re.compile(r'^accepting:(?P<states>.+);$')

# regex for matching automaton transitions
reAutTrans = re.compile(r'^(?P<src>[a-zA-Z0-9_]+)\ *->\ *(?P<tgt>[a-zA-Z0-9_]+)\ +(?P<symbol>[a-zA-Z_]+)\ *;$');

# regex for matching transducer transitions
reTransdTrans = re.compile(r'^(?P<src>[a-zA-Z0-9_]+)\ *->\ *(?P<tgt>[a-zA-Z0-9_]+)\ +(?P<fst_symbol>[a-zA-Z_]+)/(?P<snd_symbol>[a-zA-Z_]+)\ *;$');

# regex for matching epsilon transitions
reEpsTrans = re.compile(r'^(?P<src>[a-zA-Z0-9_]+)\ *->\ *(?P<tgt>[a-zA-Z0-9_]+)\ *;$');

# regex for start of automaton definition
reAutDefStart = re.compile(r'^(?P<autname>[a-zA-Z0-9]+)\ +\{$')


# symbols for encoding counters
ZERO = '0'
ONE = '1'


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
    output.startStates = aut.startStates
    output.acceptStates = aut.acceptStates
    output.epsTransitions = aut.epsTransitions

    for trans in aut.transitions:
        assert len(trans) == 3
        src = trans[0]
        tgt = trans[1]
        symbol = trans[2]
        oneState = tgt + "'one"
        zeroState = tgt + "'zero"

        output.transitions.append((src, oneState, symbol))
        output.transitions.append((oneState, oneState, ONE))
        output.transitions.append((oneState, zeroState, ONE))
        output.transitions.append((zeroState, zeroState, ZERO))
        output.transitions.append((zeroState, tgt, ZERO))

    return output


def autFinalToFair(aut):
    '''autFinalToFair(aut) -> Automaton

Encodes fairness into an automaton representing final configurations of
a system.
'''
    output = Automaton()
    output.startStates = aut.startStates + " brekeke"
    output.acceptStates = aut.acceptStates + " halabala"
    output.epsTransitions = aut.epsTransitions

    for trans in aut.transitions:
        assert len(trans) == 3
        src = trans[0]
        tgt = trans[1]
        symbol = trans[2]
        oneState = tgt + "'one"
        zeroState = tgt + "'zero"

        output.transitions.append((src, oneState, symbol))
        output.transitions.append((oneState, oneState, ONE))
        output.transitions.append((oneState, zeroState, ONE))
        output.transitions.append((zeroState, zeroState, ZERO))
        output.transitions.append((zeroState, tgt, ZERO))

    return output


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
        elif (reStartStates.match(line)): # start states
            output.processStartStates(line)
        elif (reAcceptStates.match(line)): # accepting states
            output.processAcceptStates(line)
        elif (reEpsTrans.match(line)): # epsilon transition
            output.processEpsTrans(line)
        elif (reAutTrans.match(line)) or (reTransdTrans.match(line)):
            output.processTrans(line)
        else:
            raise Exception("Syntax error: " + line)


def processTopFile(it, output):
    '''processTopFile(it, output)

Processes top file structures in a file.  Modifies it and output.
'''
    for line in it:
        if (line[0:2] == "//"): # comments
            output.append(line)
        elif (line == ""): # empty string
            output.append(line)
        elif (re.match(r'^[a-zA-Z0-9]+;$', line)): # option
            output.append(line)
        elif (re.match(r'^[a-zA-Z0-9]+:.+;$', line)): # option with parameter
            output.append(line)
        elif (reAutDefStart.match(line)): # beginning of an automaton
            name = reAutDefStart.match(line).group('autname')
            if name == "I0": # aut for initial configurations
                problem.autInit = parseAut(it)
            elif name == "F": # aut for final configurations
                problem.autFinal = parseAut(it)
            elif name == "P1": # aut for process 1
                problem.autProc1 = parseAut(it)
            elif name == "P2": # aut for process 2
                problem.autProc2 = parseAut(it)
            else:
                raise Exception("Invalid automaton name")
        else:
            output.append("*****" + line)


def processLines(inlines):
    '''processLines([inline]) -> [outline]

Process input lines into output lines, adding fairness into the system
'''
    outlines = []
    it = iter(inlines)

    processTopFile(it, outlines)

    problem.fairInit = autInitToFair(problem.autInit)
    problem.fairFinal = autFinalToFair(problem.autFinal)
    problem.fairProc1 = problem.autProc1
    problem.fairProc2 = problem.autProc2

    # output all automata
    for (name, aut) in [
      ("I0", problem.fairInit),
      ("F", problem.fairFinal),
      ("P1", problem.fairProc1),
      ("P2", problem.fairProc2)]:
        outlines.append(name + " {\n")
        outlines.append(str(aut))
        outlines.append("}\n\n")

    return outlines


if __name__ == '__main__':
    options = parseOptions()
    # input
    inlines = [line.strip() for line in open(options.filename)]
    # output
    outlines = processLines(inlines)

    for line in outlines:
        print(line)
