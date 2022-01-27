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
    if (e instanceof SeqExp) {
      return buildSeqExp((SeqExp) e);
    } 
    else if (e instanceof OpExp) {
      return buildOpExp((OpExp) e);
    } 
    else if (e instanceof LetExp) {
      return buildLetExp((LetExp) e);
    }
    return null;
  }

  /**************** Primitive types *****************/

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
    //   return buildFieldVar((FieldVar) exp);
    // } 
    return null;
  }

  private ExpTy buildSimpleVar(SimpleVar sv) {
    return buildSimpleVar(sv.name);
  }

  private ExpTy buildSimpleVar(Symbol name) {
    Object entry = env.varTable.get(name);
    if (entry == null) {
      reportError("Variável indefinida: " + name.toString(), true);
      return null;
    }
    if (!(entry instanceof TypeEntry)){
      reportError("Identificador corresponde a uma função, não uma variável: " + name.toString(), true);
      return null;
    }
    // TODO: fazer certo. solução temporária, 0 é só para testar
    return new ExpTy(Translate.translateSimpleVar(0), ((TypeEntry) entry).typ); 
  }

  /**************** Declarations ****************/

  private ExpTy buildDec(Dec dec) {
    if (dec instanceof VarDec) {
      return buildVarDec((VarDec) dec);
    } 
    else if (dec instanceof TypeDec) {
      return buildTypeDec((TypeDec) dec);
    } 
    else if (dec instanceof FunctionDec) {
      buildFunctionDec((FunctionDec) dec);
    }     
    return null;
  }

  private ExpTy buildVarDec(VarDec dec) {
    // Get dec.init type
    ExpTy result = buildExp(dec.init);

    if (dec.typ != null) {
      // Check if type exists in table
      TypeEntry typeEntryFromTable = getTypeEntryFromTable(dec.typ.name);
      // Check if types are compatible
      if (!isEquivalentTypes(result.typ, typeEntryFromTable.typ)) { 
        String msg = "Expressão do tipo " + result.typ + " incompatível com variável " + dec.name.toString() + " do tipo " + dec.typ.name.toString();
        reportError(msg, true);
        return null;
      }
      // add var to table
      env.varTable.put(dec.name, typeEntryFromTable);
    }
    else {
      TypeEntry typeEntry = new TypeEntry(result.typ);
      env.varTable.put(dec.name, typeEntry);      
    }

    // build intermediate code for declaration expression
    ExpTy var = buildSimpleVar(dec.name);
    return new ExpTy(Translate.translateAssign(var.texp, result.texp), new VOID());
  }

  private ExpTy buildTypeDec(TypeDec dec) {
    // TODO: redeclaração de tipos é permitida?
    // Type typ = (Type) env.typeTable.get(dec.name);
    // if (typ != null) {
    //   reportError("Tipo já existe", false);
    // }

    TypeEntry typeEntry = createTypeEntry(dec.ty); 
    // add new type to table
    env.typeTable.put(dec.name, typeEntry);
    // build intermediate code 
    return new ExpTy(Translate.translateNilExp(), new VOID()); // TODO: é esse codigo gerado pra declaração de tipos?
  }

  private TypeEntry createRecordTypeEntry(RecordTy ty) {
    FieldList fields = ty.fields;

    if (fields == null) {
      return new TypeEntry(new RECORD());
    }

    RECORD newRec = null;
    RECORD firstRec = null;
    RECORD prevRec = null;
    while (fields != null) {
      TypeEntry typeEntry = getTypeEntryFromTable(fields.typ);
      newRec = new RECORD(fields.name, typeEntry.typ, null);
      if (firstRec == null) {
        firstRec = newRec;
      }
      if (prevRec != null) {
        prevRec.tail = newRec;
      }
      prevRec = newRec;
      fields = fields.tail;
    }
    return new TypeEntry(firstRec);
  }

  private TypeEntry createArrayTypeEntry(ArrayTy ty) {
    TypeEntry typeEntryFromTable = getTypeEntryFromTable(ty.typ);
    return new TypeEntry(new ARRAY(typeEntryFromTable.typ));
  }

  private TypeEntry createTypeEntry(Ty ty) {
    if (ty instanceof NameTy) {
      return getTypeEntryFromTable( ((NameTy) ty).name ); // TODO: não deixar ciclo
    }
    else if (ty instanceof RecordTy) {
      return createRecordTypeEntry((RecordTy) ty);
    }
    else if (ty instanceof ArrayTy) {
      return createArrayTypeEntry((ArrayTy) ty);
    }
    return null;
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
        return null; // TODO continuar analise semantica apos 1 erro
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
    //     return null;
    //   }
    //   else {
    //     return new ExpTy(Translate.translateOpExp(exp.oper, left.texp, right.texp), new INT()); 
    //   }
    // }
  }


  /************ Lists ***********/

  private ExpTy buildDecList(DecList decs) {
    Tree.TExp declist = null;
    while (decs != null) {
      ExpTy decTree = buildDec(decs.head);
      // TODO append decTree.exp to declist
      decs = decs.tail;
    }
    return new ExpTy(declist, new VOID());
  }

   public ExpTy buildSeqExp(SeqExp exp) {
    Tree.TExp explist = null;
    ExpList exps = exp.list;
    while (exps != null) {
      ExpTy expTree = buildExp(exps.head);
      // TODO append expTree.exp to explist
      exps = exps.tail;
    }
    return new ExpTy(explist, new VOID());
   }

  /************ Control expressions ***********/

  private ExpTy buildLetExp(LetExp e){
		env.varTable.beginScope();
		env.typeTable.beginScope();
    level = level + 1;

    ExpTy declist = buildDecList(e.decs);
    ExpTy exspList = buildSeqExp(e.body);

		env.varTable.endScope();
		env.typeTable.endScope();
    level = level - 1;

    // TODO: gerar código intermediario

    return null;
  }

  /************* Helpers *************/

  private boolean isEquivalentTypes(Type typ1, Type typ2) {
    // TODO try to coerce
    return typ1.getClass() == typ2.getClass();
  }

  private TypeEntry getTypeEntryFromTable(Symbol typeName) {
    TypeEntry varType = (TypeEntry) env.typeTable.get(typeName);
    if (varType == null) {
      reportError("Tipo indefinido: " + typeName.toString(), true);
      return null;
    }
    return varType;
  }

  private void reportError(String msg, boolean exit) {
      System.out.println("Erro: " + msg);
      if (exit) {
        System.exit(-1);
      }
  }
}