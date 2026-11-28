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
import java.util.*;
@SuppressWarnings("unchecked")
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
    0,    1,    1,    2,    2,    4,    4,    4,    4,    4,
    9,    9,    9,   10,   10,    5,    6,   11,   11,   12,
   12,    8,   14,   14,   15,   15,    7,   16,   17,   17,
   18,   19,   19,   20,   20,   20,   20,   20,   20,   21,
   21,   21,   21,   22,   22,    3,    3,   23,   23,   23,
   23,   23,   23,   24,   24,   26,   31,   31,   27,   27,
   28,   25,   25,   30,   32,   32,   32,   32,   32,   32,
   29,   29,   29,   29,   29,   29,   29,   29,   29,   29,
   29,   29,   33,   33,   33,   34,   34,   35,   35,   36,
   36,   13,   13,
};
final static short yylen[] = {                            2,
    7,    1,    0,    2,    0,    1,    1,    1,    1,    2,
    1,    1,    1,    1,    3,    3,    7,    1,    3,    1,
    2,    6,    6,    6,    2,    4,    3,    2,    3,    0,
    4,    2,    0,    3,    4,    6,    3,    3,    2,    6,
    7,    5,    6,    1,    4,    2,    0,    1,    2,    1,
    1,    1,    2,    4,    7,    8,    2,    0,    6,    4,
    5,    4,    4,    3,    1,    1,    1,    1,    1,    1,
    3,    3,    3,    3,    2,    3,    3,    5,    7,    4,
    1,    1,    1,    1,    2,    1,    3,    3,    0,    1,
    3,    1,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    2,    5,    0,    0,   13,   47,    0,    0,
   11,   12,    4,    6,    7,    8,    9,    0,    5,    0,
   10,    0,    0,   28,   14,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   46,   48,    0,
   50,   51,   52,    0,    0,   16,    0,   47,    0,   33,
   27,   53,    0,    0,    0,    1,    0,    0,    0,    0,
   49,    0,    0,   15,    0,   29,    0,    0,   92,   93,
    0,    0,   82,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   18,   20,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   32,    5,    0,    0,
    0,    0,   75,    0,    0,    0,    0,    0,   54,    0,
   65,   66,   67,   68,   69,   70,    0,    0,   62,   63,
   47,   60,   44,    0,    0,   21,    0,    0,   24,   25,
    0,   23,   22,   39,   31,    0,    0,    0,    0,    0,
    0,    0,    0,   83,    0,   84,   77,    0,    0,    0,
   76,    0,    0,   73,   74,    0,    0,    0,    0,    0,
   61,   17,   19,    0,    0,    0,    0,   37,   38,    0,
    0,   34,   47,   80,   85,    0,    0,    0,    0,    0,
    0,    0,   59,   26,    0,    0,   35,    0,    0,    0,
    0,    0,   78,    0,   55,   57,    0,   45,    0,    0,
   42,    0,    0,   90,    0,    0,   56,   43,    0,   40,
   36,   88,    0,   79,   41,   91,
};
final static short yydgoto[] = {                          2,
    4,    5,   22,   13,   14,   15,   16,   17,   88,   27,
   84,   85,   73,   19,   89,   20,   30,   51,   67,   97,
   98,  122,   38,   39,   40,   41,   42,   43,   76,   77,
  181,  117,  147,  149,  193,  205,
};
final static short yysindex[] = {                      -210,
   36,    0,    0,    0, -157,   71,    0,    0, -191, -181,
    0,    0,    0,    0,    0,    0,    0, -182,    0, -167,
    0,  -60,   67,    0,    0, -117,   43, -120, -115, -116,
   88, -193,  122,  108,  135,  -35,  141,    0,    0,  126,
    0,    0,    0,  -88,  170,    0,  -33,    0,   59,    0,
    0,    0,   20,   20,   20,    0,  -28,  -42,   20,   20,
    0,   23, -186,    0,  -48,    0, -111,   -6,    0,    0,
   20,   20,    0,  169,  -10,    7,  194,  199,  127,   28,
  210,  279,   69,  -30,    0,    0,  216,    3,  111,  205,
  208,  212, -204,   16, -115, -180,    0,    0,   20,   -3,
   20,   19,    0,  289,   20,   20,   20,   20,    0,   -4,
    0,    0,    0,    0,    0,    0,   20,   28,    0,    0,
    0,    0,    0,   12,  224,    0,  229,   23,    0,    0,
 -204,    0,    0,    0,    0, -173,  234,   62,  259,   47,
   64,  -93,   14,    0,   69,    0,    0,  295,  146,  274,
    0,   75,   75,    0,    0,   20,  295,   56,   41,   28,
    0,    0,    0,   66,  285,   76,   70,    0,    0, -204,
  299,    0,    0,    0,    0,   20,   54,   20,  270,   28,
   78,  284,    0,    0, -204,  304,    0,  148, -204,   44,
  295,   69,    0,  159,    0,    0,  286,    0,  179, -204,
    0,  181,  287,    0,  -29,   54,    0,    0,  192,    0,
    0,    0,   69,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
  -78,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   83,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  -41,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   91,    0,
    0,    0,    0,    0,    0,    0,    0,  193,    0,    0,
    0,  -21,  -14,    0,    0,    0,  307,   86,    0,    0,
    0,    0,    0,    0,   91,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -34,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  203,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  -34,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,   17,  -32,    0,    0,    0,    0,    0,   46,   15,
    0,  222,  -19,    0, -107,    0,    0,    0,    0,    0,
    0,  -56,  -22,    0,    0,    0,    0,    0,   26,  292,
    0,    0,    0,  174,  147,    0,
};
final static int YYTABLESIZE=353;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         81,
   81,   81,   81,   81,   59,   81,   89,   89,   89,   89,
   89,   72,   89,  128,  213,   65,   71,   81,   81,   71,
   81,   71,   71,   71,   89,   89,   72,   89,   72,   72,
   72,  107,  105,  101,  106,   28,  108,   71,   71,  102,
   71,  145,   86,   49,   72,   72,    1,   72,  107,  105,
   18,  106,    7,  108,  100,  107,  105,  123,  106,   72,
  108,  158,  188,  126,   71,   23,  116,   83,  115,   87,
    7,   11,   12,   18,   25,   24,  139,  199,   74,   75,
  146,  202,   79,  165,   53,   82,   47,   54,  159,   11,
   12,   26,  209,  140,    3,  123,  103,  104,    6,    7,
  166,   46,   47,  183,   29,   47,    8,   47,   86,  138,
  141,    9,   96,   47,  142,   10,  107,   66,   11,   12,
  169,  108,  172,  196,  143,  175,  148,   44,  187,   21,
  152,  153,  154,  155,   14,    6,    7,  123,  136,   45,
  190,   25,  157,   48,   91,    7,   52,   50,    9,   14,
  167,  132,   10,   92,  131,   11,   12,  123,   93,   94,
   95,   55,    6,    7,   11,   12,   56,  120,  107,  105,
  173,  106,  204,  108,   57,    9,  164,    3,    3,   10,
   60,  179,   11,   12,   61,    3,  177,   18,  201,  176,
    3,  131,   62,  216,    3,   31,   32,    3,    3,  206,
   33,  191,  176,  148,   34,   35,   36,   31,   32,   63,
  107,  105,   33,  106,   37,  108,   90,   35,   36,  208,
   58,  210,  131,   64,  131,   80,   37,  109,   68,   69,
   70,   78,  215,   86,  118,  131,   86,   81,   81,  119,
   81,   81,   81,   87,   89,   89,   87,   89,   89,   89,
  124,  127,  212,  144,   69,   70,  129,   71,   71,  130,
   71,   71,   71,  133,   72,   72,  134,   72,   72,   72,
  135,  110,  137,  156,   99,  150,   68,   69,   70,  160,
   69,   70,  161,   31,   32,  111,  112,  162,   33,  113,
  114,  121,  168,   35,   36,  174,   31,   32,  170,   31,
   32,   33,   37,  171,   33,  182,   35,   36,  203,   35,
   36,  107,  105,  178,  106,   37,  108,  180,   37,  125,
  107,  105,  184,  106,  185,  108,   69,   70,  195,  151,
  107,  105,  186,  106,  192,  108,  107,  105,  189,  106,
  197,  108,  198,  200,  207,  211,   30,   64,   58,  163,
   81,  194,  214,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   44,   45,   40,   47,   41,   42,   43,   44,
   45,   40,   47,   44,   44,   48,   45,   59,   60,   41,
   62,   43,   44,   45,   59,   60,   41,   62,   43,   44,
   45,   42,   43,   40,   45,   19,   47,   59,   60,   46,
   62,   45,   62,   29,   59,   60,  257,   62,   42,   43,
    5,   45,  257,   47,   61,   42,   43,   80,   45,   40,
   47,  118,  170,   83,   45,  257,   60,   45,   62,  256,
  257,  276,  277,   28,  257,  257,  257,  185,   53,   54,
  100,  189,   57,  257,  278,   60,   44,  281,  121,  276,
  277,  274,  200,  274,   59,  118,   71,   72,  256,  257,
  274,   59,   44,  160,  272,   44,  264,   44,  128,   95,
   96,  269,   67,   44,   98,  273,   42,   59,  276,  277,
   59,   47,   59,  180,   99,  145,  101,   61,   59,   59,
  105,  106,  107,  108,   44,  256,  257,  160,   93,  257,
  173,  257,  117,  264,  256,  257,   59,  264,  269,   59,
  136,   41,  273,  265,   44,  276,  277,  180,  270,  271,
  272,   40,  256,  257,  276,  277,   59,   41,   42,   43,
  264,   45,  192,   47,   40,  269,  131,  256,  257,  273,
   40,  156,  276,  277,   59,  264,   41,  142,   41,   44,
  269,   44,  281,  213,  273,  256,  257,  276,  277,   41,
  261,  176,   44,  178,  265,  266,  267,  256,  257,   40,
   42,   43,  261,   45,  275,   47,  265,  266,  267,   41,
  256,   41,   44,  257,   44,  268,  275,   59,  257,  258,
  259,  260,   41,   41,   41,   44,   44,  279,  280,   41,
  282,  283,  284,   41,  279,  280,   44,  282,  283,  284,
   41,  282,  282,  257,  258,  259,   41,  279,  280,  257,
  282,  283,  284,   59,  279,  280,   59,  282,  283,  284,
   59,  282,  257,  278,  281,  257,  257,  258,  259,  268,
  258,  259,   59,  256,  257,  279,  280,   59,  261,  283,
  284,  264,   59,  266,  267,  282,  256,  257,   40,  256,
  257,  261,  275,  257,  261,  265,  266,  267,  265,  266,
  267,   42,   43,   40,   45,  275,   47,  262,  275,   41,
   42,   43,  257,   45,   40,   47,  258,  259,   59,   41,
   42,   43,  257,   45,  281,   47,   42,   43,   40,   45,
  263,   47,   59,   40,   59,   59,  264,   41,  263,  128,
   59,  178,  206,
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
"programa : ID fin_cabecera bloque_declarativo BEGIN lista_sentencias END ';'",
"fin_cabecera : ';'",
"fin_cabecera :",
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
"declaracion_variables : type_specifier lista_variables ';'",
"declaracion_typedef : TYPEDEF ID '=' CORCHETE_IZQ lista_valores CORCHETE_DER ';'",
"lista_valores : valor_enum",
"lista_valores : lista_valores ',' valor_enum",
"valor_enum : constante",
"valor_enum : '-' constante",
"declaracion_funcion : cabecera_funcion bloque_declarativo BEGIN lista_sentencias END ';'",
"cabecera_funcion : type_specifier FUNCTION ID '(' lista_parametros_formales ')'",
"cabecera_funcion : type_specifier FUNCTION ID '(' error ')'",
"lista_parametros_formales : type_specifier ID",
"lista_parametros_formales : lista_parametros_formales ',' type_specifier ID",
"declaracion_clase : encabezado_clase herencia_opcional cuerpo_clase",
"encabezado_clase : CLASS ID",
"herencia_opcional : EXTENDS lista_variables ';'",
"herencia_opcional :",
"cuerpo_clase : BEGIN lista_miembros END ';'",
"lista_miembros : lista_miembros miembro_clase",
"lista_miembros :",
"miembro_clase : type_specifier lista_variables ';'",
"miembro_clase : PRIVATE type_specifier lista_variables ';'",
"miembro_clase : cabecera_metodo bloque_declarativo BEGIN lista_sentencias END ';'",
"miembro_clase : FRIEND ID ';'",
"miembro_clase : EXTENDS lista_variables ';'",
"miembro_clase : error ';'",
"cabecera_metodo : type_specifier FUNCTION ID '(' lista_parametros_formales ')'",
"cabecera_metodo : PRIVATE type_specifier FUNCTION ID '(' lista_parametros_formales ')'",
"cabecera_metodo : type_specifier ID '(' lista_parametros_formales ')'",
"cabecera_metodo : PRIVATE type_specifier ID '(' lista_parametros_formales ')'",
"bloque_compuesto : sentencia",
"bloque_compuesto : BEGIN lista_sentencias END ';'",
"lista_sentencias : lista_sentencias sentencia",
"lista_sentencias :",
"sentencia : asignacion",
"sentencia : salida_pout ';'",
"sentencia : sentencia_if",
"sentencia : sentencia_while",
"sentencia : sentencia_retorno",
"sentencia : error ';'",
"asignacion : ID ASIGNACION expresion ';'",
"asignacion : ID CORCHETE_IZQ expresion CORCHETE_DER ASIGNACION expresion ';'",
"sentencia_if : IF '(' condicion ')' bloque_compuesto rama_else END_IF ';'",
"rama_else : ELSE bloque_compuesto",
"rama_else :",
"sentencia_while : WHILE '(' condicion ')' REPEAT bloque_compuesto",
"sentencia_while : WHILE error REPEAT bloque_compuesto",
"sentencia_retorno : RET '(' expresion ')' ';'",
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
"expresion : '(' expresion ')'",
"expresion : ID '=' valor_asignable",
"expresion : ID '(' lista_expresiones ')' orden_opcional",
"expresion : ID '.' ID '(' lista_expresiones ')' orden_opcional",
"expresion : ID CORCHETE_IZQ expresion CORCHETE_DER",
"expresion : ID",
"expresion : constante",
"valor_asignable : ID",
"valor_asignable : constante",
"valor_asignable : '-' constante",
"lista_expresiones : expresion",
"lista_expresiones : lista_expresiones ',' expresion",
"orden_opcional : CORCHETE_IZQ lista_orden CORCHETE_DER",
"orden_opcional :",
"lista_orden : constante",
"lista_orden : lista_orden ',' constante",
"constante : CTE_USHORT",
"constante : CTE_DOUBLEF",
};

