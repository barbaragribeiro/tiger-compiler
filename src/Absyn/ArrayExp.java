package Absyn;

import Symbol.Symbol;

public class ArrayExp extends Exp {
	public Symbol typ;
	public Exp size, init;

	public ArrayExp(Symbol t, Exp s, Exp i) {
		typ = t;
		size = s;
		init = i;
	}
}
