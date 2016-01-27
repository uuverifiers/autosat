JAVAC = javac
JAVAC_FLAGS = -sourcepath .

JAVA = java
JAVA_FLAGS =

CUP = java_cup.Main
CUPFLAGS = -nopositions -expect 100

JLEX = JLex.Main

all: test

test: absyn grammar/Yylex.class grammar/PrettyPrinter.class grammar/Test.class grammar/ComposVisitor.class grammar/AbstractVisitor.class grammar/FoldVisitor.class grammar/AllVisitor.class grammar/parser.class grammar/sym.class grammar/Test.class

.PHONY: absyn

%.class: %.java
	${JAVAC} ${JAVAC_FLAGS} $^

absyn: grammar/Absyn/ModelRule.java grammar/Absyn/Model.java grammar/Absyn/Statement.java grammar/Absyn/NumberOfStatesGuess.java grammar/Absyn/NumberOfLetters.java grammar/Absyn/NumberOfStates.java grammar/Absyn/InitialState.java grammar/Absyn/AcceptingStates.java grammar/Absyn/Transitions.java grammar/Absyn/SymmetryPairs.java grammar/Absyn/SymmetryInstances.java grammar/Absyn/FiniteOutput.java grammar/Absyn/ImpossiblePairs.java grammar/Absyn/ValidConfiguration.java grammar/Absyn/TurnOffConditions.java grammar/Absyn/TransitionRule.java grammar/Absyn/Transition.java grammar/Absyn/SymmetryPairRule.java grammar/Absyn/SymmetryPair.java grammar/Absyn/SymInstance.java grammar/Absyn/SymmetryInstance.java grammar/Absyn/GeneratorRule.java grammar/Absyn/Generator.java grammar/Absyn/ImpossiblePairRule.java grammar/Absyn/ImpossiblePair.java grammar/Absyn/AutomataRule.java grammar/Absyn/Automata.java grammar/Absyn/AutomataTransitionRule.java grammar/Absyn/AutomataTransition.java grammar/Absyn/AutomataAcceptingsRule.java grammar/Absyn/AutomataAcceptings.java grammar/Absyn/StateRule.java grammar/Absyn/AutomataState.java grammar/Absyn/CheckingConditionRule.java grammar/Absyn/ParikhCondition.java grammar/Absyn/AutomorphismCondition.java grammar/Absyn/PermutativeCondition.java grammar/Absyn/InjectiveOutputCondition.java grammar/Absyn/InjectiveInputCondition.java grammar/Absyn/CopycatCondition.java grammar/Absyn/OutputUniversalCondition.java grammar/Absyn/InputUniversalCondition.java grammar/Absyn/WordRule.java grammar/Absyn/Word.java grammar/Absyn/NumberSpaceRule.java grammar/Absyn/NumberSpace.java grammar/Absyn/Numbers.java grammar/Absyn/Sequence.java grammar/Absyn/NumberCommaRule.java grammar/Absyn/NumberComma.java grammar/Absyn/ListStatement.java grammar/Absyn/ListSymmetryPairRule.java grammar/Absyn/ListTransitionRule.java grammar/Absyn/ListSymInstance.java grammar/Absyn/ListGeneratorRule.java grammar/Absyn/ListImpossiblePairRule.java grammar/Absyn/ListAutomataTransitionRule.java grammar/Absyn/ListStateRule.java grammar/Absyn/ListCheckingConditionRule.java grammar/Absyn/ListNumberSpaceRule.java grammar/Absyn/ListNumberCommaRule.java
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

