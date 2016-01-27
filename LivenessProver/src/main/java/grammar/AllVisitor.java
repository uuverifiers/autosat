package grammar;

import grammar.Absyn.*;

/** BNFC-Generated All Visitor */
public interface AllVisitor<R,A> extends
  grammar.Absyn.ModelRule.Visitor<R,A>,
  grammar.Absyn.TransducerRule.Visitor<R,A>,
  grammar.Absyn.InitRule.Visitor<R,A>,
  grammar.Absyn.TransitionRule.Visitor<R,A>,
  grammar.Absyn.AcceptingRule.Visitor<R,A>,
  grammar.Absyn.AutomatonRule.Visitor<R,A>,
  grammar.Absyn.AutomataInitRule.Visitor<R,A>,
  grammar.Absyn.AutomataTransitionRule.Visitor<R,A>,
  grammar.Absyn.AutomataAcceptingsRule.Visitor<R,A>,
  grammar.Absyn.VerifierOption.Visitor<R,A>,
  grammar.Absyn.SymmetryOption.Visitor<R,A>,
  grammar.Absyn.MaybeClosed.Visitor<R,A>,
  grammar.Absyn.Name.Visitor<R,A>
{}
