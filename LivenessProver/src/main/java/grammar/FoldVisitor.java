package grammar;

import grammar.Absyn.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/** BNFC-Generated Fold Visitor */
public abstract class FoldVisitor<R,A> implements AllVisitor<R,A> {
    public abstract R leaf(A arg);
    public abstract R combine(R x, R y, A arg);

/* ModelRule */
    public R visit(grammar.Absyn.Model p, A arg) {
      R r = leaf(arg);
      r = combine(p.automatonrule_1.accept(this, arg), r, arg);
      r = combine(p.maybeclosed_.accept(this, arg), r, arg);
      r = combine(p.automatonrule_2.accept(this, arg), r, arg);
      r = combine(p.transducerrule_1.accept(this, arg), r, arg);
      r = combine(p.transducerrule_2.accept(this, arg), r, arg);
      for (VerifierOption x : p.listverifieroption_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }

/* TransducerRule */
    public R visit(grammar.Absyn.Transducer p, A arg) {
      R r = leaf(arg);
      r = combine(p.initrule_.accept(this, arg), r, arg);
      for (TransitionRule x : p.listtransitionrule_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      r = combine(p.acceptingrule_.accept(this, arg), r, arg);
      return r;
    }

/* InitRule */
    public R visit(grammar.Absyn.TransducerInitialState p, A arg) {
      R r = leaf(arg);
      r = combine(p.name_.accept(this, arg), r, arg);
      return r;
    }

/* TransitionRule */
    public R visit(grammar.Absyn.FulTransition p, A arg) {
      R r = leaf(arg);
      r = combine(p.name_1.accept(this, arg), r, arg);
      r = combine(p.name_2.accept(this, arg), r, arg);
      r = combine(p.name_3.accept(this, arg), r, arg);
      r = combine(p.name_4.accept(this, arg), r, arg);
      return r;
    }
    public R visit(grammar.Absyn.EmptyTransition p, A arg) {
      R r = leaf(arg);
      r = combine(p.name_1.accept(this, arg), r, arg);
      r = combine(p.name_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(grammar.Absyn.LoopingTransition p, A arg) {
      R r = leaf(arg);
      for (Name x : p.listname_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }

/* AcceptingRule */
    public R visit(grammar.Absyn.TransducerAccepting p, A arg) {
      R r = leaf(arg);
      for (Name x : p.listname_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }

/* AutomatonRule */
    public R visit(grammar.Absyn.Automaton p, A arg) {
      R r = leaf(arg);
      r = combine(p.automatainitrule_.accept(this, arg), r, arg);
      for (AutomataTransitionRule x : p.listautomatatransitionrule_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      r = combine(p.automataacceptingsrule_.accept(this, arg), r, arg);
      return r;
    }

/* AutomataInitRule */
    public R visit(grammar.Absyn.AutomataInitialState p, A arg) {
      R r = leaf(arg);
      r = combine(p.name_.accept(this, arg), r, arg);
      return r;
    }

/* AutomataTransitionRule */
    public R visit(grammar.Absyn.AutomataTransition p, A arg) {
      R r = leaf(arg);
      r = combine(p.name_1.accept(this, arg), r, arg);
      r = combine(p.name_2.accept(this, arg), r, arg);
      r = combine(p.name_3.accept(this, arg), r, arg);
      return r;
    }
    public R visit(grammar.Absyn.AutomataEmptyTransition p, A arg) {
      R r = leaf(arg);
      r = combine(p.name_1.accept(this, arg), r, arg);
      r = combine(p.name_2.accept(this, arg), r, arg);
      return r;
    }

/* AutomataAcceptingsRule */
    public R visit(grammar.Absyn.AutomataAcceptings p, A arg) {
      R r = leaf(arg);
      for (Name x : p.listname_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }

/* VerifierOption */
    public R visit(grammar.Absyn.NumOfStatesTransducerGuess p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.NumOfStatesAutomatonGuess p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.NumOfInitStatesAutomatonGuess p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.SymmetryOptions p, A arg) {
      R r = leaf(arg);
      for (SymmetryOption x : p.listsymmetryoption_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }
    public R visit(grammar.Absyn.ExplicitChecks p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.UseRankingFunctions p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.MonolithicWitness p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.NoPrecomputedInv p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* SymmetryOption */
    public R visit(grammar.Absyn.RotationSymmetry p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* MaybeClosed */
    public R visit(grammar.Absyn.ClosedInit p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.NotClosedInit p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* Name */
    public R visit(grammar.Absyn.NumberName p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.LiteralName p, A arg) {
      R r = leaf(arg);
      return r;
    }


}
