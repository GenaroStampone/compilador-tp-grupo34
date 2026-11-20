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
public final static short COMP_IGUAL=279;
public final static short COMP_DIST=280;
public final static short CORCHETE_IZQ=281;
public final static short CORCHETE_DER=282;
public final static short COMP_MAYOR_IGUAL=283;
public final static short COMP_MENOR_IGUAL=284;
public final static short UMINUS=285;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    1,    1,    3,    3,    3,    3,    3,    8,
    8,    8,    9,    9,   10,   10,   11,   11,    4,    5,
    7,   13,   14,   16,   16,    6,   18,   20,   19,   19,
   21,   21,   22,   22,   22,   22,   23,   23,   24,   15,
   15,   16,   16,   25,   25,   27,   27,   27,    2,    2,
   26,   26,   26,   28,   28,   30,   31,   33,   33,   29,
   29,   32,   34,   34,   34,   34,   34,   34,   17,   17,
   17,   17,   17,   17,   17,   17,   17,   17,   17,   35,
   35,   36,   36,   12,   12,
};
final static short yylen[] = {                            2,
    7,    6,    2,    0,    1,    1,    1,    1,    2,    1,
    1,    1,    1,    3,    1,    3,    1,    2,    3,    7,
    2,    6,    6,    5,    0,    3,    2,    4,    2,    0,
    2,    0,    2,    1,    1,    3,    1,    0,    3,    2,
    4,    5,    0,    1,    4,    1,    2,    2,    2,    0,
    1,    1,    1,    4,    7,    6,    8,    2,    0,    4,
    4,    3,    1,    1,    1,    1,    1,    1,    3,    3,
    3,    3,    2,    3,    6,    2,    4,    1,    1,    6,
    3,    1,    3,    1,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    4,    0,    0,    0,   12,   50,    0,    0,
   10,   11,    3,    5,    6,    7,    8,    0,    4,    0,
   50,    9,    0,    0,   27,   13,    0,    0,    0,   21,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   49,
   51,   46,    0,   52,   53,    0,    0,   19,    0,   50,
    0,   32,   26,    0,   48,    0,    0,    0,    2,    0,
    0,   47,    0,    0,   14,    0,    0,    1,    0,   84,
   85,    0,   79,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   15,   17,    0,    0,    0,    0,    0,   37,
    0,    0,   34,   31,    0,   35,    0,    0,    0,    0,
   76,   73,    0,    0,    0,    0,   54,    0,   63,   64,
   65,   66,   67,   68,    0,    0,   60,   61,    0,   18,
    0,    0,   40,    0,   22,    0,    0,   28,    0,    0,
   33,    0,    0,    0,    0,    0,    0,    0,   71,   72,
    0,    0,   50,    0,   44,    0,   20,   16,    0,    0,
   23,   39,   36,   77,    0,    0,    0,    0,    0,    0,
    0,   56,   41,    0,    0,    0,    0,   55,    0,   58,
    0,   24,    0,   75,   45,   57,   80,
};
final static short yydgoto[] = {                          2,
    4,   23,   13,   14,   15,   16,   17,   18,   28,   82,
   83,   73,   19,   30,   86,   88,   76,   20,   32,   53,
   67,   94,   95,   96,  144,   40,   41,   42,   43,   44,
   45,   77,  161,  115,  101,  135,
};
final static short yysindex[] = {                      -179,
   38,    0,    0, -164, -139,   40,    0,    0, -156, -151,
    0,    0,    0,    0,    0,    0,    0, -192,    0, -162,
    0,    0,  -60,   65,    0,    0, -126,   52, -128,    0,
 -124, -129,   15,   83, -154,  104,   87,  115,  117,    0,
    0,    0,  103,    0,    0, -112,  137,    0,  -75,    0,
  140,    0,    0,  130,    0,  -33,  -33,  -33,    0,  -40,
  -33,    0,  -30, -169,    0,  -81, -186,    0,   -6,    0,
    0,  -33,    0,  136,   14,    7,  150,  152,  167,  157,
 -108,  -28,    0,    0,  -46,  123,  175,  -44,  163,    0,
  -24, -124,    0,    0, -117,    0,  -33,  -33,  -33,  -17,
    0,    0,  -33,  -33,  -33,  -33,    0,  -55,    0,    0,
    0,    0,    0,    0,  -33,   28,    0,    0,  -31,    0,
  168,  -30,    0, -169,    0,  -33,  176,    0,  185,   60,
    0,   21,  267,  267,  127,  207,   11,   11,    0,    0,
  -33,  267,    0,  -11,    0,   28,    0,    0,    3,  189,
    0,    0,    0,    0,  -33,   -4, -124,  145,   41,   28,
    1,    0,    0,  208,  267,  -30,  158,    0,  214,    0,
  219,    0,   -8,    0,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
 -103,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   19,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   24,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   34,    0,    0,  -41,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  -27,  159,    0,    0,  -21,  -14,    0,    0,
    0,  238,    0,   30,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  -34,    0,    0,    0,    0,
    0,    0,    0,    0,  172,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   57,   22,  -20,    0,    0,    0,    0,  -22,    6,  134,
  179,  -19,    0,    0,    0,    0,   17,    0,    0,    0,
    0,    0,    0,    0,  -66,  -65,    0,    0,    0,    0,
    0,  243,    0,    0,    0,    0,
};
final static int YYTABLESIZE=314;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         78,
   78,   78,   78,   78,   72,   78,   81,   81,   81,   81,
   81,   72,   81,   74,   81,  122,   74,   78,   78,   69,
   78,   69,   69,   69,   81,   81,   70,   81,   70,   70,
   70,   74,   74,   99,   74,  122,   51,   69,   69,  100,
   69,   85,   33,   84,   70,   70,   93,   70,  105,  103,
  145,  104,  105,  106,   98,  105,  103,  106,  104,    5,
  106,  120,  105,  103,   26,  104,  114,  106,  113,    6,
    7,   66,   74,   75,  131,   29,   79,    1,   89,  162,
  145,   27,    9,   90,   91,   92,   10,    7,  102,   11,
   12,    6,    7,  170,  145,   49,    3,  130,   22,    8,
   24,  149,   84,   49,    9,   25,   11,   12,   10,   31,
   48,   11,   12,  132,  133,  134,    6,    7,  153,  137,
  138,  139,  140,   56,   21,   46,   57,    6,    7,    9,
   47,  142,   26,   10,   52,   50,   11,   12,    6,    7,
    9,   55,  150,   58,   10,   59,   84,   11,   12,   70,
   71,    9,    4,    4,   60,   10,   61,  158,   11,   12,
    4,   62,  167,  125,  159,    4,  124,  156,   63,    4,
  155,  165,    4,    4,   34,   35,   64,  105,  103,   36,
  104,   65,  106,   49,   38,   39,  105,  103,   68,  104,
  116,  106,  117,   87,  107,   34,   35,  119,  174,   82,
   36,   49,   82,  168,   37,   38,   39,  118,  105,  103,
  123,  104,   83,  106,  126,   83,   69,   70,   71,   78,
  127,  128,  141,   69,   70,   71,  147,   70,   71,  164,
  105,  103,  129,  104,  151,  106,  146,   78,   78,  136,
   78,   78,   78,  152,   81,   81,  157,   81,   81,   81,
  160,   74,   74,  121,   74,   74,   74,   69,   69,  163,
   69,   69,   69,  171,   70,   70,  172,   70,   70,   70,
   34,   35,  175,  177,   97,   36,  166,  176,   62,   54,
   38,   39,   30,   34,   35,  109,  110,   29,   36,  111,
  112,  143,   59,   38,   39,  108,   34,   35,   25,  173,
  148,   36,  154,   80,    0,  169,   38,   39,  105,  103,
    0,  104,    0,  106,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   44,   45,   45,   47,   41,   42,   43,   44,
   45,   45,   47,   41,   45,   44,   44,   59,   60,   41,
   62,   43,   44,   45,   59,   60,   41,   62,   43,   44,
   45,   59,   60,   40,   62,   44,   31,   59,   60,   46,
   62,   64,   21,   63,   59,   60,   67,   62,   42,   43,
  116,   45,   42,   47,   61,   42,   43,   47,   45,    3,
   47,   81,   42,   43,  257,   45,   60,   47,   62,  256,
  257,   50,   56,   57,   95,   19,   60,  257,  265,  146,
  146,  274,  269,  270,  271,  272,  273,  257,   72,  276,
  277,  256,  257,  160,  160,   44,   59,   92,   59,  264,
  257,  124,  122,   44,  269,  257,  276,  277,  273,  272,
   59,  276,  277,   97,   98,   99,  256,  257,   59,  103,
  104,  105,  106,  278,  264,   61,  281,  256,  257,  269,
  257,  115,  257,  273,  264,  264,  276,  277,  256,  257,
  269,   59,  126,   40,  273,   59,  166,  276,  277,  258,
  259,  269,  256,  257,   40,  273,   40,  141,  276,  277,
  264,   59,  157,   41,  143,  269,   44,   41,  281,  273,
   44,  155,  276,  277,  256,  257,   40,   42,   43,  261,
   45,  257,   47,   44,  266,  267,   42,   43,   59,   45,
   41,   47,   41,  275,   59,  256,  257,   41,   41,   41,
  261,   44,   44,   59,  265,  266,  267,   41,   42,   43,
  257,   45,   41,   47,   40,   44,  257,  258,  259,  260,
  265,   59,  278,  257,  258,  259,   59,  258,  259,   41,
   42,   43,  257,   45,   59,   47,  268,  279,  280,  257,
  282,  283,  284,   59,  279,  280,   40,  282,  283,  284,
  262,  279,  280,  282,  282,  283,  284,  279,  280,  257,
  282,  283,  284,  263,  279,  280,   59,  282,  283,  284,
  256,  257,   59,  282,  281,  261,  281,   59,   41,  265,
  266,  267,  264,  256,  257,  279,  280,  264,  261,  283,
  284,  264,  263,  266,  267,  282,  256,  257,  265,  166,
  122,  261,  282,   61,   -1,  265,  266,  267,   42,   43,
   -1,   45,   -1,   47,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=285;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
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
"COMP_IGUAL","COMP_DIST","CORCHETE_IZQ","CORCHETE_DER","COMP_MAYOR_IGUAL",
"COMP_MENOR_IGUAL","UMINUS",
};
final static String yyrule[] = {
"$accept : programa",
"programa : ID ';' bloque_declarativo BEGIN lista_sentencias END ';'",
"programa : ID bloque_declarativo BEGIN lista_sentencias END ';'",
"bloque_declarativo : bloque_declarativo declaracion",
"bloque_declarativo :",
"declaracion : declaracion_variables",
"declaracion : declaracion_typedef",
"declaracion : declaracion_clase",
"declaracion : declaracion_funcion",
"declaracion : error ';'",
"type_specifier : USHORTINT",
"type_specifier : DOUBLEF",
"type_specifier : ID",
"lista_variables : ID",
"lista_variables : lista_variables ',' ID",
"lista_valores : valor_enum",
"lista_valores : lista_valores ',' valor_enum",
"valor_enum : constante",
"valor_enum : '-' constante",
"declaracion_variables : type_specifier lista_variables ';'",
"declaracion_typedef : TYPEDEF ID '=' CORCHETE_IZQ lista_valores CORCHETE_DER ';'",
"declaracion_funcion : encabezado_funcion cuerpo_funcion",
"encabezado_funcion : type_specifier FUNCTION ID '(' lista_parametros_formales ')'",
"cuerpo_funcion : bloque_declarativo BEGIN lista_sentencias retorno_opcional END ';'",
"retorno_opcional : RET '(' expresion ')' ';'",
"retorno_opcional :",
"declaracion_clase : encabezado_clase herencia_opcional cuerpo_clase",
"encabezado_clase : CLASS ID",
"cuerpo_clase : BEGIN cuerpo_clase_definicion END ';'",
"herencia_opcional : EXTENDS lista_variables",
"herencia_opcional :",
"cuerpo_clase_definicion : cuerpo_clase_definicion miembro_clase",
"cuerpo_clase_definicion :",
"miembro_clase : visibilidad_opcional declaracion",
"miembro_clase : declaracion",
"miembro_clase : declaracion_friend",
"miembro_clase : EXTENDS lista_variables ';'",
"visibilidad_opcional : PRIVATE",
"visibilidad_opcional :",
"declaracion_friend : FRIEND ID ';'",
"lista_parametros_formales : type_specifier ID",
"lista_parametros_formales : lista_parametros_formales ',' type_specifier ID",
"retorno_opcional : RET '(' expresion ')' ';'",
"retorno_opcional :",
"bloque_compuesto : sentencia",
"bloque_compuesto : BEGIN lista_sentencias END ';'",
"sentencia_simple : asignacion",
"sentencia_simple : salida_pout ';'",
"sentencia_simple : error ';'",
"lista_sentencias : lista_sentencias sentencia",
"lista_sentencias :",
"sentencia : sentencia_simple",
"sentencia : sentencia_while",
"sentencia : sentencia_if",
"asignacion : ID ASIGNACION expresion ';'",
"asignacion : ID CORCHETE_IZQ expresion CORCHETE_DER ASIGNACION expresion ';'",
"sentencia_while : WHILE '(' condicion ')' REPEAT bloque_compuesto",
"sentencia_if : IF '(' condicion ')' bloque_compuesto rama_else END_IF ';'",
"rama_else : ELSE bloque_compuesto",
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
"expresion : ID '.' ID '(' lista_variables ')'",
"expresion : ID invocacion_ordenada",
"expresion : ID CORCHETE_IZQ expresion CORCHETE_DER",
"expresion : ID",
"expresion : constante",
"invocacion_ordenada : '(' lista_expresiones ')' CORCHETE_IZQ lista_valores CORCHETE_DER",
"invocacion_ordenada : '(' lista_expresiones ')'",
"lista_expresiones : expresion",
"lista_expresiones : lista_expresiones ',' expresion",
"constante : CTE_USHORT",
"constante : CTE_DOUBLEF",
};

