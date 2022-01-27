package Absyn;

public class OpExp extends Exp {
	public Exp left, right;
	public int oper;

	public OpExp(Exp l, int o, Exp r) {
		left = l;
		oper = o;
		right = r;
	}

	public static String opToStr(int oper) {
		if (oper == PLUS) {
			return "+";
		}
		if (oper == MINUS) {
			return "-";
		}
		if (oper == MUL) {
			return "*";
		}
		if (oper == DIV) {
			return "/";
		}
		if (oper == EQ) {
			return "=";
		}
		if (oper == NE) {
			return "<>";
		} 
		if (oper == GE) {
			return ">=";
		}
		if (oper == LE) {
			return "<=";
		}
		if (oper == LT) {
			return "<";
		}
		if (oper == GT) {
			return ">";
		}
		return null;
	}

	public final static int PLUS = 0, MINUS = 1, MUL = 2, DIV = 3, EQ = 4, NE = 5, LT = 6, LE = 7, GT = 8, GE = 9;
}
