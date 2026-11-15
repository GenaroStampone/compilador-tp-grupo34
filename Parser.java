//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "gramatica.y"
import java.io.*;
import java.util.*;
//#line 20 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short ID=257;
public final static short CTE_USHORT=258;
public final static short CTE_DOUBLEF=259;
public final static short CADENA=260;
public final static short IF=261;
public final static short ELSE=262;
public final static short END_IF=263;
public final static short BEGIN=264;
public final static short END=265;
public final static short POUT=266;
public final static short WHILE=267;
public final static short REPEAT=268;
public final static short TYPEDEF=269;
public final static short PRIVATE=270;
public final static short FRIEND=271;
public final static short EXTENDS=272;
public final static short CLASS=273;
public final static short FUNCTION=274;
public final static short RET=275;
public final static short USHORTINT=276;
public final static short DOUBLEF=277;
public final static short ASIGNACION=278;
public final static short ASIG_EXPR=279;
public final static short COMP_IGUAL=280;
public final static short COMP_DIST=281;
public final static short CORCHETE_IZQ=282;
public final static short CORCHETE_DER=283;
public final static short COMP_MAYOR_IGUAL=284;
public final static short COMP_MENOR_IGUAL=285;
public final static short UMINUS=286;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    1,    1,    3,    3,    3,    3,    3,    4,
    4,    4,    5,    5,    6,    9,    9,   10,   10,    7,
   12,   12,   13,   13,   14,   14,   14,   14,   15,    8,
   16,   16,   18,   18,    2,    2,   21,   17,   17,   20,
   20,   20,   20,   20,   22,   22,   23,   24,   27,   27,
   25,   25,   26,   28,   28,   28,   28,   28,   28,   19,
   19,   19,   19,   19,   19,   19,   19,   19,   19,   29,
   29,   30,   30,   30,   11,   11,
};
final static short yylen[] = {                            2,
    5,    4,    2,    0,    3,    1,    1,    1,    2,    1,
    1,    1,    1,    3,    7,    1,    3,    1,    2,    7,
    2,    0,    2,    0,    2,    1,    3,    3,    1,   12,
    2,    4,    5,    0,    1,    1,    3,    2,    0,    2,
    2,    2,    2,    2,    3,    6,    6,    7,    2,    0,
    4,    4,    3,    1,    1,    1,    1,    1,    1,    3,
    3,    3,    3,    2,    3,    2,    4,    1,    1,    6,
    3,    1,    3,    0,    1,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    4,    0,    0,    0,    0,    0,   39,    0,
    0,    0,    0,   10,   11,    0,    3,    0,    6,    7,
    8,   35,   36,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    2,   13,    0,
    0,   40,   41,   42,   43,    1,    0,   75,   76,    0,
   69,    0,    0,    0,    0,    0,    0,   37,   38,    0,
    0,    0,    0,    0,    0,    0,    5,    0,    0,    0,
    0,   66,   64,    0,    0,    0,    0,    0,   54,   55,
   56,   57,   58,   59,    0,    0,   44,   51,   52,    0,
    0,    0,   24,    0,   14,    0,    0,    0,    0,    0,
    0,   62,   63,    0,    0,    0,    0,    0,    0,   16,
   18,    0,   12,    0,    0,   67,    0,    0,    0,    0,
    0,   47,   19,    0,    0,    0,    0,   29,    0,    0,
   26,   23,    0,   31,    0,    4,    0,    0,   49,   48,
   15,   17,    9,   20,    0,    0,   25,    0,    0,    0,
   27,   28,   32,   39,   70,    0,    0,    0,    0,    0,
    0,   30,    0,   33,
};
final static short yydgoto[] = {                          2,
    4,   16,   17,   18,   41,   19,   20,   21,  109,  110,
   51,   65,  112,  132,  133,  115,   33,  158,   54,   22,
   23,   24,   25,   26,   27,   55,  121,   85,   72,   99,
};
final static short yysindex[] = {                      -206,
   32,    0,    0, -181, -181,   48, -148,   13,    0,   33,
   82, -158, -133,    0,    0,   70,    0, -195,    0,    0,
    0,    0,    0,   91,   97,  101,  103,  108,    0,   15,
   15,   15, -130,  -33,   15,  113,  -90,    0,    0,  -74,
   49,    0,    0,    0,    0,    0,   -6,    0,    0,   15,
    0,  256,   14,    7,  143,  131, -148,    0,    0,  153,
  189,  155,  -85,  -59,  -64,  161,    0,  -53,   15,   15,
   15,    0,    0,   15,   15,   15,   15,  -73,    0,    0,
    0,    0,    0,    0,   15,   29,    0,    0,    0,  -62,
  -30,  164,    0, -199,    0,   21,  256,  256,   56,   81,
   81,    0,    0,   15,  256,  -48,   29, -120,  -28,    0,
    0, -124,    0,  -46,   99,    0,   15,  -66,  256,   29,
  -45,    0,    0,  150,  -30,  158,  162,    0,  -59,  -59,
    0,    0, -175,    0, -199,    0,  256,  -30,    0,    0,
    0,    0,    0,    0,   59,   61,    0,  -37,  -84,   -8,
    0,    0,    0,    0,    0,  -80,  193,  -24,   15,  176,
  236,    0,  178,    0,
};
final static short yyrindex[] = {                         0,
  -98,    0,    0,    0,    0,    0, -170,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  -54,    0,
    0,    0,    0,    0,    0,    0,  -26,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -41,    0,    0,    0,
    0,  103,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  110,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  -16,    0,    0,    0,    0,  -27,  120,    0,  -21,
  -14,    0,    0,    0,  204,  -11,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  -34,  108,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  147,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   -4,    0,    0,    0,    0,
    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   34,   35,  -68,  -51,  -17,    0,    0,    0,  127,  157,
  -19,    0,    0,    0,    0,    0,  114,    0,   40,    9,
    0,    0,    0,    0,    0,  245,    0,    0,    0,    0,
};
final static int YYTABLESIZE=304;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         68,
   68,   68,   68,   68,   44,   68,   71,   71,   71,   71,
   71,   50,   71,   65,  108,  125,   65,   68,   68,   60,
   68,   60,   60,   60,   71,   71,   61,   71,   61,   61,
   61,   65,   65,   71,   65,  125,    5,   60,   60,   28,
   60,   59,  114,  131,   61,   61,   92,   61,   76,   74,
    1,   75,   32,   77,   70,   76,   74,  113,   75,   50,
   77,   39,   76,   74,  147,   75,   84,   77,   83,   52,
   53,  111,   34,   61,    6,    7,   14,   15,   40,    8,
  126,  113,    9,  148,   10,   11,   12,   12,  123,   73,
    3,   13,   68,   12,   14,   15,  118,   13,   36,  117,
   14,   15,   68,   12,   68,  111,   29,   67,   96,   97,
   98,  145,  146,  100,  101,  102,  103,  151,  111,  152,
  106,   35,   76,   37,  105,   56,   57,   77,   38,   30,
    8,  126,  113,   31,   58,   10,   11,   48,   49,  136,
  127,  122,  135,  119,   12,  128,  129,  130,   13,   42,
   74,   14,   15,   74,  139,   43,  137,    4,    4,   44,
   72,   45,    4,   72,   59,    4,   46,    4,    4,  149,
    4,  126,  113,   63,    4,   56,   57,    4,    4,  154,
    8,   64,   66,   86,   12,   10,   11,   73,   13,   87,
   73,   14,   15,   88,  157,   90,   91,   39,  161,   93,
   94,    9,    9,   95,  104,  107,    9,   68,  141,    9,
  134,    9,    9,  120,    9,  138,  143,  140,    9,  153,
  144,    9,    9,   47,   48,   49,   60,   48,   49,   89,
   76,   74,  159,   75,  162,   77,  164,   22,   68,   68,
  160,   68,   68,   68,   53,   71,   71,   21,   71,   71,
   71,   50,   65,   65,  124,   65,   65,   65,   60,   60,
   34,   60,   60,   60,  150,   61,   61,  156,   61,   61,
   61,   47,   48,   49,  155,   69,  163,   76,   74,   62,
   75,  142,   77,    0,   56,   57,   79,   80,    0,    8,
   81,   82,    9,    0,   10,   11,   78,   76,   74,    0,
   75,    0,   77,  116,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   44,   45,   59,   47,   41,   42,   43,   44,
   45,   45,   47,   41,   45,   44,   44,   59,   60,   41,
   62,   43,   44,   45,   59,   60,   41,   62,   43,   44,
   45,   59,   60,   40,   62,   44,    3,   59,   60,    5,
   62,   33,   94,  112,   59,   60,   64,   62,   42,   43,
  257,   45,   40,   47,   61,   42,   43,  257,   45,   45,
   47,  257,   42,   43,  133,   45,   60,   47,   62,   30,
   31,   91,   40,   34,  256,  257,  276,  277,  274,  261,
  256,  257,  264,  135,  266,  267,  257,  269,  108,   50,
   59,  273,   44,  269,  276,  277,   41,  273,  257,   44,
  276,  277,   44,  274,   44,  125,   59,   59,   69,   70,
   71,  129,  130,   74,   75,   76,   77,   59,  138,   59,
   86,   40,   42,  257,   85,  256,  257,   47,   59,  278,
  261,  256,  257,  282,  265,  266,  267,  258,  259,   41,
  265,  107,   44,  104,  269,  270,  271,  272,  273,   59,
   41,  276,  277,   44,  120,   59,  117,  256,  257,   59,
   41,   59,  261,   44,  156,  264,   59,  266,  267,  136,
  269,  256,  257,   61,  273,  256,  257,  276,  277,  264,
  261,  272,  257,   41,  269,  266,  267,   41,  273,   59,
   44,  276,  277,   41,  275,   41,  282,  257,  159,  264,
   40,  256,  257,  257,  278,  268,  261,   44,   59,  264,
  257,  266,  267,  262,  269,  282,   59,  263,  273,  257,
   59,  276,  277,  257,  258,  259,  260,  258,  259,   41,
   42,   43,   40,   45,   59,   47,   59,  264,  280,  281,
  265,  283,  284,  285,   41,  280,  281,  264,  283,  284,
  285,  263,  280,  281,  283,  283,  284,  285,  280,  281,
  265,  283,  284,  285,  138,  280,  281,  154,  283,  284,
  285,  257,  258,  259,  283,  282,   41,   42,   43,   35,
   45,  125,   47,   -1,  256,  257,  280,  281,   -1,  261,
  284,  285,  264,   -1,  266,  267,  283,   42,   43,   -1,
   45,   -1,   47,  283,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=286;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,"ID","CTE_USHORT","CTE_DOUBLEF","CADENA","IF",