//#line 704 "gramatica.y"

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
    String lexema = ""; 
    if (yylval != null && yylval.obj instanceof RegistroTS) { 
        RegistroTS reg = (RegistroTS) yylval.obj; 
        lexema = reg.getLexema(); 
    }
    String tokenNombre; 
    if (yychar >= 0 && yychar < yyname.length && yyname[yychar] != null) { 
        tokenNombre = yyname[yychar]; 
    } else if (!lexema.isEmpty()) { 
        tokenNombre = lexema; 
    } else { 
        tokenNombre = "TOKEN_INESPERADO"; 
    } 
    String err = "Error sintáctico/semántico en línea " + lexico.getNroLinea() + " (token: " + tokenNombre + "): " + msg; 
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

// Registra una sola variable aplicando Name Mangling y control de redeclaración 

public void declararVariable(String lexemaOriginal, String tipoDato) { 
    // 1. Armar clave con Name Mangling según el ámbito actual (ej. "a:main" o "a:main:consultar_clave") 
    String nameMangled = lexemaOriginal + ":" + controlAmbitos.getAmbitoActual(); 
    // 2. Control de Redeclaración en el MISMO ámbito 
    if (ts.existeSimbolo(claveMangled)) { 
        yyerror("Error Semántico: Variable redeclarada '" + lexemaOriginal + "' en el ámbito " + controlAmbitos.getAmbitoActual()); 
    } else { 
        // 3. Registrar tipo y uso en la Tabla de Símbolos 
        ts.registrarVariable(claveMangled, tipoDato, "variable"); 
    } 
} 

