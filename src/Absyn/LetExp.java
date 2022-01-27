package Absyn;

public class LetExp extends Exp {
	public DecList decs;
	public SeqExp body;

	public LetExp(DecList d, SeqExp b) {
		decs = d;
		body = b;
	}
}
