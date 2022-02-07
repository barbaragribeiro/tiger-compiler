package Absyn;

import Symbol.Symbol;

public class FunctionDec extends Dec {
	public Symbol name;
	public FieldList params;
	public NameTy result;
	public Exp body;
	public FunctionDec next;

	public FunctionDec(Symbol n, FieldList a, NameTy r, Exp b, FunctionDec nxt) {
		name = n;
		params = a;
		result = r;
		body = b;
		next = nxt;
	}
}
