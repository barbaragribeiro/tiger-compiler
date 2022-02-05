package Tree;

public class MOVE extends TExp {
	public TExp dest, source;

	public MOVE(TExp d, TExp s) {
		super(null);
		dest = d;
		source = s;
	}
}