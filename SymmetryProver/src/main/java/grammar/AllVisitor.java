package grammar;

import grammar.Absyn.*;

/** BNFC-Generated All Visitor */
public interface AllVisitor<R,A> extends
  grammar.Absyn.ModelRule.Visitor<R,A>,
  grammar.Absyn.TransducerRule.Visitor<R,A>,
  grammar.Absyn.InitRule.Visitor<R,A>,
  grammar.Absyn.TransitionRule.Visitor<R,A>,
  grammar.Absyn.AcceptingRule.Visitor<R,A>,
  grammar.Absyn.Statement.Visitor<R,A>,
  grammar.Absyn.SymmetryPairRule.Visitor<R,A>,
  grammar.Absyn.SymInstance.Visitor<R,A>,
  grammar.Absyn.GeneratorRule.Visitor<R,A>,
  grammar.Absyn.ImpossiblePairRule.Visitor<R,A>,
  grammar.Absyn.AutomataInitRule.Visitor<R,A>,
  grammar.Absyn.AutomataTransitionRule.Visitor<R,A>,
  grammar.Absyn.AutomataAcceptingsRule.Visitor<R,A>,
  grammar.Absyn.CheckingConditionRule.Visitor<R,A>,
  grammar.Absyn.IndexSequenceRule.Visitor<R,A>,
  grammar.Absyn.NumberSpaceRule.Visitor<R,A>,
  grammar.Absyn.NameSequenceRule.Visitor<R,A>,
  grammar.Absyn.NameSpaceRule.Visitor<R,A>,
  grammar.Absyn.Name.Visitor<R,A>
{}
