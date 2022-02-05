package Tree;

public class MEM extends TExp {
	public TExp addr;

	public MEM(TExp a) {
		super(null);
		addr = a;
	}
}