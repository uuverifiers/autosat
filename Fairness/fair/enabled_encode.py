#!/usr/bin/env python3

import argparse
import sys

from problem import Problem
from automaton import Automaton
from parser import Parser

from enum import Enum

# special symbols
SYMBOL_DELIM = 'delim'
SYMBOL_ENABLED = 'enabled'
SYMBOL_DISABLED = 'disabled'
SYMBOL_CHOSEN = 'chosen'


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


###############################################################################
def encodeEnablednessAut(aut, autEnabled):
    '''encodeEnablednessAut(aut, autEnabled) -> Automaton

Encodes enabledness (given by autEnabled) into an automaton aut.  This is done by performing a product of aut and autEnabled, while treating 'delim' as {'enabled', 'disabled'}.
'''

    def funDelimMatchEnDis(lhs, rhs):
        if ((lhs == SYMBOL_DELIM) and
            ((rhs == SYMBOL_ENABLED) or (rhs == SYMBOL_DISABLED))):
            return True
        else:
            return lhs == rhs

    def funTakeRhs(lhs, rhs):
        return rhs

    result = Automaton.generalIntersection(aut, autEnabled,
        funDelimMatchEnDis, funTakeRhs)
    return result


###############################################################################
def createPlayer1(autEnabled):
    '''createPlayer1(autEnabled) -> Automaton

Synthesises a transducer for Player 1 from the Enabled automaton autEnabled.
Player 1 nondeterministically chooses one enabled transition (if such exists).
'''

    enabledTransd = autEnabled.toTransducer()

    # the transducer for "no transition enabled"
    noEnabledTransTransd = enabledTransd.renameStates(lambda s: s + '_noEn')
    noEnabledTransTransd = noEnabledTransTransd.filterTrans(
        lambda trans: (trans[1], trans[2]) != (SYMBOL_ENABLED, SYMBOL_ENABLED))

    noEnabledTransTransd = noEnabledTransTransd.removeUseless()

    rename1 = lambda state: state + "_en1"
    rename2 = lambda state: state + "_en2"

    # the transducer for chosing one enabled transition
    oneChosenTransTransd1 = enabledTransd.renameStates(rename1)
    oneChosenTransTransd2 = enabledTransd.renameStates(rename2)

    oneChosenTransTransd1 = oneChosenTransTransd1.clearAcceptStates()
    oneChosenTransTransd2 = oneChosenTransTransd2.clearStartStates()

    oneChosenTransTransdMerged = Automaton.autUnion(oneChosenTransTransd1, oneChosenTransTransd2)

    for trans in enabledTransd.transitions:
        fstSymb = Automaton.getSymbol1(trans)
        sndSymb = Automaton.getSymbol2(trans)

        if (fstSymb, sndSymb) == (SYMBOL_ENABLED, SYMBOL_ENABLED):
            oneChosenTransTransdMerged.addTransTransd(
                rename1(Automaton.getSrcState(trans)),
                SYMBOL_ENABLED,
                SYMBOL_CHOSEN,
                rename2(Automaton.getTgtState(trans)))

    result = Automaton.autUnion(oneChosenTransTransdMerged, noEnabledTransTransd)

    return result


###############################################################################
if __name__ == '__main__':
    options = parseOptions()
    # input
    inlines = [line.strip() for line in open(options.filename)]
    it = iter(inlines)

    # parse the verification problem
    problem = Parser.parseProblem(it)
    assert hasattr(problem, 'autInit')
    assert hasattr(problem, 'autFinal')
    assert hasattr(problem, 'autEnabled')
    assert hasattr(problem, 'autPlay2')

    # print(problem.autInit)
    # print(problem.autFinal)
    # print(problem.autEnabled)
    # print(problem.autPlay2)

    enabledInit = encodeEnablednessAut(problem.autInit, problem.autEnabled)
    print("EnabledInit: {")
    print(enabledInit)
    print("}")

    enabledFinal = encodeEnablednessAut(problem.autFinal, problem.autEnabled)
    print("EnabledFinal: {")
    print(enabledFinal)
    print("}")

    enabledPlay1 = createPlayer1(problem.autEnabled)
    print("EnabledP1: {")
    print(enabledPlay1)
    print("}\n")

    dot = enabledPlay1.exportToDot()
    print(dot)

    with open("aut.dot", "w") as text_file:
        text_file.write(dot)

#    problem.fairInit = autInitToFair(problem.autInit)
#    problem.fairFinal = autFinalToFair(problem, problem.autFinal)
#    problem.fairPlay1 = autPlay1ToFair(problem.autPlay1)
#    problem.fairPlay2 = autPlay2ToFair(problem.autPlay2)

    # outlines.append("I0 {\n")
    # outlines.append(str(problem.fairInit))
    # outlines.append("}\n\n")

    CLOSED_UNDER_TRANSITIONS = "closedUnderTransitions"
    if CLOSED_UNDER_TRANSITIONS in problem.options:
        print(CLOSED_UNDER_TRANSITIONS + ";\n")
        problem.options.remove(CLOSED_UNDER_TRANSITIONS)

    # output all other automata
    # for (name, aut) in [
    #   ("F", problem.fairFinal),
    #   ("P1", problem.fairPlay1),
    #   ("P2", problem.fairPlay2)]:
    #     outlines.append(name + " {\n")
    #     outlines.append(str(aut))
    #     outlines.append("}\n\n")

    for option in problem.options:
        print(option + ";\n")