// Procesa la lista completa de identificadores que provienen de la regla lista_ids 

public void declararListaVariables(ArrayList<RegistroTS> listaVars, String tipoDato) {
    if (listaVars == null || tipoDato == null) {
        return;
    }
    for (RegistroTS reg : listaVars) {
        declararVariable(reg.getLexema(), tipoDato);
    }
}

public RegistroTS buscarVariableVisibilidad(String lexemaOriginal) { 
    // Obtener la jerarquía de ámbitos visibles: ["x:main:aa:aaa", "x:main:aa", "x:main"] 
    List<String> mangledCandidatas = controlAmbitos.getJerarquiaBusqueda(lexemaOriginal);

    // Buscar en la Tabla de Símbolos de adentro hacia afuera
    for (String clave : mangledCandidatas) {
        RegistroTS simbolo = ts.buscar(clave)
        if (ts.existeSimbolo(clave)) {
            return simbolo; // Encontró la declaración más cercana y visible
        }
    }

    // Si estamos dentro de una clase y no se encontró localmente, buscar en la Herencia 
    String ambitoActual = controlAmbitos.getAmbitoActual(); // ej "main:ca:metodo1" 
    String[] partesScope = ambitoActual.split(":");
    if (partesScope.length >= 2){ 
        String nombreClase = partesScope[1]; // Extrae "ca" 
        RegistroTS simboloHeredado = ts.buscarEnHerencia(nombreClase, lexemaOriginal); 
        if (simboloHeredado != null) { 
            return simboloHeredado; // Encontró la variable en una clase padre (cb, cc, etc.) 
        } 
    }

    // Si no existe en ningún ámbito visible, emitir error semántico
    yyerror("Error Semántico en línea " + lexico.getNroLinea() + 
            ": La variable '" + lexemaOriginal + "' no está declarada o no es visible en este ámbito");
    return null;
}
//#line 565 "Parser.java"
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
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) val_peek(6).obj);
        NodoArbol cuerpoProg = (NodoArbol) val_peek(2).obj;
        NodoArbol nodoProg = GeneradorCodigoIntermedio.crearNodoAST("PROGRAMA", hojaID, cuerpoProg);
        yyval = new ParserVal(nodoProg);
      }
