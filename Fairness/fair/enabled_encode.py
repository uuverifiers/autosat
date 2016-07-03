#!/usr/bin/env python3

import argparse
import os
import sys


from problem import Problem
from automaton import Automaton
from parser import Parser
from constants import *

PLAYER1_START_STATE = 'XXXXsinitXXXX'

def parseOptions():
    '''parseOptions() -> options

Parses command-line options and returns them as an object with the following
attributes:

  filename - the name of the input file as a String
'''
    parser = argparse.ArgumentParser(description="Encodes enabledness"
        " into a two-player transition system")
    parser.add_argument("filename", metavar="file")
    args = parser.parse_args()
    return args


###############################################################################
def encodeEnablednessAut(aut, autEnabled):
    '''encodeEnablednessAut(aut, autEnabled) -> Automaton

Encodes enabledness (given by autEnabled) into an automaton aut.  This is done
by performing a product of aut and autEnabled, while treating 'delim' as
{'enabled', 'disabled'}.
'''

    def funDelimMatchEnDis(lhs, rhs):
        if ((lhs == SYMBOL_DELIM) and
            ((rhs == SYMBOL_ENABLED) or (rhs == SYMBOL_DISABLED))):
            return True
        else:
            return lhs == rhs

    def funTakeRhs(lhs, rhs):
        return rhs

    aut2 = aut.removeEpsilon()
    result = Automaton.generalIntersection(aut2, autEnabled,
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

    # result = Automaton.autUnion(oneChosenTransTransdMerged, noEnabledTransTransd)
    result = oneChosenTransTransdMerged
    result = result.singleStartState(PLAYER1_START_STATE)

    return result


###########################################
def createPlayer2(autPlay2, autEnabled):
    '''createPlayer2(autPlay2, autEnabled) -> Automaton

Encode enabledness into the transducer autPlay2 for transitions of Player 2
using autEnabled defining enabledness.
'''
    #############################################################################
    def matchDelimChosenToEnDis(lhs, rhs):                                      #
        if lhs == SYMBOL_DELIM and rhs in {SYMBOL_ENABLED, SYMBOL_DISABLED}:    #
            return True                                                         #
        elif lhs == SYMBOL_CHOSEN and rhs == SYMBOL_ENABLED:                    #
            return True                                                         #
        else:                                                                   #
            return lhs == rhs                                                   #
    #############################################################################

    #############################################################################
    def matchDelimEnDis(lhs, rhs):                                              #
        if lhs == SYMBOL_DELIM and rhs in {SYMBOL_ENABLED, SYMBOL_DISABLED}:    #
            return True                                                         #
        else:                                                                   #
            return lhs == rhs                                                   #
    #############################################################################

    #####################################
    def pickChosenOrRight(lhs, rhs):    #
        if lhs == SYMBOL_CHOSEN:        #
            return lhs                  #
        else:                           #
            return rhs                  #
    #####################################

    result = Automaton.transdAutAutProd(
            autPlay2,
            autEnabled, matchDelimChosenToEnDis, pickChosenOrRight,
            autEnabled, matchDelimEnDis, lambda x, y: y
        )

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

    with open(os.path.join(OUTPUT_DIR, "init.dot"), "w") as text_file:
        text_file.write(problem.autInit.exportToDot())

    with open(os.path.join(OUTPUT_DIR, "final.dot"), "w") as text_file:
        text_file.write(problem.autFinal.exportToDot())

    with open(os.path.join(OUTPUT_DIR, "enabled.dot"), "w") as text_file:
        text_file.write(problem.autEnabled.exportToDot())

    with open(os.path.join(OUTPUT_DIR, "play2.dot"), "w") as text_file:
        text_file.write(problem.autPlay2.exportToDot())

    enabledInit = encodeEnablednessAut(problem.autInit, problem.autEnabled)
    enabledFinal = encodeEnablednessAut(problem.autFinal, problem.autEnabled)
    enabledPlay1 = createPlayer1(problem.autEnabled)
    enabledPlay2 = createPlayer2(problem.autPlay2, problem.autEnabled)

    with open(os.path.join(OUTPUT_DIR, "enabled_init.dot"), "w") as text_file:
        text_file.write(enabledInit.exportToDot())

    with open(os.path.join(OUTPUT_DIR, "enabled_final.dot"), "w") as text_file:
        text_file.write(enabledFinal.exportToDot())

    with open(os.path.join(OUTPUT_DIR, "enabled_play1.dot"), "w") as text_file:
        text_file.write(enabledPlay1.exportToDot())

    with open(os.path.join(OUTPUT_DIR, "enabled_play2.dot"), "w") as text_file:
        text_file.write(enabledPlay2.exportToDot())

    # output Init
    outlines = []
    outlines.append("I0 {\n")
    outlines.append(str(enabledInit))
    outlines.append("}\n\n")

    CLOSED_UNDER_TRANSITIONS = "closedUnderTransitions"
    if CLOSED_UNDER_TRANSITIONS in problem.options:
        outlines.append(CLOSED_UNDER_TRANSITIONS + ";\n")
        problem.options.remove(CLOSED_UNDER_TRANSITIONS)

    # output all other automata
    for (name, aut) in [
          ("F", enabledFinal),
          ("P1", enabledPlay1),
          ("P2", enabledPlay2),
          ("Enabled", problem.autEnabled),
        ]:
        outlines.append(name + " {\n")
        outlines.append(str(aut))
        outlines.append("}\n\n")

    for option in problem.options:
        outlines.append(option + ";\n")

    for line in outlines:
        print(line, end="")
