package Semant;

import Types.*;

public class VarEntry extends Entry {
	public int level;
	public String label;

    public VarEntry(Type typ) {
		super(typ);
	}
}