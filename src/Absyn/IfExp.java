package Absyn;

public class IfExp extends Exp {
	public Exp test;
	public Exp thenclause;
	public Exp elseclause;

	public IfExp(Exp x, Exp y) {
		test = x;
		thenclause = y;
		elseclause = null;
	}

	public IfExp(Exp x, Exp y, Exp z) {
		test = x;
		thenclause = y;
		elseclause = z;
	}
}
