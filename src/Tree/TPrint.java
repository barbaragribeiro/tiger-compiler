package Tree;
import Absyn.OpExp;

public class TPrint {

  java.io.PrintStream out;

  public TPrint(java.io.PrintStream o) {
    out = o;
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
    case OpExp.EQ:
      say("EQ");
      break;
    case OpExp.NE:
      say("NE");
      break;
    case OpExp.LT:
      say("LT");
      break;
    case OpExp.GT:
      say("GT");
      break;
    case OpExp.LE:
      say("LE");
      break;
    case OpExp.GE:
      say("GE");
      break;
    default:
      throw new Error("TPrint.prStm.CJUMP");
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
    case OpExp.PLUS:
      say("PLUS");
      break;
    case OpExp.MINUS:
      say("MINUS");
      break;
    case OpExp.MUL:
      say("MUL");
      break;
    case OpExp.DIV:
      say("DIV");
      break;
    case OpExp.EQ:
      say("EQ");
      break;
    case OpExp.NE:
      say("NE");
      break;
    case OpExp.LE:
      say("LE");
      break;
    case OpExp.LT:
      say("LT");
      break;
    case OpExp.GE:
      say("GE");
      break;
    case OpExp.GT:
      say("GT");
      break;
    default:
      throw new Error("TPrint.prExp.BINOP");
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
    prExp(e.addr, d + 1);
    say(")");
  }

  void prExp(TEMP e, int d) {
    indent(d);
    if ((e.t == "rv") || (e.t == "fp")) {
      say("TEMP ");
    }
    else {
      say("TEMP t");
    }
    say(e.t);
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
    sayln(",");
    prExp(e.returnTemp, d + 2);
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
    else if (e instanceof EXP)
      prStm((EXP) e, d);
    else {
      throw new Error("TPrint.prExp");
    }
  }

  public void prStm(EXP e, int d){
    indent(d);
    sayln("EXP(");
    prExp(e.exp, d + 1);
    say(")");
  }

  public void prExp(TExp e) {
    prExp(e, 0);
    say("\n");
  }

}