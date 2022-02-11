package Semant;

import Symbol.*;
import Types.*;
import Translate.Translate;
import java.util.Arrays;
import java.util.HashSet;

public class Env {
	Table varTable;
	Table typeTable; 
  	int rootLvl;
	int nextTemp;
	int nextLabel;
	HashSet<String> stdFunctions;
	
	public Env(int lvl) {
		rootLvl = lvl;
		nextTemp = 0;
		nextLabel = 0;
		typeTable = new Table();
		varTable = new Table();
		installPrimitiveTypes();
		installStdFunctions();
	}

	private void installPrimitiveTypes() {
		typeTable.put(Symbol.symbol("int"), new TypeEntry(new INT()));
		typeTable.put(Symbol.symbol("string"), new TypeEntry(new STRING()));
		typeTable.put(Symbol.symbol("nil"), new TypeEntry(new NIL()));
		typeTable.put(Symbol.symbol("void"), new TypeEntry(new VOID()));
	}

	public FuncEntry installFunc(int lvl, Symbol name, Type resultType, RECORD param, String label) {
		FuncEntry entry;
		if (!stdFunctions.contains(name.toString())) {
			if(label != null) entry = new FuncEntry(lvl, label, param, resultType);
			else {
				entry = new FuncEntry(lvl, "L" + String.valueOf(nextLabel), param, resultType);
				System.out.println(name.toString() + " esta rotulada como L" + nextLabel);
				nextLabel += 1;
			}

			varTable.put(name, entry);
			return entry;
		}
		else {
			entry = new FuncEntry(lvl, name.toString(), param, resultType);
			varTable.put(name, entry);
			return entry;
		}
	}

	public int installLabel() {
		int curLabel = nextLabel;
		nextLabel += 1;
		return curLabel;
	}

	public int installTemp() {
		int curTemp = nextTemp;
		nextTemp += 1;
		return curTemp;
	}

	public VarEntry installVar(int lvl, Symbol name, Type type) {
		VarEntry entry = new VarEntry(type, String.valueOf(nextTemp));
		varTable.put(name, entry);
		System.out.println("Variavel \"" + name + "\" esta associada a t" + entry.temp);
		nextTemp += 1;
		return entry;
	}

	private void installStdFunctions() {
		stdFunctions = new HashSet<String>(Arrays.asList(
			"substring", "concat", "initArray",
			"exit", "size", "chr", "ord", "print", 
			"malloc", "getchar", "flush"
			)
		);

		Type intType = ((TypeEntry) typeTable.get(Symbol.symbol("int"))).typ;
		Type stringType = ((TypeEntry) typeTable.get(Symbol.symbol("string"))).typ;
		Type voidType = ((TypeEntry) typeTable.get(Symbol.symbol("void"))).typ;

		installFunc(0, Symbol.symbol("flush"), voidType, null, null);
		installFunc(0, Symbol.symbol("getchar"), stringType, null, null);
		installFunc(0, Symbol.symbol("malloc"), intType, new RECORD(null, Symbol.symbol("size"), intType, null), null);
		installFunc(0, Symbol.symbol("print"), voidType, new RECORD(null, Symbol.symbol("s"), stringType, null), null);
		installFunc(0, Symbol.symbol("ord"), intType, new RECORD(null, Symbol.symbol("s"), stringType, null), null);
		installFunc(0, Symbol.symbol("chr"), stringType, new RECORD(null, Symbol.symbol("i"), intType, null), null);
		installFunc(0, Symbol.symbol("size"), intType, new RECORD(null, Symbol.symbol("s"), stringType, null), null);
		installFunc(0, Symbol.symbol("exit"), voidType, new RECORD(null, Symbol.symbol("i"), intType, null), null);

		RECORD param = null;
		param = new RECORD(null, Symbol.symbol("size"), intType, new RECORD(null, Symbol.symbol("init"), intType, null));
		installFunc(0, Symbol.symbol("initArray"), intType, param, null);
		
		param = new RECORD(null, Symbol.symbol("s2"), stringType, null);
		param = new RECORD(null, Symbol.symbol("s1"), stringType, param);
		installFunc(0, Symbol.symbol("concat"), stringType, param, null);
		
		param = new RECORD(null, Symbol.symbol("n"), intType, null);
		param = new RECORD(null, Symbol.symbol("first"), intType, param);
		param = new RECORD(null, Symbol.symbol("s"), stringType, param);
		installFunc(0, Symbol.symbol("substring"), stringType, param, null);
	}
}
