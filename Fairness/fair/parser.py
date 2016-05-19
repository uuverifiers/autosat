#!/usr/bin/env python3

import re

from automaton import Automaton
from problem import Problem

##############################################################################
#                          Regular expressions
##############################################################################

# regex for matching start states
reStartStates = re.compile(r'^init:\ *(?P<states>.+);$')

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

##############################################################################
class Parser:
    '''The parser of input files'''

    ###########################################
    def parseStartStates(aut, line):
        '''parseStartStates(line)

Parses a line with starting states of an automaton.
'''
        match = reStartStates.match(line)
        assert match is not None
        strStates = match.group('states')
        strStatesNoWs = strStates.replace(" ", "")
        states = strStatesNoWs.split(',')
        aut.startStates.extend(states)


    ###########################################
    def parseAcceptStates(aut, line):
        '''parseAcceptStates(aut, line)

Parses a line with accepting states of an automaton.  Modifies aut.
'''
        match = reAcceptStates.match(line)
        assert match is not None
        strStates = match.group('states')
        strStatesNoWs = strStates.replace(" ", "")
        states = strStatesNoWs.split(',')
        aut.acceptStates.extend(states)


    ###########################################
    def parseEpsTrans(aut, line):
        '''parseEpsTrans(line)

Parses an epsilon transition on line.  Modifies aut.
'''
        match = reEpsTrans.match(line)
        assert match is not None
        aut.transitions.append(
            (
                match.group('src'),
                match.group('tgt'),
            ))

    ###########################################
    def parseTrans(problem, aut, line):
        '''parseTrans(problem, aut, line)

Parses automaton transition from line.  Modifies problem, aut.
'''
        if reAutTrans.match(line):
            match = reAutTrans.match(line)

            problem.alphabet.add(match.group('symbol'))

            aut.transitions.append(
                (
                    match.group('src'),
                    match.group('symbol'),
                    match.group('tgt'),
                ))
        elif reTransdTrans.match(line):
            match = reTransdTrans.match(line)

            problem.alphabet.add(match.group('fst_symbol'))
            problem.alphabet.add(match.group('snd_symbol'))

            aut.transitions.append(
                (
                    match.group('src'),
                    match.group('fst_symbol'),
                    match.group('snd_symbol'),
                    match.group('tgt'),
                ))
        else:
            raise Exception("Invalid transition syntax: " + line)

    ###########################################
    def parseAut(problem, it):
        '''parseAut(problem, it) -> Automaton

Parses an automaton (resp. transducer) representation into an instance of the
Automaton class.  Modifies problem, it.
'''
        # initialize
        aut = Automaton()

        for line in it:
            if (line == "}"): # end of automaton
                return aut
            elif (line[0:2] == "//"): # comments
                pass
            elif (line == ""): # empty string
                pass
            elif (reStartStates.match(line)): # start states
                Parser.parseStartStates(aut, line)
            elif (reAcceptStates.match(line)): # accepting states
                Parser.parseAcceptStates(aut, line)
            elif (reEpsTrans.match(line)): # epsilon transition
                Parser.parseEpsTrans(aut, line)
            elif (reAutTrans.match(line)) or (reTransdTrans.match(line)):
                Parser.parseTrans(problem, aut, line)
            else:
                raise Exception("Syntax error: " + line)

    ###########################################
    def parseProblem(it):
        '''parseProblem(it)

Parses top file structures of a problem in a file.  Modifies it.
'''
        problem = Problem()
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
                    problem.autInit = Parser.parseAut(problem, it)
                elif name == "F": # aut for final configurations
                    problem.autFinal = Parser.parseAut(problem, it)
                elif name == "P1": # aut for Player 1
                    problem.autPlay1 = Parser.parseAut(problem, it)
                elif name == "P2": # aut for Player 2
                    problem.autPlay2 = Parser.parseAut(problem, it)
                elif name == "Enabled": # aut for Enabled
                    problem.autEnabled = Parser.parseAut(problem, it)
                else:
                    raise Exception("Invalid automaton name: " + name)
            else:
                raise Exception("Syntax error: " + line)

        return problem


###############################################################################
if __name__ == '__main__':
    print("Library module")
