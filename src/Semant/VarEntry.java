package Semant;

import Types.*;

public class VarEntry extends Entry {
	public String temp;

    public VarEntry(Type typ, String t) {
		super(typ);
		temp = t;
	}
}