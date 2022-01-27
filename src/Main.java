package compiler;

import java_cup.runtime.*;
import java.io.*;
import Absyn.*;
import Symbol.*;
import Semant.*;

public class Main {	
    
    public static void main(String args[]) throws Exception {
		Yylex lexer = new Yylex(System.in);
		parser p = new parser(lexer);
		Exp result = (Exp) p.parse().value;
		Print pr = new Print(System.out);
		pr.prExp(result, 0);
		System.out.println("\n");

		Semant smt = new Semant(0);
		ExpTy texp = smt.buildExp((Exp) result);

		System.out.println(texp.toString());
	}
}