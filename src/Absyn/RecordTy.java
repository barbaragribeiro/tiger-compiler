package Absyn;

public class RecordTy extends Ty {
	public FieldList fields;

	public RecordTy(FieldList f) {
		fields = f;
	}
}