//#line 655 "gramatica.y"

/* ============================================================================
   CÓDIGO JAVA DE SOPORTE DEL PARSER
   ========================================================================== */

private AnalizadorLexico lexico;
private TablaDeSimbolos ts;
private GeneradorCodigoIntermedio gen;
private ControlAmbitos controlAmbitos;
private boolean huboError = false;
private boolean modoSilencioso = false;
private final List<String> erroresSintacticos = new ArrayList<>();
private final List<String> estructurasDetectadas = new ArrayList<>();
private final Deque<String> pilaTiposRetorno = new ArrayDeque<>();
private final Deque<RegistroTS> pilaClases = new ArrayDeque<>();
private final Deque<String> pilaAmbitosClase = new ArrayDeque<>();
private final Map<String, NodoArbol> cuerposFunciones = new LinkedHashMap<>();

public Parser(AnalizadorLexico lexico, TablaDeSimbolos ts) {
    this.lexico = lexico;
    this.ts = ts;
    this.gen = new GeneradorCodigoIntermedio(ts);
    this.controlAmbitos = new ControlAmbitos();
}

private int yylex() {
    int token = lexico.yylex();
    this.yylval = (lexico.yylval != null) ? lexico.yylval : new ParserVal();
    return token;
}

public void yyerror(String msg) {
    this.huboError = true;
    String lexema = "";
    if (yylval != null && yylval.obj instanceof RegistroTS) {
        lexema = ((RegistroTS) yylval.obj).getLexema();
    }
    String tokenNombre;
    if (yychar >= 0 && yychar < yyname.length && yyname[yychar] != null) {
        tokenNombre = yyname[yychar];
    } else if (!lexema.isEmpty()) {
        tokenNombre = lexema;
    } else {
        tokenNombre = "TOKEN_INESPERADO";
    }
    String err = "Línea " + lexico.getNroLinea() + ": Error Sintáctico (token: "
            + tokenNombre + "): " + msg;
    erroresSintacticos.add(err);
    if (!modoSilencioso) System.err.println(err);
}

