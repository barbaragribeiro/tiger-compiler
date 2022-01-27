package Absyn;

public class LetExp extends Exp {
	public DecList decs;
	public Exp body;

	public LetExp(DecList d, Exp b) {
		decs = d;
		body = b;
	}
}
