package Symbol;

class Binder {
  Object value;
  Symbol prevtop;
  Binder tail;
  Binder(Object v, Symbol p, Binder t) {
	value=v; prevtop=p; tail=t;
  }
}

public class Table {

  private java.util.Dictionary dict = new java.util.Hashtable();
  private Symbol top;
  private Binder marks;

  public Table(){}

  public Object get(Symbol key) {
	Binder e = (Binder)dict.get(key);
	if (e==null) return null;
	else return e.value;
  }	

  public void put(Symbol key, Object value) {
	dict.put(key, new Binder(value, top, (Binder)dict.get(key)));
	top = key;
  }

  public void beginScope() {marks = new Binder(null,top,marks); top=null;}

  public void endScope() {
	while (top!=null) {
	   Binder e = (Binder)dict.get(top);
	   if (e.tail!=null) dict.put(top,e.tail);
	   else dict.remove(top);
	   top = e.prevtop;
	}
	top=marks.prevtop;
	marks=marks.tail;
  }
  
  public java.util.Enumeration keys() {return dict.keys();}
}
