package Absyn;

import Symbol.Symbol;

public class CallExp extends Exp {
	public Symbol func;
	public ExpList args;

	public CallExp(Symbol f, ExpList a) {
		func = f;
		args = a;
	}
}
