package Tree;

import Temp.Label;

public class CJUMP extends TExp {
  public int relop;
  public TExp left, right;
  public Label iftrue, iffalse;

  public CJUMP(int rel, TExp l, TExp r, Label t, Label f) {
    relop = rel;
    left = l;
    right = r;
    iftrue = t;
    iffalse = f;
  }

  public final static int EQ = 4, NE = 5, LT = 6, LE = 7, GT = 8, GE = 9;

  public static int notRel(int relop) {
    switch (relop) {
    case EQ:
      return NE;
    case NE:
      return EQ;
    case LT:
      return GE;
    case GE:
      return LT;
    case GT:
      return LE;
    case LE:
      return GT;
    default:
      throw new Error("bad relop in CJUMP.notRel");
    }
  }
}