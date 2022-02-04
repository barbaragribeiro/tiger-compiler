package Absyn;

import Symbol.Symbol;

public class TypeDec extends Dec {
	public Symbol name;
	public Ty ty;
	public TypeDec next;

	public TypeDec(Symbol n, Ty t, TypeDec x) {
		name = n;
		ty = t;
		next = x;
	}
}
