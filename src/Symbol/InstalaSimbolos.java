package Symbol;

abstract class Simbolo {
    int nivel;
    abstract void sair();
}
class SimboloFormal extends Simbolo {
    Absyn.FieldList fl;
    SimboloFormal(int d, Absyn.FieldList f) {
        nivel = d;
        fl = f;
        fl.escape = false;
    }
    void sair() {
        fl.escape = true;
    }
}
class SimboloVar extends Simbolo {
    Absyn.VarDec vd;
    SimboloVar(int d, Absyn.VarDec v) {
        nivel = d;
        vd = v;
        vd.escape = false;
    }

    void sair() {
        vd.escape = true;
    }
}
public class InstalaSimbolos {

    Table tabela_simbolos = new Table();
    boolean imprime_simbolos = false;

    public InstalaSimbolos(Absyn.Exp e) {
        percorreExp(0, e);
    }

    void percorreExp(int nivel, Absyn.Exp e) { 
        if ( e instanceof Absyn.VarExp ) { 
            percorreExp(nivel, ( Absyn.VarExp )e ); 
        } else  if ( e instanceof Absyn.NilExp ) { 
            percorreExp(nivel, ( Absyn.NilExp )e ); 
        } else  if ( e instanceof Absyn.IntExp ) { 
            percorreExp(nivel, ( Absyn.IntExp )e ); 
        } else if ( e instanceof Absyn.StringExp ) { 
            percorreExp(nivel, ( Absyn.StringExp )e ); 
        } else  if ( e instanceof Absyn.CallExp ) { 
            percorreExp(nivel, ( Absyn.CallExp )e ); 
        } else  if ( e instanceof Absyn.OpExp ) { 
            percorreExp(nivel, ( Absyn.OpExp )e ); 
        } else  if ( e instanceof Absyn.RecordExp ) { 
            percorreExp(nivel, ( Absyn.RecordExp )e ); 
        } else if ( e instanceof Absyn.SeqExp ) { 
            percorreExp(nivel, ( Absyn.SeqExp )e ); 
        } else  if ( e instanceof Absyn.AssignExp ) { 
            percorreExp(nivel, ( Absyn.AssignExp )e ); 
        } else if ( e instanceof Absyn.IfExp ) { 
            percorreExp(nivel, ( Absyn.IfExp )e ); 
        } else if ( e instanceof Absyn.WhileExp ) { 
            percorreExp(nivel, ( Absyn.WhileExp )e ); 
        } else  if ( e instanceof Absyn.ForExp ) { 
            percorreExp(nivel, ( Absyn.ForExp )e ); 
        } else  if ( e instanceof Absyn.BreakExp ) { 
            percorreExp(nivel, ( Absyn.BreakExp )e ); 
        } else  if ( e instanceof Absyn.LetExp ) { 
            percorreExp(nivel, ( Absyn.LetExp )e ); 
        } else  if ( e instanceof Absyn.ArrayExp ) { 
            percorreExp(nivel, ( Absyn.ArrayExp )e ); 
        } else { 
            throw new Error( "percorreExp" ); 
        } 
    } 
 
    void percorreDec(int nivel, Absyn.Dec d) {  
        if ( d instanceof Absyn.FunctionDec ) { 
            percorreDec(nivel, ( Absyn.FunctionDec )d ); 
        } else if ( d instanceof Absyn.VarDec ) { 
            percorreDec(nivel, ( Absyn.VarDec )d ); 
        } else if ( d instanceof Absyn.TypeDec ) { 
            percorreDec(nivel, ( Absyn.TypeDec )d ); 
        } else { 
            throw new Error( "transDec" ); 
        } 
    } 

    void percorreVar(int nivel, Absyn.Var v) { 
        if ( v instanceof Absyn.SimpleVar ) { 
            percorreVar(nivel, ( Absyn.SimpleVar )v ); 
        } else  if ( v instanceof Absyn.FieldVar ) { 
            percorreVar(nivel, ( Absyn.FieldVar )v ); 
        } else if ( v instanceof Absyn.SubscriptVar ) { 
            percorreVar(nivel, ( Absyn.SubscriptVar )v ); 
        } else { 
            throw new Error( "transVar" ); 
        } 
    } 

