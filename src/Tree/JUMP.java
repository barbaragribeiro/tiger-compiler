package Tree;

import Temp.*;

public class JUMP extends TExp {
  public TExp exp;
  public LabelList targets;

  public JUMP(TExp e, LabelList t) {
    super(null);
    exp = e;
    targets = t;
  }

  public JUMP(Label target) {
    this(new NAME(target.toString()), new LabelList(target, null));
  }
}