/* Generated By:JavaCC: Do not edit this line. PTAParser.java */
package pta.parser;

import java.io.*;
import java.util.*;

import pta.*;
import prism.PrismLangException;

public class PTAParser implements PTAParserConstants {
        //-----------------------------------------------------------------------------------
        // Main method for testing purposes
        //-----------------------------------------------------------------------------------

        public static void main(String[] args)
        {
                PTAParser p = null;
                InputStream str = null;
                String src = null;

                try {
                        p = new PTAParser();
                        str = (args.length > 0) ? new FileInputStream(args[0]) : System.in;
                        src = (args.length > 0) ? "file "+args[0] : "stdin";
                        System.out.println("Reading from "+src+"...\n");

                        PTA pta = p.parsePTA(str);
                        System.out.print(pta);
                }
                catch (PrismLangException e) {
                        System.out.println("Error in "+src+": " + e.getMessage()+"."); System.exit(1);
                }
                catch (FileNotFoundException e) {
                        System.out.println(e); System.exit(1);
                }
        }

        //-----------------------------------------------------------------------------------
        // Methods called by Prism
        //-----------------------------------------------------------------------------------

        // Constructor

        public PTAParser()
        {
                // Call default constructor
                this(System.in);
        }

        // Parse PTA

        public PTA parsePTA(InputStream str) throws PrismLangException
        {
                astPTA pta = null;

                // (Re)start parser
                ReInit(str);
                // Parse
                try {
                        pta = PTA();
                }
                catch (ParseException e) {
                        throw new PrismLangException(e.getMessage());
                }

                return pta.createDataStructures();
        }

        //------------------------------------------------------------------------------
        // Abstract syntax tree classes
        //------------------------------------------------------------------------------

        // Classes used to build AST representing PTA.
        // Note: locations are indexed by name here, not integer as in the normal PTA class
        // (this is the main reason for needing separate AST classes here).
        // For clocks, this approach is not needed: we just take the ordering of the clocks
        // to be as they appear in the file (unlike locations, which have an explicit ordering
        // combined with possible forward references).

        static class astPTA
        {
                // Data
                public ArrayList<String> clockNames;
                public ArrayList<String> locationNames;
                public HashMap<String,LinkedHashSet<Constraint>> invariants;
                public HashMap<String,ArrayList<astTransition>> transitions;
                // Methods
        public astPTA() {
                clockNames = new ArrayList<String>();
                locationNames = new ArrayList<String>();
                        invariants = new HashMap<String,LinkedHashSet<Constraint>>();
                transitions = new HashMap<String,ArrayList<astTransition>>();
        }
                public int getOrAddClock(String name) {
                        int i = clockNames.indexOf(name);
                        if (i == -1) { clockNames.add(name); return clockNames.size(); }
                        else return i+1;
                }
                public void addLocation(String name) {
                        locationNames.add(name);
                        invariants.put(name, new LinkedHashSet<Constraint>());
                        transitions.put(name, new ArrayList<astTransition>());
                }
                public void addInvariantCondition(String locName, Constraint c) {
                        invariants.get(locName).add(c);
                }
                public void setInvariantConditions(String locName, LinkedHashSet<Constraint> cs) {
                        invariants.put(locName, cs);
                }
                public astTransition addTransition(String locName) {
                        astTransition t = new astTransition();
                        transitions.get(locName).add(t);
                        return t;
                }
                public int getLocationIndex(String name) {
                        return locationNames.indexOf(name);
                }
                // Conversion to pta classes
                public PTA createDataStructures()
                {
                        int i, n;
                        String name;
                        PTA pta;
                        Transition trans;
                        pta = new PTA();
                        // Add all clocks
                        n = clockNames.size();
                        for (i = 0; i < n; i++)
                                pta.addClock(clockNames.get(i));
                        // Add all locations
                        n = locationNames.size();
                        for (i = 0; i < n; i++)
                                pta.addLocation(locationNames.get(i));
                        // Add invariants/transitions to locations
                        n = locationNames.size();
                        for (i = 0; i < n; i++) {
                                name = locationNames.get(i);
                                pta.setInvariantConditions(i, invariants.get(name));
                                ArrayList<astTransition> tt = transitions.get(name);
                                if (tt == null || tt.isEmpty()) continue;
                                for (astTransition t : tt) {
                                        if (!(t.edges.isEmpty())) {
                                                trans = pta.addTransition(i, t.action);
                                                t.createDataStructures(this, trans);
                                        }
                                }
                        }
                        return pta;
                }
        }

