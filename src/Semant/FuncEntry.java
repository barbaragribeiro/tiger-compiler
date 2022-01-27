package Semant;

import Types.*;

public class FuncEntry extends Entry {
	RECORD paramlist;
	public String label;

	public FuncEntry(int lvl, String lbl, RECORD p, Type returnType) {
		super(returnType, lvl);
		paramlist = p;
		label = lbl;
	}
}