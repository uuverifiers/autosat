#!/usr/bin/env python3

import argparse
import os
import sys

from problem import Problem
from automaton import Automaton
from parser import Parser
from constants import *

# symbols for encoding counters
SYMBOL_ZERO = '0'
SYMBOL_ONE = '1'
SYMBOL_ENABLED_TIMEOUT = "enabled_timeout"

FAIR_ENCODING_ALPHABET = ENCODING_ALPHABET | {
        SYMBOL_ZERO,
        SYMBOL_ONE,
        SYMBOL_ENABLED_TIMEOUT
    }

# special states
FINAL_START_STATE = "XXXinit"


###############################################################################
def parseOptions():
    '''parseOptions() -> options

Parses command-line options and returns them as an object with the following
attributes:

  filename - the name of the input file as a String
'''
    parser = argparse.ArgumentParser(description="Encodes weak fairness"
        " into a two-player transition system (with enabledness encoded beforehand)")
    parser.add_argument("filename", metavar="file")
    args = parser.parse_args()
    return args

###########################################
def encodeCounter(aut):
    '''encodeCounter(aut) -> Automaton

Encode counters into aut, yielding a new automaton.
'''
    result = Automaton()
    result.startStates = aut.startStates[:]
    result.acceptStates = aut.acceptStates[:]

    for trans in aut.transitions:
        (src, symb, tgt) = trans
        if symb in {SYMBOL_ENABLED, SYMBOL_DISABLED}:
            # for end-of-subword transitions
            oneState = tgt + "_" + symb + "_" + SYMBOL_ONE
            zeroState = tgt + "_" + symb + "_" + SYMBOL_ZERO
            symbState = tgt + "_" + symb
            result.addTrans(transition = (src, oneState))
            result.addTrans(oneState, SYMBOL_ONE, oneState)
            result.addTrans(oneState, SYMBOL_ONE, zeroState)
            result.addTrans(zeroState, SYMBOL_ZERO, zeroState)
            result.addTrans(zeroState, SYMBOL_ZERO, symbState)
            result.addTrans(symbState, symb, tgt)
        else:
            # for ordinary transitions
            result.addTrans(transition = trans)

    result.transitions = list(set(result.transitions)) # kill duplicates

    return result.removeUseless()


###########################################
def encodeCounterNoEnabled(aut):
    '''encodeCounteNoEnabledr(aut) -> Automaton

Encode counters into aut, yielding a new automaton.  Uses optimized encoding.
'''
    result = Automaton()
    result.startStates = aut.startStates[:]
    result.acceptStates = aut.acceptStates[:]

    for trans in aut.transitions:
        (src, symb, tgt) = trans
        assert symb != SYMBOL_DISABLED
        if symb == SYMBOL_ENABLED:
            # for end-of-subword transitions
            oneState = tgt + "_" + symb + "_" + SYMBOL_ONE
            zeroState = tgt + "_" + symb + "_" + SYMBOL_ZERO
            symbState = tgt + "_" + symb
            result.addTrans(transition = (src, oneState))
            result.addTrans(oneState, SYMBOL_ONE, oneState)
            result.addTrans(oneState, SYMBOL_ONE, zeroState)
            result.addTrans(zeroState, SYMBOL_ZERO, zeroState)
            result.addTrans(zeroState, SYMBOL_ZERO, tgt)
        else:
            # for ordinary transitions
            result.addTrans(transition = trans)

    result.transitions = list(set(result.transitions)) # kill duplicates

    return result.removeUseless()


###########################################
def encodeCounterWithZero(aut):
    '''encodeCounterWithZero(aut) -> Automaton

Encode counters (with potentially all zeros) into aut, yielding a new
automaton.
'''
    result = Automaton()
    result.startStates = aut.startStates[:]
    result.acceptStates = aut.acceptStates[:]

    for trans in aut.transitions:
        (src, symb, tgt) = trans
        if symb in {SYMBOL_ENABLED, SYMBOL_DISABLED}:
            # for end-of-subword transitions
            oneState = tgt + "_" + symb + "_" + SYMBOL_ONE
            zeroState = tgt + "_" + symb + "_" + SYMBOL_ZERO
            symbState = tgt + "_" + symb
            result.addTrans(transition = (src, oneState))
            result.addTrans(oneState, SYMBOL_ONE, oneState)
            result.addTrans(transition = (oneState, zeroState))
            result.addTrans(zeroState, SYMBOL_ZERO, zeroState)
            result.addTrans(zeroState, SYMBOL_ZERO, symbState)
            result.addTrans(symbState, symb, tgt)
        else:
            # for ordinary transitions
            result.addTrans(transition = trans)

    result.transitions = list(set(result.transitions)) # kill duplicates

    return result.removeUseless()


