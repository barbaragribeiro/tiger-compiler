package Semant;

import Tree.*;
import Types.*;


public class ExpTy {
	public TExp texp;
	Type typ;

	ExpTy(TExp e, Type t) {
		texp = e;
		typ = t;
	}
}