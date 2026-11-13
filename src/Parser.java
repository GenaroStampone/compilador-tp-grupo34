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
//#line 19 "Parser.java"




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
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    3,    3,    3,    3,    3,    4,    4,
    5,    5,    6,    9,    9,    7,   11,   11,   12,   12,
   13,   13,   14,   14,    8,   15,   15,   15,   16,   16,
    2,    2,   18,   18,   18,   18,   18,   19,   19,   20,
   21,   24,   24,   22,   22,   23,   25,   25,   25,   25,
   17,   17,   17,   17,   17,   17,   17,   17,   17,   26,
   26,   27,   27,   27,   10,   10,
};
final static short yylen[] = {                            2,
    6,    2,    0,    3,    1,    1,    1,    2,    1,    1,
    1,    3,    7,    1,    3,    7,    2,    0,    2,    0,
    2,    1,    1,    1,   12,    2,    4,    0,    5,    0,
    2,    0,    2,    2,    2,    2,    2,    3,    6,    7,
    7,    2,    0,    4,    4,    3,    1,    1,    1,    1,
    3,    3,    3,    3,    3,    2,    4,    1,    1,    6,
    3,    1,    3,    0,    1,    1,
};
final static short yydefred[] = {                         0,
    3,    0,    0,    0,   32,    0,    0,    9,   10,    2,
    0,    5,    6,    7,    8,    0,    0,    0,   11,    0,
    0,    0,    0,    0,    0,    0,    0,   31,    0,    0,
    0,    0,    0,    0,    0,    0,    4,    0,   37,    0,
    0,    0,    1,    0,    0,   33,   34,   35,   36,    0,
    0,   20,    0,   12,    0,   65,   66,   59,    0,    0,
    0,    0,    0,    0,    0,    0,   14,    0,    0,    0,
    0,    0,    0,   56,    0,    0,    0,    0,    0,   47,
   48,   49,   50,    0,   32,   44,   45,    0,    0,    0,
    0,   23,   24,   22,   19,    0,   26,    0,    3,    0,
    0,    0,    0,    0,    0,   53,   54,    0,    0,    0,
   32,   15,   13,   16,   21,    0,    0,   57,    0,    0,
    0,   32,    0,    0,   27,   32,    0,    0,    0,   41,
   40,    0,    0,    0,    0,   60,    0,    0,    0,   25,
    0,   29,
};
final static short yydgoto[] = {                          2,
    3,   16,   10,   11,   21,   12,   13,   14,   66,   58,
   35,   68,   95,   96,   70,  135,   61,   28,   29,   30,
   31,   32,   62,  123,   84,   74,  103,
};
final static short yysindex[] = {                      -215,
    0,    0, -135,   -2,    0, -213, -194,    0,    0,    0,
 -230,    0,    0,    0,    0,  -92,    6, -196,    0, -173,
   -8,   49,  -86,   74,   59,   80,  115,    0,   65,   98,
  107,  108,   77,  -87,  -83,  138,    0,  -71,    0,  -98,
  -98,  -98,    0, -169,  -98,    0,    0,    0,    0, -156,
  143,    0, -140,    0,    9,    0,    0,    0,  172,   33,
   11,  147,  148,  155,  160,  -27,    0, -155,  -54,    2,
  -98,  -98,  -98,    0,  -98,  -98,  -98,  -98,  -75,    0,
    0,    0,    0,  -98,    0,    0,    0,  -59, -156,  153,
  157,    0,    0,    0,    0, -124,    0, -140,    0,  172,
   40,  172,   63,   13,   13,    0,    0,  -98,  172,  -84,
    0,    0,    0,    0,    0,  -39, -129,    0,  -98,  129,
  172,    0,  -43,  -76,    0,    0,  172, -156,  -62,    0,
    0, -105,   -7,  182,  -42,    0,  -98,  164,  166,    0,
  167,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  -40,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  -38,    0,   68,    0,  -41,    0,    0,    0,  169,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   84,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -12,
    0,   90,    0,  -29,  -21,    0,    0,    0,  188,  -33,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -34,
   49,    0,    0,    0,    0,    0,  103,    0,  -32,    0,
    0,  -36,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,
};
final static short yygindex[] = {                         0,
  134,  -16,  -28,  -19,  200,    0,    0,    0,  109,  -15,
    0,    0,    0,    0,    0,    0,   21,    0,    0,    0,
    0,    0,  190,    0,    0,    0,    0,
};
final static int YYTABLESIZE=293;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         58,
   58,   58,   58,   58,   41,   58,   61,   61,   61,   61,
   61,   51,   61,   51,   51,   51,   89,   58,   58,   52,
   58,   52,   52,   52,   61,   61,   19,   61,   55,   51,
   51,   55,   51,   69,   67,   38,   89,   52,   52,   94,
   52,    1,   99,   17,   20,   98,   55,   55,   73,   55,
   37,   58,   77,   75,   77,   76,   15,   78,   61,   78,
   59,   60,   18,   51,   64,   90,   33,  115,  110,   71,
   83,   52,   82,  112,   77,   75,   34,   76,  116,   78,
   55,   77,   75,   36,   76,  136,   78,   55,   56,   57,
   63,  100,  101,  102,  124,  104,  105,  106,  107,   72,
    4,   56,   57,  120,  109,  129,  119,   39,   28,  132,
   91,   28,   67,   42,    6,   92,   93,   43,    7,   44,
    4,    8,    9,   46,   64,   79,    4,   64,  121,    5,
   62,    4,  118,   62,    6,  126,    8,    9,    7,  127,
    6,    8,    9,   63,    7,    6,   63,    8,    9,    7,
   22,   23,    8,    9,   45,   24,   47,  139,   55,   56,
   57,   26,   27,   22,   23,   48,   49,   50,   24,   19,
  134,   22,   23,   25,   26,   27,   24,   53,  122,   22,
   23,   52,   26,   27,   24,   54,   38,   85,   86,  131,
   26,   27,   40,   22,   23,   87,   77,   75,   24,   76,
   88,   78,   97,  108,   26,   27,  141,   77,   75,  111,
   76,  113,   78,   77,   75,  114,   76,  125,   78,  128,
  130,  137,  140,  138,   18,  142,   17,   38,   46,   30,
   43,   42,  117,   51,   65,    0,  133,    0,    0,   58,
   58,    0,    0,    0,    0,    0,   61,   61,    0,    0,
    0,   51,   51,    0,    0,    0,    0,    0,    0,   52,
   52,    0,    0,    0,    0,    0,    0,    0,   55,   55,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   80,   81,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   44,   45,   91,   47,   41,   42,   43,   44,
   45,   41,   47,   43,   44,   45,   44,   59,   60,   41,
   62,   43,   44,   45,   59,   60,  257,   62,   41,   59,
   60,   44,   62,   53,   50,   44,   44,   59,   60,   68,
   62,  257,   41,  257,  275,   44,   59,   60,   40,   62,
   59,   93,   42,   43,   42,   45,   59,   47,   93,   47,
   40,   41,  257,   93,   44,   93,   61,   96,   85,   61,
   60,   93,   62,   89,   42,   43,  273,   45,   98,   47,
   93,   42,   43,  257,   45,   93,   47,  257,  258,  259,
  260,   71,   72,   73,  111,   75,   76,   77,   78,   91,
  256,  258,  259,   41,   84,  122,   44,   59,   41,  126,
  266,   44,  128,   40,  270,  271,  272,   59,  274,   40,
  256,  277,  278,   59,   41,   93,  256,   44,  108,  265,
   41,  256,   93,   44,  270,  265,  277,  278,  274,  119,
  270,  277,  278,   41,  274,  270,   44,  277,  278,  274,
  256,  257,  277,  278,   40,  261,   59,  137,  257,  258,
  259,  267,  268,  256,  257,   59,   59,   91,  261,  257,
  276,  256,  257,  266,  267,  268,  261,   40,  263,  256,
  257,  265,  267,  268,  261,  257,   44,   41,   41,  266,
  267,  268,  279,  256,  257,   41,   42,   43,  261,   45,
   41,   47,  257,  279,  267,  268,   41,   42,   43,  269,
   45,   59,   47,   42,   43,   59,   45,  257,   47,   91,
  264,   40,   59,  266,  265,   59,  265,   59,   41,  266,
  264,  264,   99,   34,   45,   -1,  128,   -1,   -1,  281,
  282,   -1,   -1,   -1,   -1,   -1,  281,  282,   -1,   -1,
   -1,  281,  282,   -1,   -1,   -1,   -1,   -1,   -1,  281,
  282,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  281,  282,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  281,  282,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=284;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"ID","CTE_USHORT","CTE_DOUBLEF","CADENA",
