package Tree;

public class BINOP extends TExp {
	public int op;
	public TExp texp1, texp2;

	public BINOP(int o, TExp te1, TExp te2) {
		super(null);
		texp2 = te2;
		texp1 = te1;
        op = o;
	}

	public final static int PLUS = 0, MINUS = 1, MUL = 2, DIV = 3, AND = 4, OR = 5, LSHIFT = 6, RSHIFT = 7, ARSHIFT = 8,
      XOR = 9;
}