public void setModoSilencioso(boolean valor) {
    this.modoSilencioso = valor;
}

public boolean huboError() {
    return huboError || !erroresSintacticos.isEmpty();
}

public List<String> getErroresSintacticos() {
    return new ArrayList<>(erroresSintacticos);
}

public List<String> getEstructurasDetectadas() {
    return new ArrayList<>(estructurasDetectadas);
}

public GeneradorCodigoIntermedio getGenerador() {
    return gen;
}

public TablaDeSimbolos getTablaSimbolos() {
    return ts;
}

public Map<String, NodoArbol> getCuerposFunciones() {
    return new LinkedHashMap<>(cuerposFunciones);
}

private void registrarEstructura(String nombre) {
    estructurasDetectadas.add(nombre);
}

private void errorSemantico(String mensaje) {
    gen.registrarErrorSemantico(lexico.getNroLinea(), mensaje);
    huboError = true;
}

private NodoArbol secuencia(NodoArbol a, NodoArbol b) {
    if (a == null) return b;
    if (b == null) return a;
    return gen.crearNodoAST(";", a, b);
}

private NodoArbol crearRetorno(NodoArbol expr, String tipoFuncion) {
    NodoArbol ret = gen.crearNodoAST("RET", expr, null);
    if (expr == null) {
        ret.setTipoToken(tipoFuncion);
        return ret;
    }
    String tExpr = gen.obtenerTipoDato(expr);
    String baseEsperada = gen.tipoBaseDe(tipoFuncion);
    String baseExpr = gen.tipoBaseDe(tExpr);
    if (baseEsperada.equals("DOUBLEF") && baseExpr.equals("USHORTINT")) {
        NodoArbol conv = gen.crearNodoAST("ItoD", expr, null);
        conv.setTipoToken("DOUBLEF");
        ret.setIzq(conv);
        ret.setTipoToken("DOUBLEF");
    } else if (baseEsperada.equals("USHORTINT") && baseExpr.equals("DOUBLEF")) {
        errorSemantico("La función retorna ushortint y se encontró una expresión doublef.");
        ret.setTipoToken("USHORTINT");
    } else if (!baseEsperada.equals(baseExpr)) {
        errorSemantico("Tipo de retorno incompatible: se esperaba '" + tipoFuncion + "' y se recibió '" + tExpr + "'.");
        ret.setTipoToken(tipoFuncion);
    } else {
        ret.setTipoToken(tipoFuncion);
    }
    return ret;
}

private String tipoCanonico(String tipo) {
    if (tipo == null) return null;
    if (tipo.equalsIgnoreCase("USHORTINT")) return "USHORTINT";
    if (tipo.equalsIgnoreCase("DOUBLEF")) return "DOUBLEF";
    return tipo;
}

private RegistroTS resolverTipoDeclaracion(String tipo) {
    String canonico = tipoCanonico(tipo);
    if ("USHORTINT".equals(canonico) || "DOUBLEF".equals(canonico)) {
        return null;
    }
    RegistroTS reg = ts.buscarTipoVisible(tipo, controlAmbitos.getJerarquiaBusqueda(tipo));
    if (reg == null) {
        errorSemantico("El tipo '" + tipo + "' no está declarado o no es visible.");
    }
    return reg;
}

private ArrayList<RegistroTS> declararListaVariables(String tipo, List<RegistroTS> lista, int nroLinea, String visibilidad) {
    ArrayList<RegistroTS> resultado = new ArrayList<>();
    if (lista == null) return resultado;

    String tipoResuelto = tipoCanonico(tipo);
    RegistroTS tipoReg = resolverTipoDeclaracion(tipo);
    boolean esClase = tipoReg != null && "nombre_clase".equalsIgnoreCase(tipoReg.getUso());
    if (esClase) {
        tipoResuelto = tipoReg.getNombreSimple();
    } else if (tipoReg != null && tipoReg.esTipoEnum()) {
        tipoResuelto = tipoReg.getNombreSimple();
    }

    String ambito = controlAmbitos.getAmbitoActual();
    boolean dentroDeClase = !pilaAmbitosClase.isEmpty()
            && pilaAmbitosClase.peek().equals(ambito);

    for (RegistroTS original : lista) {
        String nombre = original.getLexema();
        String clave = nombre + ":" + ambito;
        if (ts.buscar(clave) != null) {
            errorSemantico("El símbolo '" + nombre + "' ya fue declarado en el ámbito actual.");
            continue;
        }
        String uso;
        if (dentroDeClase) {
            uso = "atributo";
        } else if (esClase) {
            uso = "objeto";
        } else {
            uso = "variable";
        }
        RegistroTS reg = ts.registrarVariable(clave, tipoResuelto, uso);
        reg.setNroLinea(original.getNroLinea() > 0 ? original.getNroLinea() : nroLinea);
        if (dentroDeClase) {
            reg.setVisibilidad(visibilidad);
            RegistroTS clase = pilaClases.peek();
            if (clase != null) clase.agregarAtributo(reg);
        }
        resultado.add(reg);
    }
    return resultado;
}

