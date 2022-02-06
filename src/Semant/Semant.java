package Semant;

import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;

import Symbol.Table;
import Symbol.Symbol;
import Absyn.*;
import Types.*;
import Translate.Translate;
import Tree.TExp;

public class Semant {
  int level;
  public Env env;
  public Deque<LabelTree> labelTree;

  public Semant(int lvl) {
    level = lvl;
    env = new Env(level);
    labelTree = new ArrayDeque<LabelTree>();
  }

  public ExpTy build(Exp e) {
    ExpTy code = buildExp(e);
    if(!Translate.isStm(code.texp)) code.texp = new TExp(code.texp);

    return code;
  }

  public ExpTy buildExp(Exp e) {
    if (e instanceof VarExp) {
      return buildVar( ((VarExp) e).var );
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
    else if (e instanceof CallExp) {
      return buildCallExp((CallExp) e);
    }
    else if (e instanceof OpExp) {
      return buildOpExp((OpExp) e);
    } 
    else if (e instanceof RecordExp) {
      return buildRecordExp((RecordExp) e);
    }
    else if (e instanceof ArrayExp) {
      return buildArrayExp((ArrayExp) e);
    }
    else if (e instanceof AssignExp) {
      return buildAssignExp((AssignExp) e);
    }
    // else if (e instanceof IfExp) {
    //   return buildIfExp((IfExp) e);
    // }
    // else if (e instanceof ForExp) {
    //   return buildForExp((ForExp) e);
    // }
    // else if (e instanceof WhileExp) {
    //   return buildWhileExp((WhileExp) e);
    // }
    // else if (e instanceof BreakExp) {
    //   return buildBreakExp((BreakExp) e);
    // }
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
    String label = "L" + Integer.toString(env.installString());
    labelTree.addFirst(new StringTree(label, exp.value));
		return new ExpTy(Translate.translateStringExp(label), new STRING());
	}

  private ExpTy buildIntExp(IntExp exp) {
    return new ExpTy(Translate.translateInt(exp.value), new INT());
  }

  /**************** Variables ****************/
  /* Todas as funções retornam a expressão para 
  o endereço da variável */

  private ExpTy buildVar(Var exp) {
    if (exp instanceof SimpleVar) {
      return buildSimpleVar((SimpleVar) exp);
    }
    else if (exp instanceof SubscriptVar) {
      return buildSubscriptVar((SubscriptVar) exp);
    }
    else if (exp instanceof FieldVar) {
      return buildFieldVar((FieldVar) exp);
    } 
    return null;
  }

  private ExpTy buildSimpleVar(SimpleVar sv) {
    Object entry = env.varTable.get(sv.name);
    if (entry == null) {
      reportError("Variável indefinida: " + sv.name.toString(), true);
      return null;
    }
    if (!(entry instanceof VarEntry)){
      reportError("Identificador corresponde a uma função, não uma variável: " + sv.name.toString(), true);
      return null;
    }
    VarEntry ventry = (VarEntry) entry;
    return new ExpTy(Translate.translateSimpleVar(ventry.temp), ventry.typ); 
  }

  private ExpTy buildSubscriptVar(SubscriptVar sv) {
    ExpTy idx = buildExp(sv.index);
    if (idx.typ instanceof INT){
      reportError("Índice de tipo não inteiro", true);
      return null;
    }

    ExpTy arrayVar = buildVar(sv.var);
    Type arrayType = arrayVar.typ;
    if (!(arrayType instanceof ARRAY)) {
      reportError("Variável indexada deve ser um array", true);
      return null;
    }

    return new ExpTy(Translate.translateSubscriptVar(arrayVar.texp, idx.texp, arrayType.size), ((ARRAY) arrayType).typ);
  }

  private ExpTy buildFieldVar(FieldVar fv){
    ExpTy recordVar = buildVar(fv.var);
    RECORD record = (RECORD) recordVar.typ;
    int offset = 0;
    while (record != null) {
      if (record.fieldName.toString() == fv.field.toString()) {
        return new ExpTy(Translate.translateFieldVar(recordVar.texp, offset), record.fieldType);
      }
      offset += record.fieldType.size;
      record = record.tail;
    }
    reportError("Variável não possui campo " + fv.field.toString(), true);
    return null;
  }

  /**************** Assign ****************/

  private ExpTy buildAssignExp(AssignExp assign) {
    ExpTy result = buildRightSideAssign(assign.exp);
    ExpTy destVar = buildVar(assign.var);
    // checa tipos compatíveis
    if (!isEquivalentTypes(destVar.typ, result.typ)) {
      String msg = "Tipo da expressão incompatível com tipo da variável";
      reportError(msg, true);
      return null;
    }
    return new ExpTy(Translate.translateAssign(destVar.texp, result.texp), new VOID());
  }

  private ExpTy buildRightSideAssign(Exp exp) {
    ExpTy result = buildExp(exp);
    if (result.typ instanceof VOID) {
      reportError("Valor de uma atribuição não deve ter tipo nulo", true);
      return null;
    }
    return result;
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
      return buildFunctionDec((FunctionDec) dec);
    }
    return null;
  }

