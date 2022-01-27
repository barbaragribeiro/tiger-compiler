package Translate;

import Tree.*;

public class Translate {
    public static TExp translateInt(int value) {
		return new CONST(value);
	}

    public static TExp translateNilExp() {
        return new CONST(0);
    }

    public static TExp translateStringExp(String str) {
        // TODO
        return null;
    }

    public static TExp translateOpExp(int op, TExp exp1, TExp exp2) {        
        return new BINOP(op, exp1, exp2);
	}

    public static TExp translateSimpleVar(int t) {
        return new TEMP(t);
    }

    public static TExp translateAssign(TExp var, TExp val) {
        return new MOVE(var, val); 
    }
}
