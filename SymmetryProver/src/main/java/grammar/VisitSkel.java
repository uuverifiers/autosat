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

      p.transducerrule_.accept(new TransducerRuleVisitor<R,A>(), arg);
      for (Statement x : p.liststatement_) {
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
  public class StatementVisitor<R,A> implements Statement.Visitor<R,A>
  {
    public R visit(grammar.Absyn.NumberOfStatesGuess p, A arg)
    {
      /* Code For NumberOfStatesGuess Goes Here */

      //p.integer_1;
      //p.integer_2;

      return null;
    }
    public R visit(grammar.Absyn.SymmetryPairs p, A arg)
    {
      /* Code For SymmetryPairs Goes Here */

      for (SymmetryPairRule x : p.listsymmetrypairrule_) {
      }

      return null;
    }
    public R visit(grammar.Absyn.SymmetryInstances p, A arg)
    {
      /* Code For SymmetryInstances Goes Here */

      for (SymInstance x : p.listsyminstance_) {
      }

      return null;
    }
    public R visit(grammar.Absyn.FiniteOutput p, A arg)
    {
      /* Code For FiniteOutput Goes Here */

      for (Name x : p.listname_) {
      }

      return null;
    }
    public R visit(grammar.Absyn.ImpossiblePairs p, A arg)
    {
      /* Code For ImpossiblePairs Goes Here */

      for (ImpossiblePairRule x : p.listimpossiblepairrule_) {
      }

      return null;
    }
    public R visit(grammar.Absyn.ValidConfiguration p, A arg)
    {
      /* Code For ValidConfiguration Goes Here */

      p.automatainitrule_.accept(new AutomataInitRuleVisitor<R,A>(), arg);
      for (AutomataTransitionRule x : p.listautomatatransitionrule_) {
      }
      p.automataacceptingsrule_.accept(new AutomataAcceptingsRuleVisitor<R,A>(), arg);

      return null;
    }
    public R visit(grammar.Absyn.TurnOffConditions p, A arg)
    {
      /* Code For TurnOffConditions Goes Here */

      for (CheckingConditionRule x : p.listcheckingconditionrule_) {
      }

      return null;
    }

  }
  public class SymmetryPairRuleVisitor<R,A> implements SymmetryPairRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.SymmetryPair p, A arg)
    {
      /* Code For SymmetryPair Goes Here */

      p.namesequencerule_1.accept(new NameSequenceRuleVisitor<R,A>(), arg);
      p.namesequencerule_2.accept(new NameSequenceRuleVisitor<R,A>(), arg);

      return null;
    }

  }
  public class SymInstanceVisitor<R,A> implements SymInstance.Visitor<R,A>
  {
    public R visit(grammar.Absyn.SymmetryInstance p, A arg)
    {
      /* Code For SymmetryInstance Goes Here */

      //p.integer_;
      for (GeneratorRule x : p.listgeneratorrule_) {
      }

      return null;
    }

  }
  public class GeneratorRuleVisitor<R,A> implements GeneratorRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.Generator p, A arg)
    {
      /* Code For Generator Goes Here */

      p.indexsequencerule_.accept(new IndexSequenceRuleVisitor<R,A>(), arg);

      return null;
    }

  }
  public class ImpossiblePairRuleVisitor<R,A> implements ImpossiblePairRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.ImpossiblePair p, A arg)
    {
      /* Code For ImpossiblePair Goes Here */

      p.name_1.accept(new NameVisitor<R,A>(), arg);
      p.name_2.accept(new NameVisitor<R,A>(), arg);

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
  public class CheckingConditionRuleVisitor<R,A> implements CheckingConditionRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.ParikhCondition p, A arg)
    {
      /* Code For ParikhCondition Goes Here */


      return null;
    }
    public R visit(grammar.Absyn.AutomorphismCondition p, A arg)
    {
      /* Code For AutomorphismCondition Goes Here */


      return null;
    }
    public R visit(grammar.Absyn.PermutativeCondition p, A arg)
    {
      /* Code For PermutativeCondition Goes Here */


      return null;
    }
    public R visit(grammar.Absyn.InjectiveOutputCondition p, A arg)
    {
      /* Code For InjectiveOutputCondition Goes Here */


      return null;
    }
    public R visit(grammar.Absyn.InjectiveInputCondition p, A arg)
    {
      /* Code For InjectiveInputCondition Goes Here */


      return null;
    }
    public R visit(grammar.Absyn.CopycatCondition p, A arg)
    {
      /* Code For CopycatCondition Goes Here */


      return null;
    }
    public R visit(grammar.Absyn.OutputUniversalCondition p, A arg)
    {
      /* Code For OutputUniversalCondition Goes Here */


      return null;
    }
    public R visit(grammar.Absyn.InputUniversalCondition p, A arg)
    {
      /* Code For InputUniversalCondition Goes Here */


      return null;
    }

  }
  public class IndexSequenceRuleVisitor<R,A> implements IndexSequenceRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.IndexSequence p, A arg)
    {
      /* Code For IndexSequence Goes Here */

      for (NumberSpaceRule x : p.listnumberspacerule_) {
      }

      return null;
    }

  }
  public class NumberSpaceRuleVisitor<R,A> implements NumberSpaceRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.NumberSpace p, A arg)
    {
      /* Code For NumberSpace Goes Here */

      //p.integer_;

      return null;
    }

  }
  public class NameSequenceRuleVisitor<R,A> implements NameSequenceRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.NameSequence p, A arg)
    {
      /* Code For NameSequence Goes Here */

      for (NameSpaceRule x : p.listnamespacerule_) {
      }

      return null;
    }

  }
  public class NameSpaceRuleVisitor<R,A> implements NameSpaceRule.Visitor<R,A>
  {
    public R visit(grammar.Absyn.NameSpace p, A arg)
    {
      /* Code For NameSpace Goes Here */

      p.name_.accept(new NameVisitor<R,A>(), arg);

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