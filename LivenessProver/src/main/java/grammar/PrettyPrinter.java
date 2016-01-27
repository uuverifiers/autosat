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
  public static String print(grammar.Absyn.AutomatonRule foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.AutomatonRule foo)
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
  public static String print(grammar.Absyn.VerifierOption foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.VerifierOption foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListVerifierOption foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListVerifierOption foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.SymmetryOption foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.SymmetryOption foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.ListSymmetryOption foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.ListSymmetryOption foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(grammar.Absyn.MaybeClosed foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(grammar.Absyn.MaybeClosed foo)
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
  /***   You shouldn't need to change anything beyond this point.   ***/

  private static void pp(grammar.Absyn.ModelRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.Model)
    {
       grammar.Absyn.Model _model = (grammar.Absyn.Model) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("I0");
       pp(_model.automatonrule_1, 0);
       pp(_model.maybeclosed_, 0);
       render("F");
       pp(_model.automatonrule_2, 0);
       render("P1");
       pp(_model.transducerrule_1, 0);
       render("P2");
       pp(_model.transducerrule_2, 0);
       pp(_model.listverifieroption_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.TransducerRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.Transducer)
    {
       grammar.Absyn.Transducer _transducer = (grammar.Absyn.Transducer) foo;
       if (_i_ > 0) render(_L_PAREN);
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

  private static void pp(grammar.Absyn.AutomatonRule foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.Automaton)
    {
       grammar.Absyn.Automaton _automaton = (grammar.Absyn.Automaton) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("{");
       pp(_automaton.automatainitrule_, 0);
       pp(_automaton.listautomatatransitionrule_, 0);
       pp(_automaton.automataacceptingsrule_, 0);
       render("}");
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

  private static void pp(grammar.Absyn.VerifierOption foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.NumOfStatesTransducerGuess)
    {
       grammar.Absyn.NumOfStatesTransducerGuess _numofstatestransducerguess = (grammar.Absyn.NumOfStatesTransducerGuess) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("transducerStateGuessing");
       render(":");
       pp(_numofstatestransducerguess.integer_1, 0);
       render("..");
       pp(_numofstatestransducerguess.integer_2, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.NumOfStatesAutomatonGuess)
    {
       grammar.Absyn.NumOfStatesAutomatonGuess _numofstatesautomatonguess = (grammar.Absyn.NumOfStatesAutomatonGuess) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("automatonStateGuessing");
       render(":");
       pp(_numofstatesautomatonguess.integer_1, 0);
       render("..");
       pp(_numofstatesautomatonguess.integer_2, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.NumOfInitStatesAutomatonGuess)
    {
       grammar.Absyn.NumOfInitStatesAutomatonGuess _numofinitstatesautomatonguess = (grammar.Absyn.NumOfInitStatesAutomatonGuess) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("initAutomatonStateGuessing");
       render(":");
       pp(_numofinitstatesautomatonguess.integer_1, 0);
       render("..");
       pp(_numofinitstatesautomatonguess.integer_2, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.SymmetryOptions)
    {
       grammar.Absyn.SymmetryOptions _symmetryoptions = (grammar.Absyn.SymmetryOptions) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("symmetries");
       render(":");
       pp(_symmetryoptions.listsymmetryoption_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.ExplicitChecks)
    {
       grammar.Absyn.ExplicitChecks _explicitchecks = (grammar.Absyn.ExplicitChecks) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("explicitChecksUntilLength");
       render(":");
       pp(_explicitchecks.integer_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.UseRankingFunctions)
    {
       grammar.Absyn.UseRankingFunctions _userankingfunctions = (grammar.Absyn.UseRankingFunctions) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("useRankingFunctions");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.ListVerifierOption foo, int _i_)
  {
     for (java.util.Iterator<VerifierOption> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(";");
       } else {
         render(";");
       }
     }
  }

  private static void pp(grammar.Absyn.SymmetryOption foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.RotationSymmetry)
    {
       grammar.Absyn.RotationSymmetry _rotationsymmetry = (grammar.Absyn.RotationSymmetry) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("rotation");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(grammar.Absyn.ListSymmetryOption foo, int _i_)
  {
     for (java.util.Iterator<SymmetryOption> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(grammar.Absyn.MaybeClosed foo, int _i_)
  {
    if (foo instanceof grammar.Absyn.ClosedInit)
    {
       grammar.Absyn.ClosedInit _closedinit = (grammar.Absyn.ClosedInit) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("closedUnderTransitions");
       render(";");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof grammar.Absyn.NotClosedInit)
    {
       grammar.Absyn.NotClosedInit _notclosedinit = (grammar.Absyn.NotClosedInit) foo;
       if (_i_ > 0) render(_L_PAREN);
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


  private static void sh(grammar.Absyn.ModelRule foo)
  {
    if (foo instanceof grammar.Absyn.Model)
    {
       grammar.Absyn.Model _model = (grammar.Absyn.Model) foo;
       render("(");
       render("Model");
       sh(_model.automatonrule_1);
       sh(_model.maybeclosed_);
       sh(_model.automatonrule_2);
       sh(_model.transducerrule_1);
       sh(_model.transducerrule_2);
       render("[");
       sh(_model.listverifieroption_);
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

  private static void sh(grammar.Absyn.AutomatonRule foo)
  {
    if (foo instanceof grammar.Absyn.Automaton)
    {
       grammar.Absyn.Automaton _automaton = (grammar.Absyn.Automaton) foo;
       render("(");
       render("Automaton");
       sh(_automaton.automatainitrule_);
       render("[");
       sh(_automaton.listautomatatransitionrule_);
       render("]");
       sh(_automaton.automataacceptingsrule_);
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

  private static void sh(grammar.Absyn.VerifierOption foo)
  {
    if (foo instanceof grammar.Absyn.NumOfStatesTransducerGuess)
    {
       grammar.Absyn.NumOfStatesTransducerGuess _numofstatestransducerguess = (grammar.Absyn.NumOfStatesTransducerGuess) foo;
       render("(");
       render("NumOfStatesTransducerGuess");
       sh(_numofstatestransducerguess.integer_1);
       sh(_numofstatestransducerguess.integer_2);
       render(")");
    }
    if (foo instanceof grammar.Absyn.NumOfStatesAutomatonGuess)
    {
       grammar.Absyn.NumOfStatesAutomatonGuess _numofstatesautomatonguess = (grammar.Absyn.NumOfStatesAutomatonGuess) foo;
       render("(");
       render("NumOfStatesAutomatonGuess");
       sh(_numofstatesautomatonguess.integer_1);
       sh(_numofstatesautomatonguess.integer_2);
       render(")");
    }
    if (foo instanceof grammar.Absyn.NumOfInitStatesAutomatonGuess)
    {
       grammar.Absyn.NumOfInitStatesAutomatonGuess _numofinitstatesautomatonguess = (grammar.Absyn.NumOfInitStatesAutomatonGuess) foo;
       render("(");
       render("NumOfInitStatesAutomatonGuess");
       sh(_numofinitstatesautomatonguess.integer_1);
       sh(_numofinitstatesautomatonguess.integer_2);
       render(")");
    }
    if (foo instanceof grammar.Absyn.SymmetryOptions)
    {
       grammar.Absyn.SymmetryOptions _symmetryoptions = (grammar.Absyn.SymmetryOptions) foo;
       render("(");
       render("SymmetryOptions");
       render("[");
       sh(_symmetryoptions.listsymmetryoption_);
       render("]");
       render(")");
    }
    if (foo instanceof grammar.Absyn.ExplicitChecks)
    {
       grammar.Absyn.ExplicitChecks _explicitchecks = (grammar.Absyn.ExplicitChecks) foo;
       render("(");
       render("ExplicitChecks");
       sh(_explicitchecks.integer_);
       render(")");
    }
    if (foo instanceof grammar.Absyn.UseRankingFunctions)
    {
       grammar.Absyn.UseRankingFunctions _userankingfunctions = (grammar.Absyn.UseRankingFunctions) foo;
       render("UseRankingFunctions");
    }
  }

  private static void sh(grammar.Absyn.ListVerifierOption foo)
  {
     for (java.util.Iterator<VerifierOption> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.SymmetryOption foo)
  {
    if (foo instanceof grammar.Absyn.RotationSymmetry)
    {
       grammar.Absyn.RotationSymmetry _rotationsymmetry = (grammar.Absyn.RotationSymmetry) foo;
       render("RotationSymmetry");
    }
  }

  private static void sh(grammar.Absyn.ListSymmetryOption foo)
  {
     for (java.util.Iterator<SymmetryOption> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(grammar.Absyn.MaybeClosed foo)
  {
    if (foo instanceof grammar.Absyn.ClosedInit)
    {
       grammar.Absyn.ClosedInit _closedinit = (grammar.Absyn.ClosedInit) foo;
       render("ClosedInit");
    }
    if (foo instanceof grammar.Absyn.NotClosedInit)
    {
       grammar.Absyn.NotClosedInit _notclosedinit = (grammar.Absyn.NotClosedInit) foo;
       render("NotClosedInit");
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

  private static void sh(grammar.Absyn.ListTransitionRule foo)
  {
     for (java.util.Iterator<TransitionRule> it = foo.iterator(); it.hasNext();)
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

  private static void sh(grammar.Absyn.ListName foo)
  {
     for (java.util.Iterator<Name> it = foo.iterator(); it.hasNext();)
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