private InfoFuncion abrirFuncion(String tipoRetorno, RegistroTS regID,
        List<ParametroFormal> parametros, String uso, String visibilidad) {
    String nombre = regID.getLexema();
    String ambitoPadre = controlAmbitos.getAmbitoActual();
    String clave = nombre + ":" + ambitoPadre;

    if (ts.buscar(clave) != null) {
        errorSemantico("La función o método '" + nombre + "' ya fue declarado en el ámbito actual.");
    }

    RegistroTS regFunc = ts.registrarFuncion(clave, tipoRetorno, uso, null);
    regFunc.setNroLinea(regID.getNroLinea());
    regFunc.setVisibilidad(visibilidad);
    for (ParametroFormal p : parametros) {
        regFunc.agregarParametroFormal(p);
    }

    pilaTiposRetorno.push(tipoRetorno);
    controlAmbitos.abrirAmbito(nombre);

    for (ParametroFormal p : parametros) {
        String claveParam = p.getNombre() + ":" + controlAmbitos.getAmbitoActual();
        if (ts.buscar(claveParam) != null) {
            errorSemantico("Parámetro redeclarado '" + p.getNombre() + "'.");
        } else {
            RegistroTS regParam = ts.registrarVariable(claveParam, tipoCanonico(p.getTipo()), "parametro");
            regParam.setNroLinea(regID.getNroLinea());
        }
    }

    return new InfoFuncion(regFunc, tipoRetorno, nombre, ambitoPadre);
}

private NodoArbol cerrarFuncion(InfoFuncion info, NodoArbol decls, NodoArbol cuerpo) {
    if (!pilaTiposRetorno.isEmpty()) pilaTiposRetorno.pop();
    controlAmbitos.cerrarAmbito();
    NodoArbol nodo = gen.crearNodoAST("FUNC", decls, cuerpo);
    nodo.setInfo(info);
    nodo.setTipoToken(info.getTipoRetorno());
    cuerposFunciones.put(info.getRegistro().getLexema(), nodo);
    if (!pilaClases.isEmpty() && !pilaAmbitosClase.isEmpty()
            && pilaAmbitosClase.peek().equals(info.getAmbitoPadre())) {
        pilaClases.peek().agregarMetodo(nodo);
    }
    return nodo;
}

private void procesarHerencia(List<RegistroTS> padres) {
    if (pilaClases.isEmpty() || padres == null) return;
    RegistroTS clase = pilaClases.peek();
    String claveActual = clase.getNombreSimple();
    for (RegistroTS padre : padres) {
        String nombrePadre = padre.getLexema();
        RegistroTS regPadre = ts.buscar(nombrePadre + ":main");
        if (regPadre == null) regPadre = ts.buscar(nombrePadre);
        if (regPadre == null || !"nombre_clase".equalsIgnoreCase(regPadre.getUso())) {
            errorSemantico("La clase base '" + nombrePadre + "' no está declarada o no es visible.");
        } else if (claveActual.equals(nombrePadre)
                || ts.esHerenciaCircular(regPadre.getLexema(), clase.getLexema())) {
            errorSemantico("Herencia circular detectada con la clase '" + nombrePadre + "'.");
        } else if (!clase.getClasesHeredadas().contains(nombrePadre)) {
            clase.agregarClaseHeredada(nombrePadre);
        }
    }
}

private void registrarAmigo(String nombreAmigo) {
    if (pilaClases.isEmpty()) {
        errorSemantico("La declaración 'friend' solo es válida dentro de una clase.");
        return;
    }
    pilaClases.peek().agregarAmigo(nombreAmigo);
}

private boolean verificarPermisoPosicional(RegistroTS regObjeto, NodoArbol indice) {
    RegistroTS cte = obtenerConstante(indice);
    if (cte == null) return true; // el chequeo exacto de índice variable se hace al ejecutar
    int pos = (int) cte.getValorNum();
    if ("UMINUS".equals(indice.getEtiqueta())) pos = -pos;
    RegistroTS attr = ts.obtenerAtributoPosicional(regObjeto.getTipoToken(), pos);
    if (attr == null) return true;
    RegistroTS clase = ts.buscar(regObjeto.getTipoToken() + ":main");
    return ts.tienePermisoAcceso(clase, attr, controlAmbitos.getAmbitoActual());
}

private RegistroTS obtenerConstante(NodoArbol nodo) {
    if (nodo == null) return null;
    RegistroTS reg = nodo.getRegTS();
    if (reg != null && (reg.getIdToken() == CTE_USHORT || reg.getIdToken() == CTE_DOUBLEF)) {
        return reg;
    }
    if ("UMINUS".equals(nodo.getEtiqueta())) return obtenerConstante(nodo.getIzq());
    return null;
}

private RegistroTS buscarVariableVisibilidad(String lexemaOriginal) {
    for (String clave : controlAmbitos.getJerarquiaBusqueda(lexemaOriginal)) {
        RegistroTS reg = ts.buscar(clave);
        if (reg != null && !"nombre_clase".equalsIgnoreCase(reg.getUso())
                && !"nombre_funcion".equalsIgnoreCase(reg.getUso())
                && !"metodo".equalsIgnoreCase(reg.getUso())
                && !"type_name".equalsIgnoreCase(reg.getUso())) {
            return reg;
        }
    }
    String ambito = controlAmbitos.getAmbitoActual();
    String[] partes = ambito.split(":");
    if (partes.length >= 2) {
        RegistroTS heredado = ts.buscarEnHerencia(partes[1], lexemaOriginal);
        if (heredado != null) return heredado;
    }
    return null;
}

private RegistroTS buscarFuncionVisible(String lexemaOriginal) {
    for (String clave : controlAmbitos.getJerarquiaBusqueda(lexemaOriginal)) {
        RegistroTS reg = ts.buscar(clave);
        if (reg != null && ("nombre_funcion".equalsIgnoreCase(reg.getUso())
                || "metodo".equalsIgnoreCase(reg.getUso()))) {
            return reg;
        }
    }
    String ambito = controlAmbitos.getAmbitoActual();
    String[] partes = ambito.split(":");
    if (partes.length >= 2) {
        RegistroTS heredado = ts.buscarMetodoEnHerencia(partes[1], lexemaOriginal);
        if (heredado != null) return heredado;
    }
    return null;
}

