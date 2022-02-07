package Tree;

public class CALL extends TExp {
    public NAME name;
    public TExp returnTemp;
    public Args args;

	public CALL(NAME n, TExp r, Args a) {
		name = n;
        returnTemp = r;
        args = a;
	}
}