"ELSE","END_IF","BEGIN","END","POUT","WHILE","REPEAT","TYPEDEF","PRIVATE",
"FRIEND","EXTENDS","CLASS","FUNCTION","RET","USHORTINT","DOUBLEF","ASIGNACION",
"ASIG_EXPR","COMP_IGUAL","COMP_DIST","CORCHETE_IZQ","CORCHETE_DER",
"COMP_MAYOR_IGUAL","COMP_MENOR_IGUAL","UMINUS",
};
final static String yyrule[] = {
"$accept : programa",
"programa : ID ';' bloque_declarativo cuerpo_ejecutable ';'",
"programa : ID bloque_declarativo cuerpo_ejecutable ';'",
"bloque_declarativo : bloque_declarativo declaracion",
"bloque_declarativo :",
"declaracion : type_specifier lista_variables ';'",
"declaracion : declaracion_typedef",
"declaracion : declaracion_clase",
"declaracion : declaracion_funcion",
"declaracion : error ';'",
"type_specifier : USHORTINT",
"type_specifier : DOUBLEF",
"type_specifier : ID",
"lista_variables : ID",
"lista_variables : lista_variables ',' ID",
"declaracion_typedef : TYPEDEF ID '=' CORCHETE_IZQ lista_valores CORCHETE_DER ';'",
"lista_valores : valor_enum",
"lista_valores : lista_valores ',' valor_enum",
"valor_enum : constante",
"valor_enum : '-' constante",
"declaracion_clase : CLASS ID herencia_opcional BEGIN cuerpo_clase END ';'",
"herencia_opcional : EXTENDS lista_variables",
"herencia_opcional :",
"cuerpo_clase : cuerpo_clase miembro_clase",
"cuerpo_clase :",
"miembro_clase : visibilidad declaracion",
"miembro_clase : declaracion",
"miembro_clase : FRIEND lista_variables ';'",
"miembro_clase : EXTENDS lista_variables ';'",
"visibilidad : PRIVATE",
"declaracion_funcion : type_specifier FUNCTION ID '(' parametros ')' bloque_declarativo BEGIN lista_sentencias retorno_opcional END ';'",
"parametros : type_specifier ID",
"parametros : parametros ',' type_specifier ID",
"retorno_opcional : RET '(' expresion ')' ';'",
"retorno_opcional :",
"cuerpo_ejecutable : sentencia",
"cuerpo_ejecutable : bloque_compuesto",
"bloque_compuesto : BEGIN lista_sentencias END",
"lista_sentencias : lista_sentencias sentencia",
"lista_sentencias :",
"sentencia : asignacion ';'",
"sentencia : sentencia_while ';'",
"sentencia : sentencia_if ';'",
"sentencia : salida_pout ';'",
"sentencia : error ';'",
"asignacion : ID ASIGNACION expresion",
"asignacion : ID CORCHETE_IZQ expresion CORCHETE_DER ASIGNACION expresion",
"sentencia_while : WHILE '(' condicion ')' REPEAT cuerpo_ejecutable",
"sentencia_if : IF '(' condicion ')' cuerpo_ejecutable rama_else END_IF",
"rama_else : ELSE cuerpo_ejecutable",
"rama_else :",
"salida_pout : POUT '(' CADENA ')'",
"salida_pout : POUT '(' expresion ')'",
"condicion : expresion comparador expresion",
"comparador : COMP_IGUAL",
"comparador : COMP_DIST",
"comparador : COMP_MAYOR_IGUAL",
"comparador : COMP_MENOR_IGUAL",
"comparador : '>'",
"comparador : '<'",
"expresion : expresion '+' expresion",
"expresion : expresion '-' expresion",
"expresion : expresion '*' expresion",
"expresion : expresion '/' expresion",
"expresion : '-' expresion",
"expresion : ID '=' expresion",
"expresion : ID invocacion_ordenada",
"expresion : ID CORCHETE_IZQ expresion CORCHETE_DER",
"expresion : ID",
"expresion : constante",
"invocacion_ordenada : '(' lista_expresiones ')' CORCHETE_IZQ lista_valores CORCHETE_DER",
"invocacion_ordenada : '(' lista_expresiones ')'",
"lista_expresiones : expresion",
"lista_expresiones : lista_expresiones ',' expresion",
"lista_expresiones :",
"constante : CTE_USHORT",
"constante : CTE_DOUBLEF",
};