###########################################
def encodeCounterWithZeroNoEnabled(aut):
    '''encodeCounterWithZeroNoEnabled(aut) -> Automaton

Encode counters (with potentially all zeros) into aut, yielding a new
automaton.  Uses optimized encoding.
'''
    result = Automaton()
    result.startStates = aut.startStates[:]
    result.acceptStates = aut.acceptStates[:]

    for trans in aut.transitions:
        (src, symb, tgt) = trans
        assert symb != SYMBOL_DISABLED
        if symb == SYMBOL_ENABLED:
            # for end-of-subword transitions
            oneState = tgt + "_" + symb + "_" + SYMBOL_ONE
            zeroState = tgt + "_" + symb + "_" + SYMBOL_ZERO
            symbState = tgt + "_" + symb
            result.addTrans(transition = (src, oneState))
            result.addTrans(oneState, SYMBOL_ONE, oneState)
            result.addTrans(transition = (oneState, zeroState))
            result.addTrans(zeroState, SYMBOL_ZERO, zeroState)
            result.addTrans(zeroState, SYMBOL_ZERO, tgt)
        else:
            # for ordinary transitions
            result.addTrans(transition = trans)

    result.transitions = list(set(result.transitions)) # kill duplicates

    return result.removeUseless()


###############################################################################
def autInitToFair(aut):
    '''autInitToFair(aut) -> Automaton

Encodes fairness into an automaton representing initial configurations of
a system.
'''
    # return encodeCounter(aut)
    return encodeCounterNoEnabled(aut)


###############################################################################
def autFinalToFair(aut, autEnabled):
    '''autFinalToFair(aut, autEnabled) -> Automaton

Encodes fairness into an automaton representing final configurations of
a system, w.r.t. enabled transitions given by autEnabled.  The final states are
states given by aut with fairness encoded, or states corresponding to one
enabled process's counter reaching zero.
'''
    aut1 = autEnabled.renameStates(lambda x: x + "Y1")
    aut1 = aut1.clearAcceptStates()

    aut2 = autEnabled.renameStates(lambda x: x + "Y2")
    aut2 = aut2.clearStartStates()

    aut3 = Automaton.autUnion(aut1, aut2)
    for (src, symb, tgt) in autEnabled.transitions:
        if symb == SYMBOL_ENABLED:
            aut3.addTrans(src + "Y1", SYMBOL_ENABLED_TIMEOUT, tgt + "Y2")

    # aut4 = encodeCounterWithZero(aut3)
    aut4 = encodeCounterWithZeroNoEnabled(aut3)

    aut5 = Automaton()
    aut5.startStates = aut4.startStates[:]
    aut5.acceptStates = aut4.acceptStates[:]

    for trans in aut4.transitions:
        if (not Automaton.isEpsilonTrans(trans) and
            Automaton.getSymbol(trans) == SYMBOL_ENABLED_TIMEOUT):
            (src, _, tgt) = trans
            # for the special timeout symbol
            zeroState = tgt + "Y0"
            aut5.addTrans(transition = (src, zeroState))
            aut5.addTrans(zeroState, SYMBOL_ZERO, zeroState)
            aut5.addTrans(zeroState, SYMBOL_ZERO, tgt)
        else:
            # for other symbols
            aut5.addTrans(transition = trans)

    # autB = encodeCounterWithZero(aut)
    autB = encodeCounterWithZeroNoEnabled(aut)

    output = Automaton.autUnion(aut5, autB)
    output = output.singleStartState(FINAL_START_STATE)

    output.transitions = list(set(output.transitions)) # kill duplicates
    return output.removeUseless()