clean:
	rm -f grammar/Absyn/*.class grammar/*.class
	rm -f .dvi grammar.aux grammar.log grammar.ps  *.class

distclean: vclean

vclean:
	 rm -f grammar/Absyn/ModelRule.java grammar/Absyn/Model.java grammar/Absyn/Statement.java grammar/Absyn/NumberOfStatesGuess.java grammar/Absyn/NumberOfLetters.java grammar/Absyn/NumberOfStates.java grammar/Absyn/InitialState.java grammar/Absyn/AcceptingStates.java grammar/Absyn/Transitions.java grammar/Absyn/SymmetryPairs.java grammar/Absyn/SymmetryInstances.java grammar/Absyn/FiniteOutput.java grammar/Absyn/ImpossiblePairs.java grammar/Absyn/ValidConfiguration.java grammar/Absyn/TurnOffConditions.java grammar/Absyn/TransitionRule.java grammar/Absyn/Transition.java grammar/Absyn/SymmetryPairRule.java grammar/Absyn/SymmetryPair.java grammar/Absyn/SymInstance.java grammar/Absyn/SymmetryInstance.java grammar/Absyn/GeneratorRule.java grammar/Absyn/Generator.java grammar/Absyn/ImpossiblePairRule.java grammar/Absyn/ImpossiblePair.java grammar/Absyn/AutomataRule.java grammar/Absyn/Automata.java grammar/Absyn/AutomataTransitionRule.java grammar/Absyn/AutomataTransition.java grammar/Absyn/AutomataAcceptingsRule.java grammar/Absyn/AutomataAcceptings.java grammar/Absyn/StateRule.java grammar/Absyn/AutomataState.java grammar/Absyn/CheckingConditionRule.java grammar/Absyn/ParikhCondition.java grammar/Absyn/AutomorphismCondition.java grammar/Absyn/PermutativeCondition.java grammar/Absyn/InjectiveOutputCondition.java grammar/Absyn/InjectiveInputCondition.java grammar/Absyn/CopycatCondition.java grammar/Absyn/OutputUniversalCondition.java grammar/Absyn/InputUniversalCondition.java grammar/Absyn/WordRule.java grammar/Absyn/Word.java grammar/Absyn/NumberSpaceRule.java grammar/Absyn/NumberSpace.java grammar/Absyn/Numbers.java grammar/Absyn/Sequence.java grammar/Absyn/NumberCommaRule.java grammar/Absyn/NumberComma.java grammar/Absyn/ListStatement.java grammar/Absyn/ListSymmetryPairRule.java grammar/Absyn/ListTransitionRule.java grammar/Absyn/ListSymInstance.java grammar/Absyn/ListGeneratorRule.java grammar/Absyn/ListImpossiblePairRule.java grammar/Absyn/ListAutomataTransitionRule.java grammar/Absyn/ListStateRule.java grammar/Absyn/ListCheckingConditionRule.java grammar/Absyn/ListNumberSpaceRule.java grammar/Absyn/ListNumberCommaRule.java grammar/Absyn/ModelRule.class grammar/Absyn/Model.class grammar/Absyn/Statement.class grammar/Absyn/NumberOfStatesGuess.class grammar/Absyn/NumberOfLetters.class grammar/Absyn/NumberOfStates.class grammar/Absyn/InitialState.class grammar/Absyn/AcceptingStates.class grammar/Absyn/Transitions.class grammar/Absyn/SymmetryPairs.class grammar/Absyn/SymmetryInstances.class grammar/Absyn/FiniteOutput.class grammar/Absyn/ImpossiblePairs.class grammar/Absyn/ValidConfiguration.class grammar/Absyn/TurnOffConditions.class grammar/Absyn/TransitionRule.class grammar/Absyn/Transition.class grammar/Absyn/SymmetryPairRule.class grammar/Absyn/SymmetryPair.class grammar/Absyn/SymInstance.class grammar/Absyn/SymmetryInstance.class grammar/Absyn/GeneratorRule.class grammar/Absyn/Generator.class grammar/Absyn/ImpossiblePairRule.class grammar/Absyn/ImpossiblePair.class grammar/Absyn/AutomataRule.class grammar/Absyn/Automata.class grammar/Absyn/AutomataTransitionRule.class grammar/Absyn/AutomataTransition.class grammar/Absyn/AutomataAcceptingsRule.class grammar/Absyn/AutomataAcceptings.class grammar/Absyn/StateRule.class grammar/Absyn/AutomataState.class grammar/Absyn/CheckingConditionRule.class grammar/Absyn/ParikhCondition.class grammar/Absyn/AutomorphismCondition.class grammar/Absyn/PermutativeCondition.class grammar/Absyn/InjectiveOutputCondition.class grammar/Absyn/InjectiveInputCondition.class grammar/Absyn/CopycatCondition.class grammar/Absyn/OutputUniversalCondition.class grammar/Absyn/InputUniversalCondition.class grammar/Absyn/WordRule.class grammar/Absyn/Word.class grammar/Absyn/NumberSpaceRule.class grammar/Absyn/NumberSpace.class grammar/Absyn/Numbers.class grammar/Absyn/Sequence.class grammar/Absyn/NumberCommaRule.class grammar/Absyn/NumberComma.class grammar/Absyn/ListStatement.class grammar/Absyn/ListSymmetryPairRule.class grammar/Absyn/ListTransitionRule.class grammar/Absyn/ListSymInstance.class grammar/Absyn/ListGeneratorRule.class grammar/Absyn/ListImpossiblePairRule.class grammar/Absyn/ListAutomataTransitionRule.class grammar/Absyn/ListStateRule.class grammar/Absyn/ListCheckingConditionRule.class grammar/Absyn/ListNumberSpaceRule.class grammar/Absyn/ListNumberCommaRule.class
	 rm -f grammar/Absyn/*.class
	 rmdir grammar/Absyn/
	 rm -f grammar/Yylex grammar/grammar.cup grammar/Yylex.java grammar/VisitSkel.java grammar/ComposVisitor.java grammar/AbstractVisitor.java grammar/FoldVisitor.java grammar/AllVisitor.java grammar/PrettyPrinter.java grammar/Skeleton.java grammar/Test.java grammar/sym.java grammar/parser.java grammar/*.class
	rm -f Makefile
	rmdir -p grammar/

