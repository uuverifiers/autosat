package grammar;
import grammar.Absyn.*;
/** BNFC-Generated Abstract Visitor */
public class AbstractVisitor<R,A> implements AllVisitor<R,A> {
/* ModelRule */
    public R visit(grammar.Absyn.Model p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.ModelRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* TransducerRule */
    public R visit(grammar.Absyn.Transducer p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.TransducerRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* InitRule */
    public R visit(grammar.Absyn.TransducerInitialState p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.InitRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* TransitionRule */
    public R visit(grammar.Absyn.FulTransition p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.EmptyTransition p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.LoopingTransition p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.TransitionRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* AcceptingRule */
    public R visit(grammar.Absyn.TransducerAccepting p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.AcceptingRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Statement */
    public R visit(grammar.Absyn.NumberOfStatesGuess p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.SymmetryPairs p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.SymmetryInstances p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.FiniteOutput p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.ImpossiblePairs p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.ValidConfiguration p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.TurnOffConditions p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.Statement p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* SymmetryPairRule */
    public R visit(grammar.Absyn.SymmetryPair p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.SymmetryPairRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* SymInstance */
    public R visit(grammar.Absyn.SymmetryInstance p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.SymInstance p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* GeneratorRule */
    public R visit(grammar.Absyn.Generator p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.GeneratorRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* ImpossiblePairRule */
    public R visit(grammar.Absyn.ImpossiblePair p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.ImpossiblePairRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* AutomataInitRule */
    public R visit(grammar.Absyn.AutomataInitialState p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.AutomataInitRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* AutomataTransitionRule */
    public R visit(grammar.Absyn.AutomataTransition p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.AutomataEmptyTransition p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.AutomataTransitionRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* AutomataAcceptingsRule */
    public R visit(grammar.Absyn.AutomataAcceptings p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.AutomataAcceptingsRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* CheckingConditionRule */
    public R visit(grammar.Absyn.ParikhCondition p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.AutomorphismCondition p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.PermutativeCondition p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.InjectiveOutputCondition p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.InjectiveInputCondition p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.CopycatCondition p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.OutputUniversalCondition p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.InputUniversalCondition p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.CheckingConditionRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* IndexSequenceRule */
    public R visit(grammar.Absyn.IndexSequence p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.IndexSequenceRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* NumberSpaceRule */
    public R visit(grammar.Absyn.NumberSpace p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.NumberSpaceRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* NameSequenceRule */
    public R visit(grammar.Absyn.NameSequence p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.NameSequenceRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* NameSpaceRule */
    public R visit(grammar.Absyn.NameSpace p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.NameSpaceRule p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Name */
    public R visit(grammar.Absyn.NumberName p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.LiteralName p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.Name p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }

}
