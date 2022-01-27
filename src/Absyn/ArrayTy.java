package Absyn;

import Symbol.Symbol;

public class ArrayTy extends Ty {
	public Symbol typ;

	public ArrayTy(Symbol t) {
		typ = t;
	}
}