break;
case 2:
//#line 36 "gramatica.y"
{
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) val_peek(5).obj);
        NodoArbol cuerpoProg = (NodoArbol) val_peek(2).obj;
        NodoArbol nodoProg = GeneradorCodigoIntermedio.crearNodoAST("PROGRAMA", hojaID, cuerpoProg);
        yyval = new ParserVal(nodoProg);
      }
break;
case 9:
//#line 54 "gramatica.y"
{ System.out.println("Error sintáctico en declaración: resincronizado en ';'"); }
break;
case 13:
//#line 67 "gramatica.y"
{
            ArrayList<RegistroTS> lista = new ArrayList<>();
            lista.add((RegistroTS) val_peek(0).obj);
            yyval = new ParserVal(lista)
        }
break;
case 14:
//#line 73 "gramatica.y"
{
            ArrayList<RegistroTS> lista = (ArrayList<RegistroTS>) val_peek(2).obj
            lista.add((RegistroTS) val_peek(0).obj);
            yyval = val_peek(2);
        }
break;
case 15:
//#line 84 "gramatica.y"
{
            yyval = val_peek(0);
        }
break;
case 16:
//#line 88 "gramatica.y"
{
            NodoArbol izq = (NodoArbol) val_peek(2).obj;
            NodoArbol der = (NodoArbol) val_peek(0).obj;
            NodoArbol list = GeneradorCodigoIntermedio.crearNodoAST(",", izq, der);
            yyval = new ParserVal(list);
        }
break;
case 18:
//#line 98 "gramatica.y"
{ aplicarSignoNegativo(val_peek(0)); yyval = val_peek(0); }
break;
case 19:
//#line 103 "gramatica.y"
{
        String tipoDato = ((RegistroTS) val_peek(2).obj).getLexema();
        ArrayList<RegistroTS> listaVars = (ArrayList<RegistroTS>) val_peek(1).obj;
        String ambitoActual = controlAmbitos.getAmbitoActual();
        if (listaVars != null) {
          for (RegistroTS reg : listaVars) {
            String lexemaOriginal = reg.getLexema();
            /* Name Mangling según el ámbito actual (ej. "a:main" o "a:main:consultar_clave")*/
            String nameMangled = lexemaOriginal + ":" + controlAmbitos.getAmbitoActual();
            /* Control de Redeclaración en el MISMO ámbito*/
            if (ts.buscar(nameMangled)) {
                yyerror("Error Semántico: Variable redeclarada '" + lexemaOriginal + "' en el ámbito " + controlAmbitos.getAmbitoActual());
            } else {
                /* Registrar tipo y uso en la Tabla de Símbolos*/
                RegistroTS regNuevo = ts.registrarVariable(nameMangled, tipoDato, "variable");
            }
          }
        }
      
     }
break;
case 20:
//#line 127 "gramatica.y"
{
        /* $1 = TYPEDEF, $2 = ID del nuevo tipo*/
        RegistroTS reg = (RegistroTS) val_peek(5).obj;
        String nombreTipo = reg.getLexema();

        /* 1. Name Mangling del nombre del tipo (ej. "codigos_control:main")*/
        String nameMangled = nombreTipo + ":" + controlAmbitos.getAmbitoActual();

        /* 2. Control de Redeclaración en el MISMO ámbito*/
        if (ts.existeSimbolo(nameMangled)) {
            yyerror("Error Semántico: El tipo de dato '" + nombreTipo + "' ya fue definido en el ámbito " + controlAmbitos.getAmbitoActual());
        } else {
            /* 3. Registrar en la TS con Uso = "nombre_tipo" y asociar sus valores*/
            ArrayList<RegistroTS> valores = (ArrayList<RegistroTS>) val_peek(2).obj;
            ts.registrarTipoPersonalizado(nameMangled, "nombre_tipo", valores);
        }
     }
break;
case 21:
//#line 148 "gramatica.y"
{
        controlAmbitos.cerrarAmbito(); 
        /* Armamos el nodo del AST de la función conectando la hoja del encabezado con el subárbol del cuerpo */
        NodoArbol hojaFunc = (NodoArbol) val_peek(1).obj; 
        NodoArbol subArbolCuerpo = (NodoArbol) val_peek(0).obj; 
        NodoArbol nodoFuncion = GeneradorCodigoIntermedio.crearNodoAST("FUNC", hojaFunc, subArbolCuerpo); 
        yyval = new ParserVal(nodoFuncion); 
      }
break;
case 22:
//#line 160 "gramatica.y"
{ 
        String lexemaFunc = ((RegistroTS) val_peek(3).obj).getLexema();
        String ambitoPadre = controlAmbitos.getAmbitoActual();
        String nameMangled = lexemaFunc + ":" + ambitoPadre;
        List<String> tiposParametros = (List<String>) val_peek(1).obj;
        if (ts.buscar(nameMangled) != null) { 
          /* Error semántico: Redeclaración en el mismo ámbito */
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": La función o símbolo '" + lexemaFunc + "' ya fue declarado/a en el ámbito '" + ambitoPadre + "'"); 
          controlAmbitos.abrirAmbito(lexemaFunc); 
          yyval = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) val_peek(3).obj)); 
        } else {
          /* Registrar la función en la Tabla de Símbolos (en el ámbito padre)*/
          RegistroTS regID = (RegistroTS) val_peek(3).obj;
          String tipoFunc = ((RegistroTS) val_peek(5).obj).getLexema();
          RegistroTS regFuncion = ts.registrarFuncion(nameMangled, tipoFunc, "nombre_funcion", tiposParametros);
          controlAmbitos.abrirAmbito(lexemaFunc);
          NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST(regFuncion);
          yyval = new ParserVal(hojaID);
        }
      }
