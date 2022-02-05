package Tree;

public class TPrint {

  java.io.PrintStream out;
  // Temp.TempMap tmap;

  public TPrint(java.io.PrintStream o) {
    out = o;
    // tmap = new Temp.DefaultMap(); //TODO
  }

  void indent(int d) {
    for (int i = 0; i < d; i++)
      out.print(' ');
  }

  void say(String s) {
    out.print(s);
  }

  void sayln(String s) {
    say(s);
    say("\n");
  }

  void prStm(SEQ s, int d) {
    indent(d);
    sayln("SEQ(");
    prExp(s.left, d + 1);
    sayln(",");
    prExp(s.right, d + 1);
    say(")");
  }

  void prStm(LABEL s, int d) {
    indent(d);
    say("LABEL ");
    say(s.label.toString());
  }

  void prStm(JUMP s, int d) {
    indent(d);
    sayln("JUMP(");
    prExp(s.exp, d + 1);
    say(")");
  }

  void prStm(CJUMP s, int d) {
    indent(d);
    say("CJUMP(");
    switch (s.relop) {
    case CJUMP.EQ:
      say("EQ");
      break;
    case CJUMP.NE:
      say("NE");
      break;
    case CJUMP.LT:
      say("LT");
      break;
    case CJUMP.GT:
      say("GT");
      break;
    case CJUMP.LE:
      say("LE");
      break;
    case CJUMP.GE:
      say("GE");
      break;
    case CJUMP.ULT:
      say("ULT");
      break;
    case CJUMP.ULE:
      say("ULE");
      break;
    case CJUMP.UGT:
      say("UGT");
      break;
    case CJUMP.UGE:
      say("UGE");
      break;
    default:
      throw new Error("Print.prStm.CJUMP");
    }
    sayln(",");
    prExp(s.left, d + 1);
    sayln(",");
    prExp(s.right, d + 1);
    sayln(",");
    indent(d + 1);
    say(s.iftrue.toString());
    say(",");
    say(s.iffalse.toString());
    say(")");
  }

  void prStm(MOVE s, int d) {
    indent(d);
    sayln("MOVE(");
    prExp(s.dest, d + 1);
    sayln(",");
    prExp(s.source, d + 1);
    say(")");
  }

  void prExp(BINOP e, int d) {
    indent(d);
    say("BINOP(");
    switch (e.op) {
    case BINOP.PLUS:
      say("PLUS");
      break;
    case BINOP.MINUS:
      say("MINUS");
      break;
    case BINOP.MUL:
      say("MUL");
      break;
    case BINOP.DIV:
      say("DIV");
      break;
    case BINOP.AND:
      say("AND");
      break;
    case BINOP.OR:
      say("OR");
      break;
    case BINOP.LSHIFT:
      say("LSHIFT");
      break;
    case BINOP.RSHIFT:
      say("RSHIFT");
      break;
    case BINOP.ARSHIFT:
      say("ARSHIFT");
      break;
    case BINOP.XOR:
      say("XOR");
      break;
    default:
      throw new Error("Print.prExp.BINOP");
    }
    sayln(",");
    prExp(e.texp1, d + 1);
    sayln(",");
    prExp(e.texp2, d + 1);
    say(")");
  }

  void prExp(MEM e, int d) {
    indent(d);
    sayln("MEM(");
    prExp(e.exp, d + 1);
    say(")");
  }

  void prExp(TEMP e, int d) {
    indent(d);
    say("TEMP t");
    say(e.t); //TODO
  }

  void prExp(ESEQ e, int d) {
    indent(d);
    sayln("ESEQ(");
    prExp(e.stm, d + 1);
    sayln(",");
    prExp(e.exp, d + 1);
    say(")");

  }

  void prExp(NAME e, int d) {
    indent(d);
    say("NAME ");
    say(e.toString());
  }

  void prExp(CONST e, int d) {
    indent(d);
    say("CONST ");
    say(String.valueOf(e.value));
  }

  void prExp(CALL e, int d) {
    indent(d);
    sayln("CALL(");
    prExp(e.name, d + 1);
    for (Args a = e.args; a != null; a = a.tail) {
      sayln(",");
      prExp(a.head, d + 2);
    }
    say(")");
  }

  void prExp(TExp e, int d) {
    if (e instanceof BINOP)
      prExp((BINOP) e, d);
    else if (e instanceof MEM)
      prExp((MEM) e, d);
    else if (e instanceof TEMP)
      prExp((TEMP) e, d);
    else if (e instanceof ESEQ)
      prExp((ESEQ) e, d);
    else if (e instanceof NAME)
      prExp((NAME) e, d);
    else if (e instanceof CONST)
      prExp((CONST) e, d);
    else if (e instanceof CALL)
      prExp((CALL) e, d);


    else if (e instanceof SEQ)
      prStm((SEQ) e, d);
    else if (e instanceof LABEL)
      prStm((LABEL) e, d);
    else if (e instanceof JUMP)
      prStm((JUMP) e, d);
    else if (e instanceof CJUMP)
      prStm((CJUMP) e, d);
    else if (e instanceof MOVE)
      prStm((MOVE) e, d);
    else {
      indent(d);
      sayln("EXP(");
      prExp(e.exp, d + 1);
      say(")");
    }
  }

  public void prExp(TExp e) {
    prExp(e, 0);
    say("\n");
  }

}