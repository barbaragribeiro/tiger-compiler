package Absyn;

import Symbol.Symbol;

public class SimpleVar extends Var {
	public Symbol name;

	public SimpleVar(Symbol n) {
		name = n;
	}
}
