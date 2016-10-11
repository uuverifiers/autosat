#!/usr/bin/env python3

# the Cartesian product
from itertools import product

import sys
import copy

##############################################################################
class Automaton:
    '''Represents an automaton'''

    ###########################################
    def __init__(self):
        '''__init__(self)

Constructor.
'''
        self.startStates = []
        self.acceptStates = []
        self.transitions = []

    ###########################################
    def clearAcceptStates(self):
        '''clearAcceptStates() -> Automaton

Clear accepting states of an automaton.
'''
        result = Automaton()
        result.startStates = self.startStates[:]
        result.transitions = self.transitions[:]
        result.acceptStates = []

        return result


    ###########################################
    def clearStartStates(self):
        '''clearStartStates() -> Automaton

Clear starting states of an automaton.
'''
        result = Automaton()
        result.startStates = []
        result.transitions = self.transitions[:]
        result.acceptStates = self.acceptStates[:]

        return result


    ###########################################
    def getTgtState(trans):
        '''getTgtState(trans) -> State

Retrieve the target state of a transition.
'''
        if len(trans) == 2:
            return trans[1]
        elif len(trans) == 3:
            return trans[2]
        elif len(trans) == 4:
            return trans[3]
        else:
            raise Exception("Internal error")


    ###########################################
    def getSrcState(trans):
        '''getSrcState(trans) -> State

Retrieve the source state of a transition.
'''
        return trans[0]


    ###########################################
    def getSymbol(trans):
        '''getSymbol(trans) -> Symbol

Get the symbol in an automaton transition.
'''
        assert len(trans) == 3
        return trans[1]


    ###########################################
    def getSymbol1(trans):
        '''getSymbol1(trans) -> Symbol

Get the first symbol in a transducer transition.
'''
        assert len(trans) == 4
        return trans[1]


    ###########################################
    def getSymbol2(trans):
        '''getSymbol2(trans) -> Symbol

Get the second symbol in a transducer transition.
'''
        assert len(trans) == 4
        return trans[2]


    ###########################################
    def isEpsilonTrans(trans):
        '''isEpsilonTrans(trans) -> Bool

Determine an epsilon transition.
    '''
        return len(trans) == 2


    ###########################################
    def makeEpsTrans(src, tgt):
        '''makeTrans(src, tgt) -> transition

Makes an epsilon transition.
'''
        return (src, tgt)


    ###########################################
    def makeTrans(src, symbol, tgt):
        '''makeTrans(src, symbol, tgt) -> transition

Makes a transition.
'''
        return (src, symbol, tgt)


    ###########################################
    def makeTransTransd(src, symbol1, symbol2, tgt):
        '''makeTrans(src, symbol1, symbol2, tgt) -> transition

Makes a transducer transition.
'''
        return (src, symbol1, symbol2, tgt)


    ###########################################
    def singleStartState(self, name):
        '''singleStartState(self, name) -> Automaton

Make a single start state called name, using epsilon transitions to go to the
original start states.
'''
        result = self
        startingStates = result.startStates[:]

        result.clearStartStates()
        result.startStates = [name]
        for state in startingStates:
            result.addTrans(transition = Automaton.makeEpsTrans(name, state))

        return result


    ###########################################
    def postTrans(self, state):
        '''postTrans(self, state) -> [transitions]

A list of transitions leaving state.
'''
        return list(filter(lambda trans: Automaton.getSrcState(trans) == state,
            self.transitions))

    ###########################################
    def preTrans(self, state):
        '''preTrans(self, state) -> [transitions]

A list of transitions entering state.
'''
        return list(filter(lambda trans: Automaton.getTgtState(trans) == state,
            self.transitions))


    ###########################################
    def addTrans(self, src = None, symb = None, tgt = None, transition = None):
        '''addTrans(self, src, symb, tgt)

Add a new transition src --symb--> tgt.
'''
        if transition is None:
            assert (src is not None) and (symb is not None) and (tgt is not None)
            self.transitions.append(Automaton.makeTrans(src, symb, tgt))
        else:
            assert (src is None) and (symb is None) and (tgt is None)
            self.transitions.append(transition)


    ###########################################
    def addTransTransd(self, src = None, symb1 = None, symb2 = None, tgt = None,
        transition = None):
        '''addTrans(self, src, symb1, symb2, tgt)

Add a new transition src --symb1/symb2--> tgt.
'''
        if transition is None:
            assert ((src is not None) and (symb1 is not None) and (symb2 is not None) and
                (tgt is not None))
            self.transitions.append(Automaton.makeTransTransd(src, symb1, symb2, tgt))
        else:
            assert (src is None) and (symb1 is None) and (symb2 is None) and (tgt is None)
            self.transitions.append(transition)


    ###########################################
    def addTransitions(self, transitions):
        '''addTransitions(transitions)

Adds a set of transitions into the automaton.
'''
        self.transitions.extend(transitions)


    ###########################################
    def appendAut(self, aut):
        '''appendAut(self, aut) -> Automaton

Append aut to the automaton. That is, keep the original starting state and add all transitions and accepting states from aut.
'''
        result = self
        result.transitions += aut.transitions
        result.acceptStates += aut.acceptStates

        return result


    ###########################################
    def flattenProductStateNames(self):
        '''flattenProductStateNames(self) -> Automaton

Flatten names of states of a product automaton.
'''
        def flattenProdState(prodState):
            return prodState[0] + "X" + prodState[1]

        # result = Automaton()
        # result.startStates = list(map(flattenProdState, self.startStates))
        # result.acceptStates = list(map(flattenProdState, self.acceptStates))
        #
        # # hey, without list(..), I get only an ierator
        # result.transitions = list(map(lambda trans: (
        #         flattenProdState(trans[0]), # src state
        #         trans[1],                   # symbol
        #         flattenProdState(trans[2])  # tgt state
        #     ), self.transitions))

        result = self.renameStates(flattenProdState)
        return result


    ###########################################
    def generalIntersection(autLhs, autRhs, funSymMatch, funDetSymb):
        '''generalIntersection(autLhs, autRhs, funSymMatch, funDetSymb) -> Automaton

Performs a general intersection of autLhs and autRhs, such that symbols are
matched using the funSymMatch predicate.  The symbol of a new transition is then
determined using funDetSymb.  Generates only reachable transitions and states.
'''
        print('generalIntersection: warning: ignoring epsilon transitions', file=sys.stderr)

        result = Automaton()
        newStartStates = list(product(autLhs.startStates, autRhs.startStates))
        result.startStates = newStartStates[:]
        result.acceptStates = []
        allAcceptStates = list(product(autLhs.acceptStates, autRhs.acceptStates))

        worklist = newStartStates[:]
        processed = set(newStartStates)

        while not (len(worklist) == 0):
            (lhs, rhs) = worklist.pop(0)

            for (_, symbLhs, tgtStateLhs) in autLhs.postTrans(lhs):
                for (_, symbRhs, tgtStateRhs) in autRhs.postTrans(rhs):
                    # go over all transitions from lhs and rhs
                    if (funSymMatch(symbLhs, symbRhs)):
                        # if the symbols match
                        newState = (tgtStateLhs, tgtStateRhs)
                        if newState not in processed:
                            worklist.append(newState)
                            processed.add(newState)
                            if newState in allAcceptStates:
                                result.acceptStates.append(newState)

                        result.addTrans(transition = Automaton.makeTrans(
                                (lhs, rhs),
                                funDetSymb(symbLhs, symbRhs),
                                newState
                            ))

        return result.flattenProductStateNames()


    ###########################################
    def intersection(autLhs, autRhs):
        '''intersection(self, autLhs, autRhs) -> Automaton

Performs a standard intersection of two automata using a product construction.
'''
        return Automaton.generalIntersection(autLhs, autRhs,
            lambda x, y: x == y,
            lambda x, y: x)


    ###########################################
    def renameStates(self, funcRem):
        '''renameStates(self, funcRem) -> Automaton

Rename states of the automaton using funcRem.
'''
        result = Automaton()

        result.startStates = list(map(funcRem, self.startStates))
        result.acceptStates = list(map(funcRem, self.acceptStates))

        ##############################################
        def sodomizeTrans(trans):                    #
            if len(trans) == 2:                      #
                return (                             #
                    funcRem(trans[0]),               #
                    funcRem(trans[1])                #
                )                                    #
            if len(trans) == 3:                      #
                return (                             #
                    funcRem(trans[0]),               #
                    trans[1],                        #
                    funcRem(trans[2])                #
                )                                    #
            elif len(trans) == 4:                    #
                return (                             #
                    funcRem(trans[0]),               #
                    trans[1],                        #
                    trans[2],                        #
                    funcRem(trans[3])                #
                )                                    #
            else:                                    #
                raise Exception("Internal error")    #
        ##############################################

        result.transitions = list(map(sodomizeTrans, self.transitions))

        return result


    ###########################################
    def removeUseless(self):
        '''removeUseless(self) -> Automaton

Removes useless (forward and backward) states from the automaton.
'''
        result = Automaton()
        result.startStates = []
        result.acceptStates = []

        # keep only forward reachable states
        worklist = self.startStates[:]
        processed = set(self.startStates)

        while not (len(worklist) == 0):
            state = worklist.pop(0)

            if state in self.acceptStates:
                result.acceptStates.append(state)

            for trans in self.postTrans(state):
                # go over all transitions _from_ state
                tgtState = Automaton.getTgtState(trans)

                if tgtState not in processed:
                    worklist.append(tgtState)
                    processed.add(tgtState)

        # keep only backward reachable states
        worklist = result.acceptStates[:]
        bwdProcessed = set(result.acceptStates)

        while not (len(worklist) == 0):
            state = worklist.pop(0)

            if state in self.startStates:
                result.startStates.append(state)

            for trans in self.preTrans(state):
                # go over all transitions _to_ state
                srcState = Automaton.getSrcState(trans)

                if srcState in processed and srcState not in bwdProcessed:
                    # if srcState is forward-useful and not backward-processed yet
                    worklist.append(srcState)
                    bwdProcessed.add(srcState)

                result.addTrans(transition = trans)

        return result


    ###########################################
    def removeEpsilon(self):
        '''removeEpsilon(self) -> Automaton

Removes epsilon transitions from the automaton.
'''
        result = copy.deepcopy(self)

        eClosure = {}
        eTransitions = list(filter(lambda trans: Automaton.isEpsilonTrans(trans),
            result.transitions))

        while len(eTransitions) > 0:
            trans = eTransitions.pop()
            result.transitions.remove(trans)
            (src, tgt) = (Automaton.getSrcState(trans), Automaton.getTgtState(trans))

            if tgt in result.acceptStates:
                result.acceptStates.append(src)

            for nextTrans in result.postTrans(tgt):
                if Automaton.isEpsilonTrans(nextTrans):
                    newTrans = (src, Automaton.getTgtState(nextTrans))
                    result.addTrans(transition = newTrans)
                    eTransitions.append(newTrans)
                else:
                    newTrans = (src, Automaton.getSymbol(nextTrans),
                        Automaton.getTgtState(nextTrans))
                    result.addTrans(transition = newTrans)

        return result


    ###########################################
    def toTransducer(self):
        '''toTransducer(self) -> Automaton

Transforms an automaton into a transducer encoding the identity relation.
'''
        result = self.mapTrans(lambda trans:
            (
                trans[0],
                trans[1],
                trans[1],
                trans[2]
            ))

        return result


    ###########################################
    def packTransdToAutomaton(self):
        '''toAutomaton(self) -> Automaton

Transforms a transducer into an automaton by packing the two symbols inside a
transition to a pair.
'''
        ##############################################
        def packer(trans):                           #
            if len(trans) == 2:                      #
                return trans                         #
            elif len(trans) == 4:                    #
                return (                             #
                    trans[0],                        #
                    (trans[1], trans[2]),            #
                    trans[3]                         #
                )                                    #
            else:                                    #
                raise Exception("Internal error")    #
        ##############################################

        result = self.mapTrans(packer)
        return result


    ###########################################
    def unpackAutomatonToTransd(self):
        '''toAutomaton(self) -> Automaton

Transforms an automaton with pairs on edges into a transducer by unpacking the pair.
'''
        ##############################################
        def unpacker(trans):                         #
            if len(trans) == 2:                      #
                return trans                         #
            elif len(trans) == 3:                    #
                return (                             #
                    trans[0],                        #
                    trans[1][0],                     #
                    trans[1][1],                     #
                    trans[2]                         #
                )                                    #
            else:                                    #
                raise Exception("Internal error")    #
        ##############################################

        result = self.mapTrans(unpacker)
        return result


    ###########################################
    def filterTrans(self, pred):
        '''filterTrans(self, pred) -> Automaton

Filters transitions of automaton with respect to the predicate pred.
'''
        result = Automaton()
        result.startStates = self.startStates[:]
        result.acceptStates = self.acceptStates[:]
        result.transitions = list(filter(pred, self.transitions))
        return result

    ###########################################
    def mapTrans(self, mapper):
        '''mapTrans(self, mapper) -> Automaton

Maps transitions w.r.t. mapper.
'''
        result = Automaton()
        result.startStates = self.startStates[:]
        result.acceptStates = self.acceptStates[:]
        result.transitions = list(map(mapper, self.transitions))
        return result


    ###########################################
    def transdAutAutProd(transd, aut1, funMatch1, funDet1, aut2, funMatch2, funDet2):
        '''transdAutAutProd(transd, aut1, funMatch1, funDet1, aut2, funMatch2, funDet2) -> Automaton

Peform a ternarny product of a transducer transd with automata aut1 and aut2,
such that aut1 matches to the first tape of trands and aut2 matches on the
second tape of transd.  funMatch1 and funMatch2 are used to determine whether
the symbols of transd and aut1 and aut2 respectively match.  funDet1 and
funDet2 are used to determine the symbol in the result.
'''
        result = Automaton()

        result.startStates = list(product(
            transd.startStates,
            aut1.startStates,
            aut2.startStates))

        result.acceptStates = []
        allAcceptStates = list(product(
            transd.acceptStates,
            aut1.acceptStates,
            aut2.acceptStates))

        worklist = result.startStates[:]
        processed = set(result.startStates)

        while not (len(worklist) == 0):
            (lhs, st1, st2) = worklist.pop(0)

            for lhsTrans in transd.postTrans(lhs):
                if Automaton.isEpsilonTrans(lhsTrans):
                    # for epsilon transitions
                    newState = (Automaton.getTgtState(lhsTrans), st1, st2)
                    if newState not in processed:
                        worklist.append(newState)
                        processed.add(newState)
                        if newState in allAcceptStates:
                            result.acceptStates.append(newState)

                    # insert an epsilon transition
                    newTrans = (
                            (lhs, st1, st2),
                            newState
                        )
                    result.addTrans(transition = newTrans)
                else:
                    # for ordinary transitions
                    (_, symbLhs1, symbLhs2, tgtStateLhs) = lhsTrans

                    for (_, symbAut1, tgtState1) in aut1.postTrans(st1):
                        if (funMatch1(symbLhs1, symbAut1)):
                            for (_, symbAut2, tgtState2) in aut2.postTrans(st2):
                                if (funMatch2(symbLhs2, symbAut2)):
                                    # go over all matching transitions from
                                    # lhs, st1, and st2

                                    # target product state
                                    newState = (tgtStateLhs, tgtState1, tgtState2)

                                    if newState not in processed:
                                        worklist.append(newState)
                                        processed.add(newState)
                                        if newState in allAcceptStates:
                                            result.acceptStates.append(newState)

                                    # insert an ordinary transition
                                    newTrans = (
                                            (lhs, st1, st2),
                                            funDet1(symbLhs1, symbAut1),
                                            funDet2(symbLhs2, symbAut2),
                                            newState
                                        )
                                    result.addTrans(transition = newTrans)


        ################################################################
        def stateSodomizer(stTuple):                                   #
            assert len(stTuple) == 3                                   #
            return stTuple[0] + "X" + stTuple[1] + "X" + stTuple[2]    #
        ################################################################

        result = result.renameStates(stateSodomizer)

        return result


    ###########################################
    def union(self, rhs):
        '''union(self, rhs) -> Automaton

Unites the automaton with automaton rhs.
'''
        result = Automaton()

        print('union: check whether the automata are disjoint first!', file=sys.stderr)
        result.startStates = self.startStates + rhs.startStates
        result.acceptStates = self.acceptStates + rhs.acceptStates
        result.transitions = self.transitions + rhs.transitions

        return result


    ###########################################
    def autUnion(lhs, rhs):
        '''autUnion(lhs, rhs) -> Automaton

Unites the automaton lhs with rhs.
'''
        return lhs.union(rhs)


    ###########################################
    def exportToDot(self):
        '''exportToDot(self) -> String

Export the automaton to the dot format.
'''
        result = ""
        result += "digraph {\n"
        result += "\"__init\" [shape=point];\n"

        for stState in self.startStates:
            result += "\"__init\" -> \"" + stState + "\";\n"

        for accState in self.acceptStates:
            result += "\"" + accState + "\" [shape=doublecircle];\n"

        for trans in self.transitions:
            result += "  \"" + Automaton.getSrcState(trans)
            result += "\" -> \"" + Automaton.getTgtState(trans)
            result += "\" [label=\""

            if len(trans) == 2:
                result += 'epsilon'
            elif len(trans) == 3:
                result += Automaton.getSymbol(trans)
            elif len(trans) == 4:
                result += Automaton.getSymbol1(trans)
                result += "/"
                result += Automaton.getSymbol2(trans)
            else:
                raise Exception("Internal error")

            result += "\"];\n"

        result += "}\n"
        return result


    ###########################################
    def __str__(self):
        '''__str__(self) -> String

Transforms automaton into a string.
'''
        output = ""
        output += "init: "
        first = True
        for state in self.startStates:
            if not first:
                output += ", "

            output += state
            first = False

        output += ";\n"

        for trans in self.transitions:
            if len(trans) == 2:
                output += trans[0] + " -> " + trans[1] + ";\n"
            elif len(trans) == 3:
                output += trans[0] + " -> " + trans[2] + " " + trans[1] + ";\n"
            elif len(trans) == 4:
                output += trans[0] + " -> " + trans[3] + " " + trans[1] + "/" + trans[2] + ";\n"
            else:
                raise Exception("Internal error")

        output += "accepting: "
        first = True
        for state in self.acceptStates:
            if not first:
                output += ", "

            output += state
            first = False

        output += ";\n"

        return output


###############################################################################
if __name__ == '__main__':
    print("Library module")
