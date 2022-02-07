package Tree;

public class ESEQ extends TExp {
  public TExp stm;
  public TExp exp;

  public ESEQ(TExp s, TExp e) {
    stm = s;
    exp = e;
  }
}