/* TP2 punto c: el léxico no conoce el signo. El Parser revalida el rango. */
private void aplicarSignoNegativo(ParserVal v) {
    if (v == null || !(v.obj instanceof NodoArbol)) return;
    NodoArbol nodo = (NodoArbol) v.obj;
    RegistroTS reg = nodo.getRegTS();
    if (reg == null) return;

    if (reg.getIdToken() == CTE_USHORT) {
        errorSemantico("La constante entera sin signo '" + reg.getLexema() + "' no admite valor negativo.");
        return;
    }
    if (reg.getIdToken() != CTE_DOUBLEF) return;

    String lexema = reg.getLexema();
    if (lexema.startsWith("-")) return;
    try {
        double valorNegativo = -Double.parseDouble(lexema.replace("d", "e").replace("D", "e"));
        if (!ts.verificarRangoDoublef(valorNegativo)) {
            errorSemantico("Constante doublef negativa fuera de rango: -" + lexema);
            return;
        }
        RegistroTS regNeg = ts.registrarConstanteNegativa(lexema, lexico.getNroLinea(), valorNegativo);
        NodoArbol hoja = new NodoArbol(regNeg);
        v.obj = hoja;
        v.sval = regNeg.getLexema();
    } catch (NumberFormatException e) {
        errorSemantico("Constante doublef negativa mal formada: -" + lexema);
    }
}

private void validarConstanteDoublef(ParserVal v) {
    if (v == null || !(v.obj instanceof RegistroTS)) return;
    RegistroTS reg = (RegistroTS) v.obj;
    try {
        double valor = Double.parseDouble(reg.getLexema().replace("d", "e").replace("D", "e"));
        if (!ts.verificarRangoDoublef(valor)) {
            errorSemantico("Constante doublef fuera de rango: " + reg.getLexema());
        }
    } catch (NumberFormatException e) {
        errorSemantico("Constante doublef mal formada: " + reg.getLexema());
    }
}
//#line 791 "Parser.java"
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
//#line 32 "gramatica.y"
{
        NodoArbol nombre = gen.crearHojaAST((RegistroTS) val_peek(6).obj);
        NodoArbol decls = (NodoArbol) val_peek(4).obj;
        NodoArbol cuerpo = (NodoArbol) val_peek(2).obj;
        NodoArbol raiz = gen.crearNodoAST("PROGRAMA", nombre, decls);
        raiz.setTer(cuerpo);
        gen.setRaizAST(raiz);
        yyval = new ParserVal(raiz);
      }
break;
case 4:
//#line 50 "gramatica.y"
{ yyval = new ParserVal(secuencia((NodoArbol) val_peek(1).obj, (NodoArbol) val_peek(0).obj)); }
break;
case 5:
//#line 52 "gramatica.y"
{ yyval = new ParserVal((Object) null); }
break;
case 10:
//#line 61 "gramatica.y"
{
        yyerrflag = 0;
        yyerror("Declaración inválida; se resincroniza en ';'.");
        yyval = new ParserVal((Object) null);
      }
break;
case 11:
//#line 69 "gramatica.y"
{ yyval = new ParserVal((Object) "USHORTINT"); }
break;
case 12:
//#line 70 "gramatica.y"
{ yyval = new ParserVal((Object) "DOUBLEF"); }
break;
case 13:
//#line 71 "gramatica.y"
{ yyval = new ParserVal((Object) ((RegistroTS) val_peek(0).obj).getLexema()); }
break;
case 14:
//#line 76 "gramatica.y"
{
        ArrayList<RegistroTS> lista = new ArrayList<>();
        lista.add((RegistroTS) val_peek(0).obj);
        yyval = new ParserVal(lista);
      }
break;
case 15:
//#line 82 "gramatica.y"
{
        @SuppressWarnings("unchecked")
        ArrayList<RegistroTS> lista = (ArrayList<RegistroTS>) val_peek(2).obj;
        lista.add((RegistroTS) val_peek(0).obj);
        yyval = val_peek(2);
      }
break;
case 16:
//#line 92 "gramatica.y"
{
        ArrayList<RegistroTS> regs = declararListaVariables((String) val_peek(2).obj,
                (List<RegistroTS>) val_peek(1).obj, lexico.getNroLinea(), "public");
        registrarEstructura("Declaración de variables");
        NodoArbol nodo = gen.crearNodoAST("VAR_DECL", null, null);
        nodo.setInfo(regs);
        yyval = new ParserVal(nodo);
      }
break;
case 17:
//#line 105 "gramatica.y"
{
        RegistroTS regID = (RegistroTS) val_peek(5).obj;
        String nombreTipo = regID.getLexema();
        String clave = nombreTipo + ":" + controlAmbitos.getAmbitoActual();
        if (ts.buscar(clave) != null) {
          errorSemantico("El tipo '" + nombreTipo + "' ya fue definido en el ámbito actual.");
          yyval = new ParserVal((Object) null);
        } else {
          List<RegistroTS> valores = (List<RegistroTS>) val_peek(2).obj;
          if (valores == null || valores.isEmpty()) {
            errorSemantico("La enumeración '" + nombreTipo + "' debe tener al menos un valor.");
          }
          String base = null;
          for (RegistroTS v : valores) {
            String baseValor = (v.getIdToken() == CTE_DOUBLEF) ? "DOUBLEF" : "USHORTINT";
            if (base != null && !base.equals(baseValor)) {
              errorSemantico("La enumeración '" + nombreTipo + "' mezcla valores ushortint y doublef.");
              break;
            }
            base = baseValor;
          }
          if (base == null) base = "USHORTINT";
          RegistroTS regTipo = ts.registrarTipoPersonalizado(clave, "type_name", valores);
          regTipo.setNroLinea(regID.getNroLinea());
          regTipo.setTipoBaseEnum(base);
          registrarEstructura("Enumeración (typedef)");
          NodoArbol nodo = gen.crearNodoAST("TYPEDEF", gen.crearHojaAST(regTipo), null);
          yyval = new ParserVal(nodo);
        }
      }
break;
case 18:
//#line 139 "gramatica.y"
{
        ArrayList<RegistroTS> lista = new ArrayList<>();
        lista.add((RegistroTS) val_peek(0).obj);
        yyval = new ParserVal(lista);
      }
break;
case 19:
//#line 145 "gramatica.y"
{
        ArrayList<RegistroTS> lista = (ArrayList<RegistroTS>) val_peek(2).obj;
        lista.add((RegistroTS) val_peek(0).obj);
        yyval = val_peek(2);
      }
break;
case 20:
//#line 154 "gramatica.y"
{
        NodoArbol n = (NodoArbol) val_peek(0).obj;
        yyval = new ParserVal(n.getRegTS());
      }
break;
case 21:
//#line 159 "gramatica.y"
{
        aplicarSignoNegativo(val_peek(0));
        NodoArbol n = (NodoArbol) val_peek(0).obj;
        yyval = new ParserVal(n.getRegTS());
      }