        static class astTransition
        {
                // Data
                private String action = null;
                private ArrayList<Constraint> guard;
                public ArrayList<astEdge> edges;
                // Methods
                public astTransition() { guard = new ArrayList<Constraint>(); edges = new ArrayList<astEdge>(); }
                public void setAction(String action) { this.action = action; }
                public void addGuardConstraint(Constraint c) { guard.add(c); }
                public astEdge addEdge(double prob, String dest) { astEdge e = new astEdge(prob, dest); edges.add(e); return e; }
                // Conversion to pta classes
                public void createDataStructures(astPTA pta, Transition trans)
                {
                        for (Constraint c : guard)
                                trans.addGuardConstraint(c);
                        for (astEdge e : edges)
                                e.createDataStructures(pta, trans);
                }
        }

        static class astEdge
        {
                // Data
                public double prob;
                public String dest;
                public HashMap<Integer,Integer> resets;
                // Methods
                public astEdge(double prob, String dest) { this.prob = prob; this.dest = dest; resets = new HashMap<Integer,Integer>(); }
                public void addReset(int clock, int val) { resets.put(clock, val); }
                // Conversion to pta classes
                public void createDataStructures(astPTA pta, Transition trans)
                {
                        int d = pta.getLocationIndex(dest);
                        if (d == -1) { System.err.println("Error: Location \""+dest+"\" does not exist"); System.exit(1); }
                        Edge edge = trans.addEdge(prob, d);
                        for (Map.Entry<Integer,Integer> e : resets.entrySet()) edge.addReset(e.getKey(), e.getValue());
                }
        }

//-----------------------------------------------------------------------------------
// Top-level production
//-----------------------------------------------------------------------------------

// PTA
  static final public astPTA PTA() throws ParseException {
        astPTA pta = new astPTA();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LBRACE:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      Location(pta);
    }
    jj_consume_token(0);
                {if (true) return pta;}
    throw new Error("Missing return statement in function");
  }

  static final public void Location(astPTA pta) throws ParseException {
        String name;
        LinkedHashSet<Constraint> constrs;
    jj_consume_token(LBRACE);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INIT:
      jj_consume_token(INIT);
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    jj_consume_token(NODE);
    name = Identifier();
                                   pta.addLocation(name);
    jj_consume_token(SEMICOLON);
    constrs = ConstraintList(pta);
                                                  pta.setInvariantConditions(name, constrs);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LBRACKET:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
      Transition(pta, name);
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case RBRACE:
      jj_consume_token(RBRACE);
      break;
    case TIMES:
      jj_consume_token(TIMES);
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void Transition(astPTA pta, String locName) throws ParseException {
        astTransition tr;
    jj_consume_token(LBRACKET);
                     tr = pta.addTransition(locName);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TRAN:
      case REG_IDENT:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_3;
      }
      Edge(pta, tr);
    }
    jj_consume_token(RBRACKET);
  }

  static final public void Edge(astPTA pta, astTransition tr) throws ParseException {
        LinkedHashSet<Constraint> constrs;
        String action = null, dest;
        double p;
        astEdge edge;
        HashMap<Integer,Integer> resets;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case REG_IDENT:
      action = Identifier();
      jj_consume_token(OR);
      jj_consume_token(OR);
      break;
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    jj_consume_token(TRAN);
    dest = Identifier();
    jj_consume_token(SEMICOLON);
    constrs = ConstraintList(pta);
    jj_consume_token(SEMICOLON);
    resets = Resets(pta);
    jj_consume_token(SEMICOLON);
    p = Probability();
                tr.setAction(action);
                edge = tr.addEdge(p, dest);
                for (Map.Entry<Integer,Integer> e : resets.entrySet()) edge.addReset(e.getKey(), e.getValue());
                for (Constraint c : constrs) tr.addGuardConstraint(c);
  }

  static final public LinkedHashSet<Constraint> ConstraintList(astPTA pta) throws ParseException {
        LinkedHashSet<Constraint> constrs = new LinkedHashSet<Constraint>();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case REG_IDENT:
      Constraint(pta, constrs);
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[6] = jj_gen;
          break label_4;
        }
        jj_consume_token(COMMA);
        Constraint(pta, constrs);
      }
      break;
    case TRUE:
      jj_consume_token(TRUE);
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
          {if (true) return constrs;}
    throw new Error("Missing return statement in function");
  }

  static final public void Constraint(astPTA pta, LinkedHashSet<Constraint> constrs) throws ParseException {
        String clock1Name, clock2Name;
        int clock1, clock2, val;
        Token t;
    clock1Name = Identifier();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LT:
      t = jj_consume_token(LT);
      break;
    case LE:
      t = jj_consume_token(LE);
      break;
    case GT:
      t = jj_consume_token(GT);
      break;
    case GE:
      t = jj_consume_token(GE);
      break;
    case EQ:
      t = jj_consume_token(EQ);
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case REG_INT:
      val = Integer();
                clock1 = pta.getOrAddClock(clock1Name);
                switch (t.kind) {
                case PTAParserConstants.LT:
                        constrs.add(Constraint.buildLt(clock1, val)); break;
                case PTAParserConstants.LE:
                        constrs.add(Constraint.buildLeq(clock1, val)); break;
                case PTAParserConstants.GT:
                        constrs.add(Constraint.buildGt(clock1, val)); break;
                case PTAParserConstants.GE:
                        constrs.add(Constraint.buildGeq(clock1, val)); break;
                case PTAParserConstants.EQ:
                        constrs.add(Constraint.buildLeq(clock1, val));
                        constrs.add(Constraint.buildGeq(clock1, val)); break;
                }
      break;
    case REG_IDENT:
      clock2Name = Identifier();
                clock1 = pta.getOrAddClock(clock1Name);
                clock2 = pta.getOrAddClock(clock2Name);
                switch (t.kind) {
                case PTAParserConstants.LT:
                        constrs.add(Constraint.buildLt(clock1, clock2)); break;
                default:
                        System.err.println("Error: Unsupported constraint type"); System.exit(1);
                }
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public HashMap<Integer,Integer> Resets(astPTA pta) throws ParseException {
        HashMap<Integer,Integer> resets = new HashMap<Integer,Integer>();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case REG_IDENT:
      Reset(pta, resets);
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[10] = jj_gen;
          break label_5;
        }
        jj_consume_token(COMMA);
        Reset(pta, resets);
      }
      break;
    case NULL:
      jj_consume_token(NULL);
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
          {if (true) return resets;}
    throw new Error("Missing return statement in function");
  }

  static final public void Reset(astPTA pta, HashMap<Integer,Integer> resets) throws ParseException {
        String clockName;
        int clock;
        int val;
    clockName = Identifier();
    jj_consume_token(EQ);
    val = Integer();
                clock = pta.getOrAddClock(clockName);
                resets.put(clock, val);
  }

  static final public double Probability() throws ParseException {
        Token t;
        double d;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case REG_DOUBLE:
      t = jj_consume_token(REG_DOUBLE);
      break;
    case REG_INT:
      t = jj_consume_token(REG_INT);
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
          {if (true) return Double.parseDouble(t.image);}
    throw new Error("Missing return statement in function");
  }