break;
case 23:
//#line 184 "gramatica.y"
{ 
        NodoArbol cuerpoFunc = (NodoArbol) val_peek(3).obj; 
        NodoArbol retFunc = (NodoArbol) val_peek(2).obj;
        String ambito = controlAmbitos.getAmbitoActual();
        int idx = ambito.lastIndexOf(':');
        String claveFunc = ambito.substring(idx + 1) + ":" + ambito.substring(0, idx);
        RegistroTS regFunc = ts.buscar(claveFunc);
        String tipoFunc = (regFunc != null) ? regFunc.getTipoToken() : null;
        if (retFunc == null) {
            RegistroTS regDefecto = ts.obtenerOCrearConstantePorDefecto(tipoFunc);
            NodoArbol hojaDefecto = GeneradorCodigoIntermedio.crearHojaAST(regDefecto);
            retFunc = GeneradorCodigoIntermedio.crearNodoAST("RETURN", hojaDefecto, null);
        } else {
          String tipoRetorno = retFunc.getTipo();
          if (tipoFunc != null && tipoRetorno != null && !tipoFunc.equalsIgnoreCase(tipoRetorno)) { /
            / Si los tipos difieren, aplicar conversión implícita o emitir advertencia/error semántico 
            retFunc = GeneradorCodigoIntermedio.verificarYConvertirTipos("RETURN", GeneradorCodigoIntermedio.crearHojaAST(regFunc), retFunc, lexico.getNroLinea()); 
          }
        }
        NodoArbol cuerpoCompleto = GeneradorCodigoIntermedio.crearNodoAST(";", cuerpoFunc, retFunc);
        yyval = new ParserVal(cuerpoCompleto);
        controlAmbitos.cerrarAmbito();
      }
break;
case 24:
//#line 211 "gramatica.y"
{
        NodoArbol expr = (NodoArbol) val_peek(2).obj;
        NodoArbol nodoRet = GeneradorCodigoIntermedio.crearNodoAST("RET", expr, null);
        yyval = new ParserVal(nodoRet);
      }
break;
case 25:
//#line 217 "gramatica.y"
{
        yyval = new ParserVal((Object) null);
      }
break;
case 26:
//#line 225 "gramatica.y"
{
       registrarEstructura("Clase");
       System.out.println("Línea " + lexico.getNroLinea() + ": Declaración de Clase reconocida");
       RegistroTS regClaseActual = (RegistroTS) val_peek(2).obj;
       ArrayList<RegistroTS> listaPadres = (ArrayList<RegistroTS>) val_peek(1).obj;
       if (regClaseActual != null && listaPadres != null){
            for (RegistroTS regPadre : listaPadres) {
                String nombrePadre = regPadre.getLexema();
                /* 1. Validar que la clase padre exista en la Tabla de Símbolos*/
                RegistroTS clasePadreEncontrada = buscarVariableVisibilidad(nombrePadre);
                if (clasePadreEncontrada == null || !"nombre_clase".equals(clasePadreEncontrada.getUso())) {
                    yyerror("Error Semántico: La clase base '" + nombrePadre + "' no está declarada o no es visible");
                } else if (ts.esHerenciaCircular(clasePadreEncontrada.getLexema(), regClaseActual.getLexema())){
                    yyerror("Error Semántico: Herencia circular detectada. La clase '" + nombrePadre + "' genera un ciclo de herencia con la clase actual");
                } else {
                    /* Cargar el nombre de la clase padre en el RegistroTS de la clase actual*/
                    regClaseActual.agregarClaseHeredada(clasePadreEncontrada.getLexema());
                }
            }   
       }
       yyval = new ParserVal(null);
     }
break;
case 27:
//#line 251 "gramatica.y"
{  
        String nombreClase = ((RegistroTS) val_peek(0).obj).getLexema();
        String ambitoPadre = controlAmbitos.getAmbitoActual();
        String nameMangled = nombreClase + ":" + ambitoPadre;
        RegistroTS regClase = null; 
        if (ts.buscar(nameMangled) != null) { 
            yyerror("Error Semántico: La clase '" + nombreClase + "' ya fue declarada en el ámbito " + controlAmbitos.getAmbitoActual());
            controlAmbitos.abrirAmbito(nombreClase); 
            regClase = (RegistroTS) val_peek(0).obj;
        } else { 
            regClase = ts.registrarClase(nameMangled, "nombre_clase");
            controlAmbitos.abrirAmbito(nombreClase); 
        }
        yyval = new ParserVal(regClase);
     }
break;
case 28:
//#line 270 "gramatica.y"
{ 
        /* Al terminar el bloque de la clase, se cierra el ámbito */
        controlAmbitos.cerrarAmbito(); 
     }
break;
case 29:
//#line 278 "gramatica.y"
{ 
        registrarEstructura("Herencia");
        yyval = val_peek(0); /* Propagamos la lista de clases heredadas*/
     }
break;
case 30:
//#line 283 "gramatica.y"
{
        yyval = new ParserVal(new ArrayList<RegistroTS>());
      }
break;
case 35:
//#line 297 "gramatica.y"
{ registrarEstructura("Declaración friend"); }
break;
case 36:
//#line 299 "gramatica.y"
{ registrarEstructura("Herencia dentro de clase"); }
break;
case 37:
//#line 304 "gramatica.y"
{ 
      yyval = new ParserVal("private");
     }
break;
case 38:
//#line 308 "gramatica.y"
{ 
      yyval = new ParserVal("public"); 
     }
break;
case 39:
//#line 315 "gramatica.y"
{ 
      String nombreAmigo = ((RegistroTS) val_peek(1).obj).getLexema(); 
      /* Obtenemos el registro de la clase actual que está abierta en el ámbito */
      String nombreClaseActual = controlAmbitos.getAmbitoActual(); 
      RegistroTS regClaseActual = ts.buscar(nombreClaseActual + ":main"); 
      if (regClaseActual != null) { 
        /* Registramos la clase o función como amiga de la clase actual */
        regClaseActual.agregarAmigo(nombreAmigo); 
        } 
     }
break;
case 40:
//#line 328 "gramatica.y"
{
      List<String> lista = new ArrayList<>();
        lista.add((String) val_peek(1).obj); /* Agrega el tipo (ej: "USHORTINT")*/
        yyval = new ParserVal(lista);
     }
