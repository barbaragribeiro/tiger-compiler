package Semant;

import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;

import Symbol.Table;
import Symbol.Symbol;
import Absyn.*;
import Types.*;
import Temp.*;
import Translate.Translate;
import Tree.TExp;

public class Semant {
  int level;
  public Env env;
  public Deque<LabelTree> labelTree;
  Label nextEscape;

  public Semant(int lvl) {
    level = lvl;
    env = new Env(level);
    labelTree = new ArrayDeque<LabelTree>();
    nextEscape = null;
  }

  public ExpTy build(Exp e) {
    ExpTy code = buildExp(e);
    code.texp = Translate.translateExp(code.texp);

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
    else if (e instanceof IfExp) {
      return buildIfExp((IfExp) e);
    }
    else if (e instanceof ForExp) {
      return buildForExp((ForExp) e);
    }
    else if (e instanceof WhileExp) {
      return buildWhileExp((WhileExp) e);
    }
    else if (e instanceof BreakExp) {
      return buildBreakExp((BreakExp) e);
    }
    else if (e instanceof LetExp) {
      return buildLetExp((LetExp) e);
    }
    return null;
  }

  /**************** Primitive types *****************/

  private ExpTy buildNilExp(NilExp exp) {
    return new ExpTy(Translate.translateNilExp(), getTypeFromTable(Symbol.symbol("nil")));
  }
  
  private ExpTy buildStringExp(StringExp exp) {
    String label = "L" + Integer.toString(env.installLabel());
    labelTree.addFirst(new StringTree(label, exp.value));
		return new ExpTy(Translate.translateStringExp(label), getTypeFromTable(Symbol.symbol("string")));
	}

