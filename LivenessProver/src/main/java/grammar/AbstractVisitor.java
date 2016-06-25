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
/* AutomatonRule */
    public R visit(grammar.Absyn.Automaton p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.AutomatonRule p, A arg) {
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
/* VerifierOption */
    public R visit(grammar.Absyn.NumOfStatesTransducerGuess p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.NumOfStatesAutomatonGuess p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.NumOfInitStatesAutomatonGuess p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.SymmetryOptions p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.ExplicitChecks p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.UseRankingFunctions p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.MonolithicWitness p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.NoPrecomputedInv p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.LogLevel p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.ParLevel p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.VerifierOption p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* SymmetryOption */
    public R visit(grammar.Absyn.RotationSymmetry p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.RotationWithSymmetry p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.SymmetryOption p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* MaybeClosed */
    public R visit(grammar.Absyn.ClosedInit p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.NotClosedInit p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.MaybeClosed p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* Name */
    public R visit(grammar.Absyn.NumberName p, A arg) { return visitDefault(p, arg); }
    public R visit(grammar.Absyn.LiteralName p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(grammar.Absyn.Name p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }

}
