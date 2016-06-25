#!/usr/bin/env python3

import argparse
import os
import sys

from problem import Problem
from automaton import Automaton
from parser import Parser
from constants import *

###############################################################################
def parseOptions():
    '''parseOptions() -> options

Parses command-line options and returns them as an object with the following
attributes:

  filename - the name of the input file as a String
'''
    parser = argparse.ArgumentParser(description="Encodes (TODO: which?) fairness"
        " into a two-player transition system (with enabledness encoded beforehand)")
    parser.add_argument("orderfile", metavar="order")
    parser.add_argument("fairproblem", metavar="problem")
    args = parser.parse_args()
    return args


###############################################################################
if __name__ == '__main__':
    options = parseOptions()
    # input
    inlinesOrder = [line.strip() for line in open(options.orderfile)]
    it = iter(inlinesOrder)

    # parse the verification problem
    order = Parser.parseProblem(it)

    ##############################################
    def transInverter(trans):                    #
        if len(trans) == 2:                      #
            return trans                         #
        elif len(trans) == 4:                    #
            return (                             #
                trans[0],                        #
                trans[2],                        #
                trans[1],                        #
                trans[3]                         #
            )                                    #
        else:                                    #
            raise Exception("Internal error")    #
    ##############################################


    invertedOrder = order.misc["T"].mapTrans(transInverter)
    print(invertedOrder)

    inlinesProblem = [line.strip() for line in open(options.fairproblem)]
    it = iter(inlinesProblem)
    problem = Parser.parseProblem(it)

    player2 = problem.autPlay2

    ####################################
    def chosenToEn(string):            #
        if string == SYMBOL_CHOSEN:    #
            return SYMBOL_ENABLED      #
        else:                          #
            return string              #
    ####################################


    ##############################################
    def chosenElim(trans):                       #
        if len(trans) == 2:                      #
            return trans                         #
        elif len(trans) == 4:                    #
            return (                             #
                trans[0],                        #
                chosenToEn(trans[1]),            #
                chosenToEn(trans[2]),            #
                trans[3]                         #
            )                                    #
        else:                                    #
            raise Exception("Internal error")    #
    ##############################################

    player2 = player2.mapTrans(chosenElim)
    print(player2)

    invertedOrder = invertedOrder.packTransdToAutomaton()
    invertedOrder = invertedOrder.removeEpsilon()
    player2 = player2.packTransdToAutomaton()
    player2 = player2.removeEpsilon()

    isected = Automaton.intersection(invertedOrder, player2)
    isected = isected.unpackAutomatonToTransd()
    print(isected)

    with open(os.path.join(OUTPUT_DIR, "order_decoded.dot"), "w") as text_file:
        text_file.write(isected.exportToDot())