break;
case 41:
//#line 334 "gramatica.y"
{ 
        List<String> lista = (List<String>) val_peek(3).obj;
        lista.add((String) val_peek(1).obj);
        yyval = new ParserVal(lista);
     }
break;
case 42:
//#line 343 "gramatica.y"
{
        NodoArbol expr = (NodoArbol) val_peek(2).obj;
        NodoArbol nodoRet = GeneradorCodigoIntermedio.crearNodoAST("RET", expr, null);
        yyval = new ParserVal(nodoRet);
      }
break;
case 43:
//#line 349 "gramatica.y"
{
        yyval = new ParserVal((Object) null);
      }
break;
case 44:
//#line 356 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 45:
//#line 358 "gramatica.y"
{ yyval = val_peek(2); }
break;
case 46:
//#line 362 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 47:
//#line 363 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 48:
//#line 364 "gramatica.y"
{ yyval = new ParserVal((Object) null); }
break;
case 49:
//#line 369 "gramatica.y"
{
        NodoArbol izq = (NodoArbol) val_peek(1).obj;
        NodoArbol der = (NodoArbol) val_peek(0).obj;
        if (izq == null) {
            yyval = val_peek(0);
        } else if (der == null) {
            yyval = val_peek(1);
        } else {
            NodoArbol seq = GeneradorCodigoIntermedio.crearNodoAST(";", izq, der);
            yyval = new ParserVal(seq);
        }
      }
break;
case 50:
//#line 382 "gramatica.y"
{
        yyval = new ParserVal((Object) null);
      }
break;
case 51:
//#line 388 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 52:
//#line 389 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 53:
//#line 390 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 54:
//#line 396 "gramatica.y"
{
        registrarEstructura("Asignación");
        String lexemaVar = ((RegistroTS) val_peek(3).obj).getLexema(); 
        RegistroTS regVariable = buscarVariableVisibilidad(lexemaVar);
        if (regVariable == null) { 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": La variable '" + lexemaVar + "' no está declarada o no es visible en este ámbito"); 
          yyval = new ParserVal(null); 
        } else if (!ts.esAsignable(regVariable)) { 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": Uso incorrecto de '" + lexemaVar + "'. No se puede asignar a un símbolo de uso '" + regVariable.getUso() + "'");
          yyval = new ParserVal(null); 
        } else {
          NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST(regVariable);
          NodoArbol expr = (NodoArbol) val_peek(1).obj;
          NodoArbol nodoAsig = GeneradorCodigoIntermedio.verificarAsignacion(hojaID, expr, lexico.getNroLinea());
          yyval = new ParserVal(nodoAsig);
        }
      }
break;
case 55:
//#line 414 "gramatica.y"
{
        registrarEstructura("Asignación a atributo posicional"); 
        /* Extraer el lexema del objeto ($1.obj) */
        String nombreObjeto = ((RegistroTS) val_peek(6).obj).getLexema(); 
        /* Resolver la visibilidad y ámbito del objeto (Local -> Clase -> Global) */
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto); 
        if (regObjeto == null) { 
          /* Error semántico: Objeto o variable no declarada */
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El objeto o variable '" + nombreObjeto + "' no está declarado o no es visible"); 
          yyval = new ParserVal(null);
        } else if (!"objeto".equalsIgnoreCase(regObjeto.getUso())) { 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El identificador '" + nombreObjeto + "' es de uso '" + regObjeto.getUso() + "' y no admite acceso posicional a atributos"); 
          yyval = new ParserVal(null);
        } else {
          NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST(regObjeto);
          NodoArbol indice = (NodoArbol) val_peek(4).obj;
          if (indice != null && indice.getTipo() != null && !indice.getTipo().toLowerCase().contains("int")) { 
            yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El índice para el acceso posicional debe ser de tipo entero, pero se recibió '" + indice.getTipo() + "'"); 
          }
          String tipoClase = regObjeto.getTipo(); /* Ej: "ca" */
          RegistroTS regClaseDuenia = ts.buscar(tipoClase + ":main"); 
          String tipoAtributo = ts.obtenerTipoAtributoPosicional(tipoClase, indice);
          NodoArbol nodoAcceso = GeneradorCodigoIntermedio.crearNodoAST("[]", hojaID, indice);
          nodoAcceso.setTipo(tipoAtributo);
          RegistroTS regAtributo = ts.buscarAtributoPosicional(tipoClase, indice); 
          if (regAtributo != null && !ts.tienePermisoAcceso(regClaseDuenia, regAtributo, controlAmbitos.getAmbitoActual())) { 
            yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": Acceso Denegado. El atributo posicional en '" + tipoClase + "' es PRIVATE y el ámbito actual no es FRIEND"); 
            yyval = new ParserVal(null); 
          } else {
            NodoArbol expr = (NodoArbol) val_peek(1).obj;
            NodoArbol nodoAsig = GeneradorCodigoIntermedio.verificarEscrituraAtributo(regObjeto, indice, expr, lexico.getNroLinea());
            yyval = new ParserVal(nodoAsig);
          }
        }
      }
break;
case 56:
//#line 454 "gramatica.y"
{
        NodoArbol condNode = GeneradorCodigoIntermedio.crearNodoAST("cond", (NodoArbol) val_peek(3).obj, null);
        NodoArbol cuerpoNode = GeneradorCodigoIntermedio.crearNodoAST("cuerpo", (NodoArbol) val_peek(0).obj, null);
        NodoArbol whileNode = GeneradorCodigoIntermedio.crearNodoAST("WHILE", condNode, cuerpoNode);
        yyval = new ParserVal(whileNode);
      }
