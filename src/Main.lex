package compiler;
import java_cup.runtime.*;

%%

%unicode
%cup

%state COMMENT

%%
<YYINITIAL> "/*"       { yybegin(COMMENT); }

<YYINITIAL> "nil"      { return new Symbol(sym.NIL); }
<YYINITIAL> "if"       { return new Symbol(sym.IF); }
<YYINITIAL> "then"     { return new Symbol(sym.THEN); }
<YYINITIAL> "else"     { return new Symbol(sym.ELSE); }
<YYINITIAL> "while"    { return new Symbol(sym.WHILE); }
<YYINITIAL> "do"       { return new Symbol(sym.DO); }
<YYINITIAL> "for"      { return new Symbol(sym.FOR); }
<YYINITIAL> "to"       { return new Symbol(sym.TO); }
<YYINITIAL> "break"    { return new Symbol(sym.BREAK); }
<YYINITIAL> "let"      { return new Symbol(sym.LET); }
<YYINITIAL> "in"       { return new Symbol(sym.IN); }
<YYINITIAL> "end"      { return new Symbol(sym.END); }
<YYINITIAL> "type"     { return new Symbol(sym.TYPE); }
<YYINITIAL> "array"    { return new Symbol(sym.ARRAY); }
<YYINITIAL> "of"       { return new Symbol(sym.OF); }
<YYINITIAL> "var"      { return new Symbol(sym.VAR); }
<YYINITIAL> "function" { return new Symbol(sym.FUNC); }

<YYINITIAL> [a-zA-Z][_a-zA-Z0-9]* { return new Symbol(sym.ID, yytext()); }
<YYINITIAL> [0-9]+ { return new Symbol(sym.NUM, new Integer(Integer.parseInt(yytext()))); }
<YYINITIAL> \"[^\"]*\" { return new Symbol(sym.STRING, yytext()); }
<YYINITIAL> [ \t\n\r\f] { ; }

<YYINITIAL> "("     { return new Symbol(sym.LPAR); }
<YYINITIAL> ")"     { return new Symbol(sym.RPAR); }

<YYINITIAL> "+"     { return new Symbol(sym.PLUS); }
<YYINITIAL> "-"     { return new Symbol(sym.MINUS); }
<YYINITIAL> "*"     { return new Symbol(sym.STAR); }
<YYINITIAL> "/"     { return new Symbol(sym.SLASH); }
<YYINITIAL> "="     { return new Symbol(sym.EQUAL); }
<YYINITIAL> "<>"    { return new Symbol(sym.NEQUAL); }
<YYINITIAL> "<"     { return new Symbol(sym.LT); }
<YYINITIAL> ">"     { return new Symbol(sym.GT); }
<YYINITIAL> "<="    { return new Symbol(sym.LEQ); }
<YYINITIAL> ">="    { return new Symbol(sym.GEQ); }
<YYINITIAL> "&"     { return new Symbol(sym.AND); }
<YYINITIAL> "|"     { return new Symbol(sym.OR); }

<YYINITIAL> "{"     { return new Symbol(sym.LCBR); }
<YYINITIAL> "}"     { return new Symbol(sym.RCBR); }
<YYINITIAL> "["     { return new Symbol(sym.LSBR); }
<YYINITIAL> "]"     { return new Symbol(sym.RSBR); }

<YYINITIAL> ":="    { return new Symbol(sym.ATTR); }
<YYINITIAL> ":"     { return new Symbol(sym.COLON); }
<YYINITIAL> ","     { return new Symbol(sym.COMMA); }
<YYINITIAL> ";"     { return new Symbol(sym.SEMI); }
<YYINITIAL> "."     { return new Symbol(sym.DOT); }


<COMMENT> "/*"        { ; }
<COMMENT> "*/"        { yybegin(YYINITIAL); }
<COMMENT> "*"+        { ; }
<COMMENT> [^\/\*\n]+  { ; }
<COMMENT> [\/]        { ; }
<COMMENT> \n          { ; }


<YYINITIAL> [^]       { throw new Error("Illegal token: " + yytext()); }