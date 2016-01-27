package grammar;
import grammar.Absyn.*;

public class PrettyPrinter
{
  //For certain applications increasing the initial size of the buffer may improve performance.
  private static final int INITIAL_BUFFER_SIZE = 128;
  //You may wish to change the parentheses used in precedence.
  private static final String _L_PAREN = new String("(");
  private static final String _R_PAREN = new String(")");
  //You may wish to change render
  private static void render(String s)
  {
    if (s.equals("{"))
    {
       buf_.append("\n");
       indent();
       buf_.append(s);
       _n_ = _n_ + 2;
       buf_.append("\n");
       indent();
    }
    else if (s.equals("(") || s.equals("["))
       buf_.append(s);
    else if (s.equals(")") || s.equals("]"))
    {
       backup();
       buf_.append(s);
       buf_.append(" ");
    }
    else if (s.equals("}"))
    {
       _n_ = _n_ - 2;
       backup();
       backup();
       buf_.append(s);
       buf_.append("\n");
       indent();
    }
    else if (s.equals(","))
    {
       backup();
       buf_.append(s);
       buf_.append(" ");
    }
    else if (s.equals(";"))
    {
       backup();
       buf_.append(s);
       buf_.append("\n");
       indent();
    }
    else if (s.equals("")) return;
    else
    {
       buf_.append(s);
       buf_.append(" ");
    }
  }