break;
case 22:
//#line 169 "gramatica.y"
{
        InfoFuncion info = (InfoFuncion) val_peek(5).obj;
        NodoArbol decls = (NodoArbol) val_peek(4).obj;
        NodoArbol cuerpo = (NodoArbol) val_peek(2).obj;
        yyval = new ParserVal(cerrarFuncion(info, decls, cuerpo));
      }
break;
case 23:
//#line 179 "gramatica.y"
{
        yyval = new ParserVal(abrirFuncion((String) val_peek(5).obj, (RegistroTS) val_peek(3).obj,
                (List<ParametroFormal>) val_peek(1).obj, "nombre_funcion", "public"));
      }
break;
case 24:
//#line 184 "gramatica.y"
{
        yyerrflag = 0;
        yyerror("Parámetros formales inválidos en la función; se resincroniza al cerrar paréntesis.");
        yyval = new ParserVal(abrirFuncion((String) val_peek(5).obj, (RegistroTS) val_peek(3).obj, new ArrayList<>(), "nombre_funcion", "public"));
      }
break;
case 25:
//#line 193 "gramatica.y"
{
        ArrayList<ParametroFormal> lista = new ArrayList<>();
        lista.add(new ParametroFormal(((RegistroTS) val_peek(0).obj).getLexema(), (String) val_peek(1).obj));
        yyval = new ParserVal(lista);
      }
break;
case 26:
//#line 199 "gramatica.y"
{
        ArrayList<ParametroFormal> lista = (ArrayList<ParametroFormal>) val_peek(3).obj;
        lista.add(new ParametroFormal(((RegistroTS) val_peek(0).obj).getLexema(), (String) val_peek(1).obj));
        yyval = val_peek(3);
      }
break;
case 27:
//#line 210 "gramatica.y"
{
        RegistroTS regClase = (RegistroTS) val_peek(2).obj;
        controlAmbitos.cerrarAmbito();
        if (!pilaClases.isEmpty()) pilaClases.pop();
        if (!pilaAmbitosClase.isEmpty()) pilaAmbitosClase.pop();
        NodoArbol metodos = null;
        for (NodoArbol m : regClase.getMetodos()) {
          metodos = secuencia(metodos, m);
        }
        NodoArbol nodo = gen.crearNodoAST("CLASS", gen.crearHojaAST(regClase), metodos);
        yyval = new ParserVal(nodo);
      }
break;
case 28:
//#line 226 "gramatica.y"
{
        String nombre = ((RegistroTS) val_peek(0).obj).getLexema();
        String clave = nombre + ":" + controlAmbitos.getAmbitoActual();
        RegistroTS regClase = ts.buscar(clave);
        if (regClase != null && "nombre_clase".equalsIgnoreCase(regClase.getUso())) {
          errorSemantico("La clase '" + nombre + "' ya fue declarada en el ámbito actual.");
        } else {
          regClase = ts.registrarClase(clave, "nombre_clase");
          regClase.setNroLinea(((RegistroTS) val_peek(0).obj).getNroLinea());
        }
        pilaClases.push(regClase);
        controlAmbitos.abrirAmbito(nombre);
        pilaAmbitosClase.push(controlAmbitos.getAmbitoActual());
        yyval = new ParserVal(regClase);
      }
break;
case 29:
//#line 245 "gramatica.y"
{ procesarHerencia((List<RegistroTS>) val_peek(1).obj); }
break;
case 30:
//#line 247 "gramatica.y"
{ }
break;
case 34:
//#line 261 "gramatica.y"
{
        declararListaVariables((String) val_peek(2).obj, (List<RegistroTS>) val_peek(1).obj, lexico.getNroLinea(), "public");
        registrarEstructura("Atributo de clase");
      }
break;
case 35:
//#line 266 "gramatica.y"
{
        declararListaVariables((String) val_peek(2).obj, (List<RegistroTS>) val_peek(1).obj, lexico.getNroLinea(), "private");
        registrarEstructura("Atributo privado de clase");
      }
break;
case 36:
//#line 271 "gramatica.y"
{
        InfoFuncion info = (InfoFuncion) val_peek(5).obj;
        cerrarFuncion(info, (NodoArbol) val_peek(4).obj, (NodoArbol) val_peek(2).obj);
        registrarEstructura("Método de clase");
      }
break;
case 37:
//#line 277 "gramatica.y"
{
        registrarAmigo(((RegistroTS) val_peek(1).obj).getLexema());
        registrarEstructura("Declaración friend");
      }
break;
case 38:
//#line 282 "gramatica.y"
{
        procesarHerencia((List<RegistroTS>) val_peek(1).obj);
        registrarEstructura("Herencia");
      }
break;
case 39:
//#line 287 "gramatica.y"
{
        yyerrflag = 0;
        yyerror("Miembro de clase inválido; se resincroniza en ';'.");
      }
break;
case 40:
//#line 295 "gramatica.y"
{
        yyval = new ParserVal(abrirFuncion((String) val_peek(5).obj, (RegistroTS) val_peek(3).obj,
                (List<ParametroFormal>) val_peek(1).obj, "metodo", "public"));
      }
break;
case 41:
//#line 300 "gramatica.y"
{
        yyval = new ParserVal(abrirFuncion((String) val_peek(5).obj, (RegistroTS) val_peek(3).obj,
                (List<ParametroFormal>) val_peek(1).obj, "metodo", "private"));
      }
break;
case 42:
//#line 305 "gramatica.y"
{
        yyval = new ParserVal(abrirFuncion((String) val_peek(4).obj, (RegistroTS) val_peek(3).obj,
                (List<ParametroFormal>) val_peek(1).obj, "metodo", "public"));
      }
break;
case 43:
//#line 310 "gramatica.y"
{
        yyval = new ParserVal(abrirFuncion((String) val_peek(4).obj, (RegistroTS) val_peek(3).obj,
                (List<ParametroFormal>) val_peek(1).obj, "metodo", "private"));
      }
break;
case 44:
//#line 320 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 45:
//#line 322 "gramatica.y"
{
        yyval = val_peek(2);
        registrarEstructura("Bloque compuesto");
      }
break;
case 46:
//#line 330 "gramatica.y"
{ yyval = new ParserVal(secuencia((NodoArbol) val_peek(1).obj, (NodoArbol) val_peek(0).obj)); }
break;
case 47:
//#line 332 "gramatica.y"
{ yyval = new ParserVal((Object) null); }
break;
case 49:
//#line 338 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 53:
//#line 343 "gramatica.y"
{
        yyerrflag = 0;
        yyerror("Sentencia inválida; se resincroniza en ';'.");
        yyval = new ParserVal((Object) null);
      }
break;
case 54:
//#line 352 "gramatica.y"
{
        RegistroTS reg = buscarVariableVisibilidad(((RegistroTS) val_peek(3).obj).getLexema());
        if (reg == null) {
          errorSemantico("La variable '" + ((RegistroTS) val_peek(3).obj).getLexema() + "' no está declarada o no es visible.");
          yyval = new ParserVal((Object) null);
        } else if (!ts.esAsignable(reg)) {
          errorSemantico("El símbolo '" + reg.getNombreSimple() + "' no es asignable (uso: " + reg.getUso() + ").");
          yyval = new ParserVal((Object) null);
        } else {
          NodoArbol nodo = gen.verificarAsignacion(gen.crearHojaAST(reg), (NodoArbol) val_peek(1).obj, lexico.getNroLinea());
          registrarEstructura("Asignación");
          yyval = new ParserVal(nodo);
        }
      }
