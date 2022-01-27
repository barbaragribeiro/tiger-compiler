package Absyn;

import Symbol.Symbol;

public class TypeDec extends Dec {
	public Symbol name;
	public Ty ty;

	public TypeDec(Symbol n, Ty t) {
		name = n;
		ty = t;
	}
}
