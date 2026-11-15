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
public final static short THEN=262;
public final static short ELSE=263;
public final static short END_IF=264;
public final static short BEGIN=265;
public final static short END=266;
public final static short POUT=267;
public final static short WHILE=268;
public final static short REPEAT=269;
public final static short TYPEDEF=270;
public final static short PRIVATE=271;
public final static short FRIEND=272;
public final static short EXTENDS=273;
public final static short CLASS=274;
public final static short FUNCTION=275;
public final static short RET=276;
public final static short USHORTINT=277;
public final static short DOUBLEF=278;
public final static short ASIGNACION=279;
public final static short ASIG_EXPR=280;
public final static short COMP_IGUAL=281;
public final static short COMP_DIST=282;
public final static short CORCHETE_IZQ=283;
public final static short CORCHETE_DER=284;
public final static short COMP_MAYOR_IGUAL=285;
public final static short COMP_MENOR_IGUAL=286;
public final static short UMINUS=287;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    1,    1,    3,    3,    3,    3,    3,    4,
    4,    4,    5,    5,    6,    9,    9,   10,   10,    7,
   12,   12,   13,   13,   14,   14,   14,   14,   15,    8,
   16,   16,   17,   17,    2,    2,   19,   19,   19,   19,
   19,   20,   20,   21,   22,   25,   25,   23,   23,   24,
   26,   26,   26,   26,   26,   26,   18,   18,   18,   18,
   18,   18,   18,   18,   18,   18,   27,   27,   28,   28,
   28,   11,   11,
};
final static short yylen[] = {                            2,
    7,    6,    2,    0,    3,    1,    1,    1,    2,    1,
    1,    1,    1,    3,    7,    1,    3,    1,    2,    7,
    2,    0,    2,    0,    2,    1,    3,    3,    1,   12,
    2,    4,    5,    0,    2,    0,    2,    2,    2,    2,
    2,    3,    6,    7,    7,    2,    0,    4,    4,    3,
    1,    1,    1,    1,    1,    1,    3,    3,    3,    3,
    2,    3,    2,    4,    1,    1,    6,    3,    1,    3,
    0,    1,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    4,    0,    0,    0,   12,   36,    0,    0,
   10,   11,    3,    0,    6,    7,    8,   36,    9,    0,
    0,    0,   13,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   35,    0,    0,    0,    0,    0,    0,    0,
    0,    5,    0,    0,   41,    0,    0,    0,    2,    0,
    0,   37,   38,   39,   40,    0,    0,   24,    0,   14,
    1,    0,   72,   73,    0,   66,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   16,   18,    0,    0,    0,
    0,    0,    0,   63,   61,    0,    0,    0,    0,    0,
   51,   52,   53,   54,   55,   56,    0,   36,   48,   49,
    0,   19,    0,    0,    0,   29,    0,    0,   26,   23,
    0,   31,    0,    4,    0,    0,    0,    0,    0,    0,
   59,   60,    0,    0,    0,   36,   15,   17,   20,    0,
    0,   25,    0,    0,   64,    0,    0,    0,   36,    0,
    0,   27,   28,   32,   36,    0,    0,    0,   45,   44,
    0,    0,    0,    0,   67,    0,    0,    0,   30,    0,
   33,
};
final static short yydgoto[] = {                          2,
    4,   20,   13,   14,   25,   15,   16,   17,   75,   76,
   66,   40,   78,  110,  111,   80,  154,   69,   33,   34,
   35,   36,   37,   70,  140,   97,   84,  118,
};
final static short yysindex[] = {                      -204,
   11,    0,    0, -138, -122,   40,    0,    0, -150, -144,
    0,    0,    0, -184,    0,    0,    0,    0,    0,  -57,
   54, -152,    0, -132,   30,  -54,   70, -167,   90,   72,
   97,   98,    0,  108,  110,  115,  117, -118,  -79,  -81,
  148,    0,  -67,  135,    0,  -33,  -33,  -33,    0,  -40,
  -33,    0,    0,    0,    0,  -30,  151,    0, -206,    0,
    0,   -6,    0,    0,  -33,    0,   99,   14,    7,  155,
  160,   81,  174, -177,  -28,    0,    0, -169,  -36,   52,
  -33,  -33,  -33,    0,    0,  -33,  -33,  -33,  -33,  -63,
    0,    0,    0,    0,    0,    0,  -33,    0,    0,    0,
  -47,    0,  168,  -30,  176,    0,  -79,  -79,    0,    0,
  -85,    0, -206,    0,   21,   99,   99,  106,   53,   53,
    0,    0,  -33,   99,   17,    0,    0,    0,    0,   31,
   33,    0,  -24,  -97,    0,  -33,  -60,   99,    0,  -26,
   34,    0,    0,    0,    0,   99,  -30,   47,    0,    0,
  -70,   -8,  197,  -20,    0,  -33,  180,  189,    0,  183,
    0,
};
final static short yyrindex[] = {                         0,
  -95,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  -16,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -12,    0,    0,    0,
    0,  -41,    0,    0,    0,    0,  203,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  113,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  -27,  122,    0,  -21,  -14,
    0,    0,    0,  225,    5,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -34,  216,    0,    0,
    0,    0,    0,    0,    0,  164,    0,   15,    0,    0,
   16,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,
};
final static short yygindex[] = {                         0,
   37,   19,  -13,    1,    3,    0,    0,    0,  134,  179,
    2,    0,    0,    0,    0,    0,    0,   -3,    0,    0,
    0,    0,    0,  235,    0,    0,    0,    0,
};
final static int YYTABLESIZE=315;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         65,
   65,   65,   65,   65,   65,   65,   68,   68,   68,   68,
   68,   65,   68,   62,   74,  104,   62,   65,   65,   57,
   65,   57,   57,   57,   68,   68,   58,   68,   58,   58,
   58,   62,   62,   83,   62,  104,   26,   57,   57,    5,
   57,   57,   67,   68,   58,   58,   72,   58,   88,   86,
    7,   87,    1,   89,   82,   88,   86,   77,   87,   79,
   89,   85,   88,   86,  109,   87,   96,   89,   95,    3,
   11,   12,   23,   43,   43,  102,   43,  115,  116,  117,
   63,   64,  119,  120,  121,  122,    6,    7,   42,  142,
   24,  143,  114,  124,   88,  113,  105,  132,   19,   89,
    9,  106,  107,  108,   10,   77,   21,   11,   12,  130,
  131,   46,   22,  133,   38,   47,  125,    6,    7,  138,
   39,  100,   88,   86,   41,   87,    8,   89,   45,   48,
   49,    9,  146,    6,    7,   10,   50,   51,   11,   12,
   88,   86,   18,   87,  141,   89,  137,    9,   77,  136,
  134,   10,  158,   71,   11,   12,   71,  148,    6,    7,
    4,    4,   69,  151,   56,   69,   52,  145,   53,    4,
    6,    7,    9,   54,    4,   55,   10,   23,    4,   11,
   12,    4,    4,   58,    9,   27,   28,   59,   10,   60,
   29,   11,   12,   61,   43,   98,   31,   32,   27,   28,
   99,   27,   28,   29,   70,  153,   29,   70,   30,   31,
   32,   44,   31,   32,  101,  123,   62,   63,   64,   71,
  112,  126,  147,   62,   63,   64,  127,   63,   64,  160,
   88,   86,  144,   87,  129,   89,  156,  149,  159,   65,
   65,  161,   65,   65,   65,  157,   68,   68,   22,   68,
   68,   68,   21,   62,   62,  103,   62,   62,   62,   57,
   57,   42,   57,   57,   57,   50,   58,   58,   47,   58,
   58,   58,   27,   28,   43,  155,   81,   29,   46,  139,
  152,   34,  128,   31,   32,   73,    0,   91,   92,   27,
   28,   93,   94,    0,   29,    0,    0,   90,    0,  150,
   31,   32,   27,   28,  135,    0,    0,   29,    0,    0,
    0,    0,    0,   31,   32,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   44,   45,   45,   47,   41,   42,   43,   44,
   45,   45,   47,   41,   45,   44,   44,   59,   60,   41,
   62,   43,   44,   45,   59,   60,   41,   62,   43,   44,
   45,   59,   60,   40,   62,   44,   18,   59,   60,    3,
   62,   39,   46,   47,   59,   60,   50,   62,   42,   43,
  257,   45,  257,   47,   61,   42,   43,   56,   45,   59,
   47,   65,   42,   43,   78,   45,   60,   47,   62,   59,
  277,  278,  257,   44,   44,   74,   44,   81,   82,   83,
  258,  259,   86,   87,   88,   89,  256,  257,   59,   59,
  275,   59,   41,   97,   42,   44,  266,  111,   59,   47,
  270,  271,  272,  273,  274,  104,  257,  277,  278,  107,
  108,  279,  257,  113,   61,  283,   98,  256,  257,  123,
  273,   41,   42,   43,  257,   45,  265,   47,   59,   40,
   59,  270,  136,  256,  257,  274,   40,   40,  277,  278,
   42,   43,  265,   45,  126,   47,   41,  270,  147,   44,
  114,  274,  156,   41,  277,  278,   44,  139,  256,  257,
  256,  257,   41,  145,  283,   44,   59,  265,   59,  265,
  256,  257,  270,   59,  270,   59,  274,  257,  274,  277,
  278,  277,  278,  265,  270,  256,  257,   40,  274,  257,
  261,  277,  278,   59,   44,   41,  267,  268,  256,  257,
   41,  256,  257,  261,   41,  276,  261,   44,  266,  267,
  268,  266,  267,  268,   41,  279,  257,  258,  259,  260,
  257,  269,  283,  257,  258,  259,   59,  258,  259,   41,
   42,   43,  257,   45,   59,   47,   40,  264,   59,  281,
  282,   59,  284,  285,  286,  266,  281,  282,  265,  284,
  285,  286,  265,  281,  282,  284,  284,  285,  286,  281,
  282,   59,  284,  285,  286,   41,  281,  282,  264,  284,
  285,  286,  256,  257,   59,  284,  283,  261,  264,  263,
  147,  266,  104,  267,  268,   51,   -1,  281,  282,  256,
  257,  285,  286,   -1,  261,   -1,   -1,  284,   -1,  266,
  267,  268,  256,  257,  284,   -1,   -1,  261,   -1,   -1,
   -1,   -1,   -1,  267,  268,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=287;
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
"THEN","ELSE","END_IF","BEGIN","END","POUT","WHILE","REPEAT","TYPEDEF",
"PRIVATE","FRIEND","EXTENDS","CLASS","FUNCTION","RET","USHORTINT","DOUBLEF",
"ASIGNACION","ASIG_EXPR","COMP_IGUAL","COMP_DIST","CORCHETE_IZQ","CORCHETE_DER",
"COMP_MAYOR_IGUAL","COMP_MENOR_IGUAL","UMINUS",
};
final static String yyrule[] = {
"$accept : programa",
"programa : ID ';' bloque_declarativo BEGIN bloque_ejecutable END ';'",
"programa : ID bloque_declarativo BEGIN bloque_ejecutable END ';'",
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
"declaracion_funcion : type_specifier FUNCTION ID '(' parametros ')' bloque_declarativo BEGIN bloque_ejecutable retorno_opcional END ';'",
"parametros : type_specifier ID",
"parametros : parametros ',' type_specifier ID",
"retorno_opcional : RET '(' expresion ')' ';'",
"retorno_opcional :",
"bloque_ejecutable : bloque_ejecutable sentencia",
"bloque_ejecutable :",
"sentencia : asignacion ';'",
"sentencia : sentencia_while ';'",
"sentencia : sentencia_if ';'",
"sentencia : salida_pout ';'",
"sentencia : error ';'",
"asignacion : ID ASIGNACION expresion",
"asignacion : ID CORCHETE_IZQ expresion CORCHETE_DER ASIGNACION expresion",
"sentencia_while : WHILE '(' condicion ')' REPEAT bloque_ejecutable END",
"sentencia_if : IF '(' condicion ')' bloque_ejecutable rama_else END_IF",
"rama_else : ELSE bloque_ejecutable",
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

//#line 231 "gramatica.y"

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
case 41:
//#line 142 "gramatica.y"
{ System.out.println("Error sintáctico en sentencia ejecutable: resincronizado en ';'"); }
break;
case 42:
//#line 148 "gramatica.y"
{ registrarEstructura("Asignación"); }
break;
case 43:
//#line 150 "gramatica.y"
{
        System.out.println("Linea " + lexico.getNroLinea() + ": Asignacion a atributo opcional");
        registrarEstructura("Asignación a atributo opcional");
      }
break;
case 44:
//#line 159 "gramatica.y"
{
        System.out.println("Linea " + lexico.getNroLinea() + ": Sentencia WHILE-REPEAT reconocida");
        registrarEstructura("Sentencia WHILE-REPEAT");
      }
break;
case 45:
//#line 167 "gramatica.y"
{ registrarEstructura("Sentencia IF"); }
break;
case 48:
//#line 177 "gramatica.y"
{ registrarEstructura("Sentencia POUT"); }
break;
case 49:
//#line 179 "gramatica.y"
{ registrarEstructura("Sentencia POUT"); }
break;
case 50:
//#line 184 "gramatica.y"
{ registrarEstructura("Condición"); }
break;
case 61:
//#line 203 "gramatica.y"
{ aplicarSignoNegativo(val_peek(0)); yyval = val_peek(0); }
break;
case 64:
//#line 207 "gramatica.y"
{ registrarEstructura("Acceso posicional"); }
break;
case 67:
//#line 214 "gramatica.y"
{ registrarEstructura("Invocación ordenada con índices"); }
break;
case 68:
//#line 216 "gramatica.y"
{ registrarEstructura("Invocación ordenada"); }
break;
case 73:
//#line 227 "gramatica.y"
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