//#line 240 "gramatica.y"

/* CÓDIGO JAVA DE SOPORTE */

private AnalizadorLexico lexico;
private TablaDeSimbolos ts;
private boolean huboError = false;
private List<String> erroresSintacticos = new ArrayList<>();
private List<String> estructurasDetectadas = new ArrayList<>();

public Parser(AnalizadorLexico lexico, TablaDeSimbolos ts) {
    this.lexico = lexico;
    this.ts = ts;
}

private int yylex() {
    int token = lexico.yylex();
    if (lexico.yylval != null) {
        this.yylval = lexico.yylval;
    } else {
        this.yylval = new ParserVal();
    }
    return token;
}

public void yyerror(String msg) {
    this.huboError = true;
    String err = "Error sintactico en línea " + lexico.getNroLinea() + ": " + msg;
    erroresSintacticos.add(err);
    System.err.println(err);
}

public boolean huboError() {
    return huboError;
}

public List<String> getErroresSintacticos() {
    return erroresSintacticos;
}

public List<String> getEstructurasDetectadas() {
    return estructurasDetectadas;
}

/* Registra una reduccion sintactica reconocida, con la linea actual del lexico. */
private void registrarEstructura(String estructura) {
    estructurasDetectadas.add("Línea " + lexico.getNroLinea() + ": " + estructura);
}