break;
case 55:
//#line 367 "gramatica.y"
{
        String nombreObjeto = ((RegistroTS) val_peek(6).obj).getLexema();
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto);
        if (regObjeto == null) {
          errorSemantico("El objeto '" + nombreObjeto + "' no está declarado o no es visible.");
          yyval = new ParserVal((Object) null);
        } else if (!gen.esTipoObjeto(regObjeto.getTipoToken())) {
          errorSemantico("El identificador '" + nombreObjeto + "' no es un objeto con acceso posicional.");
          yyval = new ParserVal((Object) null);
        } else {
          NodoArbol indice = (NodoArbol) val_peek(4).obj;
          NodoArbol expr = (NodoArbol) val_peek(1).obj;
          boolean permitido = verificarPermisoPosicional(regObjeto, indice);
          if (!permitido) {
            errorSemantico("Acceso denegado a un atributo privado de la clase '" + regObjeto.getTipoToken() + "'.");
            yyval = new ParserVal((Object) null);
          } else {
            NodoArbol nodo = gen.verificarEscrituraAtributo(regObjeto, indice, expr, lexico.getNroLinea());
            registrarEstructura("Asignación a atributo posicional");
            yyval = new ParserVal(nodo);
          }
        }
      }
break;
case 56:
//#line 394 "gramatica.y"
{
        NodoArbol cond = gen.crearNodoAST("cond", (NodoArbol) val_peek(5).obj, null);
        NodoArbol cuerpo = gen.crearNodoAST("cuerpo", (NodoArbol) val_peek(3).obj, (NodoArbol) val_peek(2).obj);
        NodoArbol nodo = gen.crearNodoAST("IF", cond, cuerpo);
        registrarEstructura("Sentencia IF");
        yyval = new ParserVal(nodo);
      }
break;
case 57:
//#line 405 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 58:
//#line 407 "gramatica.y"
{ yyval = new ParserVal((Object) null); }
break;
case 59:
//#line 411 "gramatica.y"
{ 
        NodoArbol cond = gen.crearNodoAST("cond", (NodoArbol) val_peek(3).obj, null); 
        NodoArbol cuerpo = gen.crearNodoAST("cuerpo", (NodoArbol) val_peek(0).obj, null); 
        NodoArbol nodo = gen.crearNodoAST("WHILE", cond, cuerpo); 
        registrarEstructura("Sentencia WHILE-REPEAT"); 
        yyval = new ParserVal(nodo); 
    }
break;
case 60:
//#line 418 "gramatica.y"
{ 
        yyerrflag = 0; /* Reinicia la bandera interna de error en BYACC/J (equivalente a yyerrok en Java) */
        yyerror("[RECUPERACIÓN] Error sintáctico en la condición del WHILE; resincronizado en REPEAT."); 
        NodoArbol cond = gen.crearNodoAST("cond", null, null); 
        NodoArbol cuerpo = gen.crearNodoAST("cuerpo", (NodoArbol) val_peek(0).obj, null); 
        NodoArbol nodo = gen.crearNodoAST("WHILE", cond, cuerpo); 
        yyval = new ParserVal(nodo); 
    }
break;
case 61:
//#line 429 "gramatica.y"
{
        if (pilaTiposRetorno.isEmpty()) {
          errorSemantico("Sentencia 'ret' fuera de una función.");
          yyval = new ParserVal((Object) null);
        } else {
          String tipoFuncion = pilaTiposRetorno.peek();
          NodoArbol expr = (NodoArbol) val_peek(2).obj;
          NodoArbol nodo = crearRetorno(expr, tipoFuncion);
          registrarEstructura("Sentencia RET");
          yyval = new ParserVal(nodo);
        }
      }
break;
case 62:
//#line 445 "gramatica.y"
{
        NodoArbol hoja = gen.crearHojaAST((RegistroTS) val_peek(1).obj);
        NodoArbol nodo = gen.crearNodoAST("POUT", hoja, null);
        registrarEstructura("Sentencia POUT (cadena)");
        yyval = new ParserVal(nodo);
      }
break;
case 63:
//#line 452 "gramatica.y"
{
        NodoArbol nodo = gen.crearNodoAST("POUT", (NodoArbol) val_peek(1).obj, null);
        registrarEstructura("Sentencia POUT (expresión)");
        yyval = new ParserVal(nodo);
      }
break;
case 64:
//#line 463 "gramatica.y"
{
        NodoArbol nodo = gen.verificarYConvertirTipos((String) val_peek(1).obj,
                (NodoArbol) val_peek(2).obj, (NodoArbol) val_peek(0).obj, lexico.getNroLinea());
        yyval = new ParserVal(nodo);
      }
break;
case 65:
//#line 471 "gramatica.y"
{ yyval = new ParserVal((Object) "=="); }
break;
case 66:
//#line 472 "gramatica.y"
{ yyval = new ParserVal((Object) "!="); }
break;
case 67:
//#line 473 "gramatica.y"
{ yyval = new ParserVal((Object) ">="); }
break;
case 68:
//#line 474 "gramatica.y"
{ yyval = new ParserVal((Object) "<="); }
break;
case 69:
//#line 475 "gramatica.y"
{ yyval = new ParserVal((Object) ">"); }
break;
case 70:
//#line 476 "gramatica.y"
{ yyval = new ParserVal((Object) "<"); }
break;
case 71:
//#line 481 "gramatica.y"
{ yyval = new ParserVal(gen.verificarYConvertirTipos("+", (NodoArbol) val_peek(2).obj, (NodoArbol) val_peek(0).obj, lexico.getNroLinea())); }
break;
case 72:
//#line 483 "gramatica.y"
{ yyval = new ParserVal(gen.verificarYConvertirTipos("-", (NodoArbol) val_peek(2).obj, (NodoArbol) val_peek(0).obj, lexico.getNroLinea())); }
break;
case 73:
//#line 485 "gramatica.y"
{ yyval = new ParserVal(gen.verificarYConvertirTipos("*", (NodoArbol) val_peek(2).obj, (NodoArbol) val_peek(0).obj, lexico.getNroLinea())); }
break;
case 74:
//#line 487 "gramatica.y"
{ yyval = new ParserVal(gen.verificarYConvertirTipos("/", (NodoArbol) val_peek(2).obj, (NodoArbol) val_peek(0).obj, lexico.getNroLinea())); }
break;
case 75:
//#line 489 "gramatica.y"
{
        NodoArbol expr = (NodoArbol) val_peek(0).obj;
        if (expr != null && expr.getRegTS() != null) {
          aplicarSignoNegativo(val_peek(0));
          yyval = val_peek(0);
        } else {
          NodoArbol nodo = gen.crearNodoAST("UMINUS", expr, null);
          if (expr != null) nodo.setTipoToken(expr.getTipoToken());
          yyval = new ParserVal(nodo);
        }
      }