  private ExpTy buildVarDec(VarDec dec) {
    ExpTy result = buildRightSideAssign(dec.init);  

    if (dec.typ != null) {
      // Check if type exists in table
      TypeEntry typeEntry = getTypeEntryFromTable(dec.typ.name);
      // Check if types are compatible
      if (!isEquivalentTypes(result.typ, typeEntry.typ)) { 
        String msg = "Tipo da expressão incompatível com variável " + dec.name.toString() + " do tipo " + dec.typ.name.toString();
        reportError(msg, true);
        return null;
      }
    }

    // add var to table  
    VarEntry entry = env.installVar(level, dec.name, result.typ);

    // build intermediate code
    return new ExpTy(Translate.translateAssign(Translate.translateSimpleVar(entry.temp), result.texp), new VOID());
  }

  private ExpTy buildTypeDec(TypeDec dec) {
    // redeclaração de tipos é permitida?
    TypeEntry typeEntry = createTypeEntry(dec.ty); 
    // add new type to table
    env.typeTable.put(dec.name, typeEntry);
    // build intermediate code 
    return new ExpTy(Translate.translateNilExp(), new VOID());
  }

  private TypeEntry createRecordTypeEntry(RecordTy ty) {
    FieldList fields = ty.fields;
    return new TypeEntry(createRecord(fields));
  }

  private TypeEntry createArrayTypeEntry(ArrayTy ty) {
    TypeEntry typeEntryFromTable = getTypeEntryFromTable(ty.typ);
    return new TypeEntry(new ARRAY(typeEntryFromTable.typ, typeEntryFromTable.typ.size));
  }

  private TypeEntry createTypeEntry(Ty ty) {
    if (ty instanceof NameTy) {
      return getTypeEntryFromTable( ((NameTy) ty).name ); // TODO: não deixar ciclo?
    }
    else if (ty instanceof RecordTy) {
      return createRecordTypeEntry((RecordTy) ty);
    }
    else if (ty instanceof ArrayTy) {
      return createArrayTypeEntry((ArrayTy) ty);
    }
    return null;
  }

  private ExpTy buildFunctionDec(FunctionDec dec) {
    // checa se não existe função padrão com o mesmo nome
    if (env.stdFunctions.contains(dec.name.toString())) { 
      reportError(dec.name.toString() + " é um nome reservado", true);
      return null;
    }

    newScope();

    // declara variáveis no novo escopo
    RECORD params = createRecord(dec.params);
    RECORD params_og = params;

    while (params != null) {
      env.installVar(level, params.fieldName, params.fieldType);
      params = params.tail;
    }

    // build intermediate code
    ExpTy body = buildExp(dec.body);

    // check if return type of body equals to type in dec
    Type expectedType = dec.result == null? new VOID() : getTypeEntryFromTable(dec.result.name).typ;
    if (!isEquivalentTypes(expectedType, body.typ)) {
      reportError("Tipo da expressão incompatível com tipo da função " + dec.name.toString(), true);
      return null;
    }

    endScope();

    // adiciona declaração da função à tabela no escopo anterior
    FuncEntry entry = env.installFunc(level, dec.name, expectedType, params_og);

    // adiciona código da função à lista de código
    ExpTy funcTree;
    if (dec.result != null) {
      // se tiver retorno, coloca o retorno na variável temp de retorno (rv)
      funcTree = new ExpTy(Translate.translateFunctionReturn(body.texp), new VOID());
    }
    else {
      funcTree = new ExpTy(body.texp, new VOID());
    }

    // adiciona funcao à arvore de funcoes
    labelTree.addFirst(new FuncTree(entry.label, funcTree));
    
    // Retorna void pra árvore principal
    return new ExpTy(Translate.translateNilExp(), new VOID());
  }

  
  /**************** Other Expressions *****************/