###############################################################################
def autPlay1ToFair(aut):
    '''autPlay1ToFair(aut) -> Automaton

Encodes fairness into aut for Player 1.
'''
    output = Automaton()
    output.startStates = aut.startStates[:]
    output.acceptStates = aut.acceptStates[:]

    # for trans in aut.transitions:
    #     if Automaton.isEpsilonTrans(trans):
    #         # epsilon transitions
    #         output.addTrans(transition = trans)
    #     elif (Automaton.getSymbol1(trans) in ENCODING_ALPHABET):
    #         # delimiters
    #         (src, symb1, symb2, tgt) = trans
    #         cntState = tgt + "_" + symb1 + "_" + symb2
    #         output.addTrans(transition = (src, cntState))
    #         output.addTransTransd(cntState, SYMBOL_ZERO, SYMBOL_ZERO, cntState)
    #         output.addTransTransd(cntState, SYMBOL_ONE, SYMBOL_ONE, cntState)
    #         output.addTransTransd(cntState, symb1, symb2, tgt)
    #     else:
    #         # other transitions
    #         output.addTrans(transition = trans)

    for trans in aut.transitions:
        if Automaton.isEpsilonTrans(trans):
            # epsilon transitions
            output.addTrans(transition = trans)
        elif (Automaton.getSymbol1(trans) in ENCODING_ALPHABET):
            # delimiters
            (src, symb1, symb2, tgt) = trans
            cntState = tgt + "_" + symb1 + "_" + symb2
            output.addTrans(transition = (src, cntState))
            if (symb1, symb2) == (SYMBOL_ENABLED, SYMBOL_ENABLED):
                output.addTransTransd(cntState, SYMBOL_ZERO, SYMBOL_ZERO, cntState)
                output.addTransTransd(cntState, SYMBOL_ONE, SYMBOL_ONE, cntState)
                output.addTransTransd(cntState, SYMBOL_ZERO, SYMBOL_ZERO, tgt)
            elif (symb1, symb2) == (SYMBOL_ENABLED, SYMBOL_CHOSEN):
                output.addTransTransd(cntState, SYMBOL_ZERO, SYMBOL_ONE, cntState)
                output.addTransTransd(cntState, SYMBOL_ONE, SYMBOL_ONE, cntState)
                output.addTransTransd(cntState, SYMBOL_ZERO, SYMBOL_ONE, tgt)
            else:
                raise Exception("Invalid delimiter symbol \'" + symb1 + "/" +
                    symb2 + "\' (only 'enabled' and 'chosen' are allowed)")
        else:
            # other transitions
            output.addTrans(transition = trans)


    output.transitions = list(set(output.transitions)) # kill duplicates
    return output


###############################################################################
def autPlay2ToFair(aut):
    '''autPlay2ToFair(aut) -> Automaton

Encodes fairness into aut for Player 2.
'''
    output = Automaton()
    output.startStates  = aut.startStates[:]
    output.acceptStates  = aut.acceptStates[:]

    for trans in aut.transitions:
        if Automaton.isEpsilonTrans(trans):
            output.addTrans(transition = trans)
        else:
            (src, symb1, symb2, tgt) = trans

            # if (symb1, symb2) == (SYMBOL_DISABLED, SYMBOL_DISABLED):
            #     disState = tgt + "_disXdis"
            #     output.addTrans(transition = (src, disState))
            #     output.addTransTransd(disState, SYMBOL_ZERO, SYMBOL_ZERO, disState)
            #     output.addTransTransd(disState, SYMBOL_ONE, SYMBOL_ONE, disState)
            #     output.addTransTransd(disState, SYMBOL_DISABLED, SYMBOL_DISABLED, tgt)
            # elif (symb1, symb2) == (SYMBOL_ENABLED, SYMBOL_ENABLED):
            #     oneState = tgt + "_enXenX1"
            #     zeroState = tgt + "_enXenX0"
            #     output.addTrans(transition = (src, oneState))
            #     output.addTransTransd(oneState, SYMBOL_ONE, SYMBOL_ONE, oneState)
            #     output.addTransTransd(oneState, SYMBOL_ONE, SYMBOL_ZERO, zeroState)
            #     output.addTransTransd(zeroState, SYMBOL_ZERO, SYMBOL_ZERO, zeroState)
            #     output.addTransTransd(zeroState, SYMBOL_ENABLED, SYMBOL_ENABLED, tgt)
            # elif (symb1, symb2) == (SYMBOL_DISABLED, SYMBOL_ENABLED):
            #     # NOTE: this case determines the particular notion of fairness, right?
            #     oneState = tgt + "_starXenX1"
            #     disEnState = tgt + "_disXen"
            #     output.addTrans(transition = (src, oneState))
            #     output.addTransTransd(oneState, SYMBOL_ZERO, SYMBOL_ONE, oneState)
            #     output.addTransTransd(oneState, SYMBOL_ONE, SYMBOL_ONE, oneState)
            #     output.addTransTransd(oneState, SYMBOL_ZERO, SYMBOL_ZERO, disEnState)
            #     output.addTransTransd(disEnState, SYMBOL_DISABLED, SYMBOL_ENABLED, tgt)
            # elif (symb1, symb2) == (SYMBOL_CHOSEN, SYMBOL_DISABLED):
            #     chDisState = tgt + "_chXdis"
            #     output.addTrans(transition = (src, chDisState))
            #     output.addTransTransd(chDisState, SYMBOL_ZERO, SYMBOL_ZERO, chDisState)
            #     output.addTransTransd(chDisState, SYMBOL_ONE, SYMBOL_ONE, chDisState)
            #     output.addTransTransd(chDisState, SYMBOL_CHOSEN, SYMBOL_DISABLED, tgt)
            # elif (symb1, symb2) == (SYMBOL_CHOSEN, SYMBOL_ENABLED):
            #     chEnOneState = tgt + "_chXenX1"
            #     chEnState = tgt + "_chXen"
            #     output.addTrans(transition = (src, chEnOneState))
            #     output.addTransTransd(chEnOneState, SYMBOL_ZERO, SYMBOL_ONE, chEnOneState)
            #     output.addTransTransd(chEnOneState, SYMBOL_ONE, SYMBOL_ONE, chEnOneState)
            #     output.addTransTransd(chEnOneState, SYMBOL_ZERO, SYMBOL_ZERO, chEnState)
            #     output.addTransTransd(chEnState, SYMBOL_CHOSEN, SYMBOL_ENABLED, tgt)
            # else:
            #     assert symb1 not in FAIR_ENCODING_ALPHABET
            #     assert symb2 not in FAIR_ENCODING_ALPHABET
            #
            #     output.addTrans(transition = trans)

            if (symb1, symb2) == (SYMBOL_ENABLED, SYMBOL_ENABLED):
                oneState = tgt + "_enXenX1"
                zeroState = tgt + "_enXenX0"
                output.addTrans(transition = (src, oneState))
                output.addTransTransd(oneState, SYMBOL_ONE, SYMBOL_ONE, oneState)
                output.addTransTransd(oneState, SYMBOL_ONE, SYMBOL_ZERO, zeroState)
                output.addTransTransd(zeroState, SYMBOL_ZERO, SYMBOL_ZERO, zeroState)
                output.addTransTransd(zeroState, SYMBOL_ZERO, SYMBOL_ZERO, tgt)
            elif (symb1, symb2) == (SYMBOL_CHOSEN, SYMBOL_ENABLED):
                chEnOneState = tgt + "_chXenX1"
                chEnState = tgt + "_chXen"
                output.addTrans(transition = (src, chEnOneState))
                output.addTransTransd(chEnOneState, SYMBOL_ONE, SYMBOL_ONE, chEnOneState)
                output.addTransTransd(chEnOneState, SYMBOL_ONE, SYMBOL_ZERO, tgt)
            else:
                assert symb1 not in FAIR_ENCODING_ALPHABET
                assert symb2 not in FAIR_ENCODING_ALPHABET

                output.addTrans(transition = trans)

    output.transitions = list(set(output.transitions)) # kill duplicates
    return output


