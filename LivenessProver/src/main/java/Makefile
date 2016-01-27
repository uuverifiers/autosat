JAVAC = javac
JAVAC_FLAGS = -sourcepath .

JAVA = java
JAVA_FLAGS =

CUP = java_cup.Main
CUPFLAGS = -nopositions -expect 100

JLEX = JLex.Main

LATEX = latex
DVIPS = dvips

all: test grammar.ps

test: absyn grammar/Yylex.class grammar/PrettyPrinter.class grammar/Test.class grammar/ComposVisitor.class grammar/AbstractVisitor.class grammar/FoldVisitor.class grammar/AllVisitor.class grammar/parser.class grammar/sym.class grammar/Test.class

.PHONY: absyn

%.class: %.java
	${JAVAC} ${JAVAC_FLAGS} $^

absyn: grammar/Absyn/ModelRule.java grammar/Absyn/Model.java grammar/Absyn/TransducerRule.java grammar/Absyn/Transducer.java grammar/Absyn/InitRule.java grammar/Absyn/TransducerInitialState.java grammar/Absyn/TransitionRule.java grammar/Absyn/FulTransition.java grammar/Absyn/EmptyTransition.java grammar/Absyn/LoopingTransition.java grammar/Absyn/AcceptingRule.java grammar/Absyn/TransducerAccepting.java grammar/Absyn/AutomatonRule.java grammar/Absyn/Automaton.java grammar/Absyn/AutomataInitRule.java grammar/Absyn/AutomataInitialState.java grammar/Absyn/AutomataTransitionRule.java grammar/Absyn/AutomataTransition.java grammar/Absyn/AutomataEmptyTransition.java grammar/Absyn/AutomataAcceptingsRule.java grammar/Absyn/AutomataAcceptings.java grammar/Absyn/VerifierOption.java grammar/Absyn/NumOfStatesTransducerGuess.java grammar/Absyn/NumOfStatesAutomatonGuess.java grammar/Absyn/NumOfInitStatesAutomatonGuess.java grammar/Absyn/SymmetryOptions.java grammar/Absyn/ExplicitChecks.java grammar/Absyn/UseRankingFunctions.java grammar/Absyn/ListVerifierOption.java grammar/Absyn/SymmetryOption.java grammar/Absyn/RotationSymmetry.java grammar/Absyn/ListSymmetryOption.java grammar/Absyn/MaybeClosed.java grammar/Absyn/ClosedInit.java grammar/Absyn/NotClosedInit.java grammar/Absyn/Name.java grammar/Absyn/NumberName.java grammar/Absyn/LiteralName.java grammar/Absyn/ListTransitionRule.java grammar/Absyn/ListAutomataTransitionRule.java grammar/Absyn/ListName.java
	${JAVAC} ${JAVAC_FLAGS} $^

grammar/Yylex.java: grammar/Yylex
	${JAVA} ${JAVA_FLAGS} ${JLEX} grammar/Yylex

grammar/sym.java grammar/parser.java: grammar/grammar.cup
	${JAVA} ${JAVA_FLAGS} ${CUP} ${CUPFLAGS} grammar/grammar.cup
	mv sym.java parser.java grammar/

grammar/Yylex.class: grammar/Yylex.java grammar/sym.java

grammar/sym.class: grammar/sym.java

grammar/parser.class: grammar/parser.java grammar/sym.java

grammar/PrettyPrinter.class: grammar/PrettyPrinter.java

grammar.dvi: grammar.tex
	${LATEX} grammar.tex

grammar.ps: grammar.dvi
	${DVIPS} grammar.dvi -o grammar.ps

