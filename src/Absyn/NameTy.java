package Absyn;

import Symbol.Symbol;

public class NameTy extends Ty {
	public Symbol name;

	public NameTy(Symbol n) {
		name = n;
	}
}
