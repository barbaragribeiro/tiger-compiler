package Translate;

import Tree.*;
import Absyn.OpExp;

import java.util.ArrayList;

public class Translate {
    public static int wordSize = 4;
    
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

    public static TExp translateSimpleVar(String temp) {
        return new TEMP(temp);
    }

    public static TExp translateAssign(TExp var, TExp val) {
        return new MOVE(var, val); 
    }

    public static TExp translateSubscriptVar(TExp var, TExp idx, int elementSize) {
		TExp offset_texp = new BINOP(OpExp.MUL, idx, new CONST(wordSize * elementSize));
		return new MEM(new BINOP(OpExp.PLUS, var, offset_texp));
    }

    public static TExp translateFieldVar(TExp var, int offset) {
		return new MEM(new BINOP(OpExp.PLUS, var, new CONST(offset)));
    }

    public static TExp translateFunctionReturn(TExp body) {
        return new MOVE(new TEMP("rv"), body);
    }

    public static TExp translateCall(String name, boolean returnTemp, ArrayList<TExp> argsList) {
        Args args = (Args) translateArgsList(argsList);

        if (returnTemp) {
            return new CALL(new NAME(name), new TEMP("fp"), (Args) args);
        }
        else {
            return new CALL(new NAME(name), new CONST(0), (Args) args);
        }
    }

    public static TExp translateArgsList(ArrayList<TExp> argsList) {
        Args argsHead = null;
        Args previous = null;
        for (TExp a : argsList) {
            Args arg = new Args(a, null);
            if (argsHead == null) {
                argsHead = arg;
            }
            if (previous != null) {
                previous.tail = arg;
            }
            previous = arg;
        }

        return argsHead;
    }
}
