package Tree;

public class Args extends TExp {
	public TExp head;
	public Args tail;

	public Args(TExp h, Args t) {
		head = h;
		tail = t;
	}
}
