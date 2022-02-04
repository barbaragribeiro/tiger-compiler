package Types;

public class RECORD extends Type {
	public Symbol.Symbol fieldName;
	public Type fieldType;
	public RECORD tail;

	public RECORD(Symbol.Symbol n, Type t, RECORD x, int s) {
		super(s);
		fieldName = n;
		fieldType = t;
		tail = x;
	}

	public RECORD(Symbol.Symbol n, Type t, RECORD x) {
		super(-1);
		fieldName = n;
		fieldType = t;
		tail = x;
	}

	public RECORD() {
		super(-1);
		fieldName = null;
		fieldType = null;
		tail = null;
	}
}
