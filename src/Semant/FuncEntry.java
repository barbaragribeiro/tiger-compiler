package Semant;

import Types.*;

public class FuncEntry extends Entry {
	RECORD paramlist;
	public int level;
	public String label;

	public FuncEntry(int level, String label, RECORD p, Type returnType) {
		super(returnType);
		paramlist = p;
		this.level = level;
		this.label = label;
	}
}