package compiler;
import java_cup.runtime.Symbol;


class Yylex implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NOT_ACCEPT,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NOT_ACCEPT,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NOT_ACCEPT,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"4:9,5,1,4,5,1,4:18,5,4,47,4:3,22,4,10,11,3,16,8,17,9,2,46:10,7,6,18,20,19,4" +
":2,48:26,12,4,13,4,49,4,36,40,35,38,34,28,33,32,25,48,43,29,48,26,37,23,48," +
"24,31,27,30,45,42,41,44,39,14,21,15,4:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,132,
"0,1,2,3,1:3,4,1:10,5,6,1:3,7,8,1:4,9:5,1,9:7,10,9:14,10,11,12,11,13,14,15,1" +
"6,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,4" +
"1,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,9,60,61,62,63,64,65" +
",66,67,68,69,70,71,72,73,74,9,75,76,77,78,79,80,81")[0];

	private int yy_nxt[][] = unpackFromString(82,50,
"1,2,3,4,5,2,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,124,59,86,61," +
"87,88,124,106,124,128,89,90,129,63,64,124,130,124,131,124:2,91,24,58,124,5," +
"-1:51,2,-1:3,2,-1:47,57,-1:66,25,-1:48,26,27,-1:49,28,-1:52,124,107,124:21," +
"108,-1,124,108,-1:46,24,-1:26,124:23,108,-1,124,108,-1:2,57,62,57:46,-1,60:" +
"46,34,60:2,-1:23,124:3,29,124,30,124:17,108,-1,124,108,-1:23,124:9,92,124:4" +
",31,124:6,93,124,108,-1,124,108,-1:2,42,62,57:46,-1:23,124,70,124:3,32,124:" +
"17,108,-1,124,108,-1:23,124:14,33,124:8,108,-1,124,108,-1:23,124:6,35,124:1" +
"6,108,-1,124,108,-1:23,124,36,124:21,108,-1,124,108,-1:23,124:4,37,124:18,1" +
"08,-1,124,108,-1:23,124:15,38,124:7,108,-1,124,108,-1:23,124,39,124:21,108," +
"-1,124,108,-1:23,124:15,40,124:7,108,-1,124,108,-1:23,124,41,124:21,108,-1," +
"124,108,-1:23,124:3,43,124:19,108,-1,124,108,-1:23,124:11,44,124:11,108,-1," +
"124,108,-1:23,124:11,45,124:11,108,-1,124,108,-1:23,124:11,46,124:11,108,-1" +
",124,108,-1:23,124:4,47,124:18,108,-1,124,108,-1:23,124:4,48,124:18,108,-1," +
"124,108,-1:23,124:9,49,124:13,108,-1,124,108,-1:23,124:21,50,124,108,-1,124" +
",108,-1:23,124:20,51,124:2,108,-1,124,108,-1:23,124:11,52,124:11,108,-1,124" +
",108,-1:23,124:4,53,124:18,108,-1,124,108,-1:23,124,54,124:21,108,-1,124,10" +
"8,-1:23,124:3,55,124:19,108,-1,124,108,-1:23,124:10,56,124:12,108,-1,124,10" +
"8,-1:23,124:2,65,124:20,108,-1,124,108,-1:23,124:6,109,110,124:6,66,124:8,1" +
"08,-1,124,108,-1:23,124:11,67,124:11,108,-1,124,108,-1:23,124:3,68,124:2,95" +
",124:11,96,124:4,108,-1,124,108,-1:23,124:9,69,124:4,125,124:8,108,-1,124,1" +
"08,-1:23,124:13,71,124:9,108,-1,124,108,-1:23,124:11,72,124:11,108,-1,124,1" +
"08,-1:23,73,124:22,108,-1,124,108,-1:23,124:16,74,124:6,108,-1,124,108,-1:2" +
"3,124:8,75,124:14,108,-1,124,108,-1:23,124:2,76,124:20,108,-1,124,108,-1:23" +
",124:3,77,124:19,108,-1,124,108,-1:23,124:8,78,124:14,108,-1,124,108,-1:23," +
"124:13,79,124:9,108,-1,124,108,-1:23,124:13,80,124:9,108,-1,124,108,-1:23,1" +
"24:6,81,124:16,108,-1,124,108,-1:23,124:13,82,124:9,108,-1,124,108,-1:23,12" +
"4:13,83,124:9,108,-1,124,108,-1:23,124:14,84,124:8,108,-1,124,108,-1:23,124" +
":3,85,124:19,108,-1,124,108,-1:23,124:2,94,124:4,111,124:15,108,-1,124,108," +
"-1:23,124:2,97,124:20,108,-1,124,108,-1:23,124:7,98,124:15,108,-1,124,108,-" +
"1:23,124:3,116,124:19,108,-1,124,108,-1:23,124:17,117,124:5,108,-1,124,108," +
"-1:23,124:4,126,124:18,108,-1,124,108,-1:23,124,99,124:21,108,-1,124,108,-1" +
":23,124:11,100,124:11,108,-1,124,108,-1:23,124:2,101,124:20,108,-1,124,108," +
"-1:23,124:12,119,124:10,108,-1,124,108,-1:23,124:8,127,124:14,108,-1,124,10" +
"8,-1:23,124:12,102,124:10,108,-1,124,108,-1:23,124:4,121,124:18,108,-1,124," +
"108,-1:23,124:9,103,124:13,108,-1,124,108,-1:23,124:2,104,124:20,108,-1,124" +
",108,-1:23,124,123,124:21,108,-1,124,108,-1:23,124:2,105,124:20,108,-1,124," +
"108,-1:23,124:3,118,124:19,108,-1,124,108,-1:23,124:12,120,124:10,108,-1,12" +
"4,108,-1:23,124:4,122,124:18,108,-1,124,108,-1:23,124:11,112,124:11,108,-1," +
"124,108,-1:23,124,113,124:21,108,-1,124,108,-1:23,124,114,124:21,108,-1,124" +
",108,-1:23,124:9,115,124:13,108,-1,124,108");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {
				return null;
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ }
					case -3:
						break;
					case 3:
						{ return new Symbol(sym.DIV); }
					case -4:
						break;
					case 4:
						{ return new Symbol(sym.TIMES); }
					case -5:
						break;
					case 5:
						{ System.err.println("Illegal character: "+yytext()); }
					case -6:
						break;
					case 6:
						{ return new Symbol(sym.SEMICOLON); }
					case -7:
						break;
					case 7:
						{ return new Symbol(sym.COLON); }
					case -8:
						break;
					case 8:
						{ return new Symbol(sym.COMMA); }
					case -9:
						break;
					case 9:
						{ return new Symbol(sym.DOT); }
					case -10:
						break;
					case 10:
						{ return new Symbol(sym.OPEN_PARENTHESIS); }
					case -11:
						break;
					case 11:
						{ return new Symbol(sym.CLOSE_PARENTHESIS); }
					case -12:
						break;
					case 12:
						{ return new Symbol(sym.OPEN_SQUARE); }
					case -13:
						break;
					case 13:
						{ return new Symbol(sym.CLOSE_SQUARE); }
					case -14:
						break;
					case 14:
						{ return new Symbol(sym.OPEN_CURLY); }
					case -15:
						break;
					case 15:
						{ return new Symbol(sym.CLOSE_CURLY); }
					case -16:
						break;
					case 16:
						{ return new Symbol(sym.PLUS); }
					case -17:
						break;
					case 17:
						{ return new Symbol(sym.MINUS); }
					case -18:
						break;
					case 18:
						{ return new Symbol(sym.LT); }
					case -19:
						break;
					case 19:
						{ return new Symbol(sym.GT); }
					case -20:
						break;
					case 20:
						{ return new Symbol(sym.EQ); }
					case -21:
						break;
					case 21:
						{ return new Symbol(sym.OR); }
					case -22:
						break;
					case 22:
						{ return new Symbol(sym.AND); }
					case -23:
						break;
					case 23:
						{ return new Symbol(sym.ID, yytext()); }
					case -24:
						break;
					case 24:
						{ return new Symbol(sym.INTEGER_CONSTANT, new Integer(Integer.parseInt(yytext()))); }
					case -25:
						break;
					case 25:
						{ return new Symbol(sym.ATTRIBUTION); }
					case -26:
						break;
					case 26:
						{ return new Symbol(sym.NEQ); }
					case -27:
						break;
					case 27:
						{ return new Symbol(sym.LTE); }
					case -28:
						break;
					case 28:
						{ return new Symbol(sym.GTE); }
					case -29:
						break;
					case 29:
						{ return new Symbol(sym.IN); }
					case -30:
						break;
					case 30:
						{ return new Symbol(sym.IF); }
					case -31:
						break;
					case 31:
						{ return new Symbol(sym.TO); }
					case -32:
						break;
					case 32:
						{ return new Symbol(sym.OF); }
					case -33:
						break;
					case 33:
						{ return new Symbol(sym.DO); }
					case -34:
						break;
					case 34:
						{ return new Symbol(sym.STRING_CONSTANT, yytext()); }
					case -35:
						break;
					case 35:
						{ return new Symbol(sym.NIL); }
					case -36:
						break;
					case 36:
						{ return new Symbol(sym.FOR); }
					case -37:
						break;
					case 37:
						{ return new Symbol(sym.LET); }
					case -38:
						break;
					case 38:
						{ return new Symbol(sym.END); }
					case -39:
						break;
					case 39:
						{ return new Symbol(sym.ID, yytext()); }
					case -40:
						break;
					case 40:
						{ return new Symbol(sym.ID, yytext()); }
					case -41:
						break;
					case 41:
						{ return new Symbol(sym.VAR); }
					case -42:
						break;
					case 42:
						{ }
					case -43:
						break;
					case 43:
						{ return new Symbol(sym.THEN); }
					case -44:
						break;
					case 44:
						{ return new Symbol(sym.TYPE); }
					case -45:
						break;
					case 45:
						{ return new Symbol(sym.ID, yytext()); }
					case -46:
						break;
					case 46:
						{ return new Symbol(sym.ELSE); }
					case -47:
						break;
					case 47:
						{ return new Symbol(sym.ID, yytext()); }
					case -48:
						break;
					case 48:
						{ return new Symbol(sym.ID, yytext()); }
					case -49:
						break;
					case 49:
						{ return new Symbol(sym.ID, yytext()); }
					case -50:
						break;
					case 50:
						{ return new Symbol(sym.ARRAY); }
					case -51:
						break;
					case 51:
						{ return new Symbol(sym.BREAK); }
					case -52:
						break;
					case 52:
						{ return new Symbol(sym.WHILE); }
					case -53:
						break;
					case 53:
						{ return new Symbol(sym.ID, yytext()); }
					case -54:
						break;
					case 54:
						{ return new Symbol(sym.ID, yytext()); }
					case -55:
						break;
					case 55:
						{ return new Symbol(sym.FUNCTION); }
					case -56:
						break;
					case 56:
						{ return new Symbol(sym.ID, yytext()); }
					case -57:
						break;
					case 58:
						{ System.err.println("Illegal character: "+yytext()); }
					case -58:
						break;
					case 59:
						{ return new Symbol(sym.ID, yytext()); }
					case -59:
						break;
					case 61:
						{ return new Symbol(sym.ID, yytext()); }
					case -60:
						break;
					case 63:
						{ return new Symbol(sym.ID, yytext()); }
					case -61:
						break;
					case 64:
						{ return new Symbol(sym.ID, yytext()); }
					case -62:
						break;
					case 65:
						{ return new Symbol(sym.ID, yytext()); }
					case -63:
						break;
					case 66:
						{ return new Symbol(sym.ID, yytext()); }
					case -64:
						break;
					case 67:
						{ return new Symbol(sym.ID, yytext()); }
					case -65:
						break;
					case 68:
						{ return new Symbol(sym.ID, yytext()); }
					case -66:
						break;
					case 69:
						{ return new Symbol(sym.ID, yytext()); }
					case -67:
						break;
					case 70:
						{ return new Symbol(sym.ID, yytext()); }
					case -68:
						break;
					case 71:
						{ return new Symbol(sym.ID, yytext()); }
					case -69:
						break;
					case 72:
						{ return new Symbol(sym.ID, yytext()); }
					case -70:
						break;
					case 73:
						{ return new Symbol(sym.ID, yytext()); }
					case -71:
						break;
					case 74:
						{ return new Symbol(sym.ID, yytext()); }
					case -72:
						break;
					case 75:
						{ return new Symbol(sym.ID, yytext()); }
					case -73:
						break;
					case 76:
						{ return new Symbol(sym.ID, yytext()); }
					case -74:
						break;
					case 77:
						{ return new Symbol(sym.ID, yytext()); }
					case -75:
						break;
					case 78:
						{ return new Symbol(sym.ID, yytext()); }
					case -76:
						break;
					case 79:
						{ return new Symbol(sym.ID, yytext()); }
					case -77:
						break;
					case 80:
						{ return new Symbol(sym.ID, yytext()); }
					case -78:
						break;
					case 81:
						{ return new Symbol(sym.ID, yytext()); }
					case -79:
						break;
					case 82:
						{ return new Symbol(sym.ID, yytext()); }
					case -80:
						break;
					case 83:
						{ return new Symbol(sym.ID, yytext()); }
					case -81:
						break;
					case 84:
						{ return new Symbol(sym.ID, yytext()); }
					case -82:
						break;
					case 85:
						{ return new Symbol(sym.ID, yytext()); }
					case -83:
						break;
					case 86:
						{ return new Symbol(sym.ID, yytext()); }
					case -84:
						break;
					case 87:
						{ return new Symbol(sym.ID, yytext()); }
					case -85:
						break;
					case 88:
						{ return new Symbol(sym.ID, yytext()); }
					case -86:
						break;
					case 89:
						{ return new Symbol(sym.ID, yytext()); }
					case -87:
						break;
					case 90:
						{ return new Symbol(sym.ID, yytext()); }
					case -88:
						break;
					case 91:
						{ return new Symbol(sym.ID, yytext()); }
					case -89:
						break;
					case 92:
						{ return new Symbol(sym.ID, yytext()); }
					case -90:
						break;
					case 93:
						{ return new Symbol(sym.ID, yytext()); }
					case -91:
						break;
					case 94:
						{ return new Symbol(sym.ID, yytext()); }
					case -92:
						break;
					case 95:
						{ return new Symbol(sym.ID, yytext()); }
					case -93:
						break;
					case 96:
						{ return new Symbol(sym.ID, yytext()); }
					case -94:
						break;
					case 97:
						{ return new Symbol(sym.ID, yytext()); }
					case -95:
						break;
					case 98:
						{ return new Symbol(sym.ID, yytext()); }
					case -96:
						break;
					case 99:
						{ return new Symbol(sym.ID, yytext()); }
					case -97:
						break;
					case 100:
						{ return new Symbol(sym.ID, yytext()); }
					case -98:
						break;
					case 101:
						{ return new Symbol(sym.ID, yytext()); }
					case -99:
						break;
					case 102:
						{ return new Symbol(sym.ID, yytext()); }
					case -100:
						break;
					case 103:
						{ return new Symbol(sym.ID, yytext()); }
					case -101:
						break;
					case 104:
						{ return new Symbol(sym.ID, yytext()); }
					case -102:
						break;
					case 105:
						{ return new Symbol(sym.ID, yytext()); }
					case -103:
						break;
					case 106:
						{ return new Symbol(sym.ID, yytext()); }
					case -104:
						break;
					case 107:
						{ return new Symbol(sym.ID, yytext()); }
					case -105:
						break;
					case 108:
						{ return new Symbol(sym.ID, yytext()); }
					case -106:
						break;
					case 109:
						{ return new Symbol(sym.ID, yytext()); }
					case -107:
						break;
					case 110:
						{ return new Symbol(sym.ID, yytext()); }
					case -108:
						break;
					case 111:
						{ return new Symbol(sym.ID, yytext()); }
					case -109:
						break;
					case 112:
						{ return new Symbol(sym.ID, yytext()); }
					case -110:
						break;
					case 113:
						{ return new Symbol(sym.ID, yytext()); }
					case -111:
						break;
					case 114:
						{ return new Symbol(sym.ID, yytext()); }
					case -112:
						break;
					case 115:
						{ return new Symbol(sym.ID, yytext()); }
					case -113:
						break;
					case 116:
						{ return new Symbol(sym.ID, yytext()); }
					case -114:
						break;
					case 117:
						{ return new Symbol(sym.ID, yytext()); }
					case -115:
						break;
					case 118:
						{ return new Symbol(sym.ID, yytext()); }
					case -116:
						break;
					case 119:
						{ return new Symbol(sym.ID, yytext()); }
					case -117:
						break;
					case 120:
						{ return new Symbol(sym.ID, yytext()); }
					case -118:
						break;
					case 121:
						{ return new Symbol(sym.ID, yytext()); }
					case -119:
						break;
					case 122:
						{ return new Symbol(sym.ID, yytext()); }
					case -120:
						break;
					case 123:
						{ return new Symbol(sym.ID, yytext()); }
					case -121:
						break;
					case 124:
						{ return new Symbol(sym.ID, yytext()); }
					case -122:
						break;
					case 125:
						{ return new Symbol(sym.ID, yytext()); }
					case -123:
						break;
					case 126:
						{ return new Symbol(sym.ID, yytext()); }
					case -124:
						break;
					case 127:
						{ return new Symbol(sym.ID, yytext()); }
					case -125:
						break;
					case 128:
						{ return new Symbol(sym.ID, yytext()); }
					case -126:
						break;
					case 129:
						{ return new Symbol(sym.ID, yytext()); }
					case -127:
						break;
					case 130:
						{ return new Symbol(sym.ID, yytext()); }
					case -128:
						break;
					case 131:
						{ return new Symbol(sym.ID, yytext()); }
					case -129:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
