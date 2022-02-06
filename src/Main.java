package compiler;

import java_cup.runtime.*;
import java.io.*;
import Absyn.*;
import Symbol.*;
import Semant.*;
import Tree.TPrint;
import Tree.TExp;

public class Main {	
    
    public static void main(String args[]) throws Exception {
		Yylex lexer = new Yylex(System.in);
		parser p = new parser(lexer);
		Exp result = (Exp) p.parse().value;
		Print pr = new Print(System.out);
		pr.prExp(result, 0);
		System.out.println("\n");

		Semant smt = new Semant(0);
		TPrint tpr = new TPrint(System.out);
		TExp texp = smt.build(result).texp;
		System.out.println("");
		tpr.prExp(texp);

		// Imprime as funcoes
		smt.labelTree.forEach((f) -> {
			if(f instanceof FuncTree) {
				System.out.println("\n" + f.label + ":");
				tpr.prExp(((FuncTree) f).exp.texp);
			} else {
				System.out.println("\n" + f.label + " = " + ((StringTree) f).val);
			}
		});
	}
}