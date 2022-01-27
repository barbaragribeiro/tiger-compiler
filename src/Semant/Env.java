package Semant;

import Symbol.*;
import Types.*;

public class Env {
	Table varTable;
	Table typeTable; 
    int rootLvl;
	
	Env(int lvl) {
		rootLvl = lvl;
		typeTable = new Table();
		varTable = new Table();
	}

	public void installTypes() {
		typeTable.put(Symbol.symbol("int"), new TypeEntry(new INT(), rootLvl));
		typeTable.put(Symbol.symbol("string"), new TypeEntry(new STRING(), rootLvl));
	}

	public void installFunc(String name, Type resultType, RECORD param) {
		varTable.put(Symbol.symbol(name), new FuncEntry(rootLvl, name, param, resultType));
	}

	public void installStdFunctions() {
		installFunc("flush", new VOID(), null);
		installFunc("getchar", new STRING(), null);
		
		installFunc("allocRecord", new INT(), new RECORD(Symbol.symbol("size"), new INT(), null));
		installFunc("print", new VOID(), new RECORD(Symbol.symbol("s"), new STRING(), null));
		installFunc("ord", new INT(), new RECORD(Symbol.symbol("s"), new STRING(), null));
		installFunc("chr", new STRING(), new RECORD(Symbol.symbol("i"), new INT(), null));
		installFunc("size", new INT(), new RECORD(Symbol.symbol("s"), new STRING(), null));
		installFunc("exit", new VOID(), new RECORD(Symbol.symbol("i"), new INT(), null));

		RECORD param = null;
		param = new RECORD(Symbol.symbol("size"), new INT(), new RECORD(Symbol.symbol("init"), new INT(), null));
		installFunc("initArray", new INT(), param);
		
		param = new RECORD(Symbol.symbol("s2"), new STRING(), null);
		param = new RECORD(Symbol.symbol("s1"), new STRING(), param);
		installFunc("concat", new STRING(), param);
		
		param = new RECORD(Symbol.symbol("n"), new INT(), null);
		param = new RECORD(Symbol.symbol("first"), new INT(), param);
		param = new RECORD(Symbol.symbol("s"), new STRING(), param);
		installFunc("substring", new STRING(), param);
	}
}
