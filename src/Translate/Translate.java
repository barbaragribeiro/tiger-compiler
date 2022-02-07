package Translate;

import Tree.*;
import Absyn.OpExp;
import Temp.*;

import java.util.ArrayList;

public class Translate {
    public static int wordSize = 4;

    public static boolean isStm(TExp e) {
        if (e instanceof Tree.BINOP ||
            e instanceof Tree.CALL  ||
            e instanceof Tree.CONST ||
            e instanceof Tree.ESEQ  ||
            e instanceof Tree.MEM   ||
            e instanceof Tree.NAME  ||
            e instanceof Tree.TEMP)
            return false;

        return true;
    }
    
    public static TExp translateInt(int value) {
		return new CONST(value);
	}

    public static TExp translateNilExp() {
        return new CONST(0);
    }

    public static TExp translateStringExp(String str) {
        return new NAME(str);
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
		return new MEM(new BINOP(OpExp.PLUS, var, new CONST(wordSize * offset)));
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

    //RecordExp
    public static TExp translateRecordExp(TExp aloca, TExp init) {
        return new SEQ(aloca, init);
    }

    public static TExp translateRecordAssignExp(TExp left, TExp right) {
        return new ESEQ(left, right);
    }

    public static TExp translateIfCondExp(int oper, TExp left, TExp right, Label l1, Label l2) {
        int rev_oper = CJUMP.notRel(oper);
        return new CJUMP(rev_oper, left, right, l2, l1);
    }

    public static TExp translateIfExp(TExp jump, TExp then, TExp senao, Label l1, Label l2, Label l3) {
        return translateExpList(jump, 
                    translateExpList(new LABEL(l1), 
                        translateExpList(then, 
                            translateExpList(new JUMP(l3), 
                                translateExpList(new LABEL(l2), 
                                    translateExpList(senao, new LABEL(l3)))))));
    }

    public static TExp translateWhileExp(TExp jump, TExp body, Label test, Label in, Label out) {
        return translateExpList(new LABEL(test),
                    translateExpList(jump, 
                        translateExpList(new LABEL(in), 
                            translateExpList(body, 
                                translateExpList(new JUMP(test), new LABEL(out))))));
        // return translateExpList(
                    // translateExpList(jump,
                        // translateExpList(new LABEL(in), 
                            // translateExpList(body, 
                                // new JUMP(test)))),
                    // new LABEL(l3));
    }

    public static TExp translateBreak(Label escape) {
        return new JUMP(escape);
    }

    public static TExp translateFor(TExp dec, TExp upper, TExp body, Label in, Label out, Label increment) {
        // for i := 0 to 10 do exp  ->  i := 0; while (i < 10) do (exp; i++;)
        TExp var = ((MOVE) dec).dest;
        TExp add = new MOVE(var, new BINOP(OpExp.PLUS, var, new CONST(1)));
        TExp jump = new CJUMP(OpExp.LE, var, upper, in, out);
        return  translateExpList(dec,
                    translateExpList(jump, 
                        translateExpList(new LABEL(in), 
                            translateExpList(body, 
                                translateExpList(new CJUMP(OpExp.LT, var, upper, increment, out),
                                    translateExpList(new LABEL(increment),
                                        translateExpList(add,
                                            translateExpList(new JUMP(in), new LABEL(out)))))))));
    }

    //LET
    public static TExp translateLetExp(TExp declist, TExp expseq) {
        if(declist == null) {
            return new TExp(expseq);
        }

        if(isStm(declist) && isStm(expseq))
            return new SEQ(declist, expseq);

        if(isStm(declist) && !isStm(expseq))
            return new ESEQ(declist, expseq);

        if(!isStm(declist) && isStm(expseq))
            return new SEQ(new TExp(declist), expseq);

        else
            return new ESEQ(new TExp(declist), expseq);
    }

    //DecList
    public static TExp translateDecList(TExp head, TExp tail) {
        if(isStm(head) && isStm(tail))
            return new SEQ(head, tail);

        if(isStm(head) && !isStm(tail))
            return new ESEQ(head, tail);

        if(!isStm(head) && isStm(tail))
            return new SEQ(new TExp(head), tail);

        else
            return new ESEQ(new TExp(head), tail);
    }

    //ExpList
    public static TExp translateExpList(TExp head, TExp tail) {
        if(isStm(head) && isStm(tail))
            return new SEQ(head, tail);

        if(isStm(head) && !isStm(tail))
            return new ESEQ(head, tail);

        if(!isStm(head) && isStm(tail))
            return new SEQ(new TExp(head), tail);

        else
            return new ESEQ(new TExp(head), tail);
    }
}