"IF","THEN","ELSE","END_IF","BEGIN","END","POUT","WHILE","REPEAT","TYPEDEF",
"PRIVATE","FRIEND","EXTENDS","CLASS","FUNCTION","RET","USHORTINT","DOUBLEF",
"ASIGNACION","ASIG_EXPR","COMP_IGUAL","COMP_DIST","CORCHETE_IZQ","CORCHETE_DER",
};
final static String yyrule[] = {
"$accept : programa",
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
"lista_variables : ID",
"lista_variables : lista_variables ',' ID",
"declaracion_typedef : TYPEDEF ID '=' '[' lista_valores ']' ';'",
"lista_valores : constante",
"lista_valores : lista_valores ',' constante",
"declaracion_clase : CLASS ID herencia_opcional BEGIN cuerpo_clase END ';'",
"herencia_opcional : EXTENDS lista_variables",
"herencia_opcional :",
"cuerpo_clase : cuerpo_clase miembro_clase",
"cuerpo_clase :",
"miembro_clase : visibilidad declaracion",
"miembro_clase : declaracion",
"visibilidad : PRIVATE",
"visibilidad : FRIEND",
"declaracion_funcion : type_specifier FUNCTION ID '(' parametros ')' bloque_declarativo BEGIN bloque_ejecutable retorno_opcional END ';'",
"parametros : type_specifier ID",
"parametros : parametros ',' type_specifier ID",
"parametros :",
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
"asignacion : ID '[' expresion ']' ASIGNACION expresion",
"sentencia_while : WHILE '(' condicion ')' REPEAT bloque_ejecutable END",
"sentencia_if : IF '(' condicion ')' bloque_ejecutable rama_else END_IF",
"rama_else : ELSE bloque_ejecutable",
"rama_else :",
"salida_pout : POUT '(' CADENA ')'",
"salida_pout : POUT '(' expresion ')'",
"condicion : expresion comparador expresion",
"comparador : COMP_IGUAL",
"comparador : COMP_DIST",
"comparador : '>'",
"comparador : '<'",
"expresion : expresion '+' expresion",
"expresion : expresion '-' expresion",
"expresion : expresion '*' expresion",
"expresion : expresion '/' expresion",
"expresion : ID '=' expresion",
"expresion : ID invocacion_ordenada",
"expresion : ID '[' expresion ']'",
"expresion : ID",
"expresion : constante",
"invocacion_ordenada : '(' lista_expresiones ')' '[' lista_valores ']'",
"invocacion_ordenada : '(' lista_expresiones ')'",
"lista_expresiones : expresion",
"lista_expresiones : lista_expresiones ',' expresion",
"lista_expresiones :",
"constante : CTE_USHORT",
"constante : CTE_DOUBLEF",
};

