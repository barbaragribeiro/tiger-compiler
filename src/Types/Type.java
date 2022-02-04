package Types;

public abstract class Type {
	public int size;

	public Type(int s) {
		size = s;
	}

	public Type actual() {
		return this;
	}

	public boolean coerceTo(Type t) {
		return false;
	}
}
