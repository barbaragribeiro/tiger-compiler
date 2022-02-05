package Tree;

import Temp.Label;

public class CJUMP extends TExp {
  public int relop;
  public TExp left, right;
  public Label iftrue, iffalse;

  public CJUMP(int rel, TExp l, TExp r, Label t, Label f) {
    super(null);
    relop = rel;
    left = l;
    right = r;
    iftrue = t;
    iffalse = f;
  }

  public final static int EQ = 0, NE = 1, LT = 2, GT = 3, LE = 4, GE = 5, ULT = 6, ULE = 7, UGT = 8, UGE = 9;

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
    case ULT:
      return UGE;
    case UGE:
      return ULT;
    case UGT:
      return ULE;
    case ULE:
      return UGT;
    default:
      throw new Error("bad relop in CJUMP.notRel");
    }
  }
}