break;
case 57:
//#line 464 "gramatica.y"
{
        NodoArbol condNode = GeneradorCodigoIntermedio.crearNodoAST("cond", (NodoArbol) val_peek(5).obj, null);
        NodoArbol bloqThen = (NodoArbol) val_peek(3).obj; /* Subárbol directo de sentencias*/
        NodoArbol bloqElse = (NodoArbol) val_peek(2).obj; /* Subárbol de la rama else (o null)*/

        NodoArbol cuerpoNode = GeneradorCodigoIntermedio.crearNodoAST("cuerpo", bloqThen, bloqElse);
        NodoArbol ifNode = GeneradorCodigoIntermedio.crearNodoAST("IF", condNode, cuerpoNode);

        yyval = new ParserVal(ifNode);
      }
break;
case 58:
//#line 478 "gramatica.y"
{
        NodoArbol elseBlock = (NodoArbol) val_peek(0).obj;
        NodoArbol elseNode = GeneradorCodigoIntermedio.crearNodoAST("else", elseBlock, null);
        yyval = new ParserVal(elseNode);
      }
break;
case 59:
//#line 484 "gramatica.y"
{
        yyval = new ParserVal((Object) null);
      }
break;
case 60:
//#line 491 "gramatica.y"
{
        NodoArbol hojaCadena = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) val_peek(1).obj);
        NodoArbol nodoPout = GeneradorCodigoIntermedio.crearNodoAST("POUT", hojaCadena, null);
        yyval = new ParserVal(nodoPout);
      }
break;
case 61:
//#line 497 "gramatica.y"
{
        NodoArbol expr = (NodoArbol) val_peek(1).obj;
        NodoArbol nodoPout = GeneradorCodigoIntermedio.crearNodoAST("POUT", expr, null);
        yyval = new ParserVal(nodoPout);
      }
break;
case 62:
//#line 506 "gramatica.y"
{
        NodoArbol izq = (NodoArbol) val_peek(2).obj;
        String op = (String) val_peek(1).obj;
        NodoArbol der = (NodoArbol) val_peek(0).obj;
        NodoArbol nodoComp = GeneradorCodigoIntermedio.verificarYConvertirTipos(op, izq, der, lexico.getNroLinea());
        yyval = new ParserVal(nodoComp);
      }
break;
case 63:
//#line 516 "gramatica.y"
{ yyval = new ParserVal("=="); }
break;
case 64:
//#line 517 "gramatica.y"
{ yyval = new ParserVal("!="); }
break;
case 65:
//#line 518 "gramatica.y"
{ yyval = new ParserVal(">="); }
break;
case 66:
//#line 519 "gramatica.y"
{ yyval = new ParserVal("<="); }
break;
case 67:
//#line 520 "gramatica.y"
{ yyval = new ParserVal(">"); }
break;
case 68:
//#line 521 "gramatica.y"
{ yyval = new ParserVal("<"); }
break;
case 69:
//#line 527 "gramatica.y"
{
        NodoArbol izq = (NodoArbol) val_peek(2).obj;
        NodoArbol der = (NodoArbol) val_peek(0).obj;
        NodoArbol nodo = GeneradorCodigoIntermedio.verificarYConvertirTipos("+", izq, der, lexico.getNroLinea());
        yyval = new ParserVal(nodo);
      }
break;
case 70:
//#line 534 "gramatica.y"
{
        NodoArbol izq = (NodoArbol) val_peek(2).obj;
        NodoArbol der = (NodoArbol) val_peek(0).obj;
        NodoArbol nodo = GeneradorCodigoIntermedio.verificarYConvertirTipos("-", izq, der, lexico.getNroLinea());
        yyval = new ParserVal(nodo);
      }
break;
case 71:
//#line 541 "gramatica.y"
{
        NodoArbol izq = (NodoArbol) val_peek(2).obj;
        NodoArbol der = (NodoArbol) val_peek(0).obj;
        NodoArbol nodo = GeneradorCodigoIntermedio.verificarYConvertirTipos("*", izq, der, lexico.getNroLinea());
        yyval = new ParserVal(nodo);
      }
break;
case 72:
//#line 548 "gramatica.y"
{
        NodoArbol izq = (NodoArbol) val_peek(2).obj;
        NodoArbol der = (NodoArbol) val_peek(0).obj;
        NodoArbol nodo = GeneradorCodigoIntermedio.verificarYConvertirTipos("/", izq, der, lexico.getNroLinea());
        yyval = new ParserVal(nodo);
      }
break;
case 73:
//#line 555 "gramatica.y"
{
        aplicarSignoNegativo(val_peek(0));
        NodoArbol expr = (NodoArbol) val_peek(0).obj;
        /* Usamos una etiqueta explícita "UMINUS" para no confundirlo con la resta binaria*/
        NodoArbol nodo = GeneradorCodigoIntermedio.crearNodoAST("UMINUS", expr, null);
        yyval = new ParserVal(nodo);
      }
break;
case 74:
//#line 563 "gramatica.y"
{
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) val_peek(2).obj);
        NodoArbol expr = (NodoArbol) val_peek(0).obj;
        NodoArbol nodoAsig = GeneradorCodigoIntermedio.verificarYConvertirTipos("=", hojaID, expr, lexico.getNroLinea());
        yyval = new ParserVal(nodoAsig);
      }
