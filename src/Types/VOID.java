package Types;

public class VOID extends Type {
	public VOID() {
		super(-1);
	}

	public boolean coerceTo(Type t) {
		return (t.actual() instanceof VOID);
	}
}