  private ExpTy buildArrayExp(ArrayExp exp) {
    TypeEntry type = getTypeEntryFromTable(exp.typ);

    ExpTy size = buildExp(exp.size);
    // check if index has type int
    if(!(size.typ instanceof INT)) {
      reportError("Tamanho do array de tipo não inteiro", true);
      return null;
    }

    ExpTy init = buildExp(exp.init);
    // check if types are compatible
    if (!isEquivalentTypes(init.typ, type.typ)) {
      reportError("Tipo da expressão não compatível com tipo do array", true);
      return null;
    }

    ArrayList<TExp> targs = new ArrayList<TExp>();
    targs.add(size.texp);
    targs.add(init.texp);
    return new ExpTy(Translate.translateCall("initArray", false, targs), type.typ);
  }


  private ExpTy buildRecordExp(RecordExp exp) {
    TypeEntry type = getTypeEntryFromTable(exp.typ);

    // Checa número de campos passados e tipo de cada um. Traduz código para cada inicialização
    RECORD field = (RECORD) type.typ;
    FieldExpList init = exp.fields;
    ArrayList<TExp> tinits = new ArrayList<TExp>();
    int size = 0;
    while ((init != null) && (field != null)) {
      ExpTy initTree = buildExp(init.init);
      if (!(isEquivalentTypes(initTree.typ, field.fieldType))) {
        reportError("Tipo do campo \"" + field.fieldName.toString() + "\" difere do valor passado", true);
        return null;
      }
      
      TExp tempTemp = Translate.translateSimpleVar(String.valueOf(env.nextTemp));
      TExp fieldAddr = Translate.translateFieldVar(tempTemp, size);
      tinits.add(Translate.translateAssign(fieldAddr, initTree.texp));

      size += field.size;
      init = init.tail;
      field = field.tail;
    }
    if ((field == null) && (init != null)) {
      reportError("Número de campos maior que esperado para o registro", true);
      return null;
    }
    if ((field != null) && (init == null)) {
      reportError("Número de campos menor que esperado para o registro", true); // TODO: isso pode?
      return null; 
    }

    // traduz alocação do registro
    ArrayList<TExp> targs = new ArrayList<TExp>();
    targs.add(Translate.translateInt(size));
    ExpTy alocaRegistro = new ExpTy(Translate.translateCall("malloc", false, targs), type.typ);

    // TODO: juntar todas as inicializações

    // TODO: juntar alocação e inicializações e retornar
    return null;
  }


  private ExpTy buildCallExp(CallExp exp) {
    // check if function exists
    Object entry = env.varTable.get(exp.func);
    if (entry == null) {
      reportError("Função indefinida: " + exp.func.toString(), true);
      return null;
    }
    if (!(entry instanceof FuncEntry)){
      reportError("Identificador corresponde a uma variável, não uma função: " + exp.func.toString(), true);
      return null;
    }
    FuncEntry fentry = (FuncEntry) entry;

    // check number of arguments + type of all arguments and translate args
    RECORD param = fentry.paramlist;

    ExpList arg = exp.args;
    ArrayList<TExp> targs = new ArrayList<TExp>();
    while ((arg != null) && (param != null)) {
      ExpTy argTree = buildExp(arg.head);
      if (!(isEquivalentTypes(argTree.typ, param.fieldType))) {
        reportError("Tipo do argumento difere do tipo do parâmetro formal da função " + exp.func.toString(), true);
        return null;
      }
      targs.add(argTree.texp);

      param = param.tail;
      arg = arg.tail;
    }
    if ((param == null) && (arg != null)) {
      reportError("Número de argumentos maior que esperado para a função " + exp.func.toString(), true);
      return null;        
    }
    if ((param != null) && (arg == null)) {
      reportError("Número de argumentos menor que esperado para a função " + exp.func.toString(), true);
      return null;
    }

    boolean hasReturn = !(fentry.typ instanceof VOID);
    return new ExpTy(Translate.translateCall(fentry.label, hasReturn, targs), fentry.typ);
  }