break;
case 76:
//#line 501 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 77:
//#line 503 "gramatica.y"
{
        RegistroTS reg = buscarVariableVisibilidad(((RegistroTS) val_peek(2).obj).getLexema());
        if (reg == null) {
          errorSemantico("Variable '" + ((RegistroTS) val_peek(2).obj).getLexema() + "' no declarada en asignación interna.");
          yyval = new ParserVal((Object) null);
        } else if (!ts.esAsignable(reg)) {
          errorSemantico("El símbolo '" + reg.getNombreSimple() + "' no es asignable en una expresión.");
          yyval = new ParserVal((Object) null);
        } else {
          NodoArbol nodo = gen.verificarAsignacionEnExpresion(gen.crearHojaAST(reg), (NodoArbol) val_peek(0).obj, lexico.getNroLinea());
          registrarEstructura("Asignación interna (Tema 18)");
          yyval = new ParserVal(nodo);
        }
      }
break;
case 78:
//#line 518 "gramatica.y"
{
        RegistroTS regFunc = buscarFuncionVisible(((RegistroTS) val_peek(4).obj).getLexema());
        if (regFunc == null) {
          errorSemantico("La función '" + ((RegistroTS) val_peek(4).obj).getLexema() + "' no está declarada o no es visible.");
          yyval = new ParserVal((Object) null);
        } else {
          NodoArbol nodo = gen.crearInvocacion(regFunc, (NodoArbol) val_peek(2).obj, (NodoArbol) val_peek(0).obj, lexico.getNroLinea());
          registrarEstructura("Invocación de función");
          yyval = new ParserVal(nodo);
        }
      }
break;
case 79:
//#line 530 "gramatica.y"
{
        String nombreObjeto = ((RegistroTS) val_peek(6).obj).getLexema();
        String nombreMetodo = ((RegistroTS) val_peek(4).obj).getLexema();
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto);
        if (regObjeto == null || !gen.esTipoObjeto(regObjeto.getTipoToken())) {
          errorSemantico("El objeto '" + nombreObjeto + "' no está declarado o no es de un tipo con métodos.");
          yyval = new ParserVal((Object) null);
        } else {
          String tipoClase = regObjeto.getTipoToken();
          RegistroTS regMetodo = ts.buscarMetodoEnHerencia(tipoClase, nombreMetodo);
          if (regMetodo == null) {
            errorSemantico("El método '" + nombreMetodo + "' no existe en la clase '" + tipoClase + "' ni en su herencia.");
            yyval = new ParserVal((Object) null);
          } else {
            RegistroTS regClase = ts.buscar(tipoClase + ":main");
            if (regClase != null && !ts.tienePermisoAcceso(regClase, regMetodo, controlAmbitos.getAmbitoActual())) {
              errorSemantico("Acceso denegado: el método '" + nombreMetodo + "' es privado en '" + tipoClase + "'.");
              yyval = new ParserVal((Object) null);
            } else {
              NodoArbol nodo = gen.crearInvocacion(regMetodo, (NodoArbol) val_peek(2).obj, (NodoArbol) val_peek(0).obj, lexico.getNroLinea());
              nodo.setTer(gen.crearHojaAST(regObjeto));
              registrarEstructura("Invocación de método");
              yyval = new ParserVal(nodo);
            }
          }
        }
      }
break;
case 80:
//#line 558 "gramatica.y"
{
        String nombreObjeto = ((RegistroTS) val_peek(3).obj).getLexema();
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto);
        if (regObjeto == null || !gen.esTipoObjeto(regObjeto.getTipoToken())) {
          errorSemantico("El objeto '" + nombreObjeto + "' no admite acceso posicional.");
          yyval = new ParserVal((Object) null);
        } else {
          boolean permitido = verificarPermisoPosicional(regObjeto, (NodoArbol) val_peek(1).obj);
          if (!permitido) {
            errorSemantico("Acceso denegado a un atributo privado de la clase '" + regObjeto.getTipoToken() + "'.");
            yyval = new ParserVal((Object) null);
          } else {
            NodoArbol nodo = gen.verificarLecturaAtributo(regObjeto, (NodoArbol) val_peek(1).obj, lexico.getNroLinea());
            registrarEstructura("Lectura de atributo posicional");
            yyval = new ParserVal(nodo);
          }
        }
      }
break;
case 81:
//#line 577 "gramatica.y"
{
        String lexemaVar = ((RegistroTS) val_peek(0).obj).getLexema();
        RegistroTS reg = buscarVariableVisibilidad(lexemaVar);
        if (reg == null) {
          errorSemantico("La variable '" + lexemaVar + "' no está declarada o no es visible.");
          yyval = new ParserVal((Object) null);
        } else if ("nombre_clase".equalsIgnoreCase(reg.getUso())
                || "nombre_funcion".equalsIgnoreCase(reg.getUso())
                || "metodo".equalsIgnoreCase(reg.getUso())
                || "type_name".equalsIgnoreCase(reg.getUso())) {
          errorSemantico("El identificador '" + lexemaVar + "' no puede usarse como variable.");
          yyval = new ParserVal((Object) null);
        } else {
          yyval = new ParserVal(gen.crearHojaAST(reg));
        }
      }
break;
case 82:
//#line 594 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 83:
//#line 599 "gramatica.y"
{
        RegistroTS reg = buscarVariableVisibilidad(((RegistroTS) val_peek(0).obj).getLexema());
        if (reg == null) {
          errorSemantico("Variable '" + ((RegistroTS) val_peek(0).obj).getLexema() + "' no declarada.");
          yyval = new ParserVal((Object) null);
        } else {
          yyval = new ParserVal(gen.crearHojaAST(reg));
        }
      }
break;
case 84:
//#line 609 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 85:
//#line 611 "gramatica.y"
{
        aplicarSignoNegativo(val_peek(0));
        yyval = val_peek(0);
      }
break;
case 86:
//#line 619 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 87:
//#line 621 "gramatica.y"
{
        NodoArbol nodo = gen.crearNodoAST(",", (NodoArbol) val_peek(2).obj, (NodoArbol) val_peek(0).obj);
        yyval = new ParserVal(nodo);
      }
break;
case 88:
//#line 629 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 89:
//#line 631 "gramatica.y"
{ yyval = new ParserVal((Object) null); }
break;
case 90:
//#line 636 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 91:
//#line 638 "gramatica.y"
{
        NodoArbol nodo = gen.crearNodoAST(",", (NodoArbol) val_peek(2).obj, (NodoArbol) val_peek(0).obj);
        yyval = new ParserVal(nodo);
      }
break;
case 92:
//#line 646 "gramatica.y"
{ yyval = new ParserVal(gen.crearHojaAST((RegistroTS) val_peek(0).obj)); }
break;
case 93:
//#line 648 "gramatica.y"
{
        validarConstanteDoublef(val_peek(0));
        yyval = new ParserVal(gen.crearHojaAST((RegistroTS) val_peek(0).obj));
      }
break;
//#line 1604 "Parser.java"
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
