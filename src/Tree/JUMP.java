package Tree;

import Semant.Label;

public class JUMP extends TExp {
  public TExp exp;

  public JUMP(Label target) {
    exp = new NAME(target.toString());
  }
}