break;
case 75:
//#line 570 "gramatica.y"
{
        String nombreObjeto = ((RegistroTS) val_peek(5).obj).getLexema(); 
        String nombreMetodo = ((RegistroTS) val_peek(3).obj).getLexema(); 
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto); 
        if (regObjeto == null) { 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El objeto '" + nombreObjeto + "' no está declarado o no es visible"); 
          yyval = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) null)); 
        } else { 
          String tipoClase = regObjeto.getTipo();
          RegistroTS regClaseDuenia = ts.buscar(tipoClase + ":main");
          String claveMetodoDirecto = nombreMetodo + ":main:" + tipoClase; RegistroTS regMetodo = ts.buscar(claveMetodoDirecto); 
          if (regMetodo == null) { 
            regMetodo = ts.buscarEnHerencia(tipoClase, nombreMetodo); 
          } 
          if (regMetodo == null) { 
            yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El método '" + nombreMetodo + "' no existe en la clase '" + tipoClase + "' ni en su herencia"); 
            yyval = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) null)); 
          } else if (!"metodo".equals(regMetodo.getUso()) && !"nombre_funcion".equals(regMetodo.getUso())) { 
            yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El identificador '" + nombreMetodo + "' no es un método de la clase '" + tipoClase + "'"); 
            yyval = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) null)); 
          } else if (!ts.tienePermisoAcceso(regClaseDuenia, regMetodo, controlAmbitos.getAmbitoActual())) { 
            yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": Acceso Denegado. El método '" + nombreMetodo + "' es PRIVATE en '" + tipoClase + "'"); 
            yyval = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) null)); 
          } else { 
            NodoArbol subArbolArgs = (NodoArbol) val_peek(1).obj; 
            List<String> tiposFormales = regMetodo.getTiposParametros();
            NodoArbol hojaMetodo = GeneradorCodigoIntermedio.crearHojaAST(regMetodo);
            NodoArbol nodoCall = GeneradorCodigoIntermedio.crearNodoAST("CALL", hojaMetodo, subArbolArgs);
            nodoCall.setTipo(regMetodo.getTipoToken());
            yyval = new ParserVal(nodoCall);
          }
        }
     }
break;
case 76:
//#line 605 "gramatica.y"
{
        String lexemaFunc = ((RegistroTS) val_peek(1).obj).getLexema();
        RegistroTS regFuncion = buscarVariableVisibilidad(lexemaFunc);
        if (regFuncion == null) { 
          /* Error 1: No declarada o fuera de alcance */
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": La función o método '" + lexemaFunc + "' no está declarado o no es visible en este ámbito"); 
          yyval = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS)null)); 
        } else if (!"nombre_funcion".equalsIgnoreCase(regFuncion.getUso()) && !"metodo".equalsIgnoreCase(regFuncion.getUso())) { 
          /* Error 2: El símbolo existe pero no es una función (ej: es una variable o clase) */
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El identificador '" + lexemaFunc + "' es de uso '" + regFuncion.getUso() + "' y no puede ser invocado como función"); 
          yyval = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS)null)); 
        } else {
          NodoArbol infoInvoc = (NodoArbol) val_peek(0).obj;
          NodoArbol nodoCall = GeneradorCodigoIntermedio.procesarInvocacion(regFuncion, infoInvoc, lexico.getNroLinea());
          nodoCall.setTipo(regFuncion.getTipoToken());
          yyval = new ParserVal(nodoCall);
        }
      }
break;
case 77:
//#line 624 "gramatica.y"
{ registrarEstructura("Acceso posicional");
        String nombreObjeto = ((RegistroTS) val_peek(3).obj).getLexema();
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto);
        if (regObjeto == null) { 
          /* Error semántico: el objeto o variable no está declarado */
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El objeto o variable '" + nombreObjeto + "' no está declarado o no es visible"); 
          yyval = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS)null)); 
        } else {
          NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST(regObjeto);
          NodoArbol indice = (NodoArbol) val_peek(1).obj;
          NodoArbol nodoAcceso = GeneradorCodigoIntermedio.verificarLecturaAtributo(regObj, indice, lexico.getNroLinea());
          yyval = new ParserVal(nodoAcceso);
        }
      }
break;
case 78:
//#line 639 "gramatica.y"
{
        String lexemaVar = ((RegistroTS) val_peek(0).obj).getLexema(); 
        /* Resolver visibilidad de adentro hacia afuera usando Name Mangling */
        RegistroTS regVariable = buscarVariableVisibilidad(lexemaVar); 
        if (regVariable == null) { 
          /* Error 1: Variable no declarada o fuera de alcance */
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": La variable '" + lexemaVar + "' no está declarada o no es visible en este ámbito"); 
          yyval = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS)null)); 
        } else if (!"variable".equalsIgnoreCase(regVariable.getUso()) && !"parametro".equalsIgnoreCase(regVariable.getUso()) && !"atributo".equalsIgnoreCase(regVariable.getUso()) && !"objeto".equalsIgnoreCase(regVariable.getUso())) { 
          /* Error 2: Uso incorrecto de contexto (ej. usar un nombre de función o clase como operando) */
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El identificador '" + lexemaVar + "' es de uso '" + regVariable.getUso() + "' y no puede ser utilizado como una variable"); 
          yyval = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) null)); 
        } else {
          NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST(registrarVariable);
          hojaID.setTipo(regVariable.getTipoToken());
          yyval = new ParserVal(hojaID);
        }
     }
break;
case 79:
//#line 658 "gramatica.y"
{ 
        /* $$ hereda directamente el ParserVal con el NodoArbol producido en la regla constante */
        yyval = val_peek(0); 
     }
break;
case 80:
//#line 666 "gramatica.y"
{
        NodoArbol args = (NodoArbol) val_peek(4).obj;
        NodoArbol orden = (NodoArbol) val_peek(1).obj;
        NodoArbol inv = GeneradorCodigoIntermedio.crearNodoAST("INVOC_ORDENADA", args, orden);
        yyval = new ParserVal(inv);
      }
break;
case 81:
//#line 673 "gramatica.y"
{
        yyval = val_peek(1);
      }
break;
case 82:
//#line 680 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 83:
//#line 682 "gramatica.y"
{
        NodoArbol izq = (NodoArbol) val_peek(2).obj;
        NodoArbol der = (NodoArbol) val_peek(0).obj;
        NodoArbol list = GeneradorCodigoIntermedio.crearNodoAST(",", izq, der);
        yyval = new ParserVal(list);
      }
break;
case 84:
//#line 691 "gramatica.y"
{
        NodoArbol hojaCte = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) val_peek(0).obj);
        yyval = new ParserVal(hojaCte);
      }
break;
case 85:
//#line 696 "gramatica.y"
{
        validarConstanteDoublef(val_peek(0));
        NodoArbol hojaCte = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) val_peek(0).obj);
        yyval = new ParserVal(hojaCte);
      }
break;
//#line 1411 "Parser.java"
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