//-----------------------------------------------------------------------------------
// Miscellaneous stuff
//-----------------------------------------------------------------------------------

// Identifier (returns String)
  static final public String Identifier() throws ParseException {
    jj_consume_token(REG_IDENT);
                      {if (true) return getToken(0).image;}
    throw new Error("Missing return statement in function");
  }

// Integer
  static final public int Integer() throws ParseException {
    jj_consume_token(REG_INT);
                    {if (true) return Integer.parseInt(getToken(0).image);}
    throw new Error("Missing return statement in function");
  }

  static private boolean jj_initialized_once = false;
  static public PTAParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  static public Token token, jj_nt;
  static private int jj_ntk;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[13];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_0();
      jj_la1_1();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x200000,0x8,0x80000,0x80400000,0x40,0x0,0x8000,0x80,0x1e800000,0x0,0x8000,0x20,0x0,};
   }
   private static void jj_la1_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x100,0x100,0x0,0x100,0x0,0x120,0x0,0x100,0x60,};
   }

  public PTAParser(java.io.InputStream stream) {
     this(stream, null);
  }
  public PTAParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new PTAParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
  }

  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
  }

  public PTAParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new PTAParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
  }

  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
  }

  public PTAParser(PTAParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
  }

  public void ReInit(PTAParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 13; i++) jj_la1[i] = -1;
  }

  static final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.Vector jj_expentries = new java.util.Vector();
  static private int[] jj_expentry;
  static private int jj_kind = -1;

  static public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[42];
    for (int i = 0; i < 42; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 13; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 42; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  static final public void enable_tracing() {
  }

  static final public void disable_tracing() {
  }

}