/* TP2 punto c: el lexico desconoce el signo. Al aplicar el '-' el Parser
   vuelve a validar el rango y actualiza la Tabla de Simbolos si corresponde. */
private void aplicarSignoNegativo(ParserVal v) {
    if (v == null || !(v.obj instanceof RegistroTS)) {
        return;
    }
    RegistroTS reg = (RegistroTS) v.obj;
    if (reg.getIdToken() == CTE_USHORT) {
        yyerror("La constante entera sin signo '" + reg.getLexema() + "' no admite valor negativo");
        return;
    }
    if (reg.getIdToken() != CTE_DOUBLEF) {
        return;
    }
    try {
        double valorNegativo = -Double.parseDouble(
                reg.getLexema().replace("d", "e").replace("D", "e"));
        if (!ts.verificarRangoDoublef(valorNegativo)) {
            yyerror("Constante doublef negativa fuera de rango: -" + reg.getLexema());
            return;
        }
        RegistroTS regNeg = ts.registrarConstanteNegativa(
                reg.getLexema(), lexico.getNroLinea(), valorNegativo);
        v.obj = regNeg;
        v.sval = regNeg.getLexema();
    } catch (NumberFormatException e) {
        yyerror("Constante doublef negativa mal formada: -" + reg.getLexema());
    }
}

