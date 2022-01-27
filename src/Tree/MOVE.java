package Tree;

public class MOVE extends TExp {
	public TExp dest, source;

	public MOVE(TExp d, TExp s) {
		dest = d;
		source = s;
	}
}