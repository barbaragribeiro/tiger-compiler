package Tree;

public class TEMP extends TExp {
	public String t;

	// public TEMP(int temp) {
	// 	t = "t" + String.valueOf(temp);
	// }

	public TEMP(String temp) {
		super(null);
		t = temp;
	}

    public String toString() {
        return t;
    }
}