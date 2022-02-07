package Types;

public class RECORD extends Type {
	public Symbol.Symbol name;
	public Type typ;
	public RECORD tail;

	public RECORD(Symbol.Symbol n, Type t, RECORD x, int s) {
		super(s);
		name = n;
		typ = t;
		tail = x;
	}

	public RECORD(Symbol.Symbol n, Type t, RECORD x) {
		super(-1);
		name = n;
		typ = t;
		tail = x;
	}

	public RECORD() {
		super(-1);
		name = null;
		typ = null;
		tail = null;
	}
}
