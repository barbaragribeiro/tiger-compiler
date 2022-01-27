package Absyn;

import Symbol.Symbol;

public class FieldExpList extends Absyn {
	public Symbol name;
	public Exp init;
	public FieldExpList tail;

	public FieldExpList(Symbol n, Exp i, FieldExpList t) {
		name = n;
		init = i;
		tail = t;
	}
}
