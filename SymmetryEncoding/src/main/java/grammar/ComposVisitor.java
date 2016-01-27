package grammar;
import grammar.Absyn.*;
/** BNFC-Generated Composition Visitor
*/

public class ComposVisitor<A> implements
  grammar.Absyn.ModelRule.Visitor<grammar.Absyn.ModelRule,A>,
  grammar.Absyn.TransducerRule.Visitor<grammar.Absyn.TransducerRule,A>,
  grammar.Absyn.InitRule.Visitor<grammar.Absyn.InitRule,A>,
  grammar.Absyn.TransitionRule.Visitor<grammar.Absyn.TransitionRule,A>,
  grammar.Absyn.AcceptingRule.Visitor<grammar.Absyn.AcceptingRule,A>,
  grammar.Absyn.Statement.Visitor<grammar.Absyn.Statement,A>,
  grammar.Absyn.SymmetryPairRule.Visitor<grammar.Absyn.SymmetryPairRule,A>,
  grammar.Absyn.SymInstance.Visitor<grammar.Absyn.SymInstance,A>,
  grammar.Absyn.GeneratorRule.Visitor<grammar.Absyn.GeneratorRule,A>,
  grammar.Absyn.ImpossiblePairRule.Visitor<grammar.Absyn.ImpossiblePairRule,A>,
  grammar.Absyn.AutomataInitRule.Visitor<grammar.Absyn.AutomataInitRule,A>,
  grammar.Absyn.AutomataTransitionRule.Visitor<grammar.Absyn.AutomataTransitionRule,A>,
  grammar.Absyn.AutomataAcceptingsRule.Visitor<grammar.Absyn.AutomataAcceptingsRule,A>,
  grammar.Absyn.CheckingConditionRule.Visitor<grammar.Absyn.CheckingConditionRule,A>,
  grammar.Absyn.IndexSequenceRule.Visitor<grammar.Absyn.IndexSequenceRule,A>,
  grammar.Absyn.NumberSpaceRule.Visitor<grammar.Absyn.NumberSpaceRule,A>,
  grammar.Absyn.NameSequenceRule.Visitor<grammar.Absyn.NameSequenceRule,A>,
  grammar.Absyn.NameSpaceRule.Visitor<grammar.Absyn.NameSpaceRule,A>,
  grammar.Absyn.Name.Visitor<grammar.Absyn.Name,A>
{
/* ModelRule */
    public ModelRule visit(grammar.Absyn.Model p, A arg)
    {
      TransducerRule transducerrule_ = p.transducerrule_.accept(this, arg);
      ListStatement liststatement_ = new ListStatement();
      for (Statement x : p.liststatement_) {
        liststatement_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.Model(transducerrule_, liststatement_);
    }

/* TransducerRule */
    public TransducerRule visit(grammar.Absyn.Transducer p, A arg)
    {
      InitRule initrule_ = p.initrule_.accept(this, arg);
      ListTransitionRule listtransitionrule_ = new ListTransitionRule();
      for (TransitionRule x : p.listtransitionrule_) {
        listtransitionrule_.add(x.accept(this,arg));
      }
      AcceptingRule acceptingrule_ = p.acceptingrule_.accept(this, arg);

      return new grammar.Absyn.Transducer(initrule_, listtransitionrule_, acceptingrule_);
    }

/* InitRule */
    public InitRule visit(grammar.Absyn.TransducerInitialState p, A arg)
    {
      Name name_ = p.name_.accept(this, arg);

      return new grammar.Absyn.TransducerInitialState(name_);
    }

/* TransitionRule */
    public TransitionRule visit(grammar.Absyn.FulTransition p, A arg)
    {
      Name name_1 = p.name_1.accept(this, arg);
      Name name_2 = p.name_2.accept(this, arg);
      Name name_3 = p.name_3.accept(this, arg);
      Name name_4 = p.name_4.accept(this, arg);

      return new grammar.Absyn.FulTransition(name_1, name_2, name_3, name_4);
    }
    public TransitionRule visit(grammar.Absyn.EmptyTransition p, A arg)
    {
      Name name_1 = p.name_1.accept(this, arg);
      Name name_2 = p.name_2.accept(this, arg);

      return new grammar.Absyn.EmptyTransition(name_1, name_2);
    }
    public TransitionRule visit(grammar.Absyn.LoopingTransition p, A arg)
    {
      ListName listname_ = new ListName();
      for (Name x : p.listname_) {
        listname_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.LoopingTransition(listname_);
    }

/* AcceptingRule */
    public AcceptingRule visit(grammar.Absyn.TransducerAccepting p, A arg)
    {
      ListName listname_ = new ListName();
      for (Name x : p.listname_) {
        listname_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.TransducerAccepting(listname_);
    }

/* Statement */
    public Statement visit(grammar.Absyn.NumberOfStatesGuess p, A arg)
    {
      Integer integer_1 = p.integer_1;
      Integer integer_2 = p.integer_2;

      return new grammar.Absyn.NumberOfStatesGuess(integer_1, integer_2);
    }
    public Statement visit(grammar.Absyn.SymmetryPairs p, A arg)
    {
      ListSymmetryPairRule listsymmetrypairrule_ = new ListSymmetryPairRule();
      for (SymmetryPairRule x : p.listsymmetrypairrule_) {
        listsymmetrypairrule_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.SymmetryPairs(listsymmetrypairrule_);
    }
    public Statement visit(grammar.Absyn.SymmetryInstances p, A arg)
    {
      ListSymInstance listsyminstance_ = new ListSymInstance();
      for (SymInstance x : p.listsyminstance_) {
        listsyminstance_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.SymmetryInstances(listsyminstance_);
    }
    public Statement visit(grammar.Absyn.FiniteOutput p, A arg)
    {
      ListName listname_ = new ListName();
      for (Name x : p.listname_) {
        listname_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.FiniteOutput(listname_);
    }
    public Statement visit(grammar.Absyn.ImpossiblePairs p, A arg)
    {
      ListImpossiblePairRule listimpossiblepairrule_ = new ListImpossiblePairRule();
      for (ImpossiblePairRule x : p.listimpossiblepairrule_) {
        listimpossiblepairrule_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.ImpossiblePairs(listimpossiblepairrule_);
    }
    public Statement visit(grammar.Absyn.ValidConfiguration p, A arg)
    {
      AutomataInitRule automatainitrule_ = p.automatainitrule_.accept(this, arg);
      ListAutomataTransitionRule listautomatatransitionrule_ = new ListAutomataTransitionRule();
      for (AutomataTransitionRule x : p.listautomatatransitionrule_) {
        listautomatatransitionrule_.add(x.accept(this,arg));
      }
      AutomataAcceptingsRule automataacceptingsrule_ = p.automataacceptingsrule_.accept(this, arg);

      return new grammar.Absyn.ValidConfiguration(automatainitrule_, listautomatatransitionrule_, automataacceptingsrule_);
    }
    public Statement visit(grammar.Absyn.TurnOffConditions p, A arg)
    {
      ListCheckingConditionRule listcheckingconditionrule_ = new ListCheckingConditionRule();
      for (CheckingConditionRule x : p.listcheckingconditionrule_) {
        listcheckingconditionrule_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.TurnOffConditions(listcheckingconditionrule_);
    }

/* SymmetryPairRule */
    public SymmetryPairRule visit(grammar.Absyn.SymmetryPair p, A arg)
    {
      NameSequenceRule namesequencerule_1 = p.namesequencerule_1.accept(this, arg);
      NameSequenceRule namesequencerule_2 = p.namesequencerule_2.accept(this, arg);

      return new grammar.Absyn.SymmetryPair(namesequencerule_1, namesequencerule_2);
    }

/* SymInstance */
    public SymInstance visit(grammar.Absyn.SymmetryInstance p, A arg)
    {
      Integer integer_ = p.integer_;
      ListGeneratorRule listgeneratorrule_ = new ListGeneratorRule();
      for (GeneratorRule x : p.listgeneratorrule_) {
        listgeneratorrule_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.SymmetryInstance(integer_, listgeneratorrule_);
    }

/* GeneratorRule */
    public GeneratorRule visit(grammar.Absyn.Generator p, A arg)
    {
      IndexSequenceRule indexsequencerule_ = p.indexsequencerule_.accept(this, arg);

      return new grammar.Absyn.Generator(indexsequencerule_);
    }

/* ImpossiblePairRule */
    public ImpossiblePairRule visit(grammar.Absyn.ImpossiblePair p, A arg)
    {
      Name name_1 = p.name_1.accept(this, arg);
      Name name_2 = p.name_2.accept(this, arg);

      return new grammar.Absyn.ImpossiblePair(name_1, name_2);
    }

/* AutomataInitRule */
    public AutomataInitRule visit(grammar.Absyn.AutomataInitialState p, A arg)
    {
      Name name_ = p.name_.accept(this, arg);

      return new grammar.Absyn.AutomataInitialState(name_);
    }

/* AutomataTransitionRule */
    public AutomataTransitionRule visit(grammar.Absyn.AutomataTransition p, A arg)
    {
      Name name_1 = p.name_1.accept(this, arg);
      Name name_2 = p.name_2.accept(this, arg);
      Name name_3 = p.name_3.accept(this, arg);

      return new grammar.Absyn.AutomataTransition(name_1, name_2, name_3);
    }
    public AutomataTransitionRule visit(grammar.Absyn.AutomataEmptyTransition p, A arg)
    {
      Name name_1 = p.name_1.accept(this, arg);
      Name name_2 = p.name_2.accept(this, arg);

      return new grammar.Absyn.AutomataEmptyTransition(name_1, name_2);
    }

/* AutomataAcceptingsRule */
    public AutomataAcceptingsRule visit(grammar.Absyn.AutomataAcceptings p, A arg)
    {
      ListName listname_ = new ListName();
      for (Name x : p.listname_) {
        listname_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.AutomataAcceptings(listname_);
    }

/* CheckingConditionRule */
    public CheckingConditionRule visit(grammar.Absyn.ParikhCondition p, A arg)
    {

      return new grammar.Absyn.ParikhCondition();
    }
    public CheckingConditionRule visit(grammar.Absyn.AutomorphismCondition p, A arg)
    {

      return new grammar.Absyn.AutomorphismCondition();
    }
    public CheckingConditionRule visit(grammar.Absyn.PermutativeCondition p, A arg)
    {

      return new grammar.Absyn.PermutativeCondition();
    }
    public CheckingConditionRule visit(grammar.Absyn.InjectiveOutputCondition p, A arg)
    {

      return new grammar.Absyn.InjectiveOutputCondition();
    }
    public CheckingConditionRule visit(grammar.Absyn.InjectiveInputCondition p, A arg)
    {

      return new grammar.Absyn.InjectiveInputCondition();
    }
    public CheckingConditionRule visit(grammar.Absyn.CopycatCondition p, A arg)
    {

      return new grammar.Absyn.CopycatCondition();
    }
    public CheckingConditionRule visit(grammar.Absyn.OutputUniversalCondition p, A arg)
    {

      return new grammar.Absyn.OutputUniversalCondition();
    }
    public CheckingConditionRule visit(grammar.Absyn.InputUniversalCondition p, A arg)
    {

      return new grammar.Absyn.InputUniversalCondition();
    }

/* IndexSequenceRule */
    public IndexSequenceRule visit(grammar.Absyn.IndexSequence p, A arg)
    {
      ListNumberSpaceRule listnumberspacerule_ = new ListNumberSpaceRule();
      for (NumberSpaceRule x : p.listnumberspacerule_) {
        listnumberspacerule_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.IndexSequence(listnumberspacerule_);
    }

/* NumberSpaceRule */
    public NumberSpaceRule visit(grammar.Absyn.NumberSpace p, A arg)
    {
      Integer integer_ = p.integer_;

      return new grammar.Absyn.NumberSpace(integer_);
    }

/* NameSequenceRule */
    public NameSequenceRule visit(grammar.Absyn.NameSequence p, A arg)
    {
      ListNameSpaceRule listnamespacerule_ = new ListNameSpaceRule();
      for (NameSpaceRule x : p.listnamespacerule_) {
        listnamespacerule_.add(x.accept(this,arg));
      }

      return new grammar.Absyn.NameSequence(listnamespacerule_);
    }

/* NameSpaceRule */
    public NameSpaceRule visit(grammar.Absyn.NameSpace p, A arg)
    {
      Name name_ = p.name_.accept(this, arg);

      return new grammar.Absyn.NameSpace(name_);
    }

/* Name */
    public Name visit(grammar.Absyn.NumberName p, A arg)
    {
      Integer integer_ = p.integer_;

      return new grammar.Absyn.NumberName(integer_);
    }
    public Name visit(grammar.Absyn.LiteralName p, A arg)
    {
      String ident_ = p.ident_;

      return new grammar.Absyn.LiteralName(ident_);
    }

}