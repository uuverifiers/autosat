#!/usr/bin/env python3

# the Cartesian product
from itertools import product

##############################################################################
class Automaton:
    '''Represents an automaton'''

    ###########################################
    def __init__(self):
        '''__init__(self)

Constructor.
'''
        self.startState = None
        self.acceptStates = []
        self.transitions = []
        self.epsTransitions = []

    ###########################################
    def clearAcceptStates(self):
        '''clearAcceptStates() -> Automaton

Clear accepting states of an automaton.
'''
        result = Automaton()
        result.startState = self.startState
        result.transitions = self.transitions[:]
        result.acceptStates = []

        return result


    ###########################################
    def getTgtState(trans):
        '''getTgtState(trans) -> State

Retrieve the target state of a transition.
'''
        if len(trans) == 3:
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
            self.transitions.append((src, symb, tgt))
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
            self.transitions.append((src, symb1, symb2, tgt))
        else:
            assert (src is None) and (symb1 is None) and (symb2 is None) and (tgt is None)
            self.transitions.append(transition)


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
            return prodState[0] + "_" + prodState[1]

        result = Automaton()
        result.startState = flattenProdState(self.startState)
        result.acceptStates = list(map(lambda state: flattenProdState(state),
            self.acceptStates))

        # hey, without list(..), I get only an ierator
        result.transitions = list(map(lambda trans: (
                flattenProdState(trans[0]), # src state
                trans[1],                   # symbol
                flattenProdState(trans[2])  # tgt state
            ), self.transitions))

        return result


    ###########################################
    def generalIntersection(autLhs, autRhs, funSymMatch, funDetSymb):
        '''generalIntersection(autLhs, autRhs, funSymMatch, funDetSymb) -> Automaton

Performs a general intersction of autLhs and autRhs, such that symbols are
matched using the funSymMatch predicate.  The symbol of a new transition is then
determined using funDetSymb.  Generates only reachable transitions and states.
'''

        print('generalIntersection: warning: ignoring epsilon transitions')

        result = Automaton()
        newStartState = (autLhs.startState, autRhs.startState)
        result.startState = newStartState
        result.acceptStates = []
        allAcceptStates = product(autLhs.acceptStates, autRhs.acceptStates)

        worklist = [newStartState]
        processed = {newStartState}

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

                        result.addTrans((lhs, rhs), funDetSymb(symbLhs,
                            symbRhs), newState)

        return result.flattenProductStateNames()


    ###########################################
    def renameStates(self, funcRem):
        '''renameStates(self, funcRem) -> Automaton

Rename states of the automaton using funcRem.
'''
        result = Automaton()

        result.startState = funcRem(self.startState)
        result.acceptStates = list(map(funcRem, self.acceptStates))

        ##############################################
        def sodomizeTrans(trans):                    #
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
        result.startState = self.startState
        result.acceptStates = []

        # keep only forward reachable states
        worklist = [self.startState]
        processed = {self.startState}

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
    def toTransducer(self):
        '''toTransducer(self) -> Automaton

Transforms an automaton into a transducer encoding the identity relation.
'''
        result = Automaton()
        result.startState = self.startState
        result.acceptStates = self.acceptStates[:]
        result.transitions = list(map(lambda trans:
            (
                trans[0],
                trans[1],
                trans[1],
                trans[2]
            ),
            self.transitions))

        return result


    ###########################################
    def filterTrans(self, pred):
        '''filterTrans(self, pred) -> Automaton

Filters transitions of automaton with respect to the predicate pred.
'''
        result = Automaton()
        result.startState = self.startState
        result.acceptStates = self.acceptStates[:]
        result.transitions = list(filter(pred, self.transitions))
        return result


    ###########################################
    def exportToDot(self):
        '''exportToDot(self) -> String

Export the automaton to the dot format.
'''
        result = ""
        result += "digraph {\n"
        result += "\"__init\" [shape=point];\n"
        result += "\"__init\" -> \"" + self.startState + "\";\n"

        for accState in self.acceptStates:
            result += "\"" + accState + "\" [shape=doublecircle];\n"

        for trans in self.transitions:
            result += "  \"" + Automaton.getSrcState(trans)
            result += "\" -> \"" + Automaton.getTgtState(trans)
            result += "\" [label=\""

            if len(trans) == 3:
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
        output += "init: " + self.startState + ";\n"

        for trans in self.epsTransitions:
            output += trans + "\n"

        for trans in self.transitions:
            if len(trans) == 3:
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
