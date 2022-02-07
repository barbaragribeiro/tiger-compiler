package Tree;

public class BINOP extends TExp {
	public int op;
	public TExp texp1, texp2;

	public BINOP(int o, TExp te1, TExp te2) {
		texp2 = te2;
		texp1 = te1;
        op = o;
	}
}