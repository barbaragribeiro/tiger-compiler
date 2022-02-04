package Types;

public class STRING extends Type {
	public STRING() {
		super(1);
	}

	public boolean coerceTo(Type t) {
		return (t.actual() instanceof STRING);
	}
}
