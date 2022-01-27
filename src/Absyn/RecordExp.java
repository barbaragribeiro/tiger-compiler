package Absyn;

import Symbol.Symbol;

public class RecordExp extends Exp {
	public Symbol typ;
	public FieldExpList fields;

	public RecordExp(Symbol t, FieldExpList f) {
		typ = t;
		fields = f;
	}
}
