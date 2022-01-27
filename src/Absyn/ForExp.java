package Absyn;

public class ForExp extends Exp {
	public VarDec var;
	public Exp hi, body;

	public ForExp(VarDec v, Exp h, Exp b) {
		var = v;
		hi = h;
		body = b;
	}
}