  private ExpTy buildIntExp(IntExp exp) {
    return new ExpTy(Translate.translateInt(exp.value), getTypeFromTable(Symbol.symbol("int")));
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

    if (!(idx.typ instanceof INT)){
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

    if(!(recordVar.typ instanceof RECORD)) {
      reportError("Variável não é um registro", true);
      return null;  
    }

    RECORD record = (RECORD) recordVar.typ;
    int offset = 0;
    while (record != null) {
      if (record.name.toString() == fv.field.toString()) {
        return new ExpTy(Translate.translateFieldVar(recordVar.texp, offset), record.typ);
      }
      offset += record.typ.size;
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
    return new ExpTy(Translate.translateAssign(destVar.texp, result.texp), getTypeFromTable(Symbol.symbol("void")));
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
      Type type = getTypeFromTable(dec.typ.name);
      // Check if types are compatible
      if (!isEquivalentTypes(result.typ, type)) { 
        String msg = "Tipo da expressão incompatível com variável " + dec.name.toString() + " do tipo " + dec.typ.name.toString();
        reportError(msg, true);
        return null;
      }
    }

    // add var to table  
    VarEntry entry = env.installVar(level, dec.name, result.typ);

    // build intermediate code
    return new ExpTy(Translate.translateAssign(Translate.translateSimpleVar(entry.temp), result.texp), getTypeFromTable(Symbol.symbol("void")));
  }

  private ExpTy buildTypeDec(TypeDec dec) {
    env.typeTable.put(dec.name, new TypeEntry(new RECORD(dec.name)));
    TypeEntry typeEntry = createTypeEntry(dec.name, dec.ty); 
    // add new type to table
    env.typeTable.put(dec.name, typeEntry);

    // build intermediate code 
    return new ExpTy(Translate.translateNilExp(), getTypeFromTable(Symbol.symbol("void")));
  }

  private TypeEntry createRecordTypeEntry(Symbol name, RecordTy ty) {
    FieldList fields = ty.fields;
    return new TypeEntry(createRecord(name, fields));
  }

  private TypeEntry createArrayTypeEntry(ArrayTy ty) {
    Type typeFromTable = getTypeFromTable(ty.typ);
    return new TypeEntry(new ARRAY(typeFromTable, typeFromTable.size));
  }

  private TypeEntry createTypeEntry(Symbol name, Ty ty) {
    if (ty instanceof NameTy) {
      Symbol typeName = ((NameTy) ty).name;
      TypeEntry varType = (TypeEntry) env.typeTable.get(typeName);
      if (varType == null) {
        reportError("Tipo indefinido: " + typeName.toString(), true);
        return null;
      }
      return varType;
    }
    else if (ty instanceof RecordTy) {
      return createRecordTypeEntry(name, (RecordTy) ty);
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

    RECORD params = createRecord(null, dec.params);
    RECORD params_og = params;

    while (params != null && params.name != null) {
      env.installVar(level, params.name, params.typ);
      params = params.tail;
    }

    // adiciona declaração da função à tabela no proprio escopo
    Type expectedType = dec.result == null? getTypeFromTable(Symbol.symbol("void")) : getTypeFromTable(dec.result.name);
    FuncEntry entry_in = env.installFunc(level, dec.name, expectedType, params_og, null);

    // build intermediate code
    ExpTy body = buildExp(dec.body);

    // check if return type of body equals to type in dec
    if (!isEquivalentTypes(expectedType, body.typ)) {
      reportError("Tipo da expressão incompatível com tipo da função " + dec.name.toString(), true);
      return null;
    }

    endScope();

    // adiciona declaração da função à tabela no escopo anterior
    FuncEntry entry_out = env.installFunc(level, dec.name, expectedType, params_og, entry_in.label);

    // adiciona código da função à lista de código
    ExpTy funcTree;
    if (dec.result != null) {
      // se tiver retorno, coloca o retorno na variável temp de retorno (rv)
      funcTree = new ExpTy(Translate.translateFunctionReturn(body.texp), getTypeFromTable(Symbol.symbol("void")));
    }
    else {
      funcTree = new ExpTy(body.texp, getTypeFromTable(Symbol.symbol("void")));
    }

    // adiciona funcao à arvore de funcoes
    labelTree.addFirst(new FuncTree(entry_out.label, funcTree));
    
    // Retorna void pra árvore principal
    return new ExpTy(Translate.translateNilExp(), getTypeFromTable(Symbol.symbol("void")));
  }

  
  /**************** Other Expressions *****************/

  private ExpTy buildArrayExp(ArrayExp exp) {
    Type type = getTypeFromTable(exp.typ);

    ExpTy size = buildExp(exp.size);
    // check if index has type int
    if(!(size.typ instanceof INT)) {
      reportError("Tamanho do array de tipo não inteiro", true);
      return null;
    }

    ExpTy init = buildExp(exp.init);

    // check if types are compatible
    if (!isEquivalentTypes(init.typ, ((ARRAY) type).typ)) {
      reportError("Tipo da expressão não compatível com tipo do array", true);
      return null;
    }

    ArrayList<TExp> targs = new ArrayList<TExp>();
    targs.add(size.texp);
    targs.add(init.texp);
    return new ExpTy(Translate.translateCall("initArray", false, targs), type);
  }


  private ExpTy buildRecordExp(RecordExp exp) {
    Type type = getTypeFromTable(exp.typ);

    // Checa número de campos passados e tipo de cada um. Traduz código para cada inicialização
    RECORD field = (RECORD) type;
    FieldExpList init = exp.fields;
    ArrayList<TExp> tinits = new ArrayList<TExp>();
    
    TExp tempTemp = Translate.translateSimpleVar(String.valueOf(env.nextTemp));
    env.nextTemp++;

    int size = 0;
    while ((init != null) && (field != null)) {
      ExpTy initTree = buildExp(init.init);

      if (!(isEquivalentTypes(initTree.typ, field.typ))) {
        reportError("Tipo do campo \"" + field.name.toString() + "\" difere do valor passado", true);
        return null;
      }
      
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
    targs.add(Translate.translateNilExp());
    targs.add(Translate.translateInt(size * Translate.wordSize));
    ExpTy mallocaRegistro = new ExpTy(Translate.translateCall("malloc", false, targs), type);
    ExpTy alocaRegistro = new ExpTy(Translate.translateAssign(tempTemp, mallocaRegistro.texp), type);

    ExpTy initTree = buildFieldList(tinits, 0);

    ExpTy retornaRegistro = new ExpTy(Translate.translateRecordExp(alocaRegistro.texp, initTree.texp), type);
    return new ExpTy(Translate.translateRecordAssignExp(retornaRegistro.texp, tempTemp), type);
  }

  private ExpTy buildFieldList(ArrayList<TExp> tinits, int index) {
    // Vazia
    if(index == tinits.size()) {
      return new ExpTy(Translate.translateNilExp(), getTypeFromTable(Symbol.symbol("void")));
    }

    // ExpList possui apenas cabeca -> nao tem SEQ
    if(index == tinits.size()-1) {
      return new ExpTy(tinits.get(index), getTypeFromTable(Symbol.symbol("void")));
    }
    
    // Completa
    ExpTy head = new ExpTy(tinits.get(index), getTypeFromTable(Symbol.symbol("void")));
    ExpTy tail = buildFieldList(tinits, index+1);
    return new ExpTy(Translate.translateRecordExp(head.texp, tail.texp), getTypeFromTable(Symbol.symbol("void")));
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
    while ((arg != null) && (param != null && param.name != null)) {
      ExpTy argTree = buildExp(arg.head);
      if (!(isEquivalentTypes(argTree.typ, param.typ))) {
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
    if ((param != null && param.name != null) && (arg == null)) {
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
      return new ExpTy(Translate.translateOpExp(exp.oper, left.texp, right.texp), getTypeFromTable(Symbol.symbol("int"))); 
    }
  }


  /************ Lists ***********/

  private ExpTy buildDecList(DecList d) {
    if(d == null) return new ExpTy(null, getTypeFromTable(Symbol.symbol("void")));

    // DecList possui apenas cabeca -> nao tem SEQ
    if(d.tail == null) {
      return new ExpTy(buildDec(d.head).texp, getTypeFromTable(Symbol.symbol("void"))); 
    }
    
    // Completa
    ExpTy head = buildDec(d.head);
    ExpTy tail = buildDecList(d.tail);
    return new ExpTy(Translate.translateDecList(head.texp, tail.texp), getTypeFromTable(Symbol.symbol("void"))); 
  }

  // ExpList
  public ExpTy buildExpList(ExpList e) {
    // Vazia
    if(e == null) {
      return new ExpTy(Translate.translateNilExp(), getTypeFromTable(Symbol.symbol("void")));
    }

    // ExpList possui apenas cabeca -> nao tem SEQ
    if(e.tail == null) {
      ExpTy head = buildExp(e.head);
      return new ExpTy(head.texp, head.typ);
    }
    
    // Completa
    ExpTy head = buildExp(e.head);
    ExpTy tail = buildExpList(e.tail);
    return new ExpTy(Translate.translateExpList(head.texp, tail.texp), tail.typ);
  }

  /************ Control expressions ***********/

  private ExpTy buildLetExp(LetExp e){
		newScope();

    ExpTy declist = buildDecList(e.decs);
    ExpTy expList = buildSeqExp(e.body);
    
    ExpTy returnExp = new ExpTy(Translate.translateLetExp(declist.texp, expList.texp), expList.typ);

    endScope();

    return returnExp;
  }

  public ExpTy buildSeqExp(SeqExp e) {
    return buildExpList(e.list);
  }


  /************* Commands *************/
  private ExpTy buildIfExp(IfExp e) {
    // Gero tres labels novos: cai no if, cai no else, sai de tudo
    Label l1 = new Label("L" + Integer.toString(env.installLabel()));
    Label l2 = new Label("L" + Integer.toString(env.installLabel()));
    Label l3 = new Label("L" + Integer.toString(env.installLabel()));

    ExpTy condition = buildExp(e.test);
    ExpTy then = buildExp(e.thenclause);
    ExpTy senao = new ExpTy(Translate.translateNilExp(), getTypeFromTable(Symbol.symbol("void")));
    if(e.elseclause != null) senao = buildExp(e.elseclause);

    // Checa se expressao da condicao retorna inteiro
    if(!(condition.typ instanceof INT)) {
      String errorMsg = "Tipo invalido: inteiro esperado na condicao do IF";
      reportError(errorMsg, true);
      return null;
    }

    // Checa se tipos de then e else sao iguais
    if(e.elseclause != null) {
      if(!(isEquivalentTypes(then.typ, senao.typ))) {
        String errorMsg = "Clausulas then e else da expressao if devem retornar o mesmo tipo.";
        reportError(errorMsg, true);
        return null;
      }
    } else {
      if(!(then.typ instanceof VOID)) {
        String errorMsg = "void esperado no returno de IF.";
        reportError(errorMsg, true);
        return null;
      }
    }

    ExpTy jump = buildBranch(e.test, l1, l2);

    // Checa se if retorna algo ou nao
    if(then.typ instanceof VOID) {
      return new ExpTy(Translate.translateIfExp(jump.texp, then.texp, senao.texp, l1, l2, l3), then.typ);
    } else {
      ExpTy rv = new ExpTy(Translate.translateSimpleVar(Integer.toString(env.installTemp())), then.typ);
      ExpTy then_return = new ExpTy(Translate.translateAssign(rv.texp, then.texp), then.typ);
      ExpTy senao_return = new ExpTy(Translate.translateAssign(rv.texp, senao.texp), then.typ);
      ExpTy if_exp = new ExpTy(Translate.translateIfExp(jump.texp, then_return.texp, senao_return.texp, l1, l2, l3), then.typ);
      return new ExpTy(Translate.translateExpList(if_exp.texp, rv.texp), rv.typ);
    }
  }

  private ExpTy buildBreakExp(BreakExp exp) {
    ExpTy jump = new ExpTy(Translate.translateBreak(nextEscape), getTypeFromTable(Symbol.symbol("nil")));
    nextEscape = null;
    return jump;
  }

  private ExpTy buildWhileExp(WhileExp e) {
    // Gero tres labels novos: cai no teste, cai no corpo do while, sai de tudo
    Label test = new Label("L" + Integer.toString(env.installLabel()));
    Label in = new Label("L" + Integer.toString(env.installLabel()));
    Label out = new Label("L" + Integer.toString(env.installLabel()));

    Label previousEscape = nextEscape;
    nextEscape = out;
    ExpTy condition = buildExp(e.test);
    ExpTy body = buildExp(e.body);
    nextEscape = previousEscape;

    // Checa se expressao da condicao retorna inteiro
    if(!(condition.typ instanceof INT)) {
      String errorMsg = "Tipo invalido: inteiro esperado na condicao do WHILE";
      reportError(errorMsg, true);
      return null;
    }

    ExpTy jump = buildBranch(e.test, in, out);
    return new ExpTy(Translate.translateWhileExp(jump.texp, body.texp, test, in, out), getTypeFromTable(Symbol.symbol("void")));
  }

  private ExpTy buildForExp(ForExp e) {
    Label in = new Label("L" + Integer.toString(env.installLabel()));
    Label out = new Label("L" + Integer.toString(env.installLabel()));
    Label increment = new Label("L" + Integer.toString(env.installLabel()));

    Label previousEscape = nextEscape;
    nextEscape = out;
    newScope();
    
    ExpTy varDec = buildVarDec(e.var);
    ExpTy upper = buildExp(e.hi);
    if (!(upper.typ instanceof INT)) {
      reportError("Tipo invalido: inteiro esperado no TO do FOR", true);
      return null;
    }
    ExpTy body = buildExp(e.body);

    endScope();
    nextEscape = previousEscape;
  
    return new ExpTy(Translate.translateFor(varDec.texp, upper.texp, body.texp, in, out, increment), getTypeFromTable(Symbol.symbol("void")));
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
    if(typ1 instanceof RECORD && typ2 instanceof NIL) return true;
    if(typ1 instanceof NIL && typ2 instanceof RECORD) return true;
    if(typ1 instanceof RECORD && typ2 instanceof RECORD)  {
      return ((RECORD) typ1).recordName == ((RECORD) typ2).recordName;
    }
    if(typ1 == typ2) return true;
    return false;
  }

  private Type getTypeFromTable(Symbol typeName) {
    TypeEntry varType = (TypeEntry) env.typeTable.get(typeName);
    if (varType == null) {
      reportError("Tipo indefinido: " + typeName.toString(), true);
      return null;
    }
    return varType.typ;
  }

  private void reportError(String msg, boolean exit) {
      System.out.println("Erro: " + msg);
      if (exit) {
        System.exit(-1);
      }
  }

  private RECORD createRecord(Symbol name, FieldList fields) {
    // int size = 0;

    if (fields == null) {
      return new RECORD(name);
    }

    RECORD newRec = null;
    RECORD firstRec = null;
    RECORD prevRec = null;
    while (fields != null) {
      Type type = getTypeFromTable(fields.typ);
      newRec = new RECORD(name, fields.name, type, null, type.size);
      // size += type.size;
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

  private ExpTy buildBranch(Exp test, Label l1, Label l2) {    
    int oper;
    ExpTy left, right;
    if (test instanceof OpExp) {
      int test_oper = ((OpExp) test).oper;
      if (!(test_oper == OpExp.PLUS  ||
         test_oper == OpExp.MINUS ||
         test_oper == OpExp.MUL   ||
         test_oper == OpExp.DIV)) {
        oper = test_oper;
        left = buildExp(((OpExp) test).left);
        right = buildExp(((OpExp) test).right);
      } else {
        oper = OpExp.NE;
        left = buildExp(test);
        right = new ExpTy(Translate.translateInt(0), getTypeFromTable(Symbol.symbol("int")));
      }
    } else {
      oper = OpExp.NE;
      left = buildExp(test);
      right = new ExpTy(Translate.translateInt(0), getTypeFromTable(Symbol.symbol("int")));
    }
    
    return new ExpTy(Translate.translateIfCondExp(oper, left.texp, right.texp, l1, l2), getTypeFromTable(Symbol.symbol("void")));
  }

}