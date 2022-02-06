package Semant;

import Types.*;

public class FuncTree extends LabelTree{
  public ExpTy exp;

  public FuncTree(String label_, ExpTy exp_) {
    super(label_);
    exp = exp_;
  }
}