package Absyn;

import Symbol.Symbol;

public class FieldList extends Absyn {
	public Symbol name;
	public Symbol typ;
	public FieldList tail;
	public boolean escape = true;

	public FieldList(Symbol n, Symbol t, FieldList x) {
		name = n;
		typ = t;
		tail = x;
	}
}