  //  print and show methods are defined for each category.
  public static String print(grammar.Absyn.ModelRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ModelRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.TransducerRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.TransducerRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.InitRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.InitRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.TransitionRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.TransitionRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.AcceptingRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.AcceptingRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.Statement foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.Statement foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.SymmetryPairRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.SymmetryPairRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.SymInstance foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.SymInstance foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.GeneratorRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.GeneratorRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ImpossiblePairRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ImpossiblePairRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.AutomataInitRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.AutomataInitRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.AutomataTransitionRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.AutomataTransitionRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.AutomataAcceptingsRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.AutomataAcceptingsRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.CheckingConditionRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.CheckingConditionRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.IndexSequenceRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.IndexSequenceRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.NumberSpaceRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.NumberSpaceRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.NameSequenceRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.NameSequenceRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.NameSpaceRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.NameSpaceRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.Name foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.Name foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListStatement foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListStatement foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListSymmetryPairRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListSymmetryPairRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListTransitionRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListTransitionRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListSymInstance foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListSymInstance foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListGeneratorRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListGeneratorRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListImpossiblePairRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListImpossiblePairRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListAutomataTransitionRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListAutomataTransitionRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListCheckingConditionRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListCheckingConditionRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListNumberSpaceRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListNumberSpaceRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListName foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListName foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListNameSpaceRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListNameSpaceRule foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  /***   You shouldn't need to change anything beyond this point.   ***/

  private static void pp(grammar.Absyn.ModelRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.Model)
    {
       grammar.Absyn.Model _model = (grammar.Absyn.Model) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("model");
       render("{");
       pp(_model.transducerrule_, 0);
       pp(_model.liststatement_, 0);
       render("}");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.TransducerRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.Transducer)
    {
       grammar.Absyn.Transducer _transducer = (grammar.Absyn.Transducer) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("systemTransitions");
       render("{");
       pp(_transducer.initrule_, 0);
       pp(_transducer.listtransitionrule_, 0);
       pp(_transducer.acceptingrule_, 0);
       render("}");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.InitRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.TransducerInitialState)
    {
       grammar.Absyn.TransducerInitialState _transducerinitialstate = (grammar.Absyn.TransducerInitialState) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("init");
       render(":");
       pp(_transducerinitialstate.name_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.TransitionRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.FulTransition)
    {
       grammar.Absyn.FulTransition _fultransition = (grammar.Absyn.FulTransition) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_fultransition.name_1, 0);
       render("->");
       pp(_fultransition.name_2, 0);
       pp(_fultransition.name_3, 0);
       render("/");
       pp(_fultransition.name_4, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.EmptyTransition)
    {
       grammar.Absyn.EmptyTransition _emptytransition = (grammar.Absyn.EmptyTransition) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_emptytransition.name_1, 0);
       render("->");
       pp(_emptytransition.name_2, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.LoopingTransition)
    {
       grammar.Absyn.LoopingTransition _loopingtransition = (grammar.Absyn.LoopingTransition) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("loop");
       render(":");
       pp(_loopingtransition.listname_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.AcceptingRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.TransducerAccepting)
    {
       grammar.Absyn.TransducerAccepting _transduceraccepting = (grammar.Absyn.TransducerAccepting) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("accepting");
       render(":");
       pp(_transduceraccepting.listname_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.Statement foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.NumberOfStatesGuess)
    {
       grammar.Absyn.NumberOfStatesGuess _numberofstatesguess = (grammar.Absyn.NumberOfStatesGuess) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("statesGuessing");
       render(":");
       pp(_numberofstatesguess.integer_1, 0);
       render("..");
       pp(_numberofstatesguess.integer_2, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.SymmetryPairs)
    {
       grammar.Absyn.SymmetryPairs _symmetrypairs = (grammar.Absyn.SymmetryPairs) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("forcedSymmetryInstances");
       render(":");
       pp(_symmetrypairs.listsymmetrypairrule_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.SymmetryInstances)
    {
       grammar.Absyn.SymmetryInstances _symmetryinstances = (grammar.Absyn.SymmetryInstances) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("forcedSymmetryGenerators");
       render(":");
       pp(_symmetryinstances.listsyminstance_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.FiniteOutput)
    {
       grammar.Absyn.FiniteOutput _finiteoutput = (grammar.Absyn.FiniteOutput) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("outputBoundedLetters");
       render(":");
       pp(_finiteoutput.listname_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.ImpossiblePairs)
    {
       grammar.Absyn.ImpossiblePairs _impossiblepairs = (grammar.Absyn.ImpossiblePairs) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("impossible");
       render("pairs");
       render(":");
       pp(_impossiblepairs.listimpossiblepairrule_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.ValidConfiguration)
    {
       grammar.Absyn.ValidConfiguration _validconfiguration = (grammar.Absyn.ValidConfiguration) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("validConfigurations");
       render("{");
       pp(_validconfiguration.automatainitrule_, 0);
       pp(_validconfiguration.listautomatatransitionrule_, 0);
       pp(_validconfiguration.automataacceptingsrule_, 0);
       render("}");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.TurnOffConditions)
    {
       grammar.Absyn.TurnOffConditions _turnoffconditions = (grammar.Absyn.TurnOffConditions) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("off");
       render(":");
       pp(_turnoffconditions.listcheckingconditionrule_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.SymmetryPairRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.SymmetryPair)
    {
       grammar.Absyn.SymmetryPair _symmetrypair = (grammar.Absyn.SymmetryPair) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("(");
       pp(_symmetrypair.namesequencerule_1, 0);
       render(",");
       pp(_symmetrypair.namesequencerule_2, 0);
       render(")");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.SymInstance foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.SymmetryInstance)
    {
       grammar.Absyn.SymmetryInstance _symmetryinstance = (grammar.Absyn.SymmetryInstance) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("(");
       pp(_symmetryinstance.integer_, 0);
       render(",");
       pp(_symmetryinstance.listgeneratorrule_, 0);
       render(")");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.GeneratorRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.Generator)
    {
       grammar.Absyn.Generator _generator = (grammar.Absyn.Generator) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("(");
       pp(_generator.indexsequencerule_, 0);
       render(")");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.ImpossiblePairRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.ImpossiblePair)
    {
       grammar.Absyn.ImpossiblePair _impossiblepair = (grammar.Absyn.ImpossiblePair) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("(");
       pp(_impossiblepair.name_1, 0);
       render(",");
       pp(_impossiblepair.name_2, 0);
       render(")");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.AutomataInitRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.AutomataInitialState)
    {
       grammar.Absyn.AutomataInitialState _automatainitialstate = (grammar.Absyn.AutomataInitialState) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("init");
       render(":");
       pp(_automatainitialstate.name_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.AutomataTransitionRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.AutomataTransition)
    {
       grammar.Absyn.AutomataTransition _automatatransition = (grammar.Absyn.AutomataTransition) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_automatatransition.name_1, 0);
       render("->");
       pp(_automatatransition.name_2, 0);
       pp(_automatatransition.name_3, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.AutomataEmptyTransition)
    {
       grammar.Absyn.AutomataEmptyTransition _automataemptytransition = (grammar.Absyn.AutomataEmptyTransition) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_automataemptytransition.name_1, 0);
       render("->");
       pp(_automataemptytransition.name_2, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.AutomataAcceptingsRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.AutomataAcceptings)
    {
       grammar.Absyn.AutomataAcceptings _automataacceptings = (grammar.Absyn.AutomataAcceptings) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("accepting");
       render(":");
       pp(_automataacceptings.listname_, 0);
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.CheckingConditionRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.ParikhCondition)
    {
       grammar.Absyn.ParikhCondition _parikhcondition = (grammar.Absyn.ParikhCondition) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("parikh");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.AutomorphismCondition)
    {
       grammar.Absyn.AutomorphismCondition _automorphismcondition = (grammar.Absyn.AutomorphismCondition) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("automorphism");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.PermutativeCondition)
    {
       grammar.Absyn.PermutativeCondition _permutativecondition = (grammar.Absyn.PermutativeCondition) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("permutative");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.InjectiveOutputCondition)
    {
       grammar.Absyn.InjectiveOutputCondition _injectiveoutputcondition = (grammar.Absyn.InjectiveOutputCondition) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("injectiveoutput");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.InjectiveInputCondition)
    {
       grammar.Absyn.InjectiveInputCondition _injectiveinputcondition = (grammar.Absyn.InjectiveInputCondition) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("injectiveinput");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.CopycatCondition)
    {
       grammar.Absyn.CopycatCondition _copycatcondition = (grammar.Absyn.CopycatCondition) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("copycat");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.OutputUniversalCondition)
    {
       grammar.Absyn.OutputUniversalCondition _outputuniversalcondition = (grammar.Absyn.OutputUniversalCondition) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("outputUniversal");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.InputUniversalCondition)
    {
       grammar.Absyn.InputUniversalCondition _inputuniversalcondition = (grammar.Absyn.InputUniversalCondition) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("inputUniversal");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.IndexSequenceRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.IndexSequence)
    {
       grammar.Absyn.IndexSequence _indexsequence = (grammar.Absyn.IndexSequence) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_indexsequence.listnumberspacerule_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.NumberSpaceRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.NumberSpace)
    {
       grammar.Absyn.NumberSpace _numberspace = (grammar.Absyn.NumberSpace) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_numberspace.integer_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.NameSequenceRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.NameSequence)
    {
       grammar.Absyn.NameSequence _namesequence = (grammar.Absyn.NameSequence) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_namesequence.listnamespacerule_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.NameSpaceRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.NameSpace)
    {
       grammar.Absyn.NameSpace _namespace = (grammar.Absyn.NameSpace) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_namespace.name_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.Name foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.NumberName)
    {
       grammar.Absyn.NumberName _numbername = (grammar.Absyn.NumberName) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_numbername.integer_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.LiteralName)
    {
       grammar.Absyn.LiteralName _literalname = (grammar.Absyn.LiteralName) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_literalname.ident_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.ListStatement foo, int _i_)
  {
     for (java.util.Iterator<Statement> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render("");
       } else {
         render("");
       }
     }
  }

  private static void pp(grammar.Absyn.ListSymmetryPairRule foo, int _i_)
  {
     for (java.util.Iterator<SymmetryPairRule> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(grammar.Absyn.ListTransitionRule foo, int _i_)
  {
     for (java.util.Iterator<TransitionRule> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(";");
       } else {
         render("");
       }
     }
  }

  private static void pp(grammar.Absyn.ListSymInstance foo, int _i_)
  {
     for (java.util.Iterator<SymInstance> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(grammar.Absyn.ListGeneratorRule foo, int _i_)
  {
     for (java.util.Iterator<GeneratorRule> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render("");
       } else {
         render("");
       }
     }
  }

  private static void pp(grammar.Absyn.ListImpossiblePairRule foo, int _i_)
  {
     for (java.util.Iterator<ImpossiblePairRule> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(grammar.Absyn.ListAutomataTransitionRule foo, int _i_)
  {
     for (java.util.Iterator<AutomataTransitionRule> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(";");
       } else {
         render("");
       }
     }
  }

  private static void pp(grammar.Absyn.ListCheckingConditionRule foo, int _i_)
  {
     for (java.util.Iterator<CheckingConditionRule> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(grammar.Absyn.ListNumberSpaceRule foo, int _i_)
  {
     for (java.util.Iterator<NumberSpaceRule> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render("");
       } else {
         render("");
       }
     }
  }

  private static void pp(grammar.Absyn.ListName foo, int _i_)
  {
     for (java.util.Iterator<Name> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(grammar.Absyn.ListNameSpaceRule foo, int _i_)
  {
     for (java.util.Iterator<NameSpaceRule> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render("");
       } else {
         render("");
       }
     }
  }


  private static void sh(grammar.Absyn.ModelRule foo)
  {
    if (foo instanceof grammar.Absyn.Model)
    {
       grammar.Absyn.Model _model = (grammar.Absyn.Model) foo;
       render("(");
       render("Model");
       sh(_model.transducerrule_);
       render("[");
       sh(_model.liststatement_);
       render("]");
       render(")");
    }
  }

  private static void sh(grammar.Absyn.TransducerRule foo)
  {
    if (foo instanceof grammar.Absyn.Transducer)
    {
       grammar.Absyn.Transducer _transducer = (grammar.Absyn.Transducer) foo;
       render("(");
       render("Transducer");
       sh(_transducer.initrule_);
       render("[");
       sh(_transducer.listtransitionrule_);
       render("]");
       sh(_transducer.acceptingrule_);
       render(")");
    }
  }

  private static void sh(grammar.Absyn.InitRule foo)
  {
    if (foo instanceof grammar.Absyn.TransducerInitialState)
    {
       grammar.Absyn.TransducerInitialState _transducerinitialstate = (grammar.Absyn.TransducerInitialState) foo;
       render("(");
       render("TransducerInitialState");
       sh(_transducerinitialstate.name_);
       render(")");
    }
  }

  private static void sh(grammar.Absyn.TransitionRule foo)
  {
    if (foo instanceof grammar.Absyn.FulTransition)
    {
       grammar.Absyn.FulTransition _fultransition = (grammar.Absyn.FulTransition) foo;
       render("(");
       render("FulTransition");
       sh(_fultransition.name_1);
       sh(_fultransition.name_2);
       sh(_fultransition.name_3);
       sh(_fultransition.name_4);
       render(")");
    }
    if (foo instanceof grammar.Absyn.EmptyTransition)
    {
       grammar.Absyn.EmptyTransition _emptytransition = (grammar.Absyn.EmptyTransition) foo;
       render("(");
       render("EmptyTransition");
       sh(_emptytransition.name_1);
       sh(_emptytransition.name_2);
       render(")");
    }
    if (foo instanceof grammar.Absyn.LoopingTransition)
    {
       grammar.Absyn.LoopingTransition _loopingtransition = (grammar.Absyn.LoopingTransition) foo;
       render("(");
       render("LoopingTransition");
       render("[");
       sh(_loopingtransition.listname_);
       render("]");
       render(")");
    }
  }

  private static void sh(grammar.Absyn.AcceptingRule foo)
  {
    if (foo instanceof grammar.Absyn.TransducerAccepting)
    {
       grammar.Absyn.TransducerAccepting _transduceraccepting = (grammar.Absyn.TransducerAccepting) foo;
       render("(");
       render("TransducerAccepting");
       render("[");
       sh(_transduceraccepting.listname_);
       render("]");
       render(")");
    }
  }

  private static void sh(grammar.Absyn.Statement foo)
  {
    if (foo instanceof grammar.Absyn.NumberOfStatesGuess)
    {
       grammar.Absyn.NumberOfStatesGuess _numberofstatesguess = (grammar.Absyn.NumberOfStatesGuess) foo;
       render("(");
       render("NumberOfStatesGuess");
       sh(_numberofstatesguess.integer_1);
       sh(_numberofstatesguess.integer_2);
       render(")");
    }
    if (foo instanceof grammar.Absyn.SymmetryPairs)
    {
       grammar.Absyn.SymmetryPairs _symmetrypairs = (grammar.Absyn.SymmetryPairs) foo;
       render("(");
       render("SymmetryPairs");
       render("[");
       sh(_symmetrypairs.listsymmetrypairrule_);
       render("]");
       render(")");
    }
    if (foo instanceof grammar.Absyn.SymmetryInstances)
    {
       grammar.Absyn.SymmetryInstances _symmetryinstances = (grammar.Absyn.SymmetryInstances) foo;
       render("(");
       render("SymmetryInstances");
       render("[");
       sh(_symmetryinstances.listsyminstance_);
       render("]");
       render(")");
    }
    if (foo instanceof grammar.Absyn.FiniteOutput)
    {
       grammar.Absyn.FiniteOutput _finiteoutput = (grammar.Absyn.FiniteOutput) foo;
       render("(");
       render("FiniteOutput");
       render("[");
       sh(_finiteoutput.listname_);
       render("]");
       render(")");
    }
    if (foo instanceof grammar.Absyn.ImpossiblePairs)
    {
       grammar.Absyn.ImpossiblePairs _impossiblepairs = (grammar.Absyn.ImpossiblePairs) foo;
       render("(");
       render("ImpossiblePairs");
       render("[");
       sh(_impossiblepairs.listimpossiblepairrule_);
       render("]");
       render(")");
    }
    if (foo instanceof grammar.Absyn.ValidConfiguration)
    {
       grammar.Absyn.ValidConfiguration _validconfiguration = (grammar.Absyn.ValidConfiguration) foo;
       render("(");
       render("ValidConfiguration");
       sh(_validconfiguration.automatainitrule_);
       render("[");
       sh(_validconfiguration.listautomatatransitionrule_);
       render("]");
       sh(_validconfiguration.automataacceptingsrule_);
       render(")");
    }
    if (foo instanceof grammar.Absyn.TurnOffConditions)
    {
       grammar.Absyn.TurnOffConditions _turnoffconditions = (grammar.Absyn.TurnOffConditions) foo;
       render("(");
       render("TurnOffConditions");
       render("[");
       sh(_turnoffconditions.listcheckingconditionrule_);
       render("]");
       render(")");
    }
  }

  private static void sh(grammar.Absyn.SymmetryPairRule foo)
  {
    if (foo instanceof grammar.Absyn.SymmetryPair)
    {
       grammar.Absyn.SymmetryPair _symmetrypair = (grammar.Absyn.SymmetryPair) foo;
       render("(");
       render("SymmetryPair");
       sh(_symmetrypair.namesequencerule_1);
       sh(_symmetrypair.namesequencerule_2);
       render(")");
    }
  }

  private static void sh(grammar.Absyn.SymInstance foo)
  {
    if (foo instanceof grammar.Absyn.SymmetryInstance)
    {
       grammar.Absyn.SymmetryInstance _symmetryinstance = (grammar.Absyn.SymmetryInstance) foo;
       render("(");
       render("SymmetryInstance");
       sh(_symmetryinstance.integer_);
       render("[");
       sh(_symmetryinstance.listgeneratorrule_);
       render("]");
       render(")");
    }
  }

  private static void sh(grammar.Absyn.GeneratorRule foo)
  {
    if (foo instanceof grammar.Absyn.Generator)
    {
       grammar.Absyn.Generator _generator = (grammar.Absyn.Generator) foo;
       render("(");
       render("Generator");
       sh(_generator.indexsequencerule_);
       render(")");
    }
  }

  private static void sh(grammar.Absyn.ImpossiblePairRule foo)
  {
    if (foo instanceof grammar.Absyn.ImpossiblePair)
    {
       grammar.Absyn.ImpossiblePair _impossiblepair = (grammar.Absyn.ImpossiblePair) foo;
       render("(");
       render("ImpossiblePair");
       sh(_impossiblepair.name_1);
       sh(_impossiblepair.name_2);
       render(")");
    }
  }

  private static void sh(grammar.Absyn.AutomataInitRule foo)
  {
    if (foo instanceof grammar.Absyn.AutomataInitialState)
    {
       grammar.Absyn.AutomataInitialState _automatainitialstate = (grammar.Absyn.AutomataInitialState) foo;
       render("(");
       render("AutomataInitialState");
       sh(_automatainitialstate.name_);
       render(")");
    }
  }

  private static void sh(grammar.Absyn.AutomataTransitionRule foo)
  {
    if (foo instanceof grammar.Absyn.AutomataTransition)
    {
       grammar.Absyn.AutomataTransition _automatatransition = (grammar.Absyn.AutomataTransition) foo;
       render("(");
       render("AutomataTransition");
       sh(_automatatransition.name_1);
       sh(_automatatransition.name_2);
       sh(_automatatransition.name_3);
       render(")");
    }
    if (foo instanceof grammar.Absyn.AutomataEmptyTransition)
    {
       grammar.Absyn.AutomataEmptyTransition _automataemptytransition = (grammar.Absyn.AutomataEmptyTransition) foo;
       render("(");
       render("AutomataEmptyTransition");
       sh(_automataemptytransition.name_1);
       sh(_automataemptytransition.name_2);
       render(")");
    }
  }

  private static void sh(grammar.Absyn.AutomataAcceptingsRule foo)
  {
    if (foo instanceof grammar.Absyn.AutomataAcceptings)
    {
       grammar.Absyn.AutomataAcceptings _automataacceptings = (grammar.Absyn.AutomataAcceptings) foo;
       render("(");
       render("AutomataAcceptings");
       render("[");
       sh(_automataacceptings.listname_);
       render("]");
       render(")");
    }
  }

  private static void sh(grammar.Absyn.CheckingConditionRule foo)
  {
    if (foo instanceof grammar.Absyn.ParikhCondition)
    {
       grammar.Absyn.ParikhCondition _parikhcondition = (grammar.Absyn.ParikhCondition) foo;
       render("ParikhCondition");
    }
    if (foo instanceof grammar.Absyn.AutomorphismCondition)
    {
       grammar.Absyn.AutomorphismCondition _automorphismcondition = (grammar.Absyn.AutomorphismCondition) foo;
       render("AutomorphismCondition");
    }
    if (foo instanceof grammar.Absyn.PermutativeCondition)
    {
       grammar.Absyn.PermutativeCondition _permutativecondition = (grammar.Absyn.PermutativeCondition) foo;
       render("PermutativeCondition");
    }
    if (foo instanceof grammar.Absyn.InjectiveOutputCondition)
    {
       grammar.Absyn.InjectiveOutputCondition _injectiveoutputcondition = (grammar.Absyn.InjectiveOutputCondition) foo;
       render("InjectiveOutputCondition");
    }
    if (foo instanceof grammar.Absyn.InjectiveInputCondition)
    {
       grammar.Absyn.InjectiveInputCondition _injectiveinputcondition = (grammar.Absyn.InjectiveInputCondition) foo;
       render("InjectiveInputCondition");
    }
    if (foo instanceof grammar.Absyn.CopycatCondition)
    {
       grammar.Absyn.CopycatCondition _copycatcondition = (grammar.Absyn.CopycatCondition) foo;
       render("CopycatCondition");
    }
    if (foo instanceof grammar.Absyn.OutputUniversalCondition)
    {
       grammar.Absyn.OutputUniversalCondition _outputuniversalcondition = (grammar.Absyn.OutputUniversalCondition) foo;
       render("OutputUniversalCondition");
    }
    if (foo instanceof grammar.Absyn.InputUniversalCondition)
    {
       grammar.Absyn.InputUniversalCondition _inputuniversalcondition = (grammar.Absyn.InputUniversalCondition) foo;
       render("InputUniversalCondition");
    }
  }

  private static void sh(grammar.Absyn.IndexSequenceRule foo)
  {
    if (foo instanceof grammar.Absyn.IndexSequence)
    {
       grammar.Absyn.IndexSequence _indexsequence = (grammar.Absyn.IndexSequence) foo;
       render("(");
       render("IndexSequence");
       render("[");
       sh(_indexsequence.listnumberspacerule_);
       render("]");
       render(")");
    }
  }

  private static void sh(grammar.Absyn.NumberSpaceRule foo)
  {
    if (foo instanceof grammar.Absyn.NumberSpace)
    {
       grammar.Absyn.NumberSpace _numberspace = (grammar.Absyn.NumberSpace) foo;
       render("(");
       render("NumberSpace");
       sh(_numberspace.integer_);
       render(")");
    }
  }

  private static void sh(grammar.Absyn.NameSequenceRule foo)
  {
    if (foo instanceof grammar.Absyn.NameSequence)
    {
       grammar.Absyn.NameSequence _namesequence = (grammar.Absyn.NameSequence) foo;
       render("(");
       render("NameSequence");
       render("[");
       sh(_namesequence.listnamespacerule_);
       render("]");
       render(")");
    }
  }

  private static void sh(grammar.Absyn.NameSpaceRule foo)
  {
    if (foo instanceof grammar.Absyn.NameSpace)
    {
       grammar.Absyn.NameSpace _namespace = (grammar.Absyn.NameSpace) foo;
       render("(");
       render("NameSpace");
       sh(_namespace.name_);
       render(")");
    }
  }

  private static void sh(grammar.Absyn.Name foo)
  {
    if (foo instanceof grammar.Absyn.NumberName)
    {
       grammar.Absyn.NumberName _numbername = (grammar.Absyn.NumberName) foo;
       render("(");
       render("NumberName");
       sh(_numbername.integer_);
       render(")");
    }
    if (foo instanceof grammar.Absyn.LiteralName)
    {
       grammar.Absyn.LiteralName _literalname = (grammar.Absyn.LiteralName) foo;
       render("(");
       render("LiteralName");
       sh(_literalname.ident_);
       render(")");
    }
  }

  private static void sh(grammar.Absyn.ListStatement foo)
  {
     for (java.util.Iterator<Statement> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.ListSymmetryPairRule foo)
  {
     for (java.util.Iterator<SymmetryPairRule> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.ListTransitionRule foo)
  {
     for (java.util.Iterator<TransitionRule> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.ListSymInstance foo)
  {
     for (java.util.Iterator<SymInstance> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.ListGeneratorRule foo)
  {
     for (java.util.Iterator<GeneratorRule> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.ListImpossiblePairRule foo)
  {
     for (java.util.Iterator<ImpossiblePairRule> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.ListAutomataTransitionRule foo)
  {
     for (java.util.Iterator<AutomataTransitionRule> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.ListCheckingConditionRule foo)
  {
     for (java.util.Iterator<CheckingConditionRule> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.ListNumberSpaceRule foo)
  {
     for (java.util.Iterator<NumberSpaceRule> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.ListName foo)
  {
     for (java.util.Iterator<Name> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.ListNameSpaceRule foo)
  {
     for (java.util.Iterator<NameSpaceRule> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }


  private static void pp(Integer n, int _i_) { buf_.append(n); buf_.append(" "); }
  private static void pp(Double d, int _i_) { buf_.append(d); buf_.append(" "); }
  private static void pp(String s, int _i_) { buf_.append(s); buf_.append(" "); }
  private static void pp(Character c, int _i_) { buf_.append("'" + c.toString() + "'"); buf_.append(" "); }
  private static void sh(Integer n) { render(n.toString()); }
  private static void sh(Double d) { render(d.toString()); }
  private static void sh(Character c) { render(c.toString()); }
  private static void sh(String s) { printQuoted(s); }
  private static void printQuoted(String s) { render("\"" + s + "\""); }
  private static void indent()
  {
    int n = _n_;
    while (n > 0)
    {
      buf_.append(" ");
      n--;
    }
  }
  private static void backup()
  {
     if (buf_.charAt(buf_.length() - 1) == ' ') {
      buf_.setLength(buf_.length() - 1);
    }
  }
  private static void trim()
  {
     while (buf_.length() > 0 && buf_.charAt(0) == ' ')
        buf_.deleteCharAt(0); 
    while (buf_.length() > 0 && buf_.charAt(buf_.length()-1) == ' ')
        buf_.deleteCharAt(buf_.length()-1);
  }
  private static int _n_ = 0;
  private static StringBuilder buf_ = new StringBuilder(INITIAL_BUFFER_SIZE);
}