    void percorreDec(int nivel, Absyn.FunctionDec d) {
        Absyn.FunctionDec decList = d;
        while (decList != null) {
            tabela_simbolos.beginScope();
            Absyn.FieldList paramList = decList.params;
            while (paramList != null) {
        	if (imprime_simbolos) System.out.println("Instalando parametro formal:" + paramList.name + " Profundidade: " + (nivel+1));
                tabela_simbolos.put(paramList.name, new SimboloFormal(nivel+1, paramList));
                paramList = paramList.tail;
            }
            percorreExp(nivel+1, decList.body);
            decList = decList.next;
        }

            
    }
    void percorreDec(int nivel, Absyn.VarDec d) {
        if (imprime_simbolos) System.out.println("Instalando variavel:" + d.name + " Profundidade: " + nivel);
        tabela_simbolos.put(d.name, new SimboloVar(nivel, d));
    }
    void percorreDec(int nivel, Absyn.TypeDec d) {
    }

    void percorreVar(int nivel, Absyn.SimpleVar v) {
        Simbolo simbolo = (Simbolo)tabela_simbolos.get(v.name);
        if ((simbolo != null) && (simbolo.nivel != nivel)) {
            simbolo.sair();
        }
    }
    void percorreVar(int nivel, Absyn.FieldVar v) {
    }
    void percorreVar(int nivel, Absyn.SubscriptVar v) {
    }

    void percorreExp(int nivel, Absyn.WhileExp exp) {
        percorreExp(nivel, exp.test);
        percorreExp(nivel, exp.body);
    }
    void percorreExp(int nivel, Absyn.ForExp exp) {
        percorreDec(nivel, exp.var);
        percorreExp(nivel, exp.hi);
        percorreExp(nivel, exp.body);
    }
    void percorreExp(int nivel, Absyn.BreakExp exp) {
    }
    void percorreExp(int nivel, Absyn.LetExp exp) {
        tabela_simbolos.beginScope();
        Absyn.DecList decList = exp.decs;
        while (decList != null) {
            percorreDec(nivel, decList.head);
            decList = decList.tail;
        }
        percorreExp(nivel, exp.body);
        tabela_simbolos.endScope();    
    }
    void percorreExp(int nivel, Absyn.ArrayExp exp) {
        percorreExp(nivel, exp.size);
        if (exp.init != null) {
            percorreExp(nivel, exp.init);
        }    
    }
    void percorreExp(int nivel, Absyn.VarExp exp) {        
    }
    void percorreExp(int nivel, Absyn.NilExp exp) {        
    }
    void percorreExp(int nivel, Absyn.IntExp exp) {
    }
    void percorreExp(int nivel, Absyn.StringExp exp) {
    }
    void percorreExp(int nivel, Absyn.CallExp exp) {
        Simbolo simbolo = (Simbolo)tabela_simbolos.get(exp.func);
        if ((simbolo != null) && (simbolo.nivel != nivel)) {
            simbolo.sair();
        }
        Absyn.ExpList expList = exp.args;
        while (expList != null) {
            percorreExp(nivel, expList.head);
            expList = expList.tail;
        }
    }
    void percorreExp(int nivel, Absyn.OpExp exp) {
        percorreExp(nivel, exp.left);
        percorreExp(nivel, exp.right);
    }
    void percorreExp(int nivel, Absyn.RecordExp exp) {
        Absyn.FieldExpList fieldExpList = exp.fields;
        while (fieldExpList != null) {
            percorreExp(nivel, fieldExpList.init);
            fieldExpList = fieldExpList.tail;
        }
    }
    void percorreExp(int nivel, Absyn.SeqExp exp) {
        Absyn.ExpList expList = exp.list;
        while(expList != null) {
            percorreExp(nivel, expList.head);
            expList = expList.tail;
        }
    }
    void percorreExp(int nivel, Absyn.AssignExp exp) {
        percorreVar(nivel, exp.var);
        percorreExp(nivel, exp.exp);
    }
    void percorreExp(int nivel, Absyn.IfExp exp) {
        percorreExp(nivel, exp.test);
        percorreExp(nivel, exp.thenclause);
        if (exp.elseclause != null)     {
            percorreExp(nivel, exp.elseclause);
        }
    }

}
