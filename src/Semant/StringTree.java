package Semant;

import Types.*;

public class StringTree extends LabelTree{
  public String val;

  public StringTree(String label_, String val_) {
    super(label_);
    val = val_;
  }
}