clean:
	 rm -f grammar/Absyn/*.class grammar/*.class
	 rm -f .dvi grammar.aux grammar.log grammar.ps  *.class

distclean: vclean

vclean:
	 rm -f grammar/Absyn/ModelRule.java grammar/Absyn/Model.java grammar/Absyn/TransducerRule.java grammar/Absyn/Transducer.java grammar/Absyn/InitRule.java grammar/Absyn/TransducerInitialState.java grammar/Absyn/TransitionRule.java grammar/Absyn/FulTransition.java grammar/Absyn/EmptyTransition.java grammar/Absyn/LoopingTransition.java grammar/Absyn/AcceptingRule.java grammar/Absyn/TransducerAccepting.java grammar/Absyn/AutomatonRule.java grammar/Absyn/Automaton.java grammar/Absyn/AutomataInitRule.java grammar/Absyn/AutomataInitialState.java grammar/Absyn/AutomataTransitionRule.java grammar/Absyn/AutomataTransition.java grammar/Absyn/AutomataEmptyTransition.java grammar/Absyn/AutomataAcceptingsRule.java grammar/Absyn/AutomataAcceptings.java grammar/Absyn/VerifierOption.java grammar/Absyn/NumOfStatesTransducerGuess.java grammar/Absyn/NumOfStatesAutomatonGuess.java grammar/Absyn/NumOfInitStatesAutomatonGuess.java grammar/Absyn/SymmetryOptions.java grammar/Absyn/ExplicitChecks.java grammar/Absyn/UseRankingFunctions.java grammar/Absyn/ListVerifierOption.java grammar/Absyn/SymmetryOption.java grammar/Absyn/RotationSymmetry.java grammar/Absyn/ListSymmetryOption.java grammar/Absyn/MaybeClosed.java grammar/Absyn/ClosedInit.java grammar/Absyn/NotClosedInit.java grammar/Absyn/Name.java grammar/Absyn/NumberName.java grammar/Absyn/LiteralName.java grammar/Absyn/ListTransitionRule.java grammar/Absyn/ListAutomataTransitionRule.java grammar/Absyn/ListName.java grammar/Absyn/ModelRule.class grammar/Absyn/Model.class grammar/Absyn/TransducerRule.class grammar/Absyn/Transducer.class grammar/Absyn/InitRule.class grammar/Absyn/TransducerInitialState.class grammar/Absyn/TransitionRule.class grammar/Absyn/FulTransition.class grammar/Absyn/EmptyTransition.class grammar/Absyn/LoopingTransition.class grammar/Absyn/AcceptingRule.class grammar/Absyn/TransducerAccepting.class grammar/Absyn/AutomatonRule.class grammar/Absyn/Automaton.class grammar/Absyn/AutomataInitRule.class grammar/Absyn/AutomataInitialState.class grammar/Absyn/AutomataTransitionRule.class grammar/Absyn/AutomataTransition.class grammar/Absyn/AutomataEmptyTransition.class grammar/Absyn/AutomataAcceptingsRule.class grammar/Absyn/AutomataAcceptings.class grammar/Absyn/VerifierOption.class grammar/Absyn/NumOfStatesTransducerGuess.class grammar/Absyn/NumOfStatesAutomatonGuess.class grammar/Absyn/NumOfInitStatesAutomatonGuess.class grammar/Absyn/SymmetryOptions.class grammar/Absyn/ExplicitChecks.class grammar/Absyn/UseRankingFunctions.class grammar/Absyn/ListVerifierOption.class grammar/Absyn/SymmetryOption.class grammar/Absyn/RotationSymmetry.class grammar/Absyn/ListSymmetryOption.class grammar/Absyn/MaybeClosed.class grammar/Absyn/ClosedInit.class grammar/Absyn/NotClosedInit.class grammar/Absyn/Name.class grammar/Absyn/NumberName.class grammar/Absyn/LiteralName.class grammar/Absyn/ListTransitionRule.class grammar/Absyn/ListAutomataTransitionRule.class grammar/Absyn/ListName.class
	 rm -f grammar/Absyn/*.class
	 rmdir grammar/Absyn/
	 rm -f grammar.tex grammar.dvi grammar.aux grammar.log grammar.ps 
	 rm -f grammar/Yylex grammar/grammar.cup grammar/Yylex.java grammar/VisitSkel.java grammar/ComposVisitor.java grammar/AbstractVisitor.java grammar/FoldVisitor.java grammar/AllVisitor.java grammar/PrettyPrinter.java grammar/Skeleton.java grammar/Test.java grammar/sym.java grammar/parser.java grammar/*.class
	 rm -f Makefile
	 rmdir -p grammar/

