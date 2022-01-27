package Absyn;

public class WhileExp extends Exp {
	public Exp test, body;

	public WhileExp(Exp t, Exp b) {
		test = t;
		body = b;
	}
}
