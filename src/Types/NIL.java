package Types;

public class NIL extends Type {
	public NIL() {
		super(0);
	}

	public boolean coerceTo(Type t) {
		Type a = t.actual();
		return (a instanceof RECORD) || (a instanceof NIL);
	}
}