//#line 184 "gramatica.y"

/* CÓDIGO JAVA DE SOPORTE */

private AnalizadorLexico lexico;
private TablaDeSimbolos ts;
private boolean huboError = false;

public Parser(AnalizadorLexico lexico, TablaDeSimbolos ts) {
    this.lexico = lexico;
    this.ts = ts;
}

private int yylex() {
    return lexico.yylex();
}

public void yyerror(String msg) {
    this.huboError = true;
    System.err.println("Error sintactico en línea " + lexico.getNroLinea() + ": " + msg);
}

public boolean huboError() {
    return huboError;
}
//#line 388 "Parser.java"
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
//#line 25 "gramatica.y"
{ System.out.println("Programa estructurado correctamente."); }
break;
case 8:
//#line 38 "gramatica.y"
{ System.out.println("Error sintáctico en declaración: resincronizado en ';'"); }
break;
case 13:
//#line 54 "gramatica.y"
{ System.out.println("Línea " + lexico.getNroLinea() + ": Enumeración (typedef) declarada"); }
break;
case 16:
//#line 65 "gramatica.y"
{ System.out.println("Línea " + lexico.getNroLinea() + ": Declaración de Clase reconocida"); }
break;
case 37:
//#line 113 "gramatica.y"
{ System.out.println("Error sintáctico en sentencia ejecutable: resincronizado en ';'"); }
break;
case 39:
//#line 120 "gramatica.y"
{ System.out.println("Linea " + lexico.getNroLinea() + ": Asignacion a atributo opcional"); }
break;
case 40:
//#line 126 "gramatica.y"
{ System.out.println("Linea " + lexico.getNroLinea() + ": Sentencia WHILE-REPEAT reconocida"); }
break;
//#line 565 "Parser.java"
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