###########################################
def enabledToFair(autEnabled):
    '''enabledToFair(autEnabled) -> Automaton

Encodes fairness into the Enabled automaton, thus obtaining the language of all
possible configurations with enabledness encoded.
'''
    result = Automaton()

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
    assert hasattr(problem, 'autPlay1')
    assert hasattr(problem, 'autPlay2')
    assert hasattr(problem, 'autEnabled')

    fairInit = autInitToFair(problem.autInit)
    fairFinal = autFinalToFair(problem.autFinal, problem.autEnabled)
    fairPlay1 = autPlay1ToFair(problem.autPlay1)
    fairPlay2 = autPlay2ToFair(problem.autPlay2)
    fairEnabled = enabledToFair(problem.autEnabled)

    with open(os.path.join(OUTPUT_DIR, "fair_init.dot"), "w") as text_file:
        text_file.write(fairInit.exportToDot())

    with open(os.path.join(OUTPUT_DIR, "fair_final.dot"), "w") as text_file:
        text_file.write(fairFinal.exportToDot())

    with open(os.path.join(OUTPUT_DIR, "fair_play1.dot"), "w") as text_file:
        text_file.write(fairPlay1.exportToDot())

    with open(os.path.join(OUTPUT_DIR, "fair_play2.dot"), "w") as text_file:
        text_file.write(fairPlay2.exportToDot())

    with open(os.path.join(OUTPUT_DIR, "fair_enabled.dot"), "w") as text_file:
        text_file.write(fairEnabled.exportToDot())

    outlines = []

    # output Init
    outlines = []
    outlines.append("I0 {\n")
    outlines.append(str(fairInit))
    outlines.append("}\n\n")

    CLOSED_UNDER_TRANSITIONS = "closedUnderTransitions"
    if CLOSED_UNDER_TRANSITIONS in problem.options:
        outlines.append(CLOSED_UNDER_TRANSITIONS + ";\n")
        problem.options.remove(CLOSED_UNDER_TRANSITIONS)

    # output all other automata
    for (name, aut) in [
            ("F", fairFinal),
            ("P1", fairPlay1),
            ("P2", fairPlay2),
            # SLRD needs to be modified to allow this input
            # ("FairEnabled", fairEnabled),
        ]:
        outlines.append(name + " {\n")
        outlines.append(str(aut))
        outlines.append("}\n\n")

    for option in problem.options:
        outlines.append(option + ";\n")

    for line in outlines:
        print(line)
