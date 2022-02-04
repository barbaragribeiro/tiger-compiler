package Types;

public class ARRAY extends Type {
	public Type typ;

	public ARRAY(Type t, int typeSize) {
		super(typeSize);
		typ = t;
	}

	public boolean coerceTo(Type t) {
		return this == t.actual();
	}
}