private void validarConstanteDoublef(ParserVal v) {
    if (v == null || !(v.obj instanceof RegistroTS) || ts == null) {
        return;
    }
    RegistroTS reg = (RegistroTS) v.obj;
    try {
        double valor = Double.parseDouble(
                reg.getLexema().replace("d", "e").replace("D", "e"));
        if (!ts.verificarRangoDoublef(valor)) {
            yyerror("Constante doublef fuera de rango: " + reg.getLexema());
        }
    } catch (NumberFormatException e) {
        yyerror("Constante doublef mal formada: " + reg.getLexema());
    }
}
//#line 481 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 29 "gramatica.y"
{
        registrarEstructura("Programa");
        System.out.println("Programa estructurado correctamente.");
      }
break;
case 2:
//#line 34 "gramatica.y"
{
        registrarEstructura("Programa");
        System.out.println("Programa estructurado correctamente.");
      }
break;
case 5:
//#line 47 "gramatica.y"
{ registrarEstructura("Declaración de variables"); }
break;
case 9:
//#line 51 "gramatica.y"
{ System.out.println("Error sintáctico en declaración: resincronizado en ';'"); }
break;
case 15:
//#line 68 "gramatica.y"
{
       registrarEstructura("Enumeración (typedef)");
       System.out.println("Línea " + lexico.getNroLinea() + ": Enumeración (typedef) declarada");
     }
break;
case 19:
//#line 81 "gramatica.y"
{ aplicarSignoNegativo(val_peek(0)); yyval = val_peek(0); }
break;
case 20:
//#line 87 "gramatica.y"
{
       registrarEstructura("Clase");
       System.out.println("Línea " + lexico.getNroLinea() + ": Declaración de Clase reconocida");
     }
break;
case 21:
//#line 94 "gramatica.y"
{ registrarEstructura("Herencia"); }
break;
case 27:
//#line 107 "gramatica.y"
{ registrarEstructura("Declaración friend"); }
break;
case 28:
//#line 109 "gramatica.y"
{ registrarEstructura("Herencia dentro de clase"); }
break;
case 30:
//#line 118 "gramatica.y"
{ registrarEstructura("Declaración de función"); }
break;
case 33:
//#line 128 "gramatica.y"
{ registrarEstructura("Sentencia RET"); }
break;
case 44:
//#line 151 "gramatica.y"
{ System.out.println("Error sintáctico en sentencia ejecutable: resincronizado en ';'"); }
break;
case 45:
//#line 157 "gramatica.y"
{ registrarEstructura("Asignación"); }
break;
case 46:
//#line 159 "gramatica.y"
{
        System.out.println("Linea " + lexico.getNroLinea() + ": Asignacion a atributo opcional");
        registrarEstructura("Asignación a atributo opcional");
      }
break;
case 47:
//#line 168 "gramatica.y"
{
        System.out.println("Linea " + lexico.getNroLinea() + ": Sentencia WHILE-REPEAT reconocida");
        registrarEstructura("Sentencia WHILE-REPEAT");
      }
break;
case 48:
//#line 176 "gramatica.y"
{ registrarEstructura("Sentencia IF"); }
break;
case 51:
//#line 186 "gramatica.y"
{ registrarEstructura("Sentencia POUT"); }
break;
case 52:
//#line 188 "gramatica.y"
{ registrarEstructura("Sentencia POUT"); }
break;
case 53:
//#line 193 "gramatica.y"
{ registrarEstructura("Condición"); }
break;
case 64:
//#line 212 "gramatica.y"
{ aplicarSignoNegativo(val_peek(0)); yyval = val_peek(0); }
break;
case 67:
//#line 216 "gramatica.y"
{ registrarEstructura("Acceso posicional"); }
break;
case 70:
//#line 223 "gramatica.y"
{ registrarEstructura("Invocación ordenada con índices"); }
break;
case 71:
//#line 225 "gramatica.y"
{ registrarEstructura("Invocación ordenada"); }
break;
case 76:
//#line 236 "gramatica.y"
{ validarConstanteDoublef(val_peek(0)); yyval = val_peek(0); }
break;
//#line 748 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
