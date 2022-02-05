package Semant;

import Types.*;

public class FuncTree {
  public String label;
  public ExpTy exp;

  public FuncTree(String label_, ExpTy exp_) {
    label = label_;
    exp = exp_;
  }
}