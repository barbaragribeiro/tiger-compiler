package Types;

public class RECORD extends Type {
	public Symbol.Symbol recordName;
	public Symbol.Symbol name;
	public Type typ;
	public RECORD tail;

	public RECORD(Symbol.Symbol rn, Symbol.Symbol n, Type t, RECORD x, int s) {
		super(s);
		recordName = rn;
		name = n;
		typ = t;
		tail = x;
	}

	public RECORD(Symbol.Symbol rn, Symbol.Symbol n, Type t, RECORD x) {
		super(-1);
		recordName = rn;
		name = n;
		typ = t;
		tail = x;
	}

	public RECORD(Symbol.Symbol rn) {
		super(-1);
		recordName = rn;
		name = null;
		typ = null;
		tail = null;
	}
}
