package Absyn;
import Symbol.Symbol;
public class VarDec extends Dec {
   public Symbol name;
   public boolean escape = true;
   public NameTy typ; /* optional */
   public Exp init;
   public VarDec(Symbol n, NameTy t, Exp i) {name=n; typ=t; init=i;}
}