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
      r = combine(p.transducerrule_.accept(this, arg), r, arg);
      for (Statement x : p.liststatement_) {
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

/* Statement */
    public R visit(grammar.Absyn.NumberOfStatesGuess p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.SymmetryPairs p, A arg) {
      R r = leaf(arg);
      for (SymmetryPairRule x : p.listsymmetrypairrule_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }
    public R visit(grammar.Absyn.SymmetryInstances p, A arg) {
      R r = leaf(arg);
      for (SymInstance x : p.listsyminstance_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }
    public R visit(grammar.Absyn.FiniteOutput p, A arg) {
      R r = leaf(arg);
      for (Name x : p.listname_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }
    public R visit(grammar.Absyn.ImpossiblePairs p, A arg) {
      R r = leaf(arg);
      for (ImpossiblePairRule x : p.listimpossiblepairrule_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }
    public R visit(grammar.Absyn.ValidConfiguration p, A arg) {
      R r = leaf(arg);
      r = combine(p.automatainitrule_.accept(this, arg), r, arg);
      for (AutomataTransitionRule x : p.listautomatatransitionrule_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      r = combine(p.automataacceptingsrule_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(grammar.Absyn.TurnOffConditions p, A arg) {
      R r = leaf(arg);
      for (CheckingConditionRule x : p.listcheckingconditionrule_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }

/* SymmetryPairRule */
    public R visit(grammar.Absyn.SymmetryPair p, A arg) {
      R r = leaf(arg);
      r = combine(p.namesequencerule_1.accept(this, arg), r, arg);
      r = combine(p.namesequencerule_2.accept(this, arg), r, arg);
      return r;
    }

/* SymInstance */
    public R visit(grammar.Absyn.SymmetryInstance p, A arg) {
      R r = leaf(arg);
      for (GeneratorRule x : p.listgeneratorrule_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }

/* GeneratorRule */
    public R visit(grammar.Absyn.Generator p, A arg) {
      R r = leaf(arg);
      r = combine(p.indexsequencerule_.accept(this, arg), r, arg);
      return r;
    }

/* ImpossiblePairRule */
    public R visit(grammar.Absyn.ImpossiblePair p, A arg) {
      R r = leaf(arg);
      r = combine(p.name_1.accept(this, arg), r, arg);
      r = combine(p.name_2.accept(this, arg), r, arg);
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

/* CheckingConditionRule */
    public R visit(grammar.Absyn.ParikhCondition p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.AutomorphismCondition p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.PermutativeCondition p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.InjectiveOutputCondition p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.InjectiveInputCondition p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.CopycatCondition p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.OutputUniversalCondition p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(grammar.Absyn.InputUniversalCondition p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* IndexSequenceRule */
    public R visit(grammar.Absyn.IndexSequence p, A arg) {
      R r = leaf(arg);
      for (NumberSpaceRule x : p.listnumberspacerule_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }

/* NumberSpaceRule */
    public R visit(grammar.Absyn.NumberSpace p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* NameSequenceRule */
    public R visit(grammar.Absyn.NameSequence p, A arg) {
      R r = leaf(arg);
      for (NameSpaceRule x : p.listnamespacerule_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }

/* NameSpaceRule */
    public R visit(grammar.Absyn.NameSpace p, A arg) {
      R r = leaf(arg);
      r = combine(p.name_.accept(this, arg), r, arg);
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
