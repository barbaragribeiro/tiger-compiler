package Semant;

import java.util.List;
import java.util.ArrayList;

import Symbol.Table;
import Symbol.Symbol;
import Absyn.*;
import Types.*;
import Translate.Translate;

public class Semant {
  int level;
  public Env env ;

  public Semant(int lvl) {
    level = lvl;
    env = new Env(level);
    env.installTypes();
    env.installStdFunctions();
  }

  public ExpTy buildExp(Exp e) {
    // Únicos tipos que criam novo contexto em InstalaSimbolo: Let e FunctionDec ??
    if (e instanceof VarExp) {
      return buildVarExp((VarExp) e);
    } 
    else if (e instanceof NilExp) {
      return buildNilExp((NilExp) e);
    } 
    else if (e instanceof IntExp) {
      return buildIntExp((IntExp) e);
    } 
    else if (e instanceof StringExp) {
      return buildStringExp((StringExp) e);
    } 
    else if (e instanceof OpExp) {
      return buildOpExp((OpExp) e);
    } 
    else if (e instanceof LetExp) {
      return buildLetExp((LetExp) e);
    }
    return null;
  }

  /**************** Primitive expressions *****************/

  private ExpTy buildNilExp(NilExp exp) {
    return new ExpTy(Translate.translateNilExp(), new NIL());
  }
  
  private ExpTy buildStringExp(StringExp exp) {
		return new ExpTy(Translate.translateStringExp(exp.value), new STRING());
	}

  private ExpTy buildIntExp(IntExp exp) {
    return new ExpTy(Translate.translateInt(exp.value), new INT());
  }

  /**************** Variables ****************/

  private ExpTy buildVarExp(VarExp exp) {
    if (exp.var instanceof SimpleVar) {
      return buildSimpleVar((SimpleVar) exp.var);
    } 
    // else if (exp instanceof SubscriptVar) {
    //   return buildSubscriptVar((SubscriptVar) exp);
    // } 
    // else if (exp instanceof FieldVar) {
    //   return buildIntExp((FieldVar) exp);
    // } 
    return null;
  }

  private ExpTy buildSimpleVar(SimpleVar sv) {
    return buildSimpleVar(sv.name);
  }

  private ExpTy buildSimpleVar(Symbol name) {
    Entry entry = (Entry) env.varTable.get(name);
    if (entry == null) {
      reportError("Variável indefinida: " + name.toString(), true);
      return null;
    }
    if (!(entry instanceof VarEntry)){
      reportError("Identificador corresponde a uma função, não uma variável: " + name.toString());
      return null;
    }
    // TODO: fazer certo. solução temporária, 0 é só para testar
    return new ExpTy(Translate.translateSimpleVar(0), entry.typ); 
  }

  /**************** Declarations ****************/

  private ExpTy buildVarDec(VarDec dec) {
    // Check if type exists
    Type varType = getTypeFromTable(dec.typ.name);
    // Get dec.init type
    ExpTy result = buildExp(dec.init);
    // Check types are compatible
    if ((typeName != null) && !isEquivalentTypes(result.typ, varType)) { 
      String msg = "Expressão do tipo " + result.typ + " incompatível com variável " + dec.name.toString() + " do tipo " + typeName.toString();
      reportError(msg, true);
      return null;
    }
    // add var to table
    env.varTable.put(dec.name, new VarEntry(result.typ));

    // build intermediate code for declaration expression
    ExpTy var = buildSimpleVar(dec.name);
    return new ExpTy(Translate.translateAssign(var.texp, result.texp), new VOID());
  }


  private ExpTy buildDec(Dec dec) {
    if (dec instanceof VarDec) {
      return buildVarDec((VarDec) dec);
    } 
    else if (dec instanceof TypeDec) {
      return buildTypeDec((TypeDec) dec);
    } 
    // else if (dec instanceof FunctionDec) {
    //   buildFunctionDec((FunctionDec) dec);
    // }     
    return null;
  }

  private ExpTy buildDecList(DecList decs) {
    Tree.TExp declist = null;
    while (decs != null) {
      Dec dec = decs.head;
      ExpTy decTree = buildDec(dec);
      // TODO append decTree.exp to declist
      decs = decs.tail;
    }
    return new ExpTy(declist, new VOID());
  }

  /********** Logic/Aritmetic expressions ***********/

  private ExpTy buildOpExp(OpExp exp) {
		ExpTy left = buildExp(exp.left); 
    ExpTy right = buildExp(exp.right); 
    // if (exp.oper <= OpExp.DIV || exp.oper >= OpExp.GE) {
    //  // Aritméticos e maior/menor só pode com int
      if (!(left.typ instanceof INT) || !(right.typ instanceof INT)) { // instanceof funciona aqui?
        String errorMsg = "A operação " + OpExp.opToStr(exp.oper) + " deve ser feita sobre tipos numéricos";
        reportError(errorMsg, true);
        return null;
      }
      else {
        return new ExpTy(Translate.translateOpExp(exp.oper, left.texp, right.texp), new INT()); 
      }
    // }
    // if (exp.oper >= OpExp.EQ) {
    //   // igual pode com int ou string
    //   if (!(left.typ instanceof INT) || !(left.typ instanceof STRING) || 
    //       !(right.typ instanceof INT) || !(right.typ instanceof STRING)) { // instanceof funciona aqui?
    //     System.out.println("Erro: A operação " + OpExp.opToStr(exp.oper) + " deve ser feita sobre tipos numéricos");
    //     return null; // TODO exit?
    //   }
    //   else {
    //     return new ExpTy(Translate.translateOpExp(exp.oper, left.texp, right.texp), new INT()); 
    //   }
    // }
  }

  /************ Control expressions ***********/

  private ExpTy buildLetExp(LetExp e){
    // Declarations
		env.varTable.beginScope();
		env.typeTable.beginScope();
    level = level + 1;
    ExpTy declist = buildDecList(e.decs);
		env.varTable.endScope();
		env.typeTable.endScope();
    level = level - 1;

    // Expressions
    // TODO
    return null;
  }

  /************* Helpers *************/

  private boolean isEquivalentTypes(Type typ1, Type typ2) {
    // TODO try to coerce
    return typ1.getClass() == typ2.getClass();
  }

  private void getTypeFromTable(Symbol typeName) {
    Type varType = (Type) env.typeTable.get(typeName);
    if (varType == null) {
      reportError("Tipo indefinido: " + typeName.toString());
      return null;
    }
    return varType;
  }

  private void reportError(String msg, boolean exit) {
      System.out.println("Erro: " + msg);
      if boolean {
        System.exit(-1);
      }
  }
}