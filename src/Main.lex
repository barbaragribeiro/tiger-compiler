package compiler;

import java_cup.runtime.Symbol;

%%
%cup

WHITESPACE=[ \t\n\r\f]

DIGIT=[0-9]
LETTER=[A-Za-z]
ASCII_SEQ=[\x00-\x7F]*
SIGN=[+-]?

ID={LETTER}({LETTER}|{DIGIT}|_)*

UNSIGNED_INTEGER={DIGIT}+
INTEGER_CONSTANT={UNSIGNED_INTEGER}
STRING_CONSTANT=\"([^\"]*)\"

COMMENT=/\*(.*)\*/

%%
{WHITESPACE}+      { }
{COMMENT}          { }

";"                { return new Symbol(sym.SEMICOLON); }
":"                { return new Symbol(sym.COLON); }
","                { return new Symbol(sym.COMMA); }
"."                { return new Symbol(sym.DOT); }

"("                { return new Symbol(sym.OPEN_PARENTHESIS); }
")"                { return new Symbol(sym.CLOSE_PARENTHESIS); }
"["                { return new Symbol(sym.OPEN_SQUARE); }
"]"                { return new Symbol(sym.CLOSE_SQUARE); }
"{"                { return new Symbol(sym.OPEN_CURLY); }
"}"                { return new Symbol(sym.CLOSE_CURLY); }

"+"                 { return new Symbol(sym.PLUS); }
"-"                 { return new Symbol(sym.MINUS); }
"*"                 { return new Symbol(sym.TIMES); }
"/"                 { return new Symbol(sym.DIV); }

"<>"                { return new Symbol(sym.NEQ); }
"<="                { return new Symbol(sym.LTE); }
">="                { return new Symbol(sym.GTE); }
"="                 { return new Symbol(sym.EQ); }
"<"                 { return new Symbol(sym.LT); }
">"                 { return new Symbol(sym.GT); }

"|"                 { return new Symbol(sym.OR); }
"&"                 { return new Symbol(sym.AND); }

":="                { return new Symbol(sym.ATTRIBUTION); }

"print"             { return new Symbol(sym.ID, yytext()); }
"flush"             { return new Symbol(sym.ID, yytext()); }
"getchar"           { return new Symbol(sym.ID, yytext()); }
"ord"               { return new Symbol(sym.ID, yytext()); }
"chr"               { return new Symbol(sym.ID, yytext()); }
"size"              { return new Symbol(sym.ID, yytext()); }
"substring"         { return new Symbol(sym.ID, yytext()); }
"concat"            { return new Symbol(sym.ID, yytext()); }
"exit"              { return new Symbol(sym.ID, yytext()); }

"of"           	    { return new Symbol(sym.OF); }
"nil"               { return new Symbol(sym.NIL); }
"if"                { return new Symbol(sym.IF); }
"then"              { return new Symbol(sym.THEN); }
"else"              { return new Symbol(sym.ELSE); }
"while"             { return new Symbol(sym.WHILE); }
"do"                { return new Symbol(sym.DO); }
"for"               { return new Symbol(sym.FOR); }
"to"                { return new Symbol(sym.TO); }
"break"             { return new Symbol(sym.BREAK); }
"let"               { return new Symbol(sym.LET); }
"in"                { return new Symbol(sym.IN); }
"end"               { return new Symbol(sym.END); }
"type"              { return new Symbol(sym.TYPE); }
"array"             { return new Symbol(sym.ARRAY); }
"var"               { return new Symbol(sym.VAR); }
"function"          { return new Symbol(sym.FUNCTION); }

{INTEGER_CONSTANT}  { return new Symbol(sym.INTEGER_CONSTANT, new Integer(Integer.parseInt(yytext()))); }
{STRING_CONSTANT}   { return new Symbol(sym.STRING_CONSTANT, yytext()); }
{ID}        	    { return new Symbol(sym.ID, yytext()); }

.            	    { System.err.println("Illegal character: "+yytext()); } 
