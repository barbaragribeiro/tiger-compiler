package Absyn;

import Symbol.Symbol;

public class FieldVar extends Var {
	public Var var;
	public Symbol field;

	public FieldVar(Var v, Symbol f) {
		var = v;
		field = f;
	}
}
