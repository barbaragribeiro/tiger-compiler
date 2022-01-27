package Absyn;

public class SeqExp extends Exp {
	public ExpList list;

	public SeqExp(ExpList l) {
		list = l;
	}
}
