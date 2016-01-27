package grammar;
import grammar.Absyn.*;
/*** BNFC-Generated Visitor Design Pattern Skeleton. ***/
/* This implements the common visitor design pattern.
   Tests show it to be slightly less efficient than the
   instanceof method, but easier to use. 
   Replace the R and A parameters with the desired return
   and context types.*/

public class VisitSkel
{
  public class ModelRuleVisitor<R,A> implements ModelRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.Model p, A arg)
    {
      /* Code For Model Goes Here */

      p.automatonrule_1.accept(new AutomatonRuleVisitor<R,A>(), arg);
      p.maybeclosed_.accept(new MaybeClosedVisitor<R,A>(), arg);
      p.automatonrule_2.accept(new AutomatonRuleVisitor<R,A>(), arg);
      p.transducerrule_1.accept(new TransducerRuleVisitor<R,A>(), arg);
      p.transducerrule_2.accept(new TransducerRuleVisitor<R,A>(), arg);
      for (VerifierOption x : p.listverifieroption_) {
      }

      return null;
    }

  }
  public class TransducerRuleVisitor<R,A> implements TransducerRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.Transducer p, A arg)
    {
      /* Code For Transducer Goes Here */

      p.initrule_.accept(new InitRuleVisitor<R,A>(), arg);
      for (TransitionRule x : p.listtransitionrule_) {
      }
      p.acceptingrule_.accept(new AcceptingRuleVisitor<R,A>(), arg);

      return null;
    }

  }
  public class InitRuleVisitor<R,A> implements InitRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.TransducerInitialState p, A arg)
    {
      /* Code For TransducerInitialState Goes Here */

      p.name_.accept(new NameVisitor<R,A>(), arg);

      return null;
    }

  }
  public class TransitionRuleVisitor<R,A> implements TransitionRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.FulTransition p, A arg)
    {
      /* Code For FulTransition Goes Here */

      p.name_1.accept(new NameVisitor<R,A>(), arg);
      p.name_2.accept(new NameVisitor<R,A>(), arg);
      p.name_3.accept(new NameVisitor<R,A>(), arg);
      p.name_4.accept(new NameVisitor<R,A>(), arg);

      return null;
    }
    public R visit(grammar.Absyn.EmptyTransition p, A arg)
    {
      /* Code For EmptyTransition Goes Here */

      p.name_1.accept(new NameVisitor<R,A>(), arg);
      p.name_2.accept(new NameVisitor<R,A>(), arg);

      return null;
    }
    public R visit(grammar.Absyn.LoopingTransition p, A arg)
    {
      /* Code For LoopingTransition Goes Here */

      for (Name x : p.listname_) {
      }

      return null;
    }

  }
  public class AcceptingRuleVisitor<R,A> implements AcceptingRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.TransducerAccepting p, A arg)
    {
      /* Code For TransducerAccepting Goes Here */

      for (Name x : p.listname_) {
      }

      return null;
    }

  }
  public class AutomatonRuleVisitor<R,A> implements AutomatonRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.Automaton p, A arg)
    {
      /* Code For Automaton Goes Here */

      p.automatainitrule_.accept(new AutomataInitRuleVisitor<R,A>(), arg);
      for (AutomataTransitionRule x : p.listautomatatransitionrule_) {
      }
      p.automataacceptingsrule_.accept(new AutomataAcceptingsRuleVisitor<R,A>(), arg);

      return null;
    }

  }
  public class AutomataInitRuleVisitor<R,A> implements AutomataInitRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.AutomataInitialState p, A arg)
    {
      /* Code For AutomataInitialState Goes Here */

      p.name_.accept(new NameVisitor<R,A>(), arg);

      return null;
    }

  }
  public class AutomataTransitionRuleVisitor<R,A> implements AutomataTransitionRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.AutomataTransition p, A arg)
    {
      /* Code For AutomataTransition Goes Here */

      p.name_1.accept(new NameVisitor<R,A>(), arg);
      p.name_2.accept(new NameVisitor<R,A>(), arg);
      p.name_3.accept(new NameVisitor<R,A>(), arg);

      return null;
    }
    public R visit(grammar.Absyn.AutomataEmptyTransition p, A arg)
    {
      /* Code For AutomataEmptyTransition Goes Here */

      p.name_1.accept(new NameVisitor<R,A>(), arg);
      p.name_2.accept(new NameVisitor<R,A>(), arg);

      return null;
    }

  }
  public class AutomataAcceptingsRuleVisitor<R,A> implements AutomataAcceptingsRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.AutomataAcceptings p, A arg)
    {
      /* Code For AutomataAcceptings Goes Here */

      for (Name x : p.listname_) {
      }

      return null;
    }

  }
  public class VerifierOptionVisitor<R,A> implements VerifierOption.Visitor<R,A>
  {
    public R visit(grammar.Absyn.NumOfStatesTransducerGuess p, A arg)
    {
      /* Code For NumOfStatesTransducerGuess Goes Here */

      //p.integer_1;
      //p.integer_2;

      return null;
    }
    public R visit(grammar.Absyn.NumOfStatesAutomatonGuess p, A arg)
    {
      /* Code For NumOfStatesAutomatonGuess Goes Here */

      //p.integer_1;
      //p.integer_2;

      return null;
    }
    public R visit(grammar.Absyn.NumOfInitStatesAutomatonGuess p, A arg)
    {
      /* Code For NumOfInitStatesAutomatonGuess Goes Here */

      //p.integer_1;
      //p.integer_2;

      return null;
    }
    public R visit(grammar.Absyn.SymmetryOptions p, A arg)
    {
      /* Code For SymmetryOptions Goes Here */

      for (SymmetryOption x : p.listsymmetryoption_) {
      }

      return null;
    }
    public R visit(grammar.Absyn.ExplicitChecks p, A arg)
    {
      /* Code For ExplicitChecks Goes Here */

      //p.integer_;

      return null;
    }
    public R visit(grammar.Absyn.UseRankingFunctions p, A arg)
    {
      /* Code For UseRankingFunctions Goes Here */


      return null;
    }

  }
  public class SymmetryOptionVisitor<R,A> implements SymmetryOption.Visitor<R,A>
  {
    public R visit(grammar.Absyn.RotationSymmetry p, A arg)
    {
      /* Code For RotationSymmetry Goes Here */


      return null;
    }

  }
  public class MaybeClosedVisitor<R,A> implements MaybeClosed.Visitor<R,A>
  {
    public R visit(grammar.Absyn.ClosedInit p, A arg)
    {
      /* Code For ClosedInit Goes Here */


      return null;
    }
    public R visit(grammar.Absyn.NotClosedInit p, A arg)
    {
      /* Code For NotClosedInit Goes Here */


      return null;
    }

  }
  public class NameVisitor<R,A> implements Name.Visitor<R,A>
  {
    public R visit(grammar.Absyn.NumberName p, A arg)
    {
      /* Code For NumberName Goes Here */

      //p.integer_;

      return null;
    }
    public R visit(grammar.Absyn.LiteralName p, A arg)
    {
      /* Code For LiteralName Goes Here */

      //p.ident_;

      return null;
    }

  }
}