  private ExpTy buildOpExp(OpExp exp) {
		ExpTy left = buildExp(exp.left); 
    ExpTy right = buildExp(exp.right);
      if (!(left.typ instanceof INT) || !(right.typ instanceof INT)) {
        String errorMsg = "A operação " + OpExp.opToStr(exp.oper) + " deve ser feita sobre tipos numéricos";
        reportError(errorMsg, true);
        return null;
      }
      else {
        return new ExpTy(Translate.translateOpExp(exp.oper, left.texp, right.texp), new INT()); 
      }
  }


  /************ Lists ***********/

  private ExpTy buildDecList(DecList d) {
    if(d == null) return new ExpTy(null, new VOID());

    // DecList possui apenas cabeca -> nao tem SEQ
    if(d.tail == null) {
      // if(debug) System.out.println("DecList: nao tem tail");
      return new ExpTy(buildDec(d.head).texp, new VOID()); //TODO: VOID?
    }
    
    // Completa
    ExpTy head = buildDec(d.head);
    // if(debug) System.out.println("DecList: fiz o head, vou pro tail");
    ExpTy tail = buildDecList(d.tail);
    return new ExpTy(Translate.translateDecList(head.texp, tail.texp), new VOID()); //TODO: VOID?
  }

  // ExpList
  public ExpTy buildExpList(ExpList e) {
    // Vazia
    if(e == null) {
      return new ExpTy(Translate.translateNilExp(), new VOID());
    }

    // ExpList possui apenas cabeca -> nao tem SEQ
    if(e.tail == null) {
      ExpTy head = buildExp(e.head);
      return new ExpTy(head.texp, head.typ);
    }
    
    // Completa
    ExpTy head = buildExp(e.head);
    ExpTy tail = buildExpList(e.tail);
    return new ExpTy(Translate.translateExpList(head.texp, tail.texp), new VOID()); //TODO: VOID?
  }

  /************ Control expressions ***********/

  private ExpTy buildLetExp(LetExp e){
		newScope();

    ExpTy declist = buildDecList(e.decs);
    ExpTy expList = buildSeqExp(e.body);
    
    ExpTy returnExp = new ExpTy(Translate.translateLetExp(declist.texp, expList.texp), new VOID());

    endScope();

    return returnExp;
  }

  public ExpTy buildSeqExp(SeqExp e) {
    return buildExpList(e.list);
  }

  /************* Helpers *************/

  private void newScope() {
    env.varTable.beginScope();
    env.typeTable.beginScope();
    level = level + 1;
  }

  private void endScope() {
		env.varTable.endScope();
		env.typeTable.endScope();
    level = level - 1;
  }

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

  private RECORD createRecord(FieldList fields) {
    // int size = 0;

    if (fields == null) {
      return new RECORD();
    }

    RECORD newRec = null;
    RECORD firstRec = null;
    RECORD prevRec = null;
    while (fields != null) {
      TypeEntry typeEntry = getTypeEntryFromTable(fields.typ);
      newRec = new RECORD(fields.name, typeEntry.typ, null, typeEntry.typ.size);
      // size += typeEntry.typ.size;
      if (firstRec == null) {
        firstRec = newRec;
      }
      if (prevRec != null) {
        prevRec.tail = newRec;
      }
      prevRec = newRec;
      fields = fields.tail;
    }

    // firstRec.size = size;
    return firstRec;
  }

}