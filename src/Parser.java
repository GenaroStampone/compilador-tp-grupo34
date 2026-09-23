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






//#line 2 "../gramatica.y"
import java.util.ArrayList;
import java.util.List;
//#line 21 "Parser.java"




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
public final static short CTE_ENTERA=258;
public final static short DOUBLEF_VAL=259;
public final static short CADENA=260;
public final static short MAYOR_IGUAL=261;
public final static short MENOR_IGUAL=262;
public final static short IGUAL_IGUAL=263;
public final static short DISTINTO=264;
public final static short ASIGNACION=265;
public final static short IF=270;
public final static short ELSE=271;
public final static short END_IF=272;
public final static short BEGIN=273;
public final static short END=274;
public final static short POUT=275;
public final static short RET=276;
public final static short CLASS=277;
public final static short FUNCTION=278;
public final static short USHORTINT=279;
public final static short DOUBLEF=280;
public final static short THEN=281;
public final static short WHILE=282;
public final static short REPEAT=283;
public final static short TYPEDEF=284;
public final static short PRIVATE=285;
public final static short FRIEND=286;
public final static short EXTENDS=287;
public final static short TODF=288;
public final static short TOUS=289;
public final static short LOWER_THAN_ELSE=266;
public final static short UMINUS=267;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    1,    1,    3,    2,    2,    6,    6,
    6,    6,    6,    4,    4,    4,    4,    4,    4,    7,
   14,   14,   14,   12,   12,   13,   13,    8,   15,   15,
   16,   16,   16,    9,   17,   17,   18,   18,   18,   18,
   19,   19,   10,   10,   10,   11,   11,   11,   20,   20,
   21,   21,   22,    5,    5,   23,   23,   23,   23,   23,
   23,   23,   23,   30,   24,   24,   25,   31,   31,   31,
   31,   26,   26,   35,   35,   27,   28,   28,   28,   29,
   29,   34,   34,   36,   36,   36,   36,   36,   36,   32,
   32,   32,   32,   32,   32,   32,   32,   32,   32,   38,
   38,   38,   38,   38,   38,   33,   33,   33,   33,   39,
   39,   40,   40,   37,   37,
};
final static short yylen[] = {                            2,
    5,    4,    2,    1,    0,    3,    0,    2,    2,    2,
    2,    2,    2,    2,    2,    2,    3,    2,    2,    2,
    1,    1,    1,    1,    1,    1,    3,    6,    1,    3,
    1,    1,    2,    5,    0,    2,    1,    2,    3,    3,
    1,    3,   10,   10,    9,   10,   10,    9,    0,    1,
    1,    3,    2,    0,    2,    2,    2,    1,    1,    2,
    2,    1,    2,    4,    3,    3,    1,    1,    3,    4,
    6,    8,   10,    1,    0,    6,    4,    4,    2,    4,
    1,    3,    1,    1,    1,    1,    1,    1,    1,    3,
    3,    3,    3,    2,    3,    3,    1,    1,    1,    1,
    3,    4,    6,    1,    1,    5,    4,    7,    6,    1,
    3,    3,    0,    4,    4,
};
final static short yydefred[] = {                         0,
    0,    0,   54,    0,    0,   24,   25,    0,    0,    0,
    0,    7,    0,    0,    0,    0,   19,    4,    7,    0,
    0,   23,   21,   22,    0,    0,    0,    0,   18,    0,
    0,    0,    0,    0,    3,    0,   14,   15,   16,    0,
    0,    0,    0,    0,    0,    0,   54,    6,    0,    0,
    0,   55,    0,    0,   58,   59,    0,    0,   62,    0,
   67,   35,    0,    0,   13,    0,    9,   10,   11,   12,
    0,    0,    0,    0,    8,    0,    0,   17,    0,    0,
   63,    0,    0,    0,    0,    0,   79,    0,    0,    0,
   56,   57,   60,   61,    0,    0,    0,    0,    0,    0,
    0,    0,    2,    0,    0,    0,   51,    0,   27,    1,
    0,  104,  105,    0,    0,    0,    0,    0,   97,   98,
   99,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   34,    0,    0,    0,   37,   36,
    0,   31,   32,    0,    0,   29,    0,    0,    0,   53,
    7,    0,    0,    0,    0,    0,    0,    0,   94,    0,
    0,    0,    0,    0,   70,    0,  107,    0,    0,    0,
    0,   84,   85,   86,   87,   89,   88,    0,    0,   64,
   77,   78,   80,    0,   38,    0,   41,    0,    7,   33,
    0,   28,    0,    7,    0,    0,   52,    7,    0,    0,
    0,    0,    0,   96,    0,    0,   92,   93,    0,    0,
  106,    0,    0,    0,    0,   74,   54,    0,   39,   40,
    0,    0,   30,    7,    0,    7,   54,    0,  102,    0,
  114,  115,  112,   71,  109,    0,    0,   76,   42,   54,
    0,   54,    0,    0,   54,    0,  108,   54,    0,    0,
   54,    0,   54,   48,    0,  103,    0,   72,   47,    0,
   45,    0,   46,    0,   44,   43,   73,
};
final static short yydgoto[] = {                         10,
   19,   36,   11,   12,   20,   75,   30,   31,   32,   33,
   15,   16,   42,   34,  145,  146,   97,  140,  188,  105,
  106,  107,   52,   53,   54,   55,   56,   57,   58,   59,
   60,  123,  119,  127,  217,  178,  120,  121,  124,  167,
};
final static short yysindex[] = {                       149,
  -52,  -48,    0, -244, -227,    0,    0, -242,  242,    0,
  -48,    0,  -18,  -48,  -48, -230,    0,    0,    0,  288,
 -257,    0,    0,    0, -206,   26,   39, -227,    0,   56,
   57,  -48,  -48, -224,    0,  -91,    0,    0,    0,   19,
 -128,  -19,  -91,   83,  -14,  108,    0,    0,  -36,  115,
  138,    0,  113,  131,    0,    0,  137,  141,    0,  -33,
    0,    0,  144,   41,    0,  -66,    0,    0,    0,    0,
  158,  -56,  159,  -48,    0, -227,  164,    0,  -51,  -48,
    0,  -28,  -40,  -50,  -28,  299,    0,  -37,  -28,  -28,
    0,    0,    0,    0,  -28,  -28,  103, -227,  -43,  169,
 -227,  172,    0,  -20,  173,  181,    0, -227,    0,    0,
   73,    0,    0,  194,  202,  -28,  -28,   13,    0,    0,
    0,  152,  132,  119,   -6,   58,  203,  -48,  204,  282,
  310,  205,  132,  132,    0,  242,  -10,   -7,    0,    0,
  215,    0,    0,   -5,   44,    0, -227,  218, -227,    0,
    0, -227,  226,  -28,  -28,   12,  -28,  -28,    0,  495,
  -28,  -28,  -28,  -28,    0,  -43,    0,  -28,  152,  -28,
  -31,    0,    0,    0,    0,    0,    0,  -28,   -9,    0,
    0,    0,    0,  -12,    0,  217,    0,   -2,    0,    0,
  -43,    0,  233,    0,  236,  162,    0,    0,  132,   80,
   23,  619,  626,    0,   84,   84,    0,    0,   68,  132,
    0,  102,  152,  126,  132,    0,    0,  383,    0,    0,
   22,  180,    0,    0,  188,    0,    0,  198,    0,  -28,
    0,    0,    0,    0,    0,  152,  257,    0,    0,    0,
  213,    0,  223,  309,    0,  109,    0,    0,  222,  320,
    0,  330,    0,    0,  341,    0,  352,    0,    0,  362,
    0,  373,    0,  224,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,  231,    0,    0,    0,    0,    0,    0,    0,    0,
  284,    0,    0,  231,  231,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   88,   88,    0,    0,    0,    0,    0,    0,   50,
    0,    0,    0,    0,  -32,    0,    0,    0,    0,   83,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   50,    0,  230,  284,    0,  241,    0,    0,    0,  284,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  241,    0,    0,
  241,    0,    0,    0,    0,  249,    0,  241,    0,    0,
  -23,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    2,  127,    0,  -30,  250,    0,  122,    0,    0,
    0,    0,   39,  237,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  241,    0,  241,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    2,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  278,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   48,    0,
   24,    0,    0,    0,   31,   37,    0,    0,    0,  139,
    0,    0,    2,    0,  256,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    2,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  125,  -13,   92,    0,   86,   14,    0,  302,  304,    0,
    0,    0,  271,   45,  140,  116,    0,    0,    0,   61,
    0,  156,   96,    0,    0,    0,    0,    0,    0,    0,
    0,  253,   18,  225,    0,    0,    0,    0,  145, -134,
};
final static int YYTABLESIZE=673;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        117,
  122,  144,  117,   88,  116,   43,   17,  116,  117,  213,
   18,  117,   21,  116,   26,   62,  116,  100,  100,  100,
  100,  100,   29,  100,   79,   83,   40,   96,   68,   22,
   69,   84,   71,  171,  211,  100,  100,   61,  100,   78,
   37,  221,  113,  113,  113,  113,  113,   41,  113,   25,
   63,   23,   24,   72,  163,  161,  220,  162,   76,  164,
  113,  113,  171,  113,  101,  101,  101,  101,  101,  100,
  101,   90,   66,   90,   90,   90,   82,   91,  235,   91,
   91,   91,  101,  101,  170,  101,   64,  191,   95,   90,
   90,   95,   90,   26,  113,   91,   91,   65,   91,  163,
  161,  247,  162,   61,  164,  165,   95,   95,   26,   95,
  139,  191,   83,  230,   67,   68,  101,  176,  156,  177,
  104,  163,  161,   90,  162,  163,  164,   74,   77,   91,
  164,   99,   86,  154,   80,   35,  192,  196,   38,   39,
   95,   81,  104,  163,  161,  104,  162,   85,  164,  185,
  163,  161,  104,  162,   89,  164,   69,   70,  141,  169,
  233,  148,  168,  155,   27,   22,  236,  110,  153,  168,
  110,   91,  229,  163,  161,  222,  162,   90,  164,  111,
  225,    3,  111,   98,  228,    4,   28,   23,   24,   92,
  100,  104,    8,  104,  234,   93,  104,  101,  103,   94,
  102,  256,   79,  108,  110,  109,  125,  193,  147,  195,
  241,  149,  243,  151,  142,  143,  111,  112,  113,  111,
  112,  113,  129,   87,  152,  111,  112,  113,  111,  112,
  113,   95,   68,  157,   69,   61,  150,  100,  100,  100,
  100,  158,  166,  179,  181,  184,  186,  114,  115,  187,
  114,  115,  180,  190,   61,  189,  114,  115,  194,  114,
  115,   61,  113,  113,  113,  113,  198,   61,  201,   61,
  218,  216,   61,  224,   61,  219,  226,   61,  239,   61,
  258,   49,  267,    5,  101,  101,  101,  101,   20,   50,
   83,   90,   90,   90,   90,   66,   82,   91,   91,   91,
   91,   13,  237,   14,   73,  209,  223,  197,   95,   95,
   95,   95,  244,  238,  132,  214,    0,    0,  172,  173,
  174,  175,  182,  163,  161,  250,  162,  252,  164,    0,
  255,    0,    0,  257,  118,    0,  260,  126,  262,    0,
  130,  131,  126,    5,    5,    0,    0,  133,  134,    0,
  183,  163,  161,    0,  162,    0,  164,    0,   27,   22,
    5,    5,    0,    0,    5,    5,    5,    5,  159,  160,
    0,    5,    5,    5,    5,    0,  135,    5,    5,    4,
   28,   23,   24,    0,    0,    0,    8,  136,  137,  138,
    0,    5,    5,    5,    5,    5,    5,    5,    0,    0,
    0,    0,    0,    5,    1,    2,  199,  200,    0,  202,
  203,    0,    0,  205,  206,  207,  208,   27,   22,    0,
  210,    3,  212,    0,    0,    4,    5,    6,    7,    0,
  215,    0,    8,    9,  227,   27,   22,    0,    4,   28,
   23,   24,    0,   27,   22,    8,    0,    0,    0,    0,
    0,    0,  240,   27,   22,    0,    4,   28,   23,   24,
  242,    0,    0,    8,    4,   28,   23,   24,   27,   22,
  245,    8,    0,    0,    4,   28,   23,   24,   27,   22,
    0,    8,  246,    0,    0,  251,    5,    5,    0,    4,
   28,   23,   24,    0,    0,  253,    8,   27,   22,    4,
   28,   23,   24,    5,    0,    0,    8,    5,    5,    5,
    5,    0,   44,   45,    5,    0,    0,    0,    4,   28,
   23,   24,    0,    0,    0,    8,   46,  248,  249,   47,
    0,   49,   50,   75,   75,  204,  163,  161,   51,  162,
    0,  164,    0,   44,   45,    0,    0,   75,   75,   75,
   75,    0,   75,   75,   44,   45,    0,   46,    0,   75,
   47,   48,   49,   50,   44,   45,    0,    0,   46,   51,
    0,   47,  128,   49,   50,   44,   45,    0,   46,    0,
   51,   47,  254,   49,   50,   44,   45,    0,    0,   46,
   51,    0,   47,  259,   49,   50,   44,   45,    0,   46,
    0,   51,   47,  261,   49,   50,    0,   44,   45,    0,
   46,   51,    0,   47,  263,   49,   50,   44,   45,    0,
    0,   46,   51,  264,   47,    0,   49,   50,   44,   45,
    0,   46,    0,   51,   47,  265,   49,   50,   44,   45,
    0,    0,   46,   51,    0,   47,  266,   49,   50,    0,
    0,    0,   46,    0,   51,   47,    0,   49,   50,  231,
  163,  161,    0,  162,   51,  164,  232,  163,  161,    0,
  162,    0,  164,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
   41,   45,   40,   40,   45,   19,   59,   45,   40,   41,
   59,   40,  257,   45,  257,  273,   45,   41,   42,   43,
   44,   45,    9,   47,   44,   40,  257,   61,   61,  257,
   61,   46,  257,   40,  169,   59,   60,   20,   62,   59,
   59,   44,   41,   42,   43,   44,   45,  278,   47,    5,
  257,  279,  280,  278,   42,   43,   59,   45,   40,   47,
   59,   60,   40,   62,   41,   42,   43,   44,   45,   93,
   47,   41,   28,   43,   44,   45,   91,   41,  213,   43,
   44,   45,   59,   60,   91,   62,   61,   44,   41,   59,
   60,   44,   62,   44,   93,   59,   60,   59,   62,   42,
   43,  236,   45,   86,   47,   93,   59,   60,   59,   62,
   97,   44,   40,   91,   59,   59,   93,   60,   46,   62,
   76,   42,   43,   93,   45,   42,   47,   36,  257,   93,
   47,   91,   47,   61,   43,   11,   93,  151,   14,   15,
   93,   59,   98,   42,   43,  101,   45,   40,   47,  136,
   42,   43,  108,   45,   40,   47,   32,   33,   98,   41,
   93,  101,   44,   91,  256,  257,   41,   41,  108,   44,
   44,   59,   93,   42,   43,  189,   45,   40,   47,   41,
  194,  273,   44,   40,  198,  277,  278,  279,  280,   59,
  257,  147,  284,  149,   93,   59,  152,   40,   74,   59,
  257,   93,   44,   40,   80,  257,  257,  147,   40,  149,
  224,   40,  226,   41,  258,  259,  257,  258,  259,  257,
  258,  259,  260,  260,   44,  257,  258,  259,  257,  258,
  259,  265,  265,   40,  265,  218,  257,  261,  262,  263,
  264,   40,   91,   41,   41,   41,  257,  288,  289,  257,
  288,  289,  128,  259,  237,   41,  288,  289,   41,  288,
  289,  244,  261,  262,  263,  264,   41,  250,  257,  252,
  283,  281,  255,   41,  257,   59,   41,  260,  257,  262,
   59,   41,   59,    0,  261,  262,  263,  264,   59,   41,
   41,  261,  262,  263,  264,   59,   41,  261,  262,  263,
  264,    0,  217,    0,   34,  166,  191,  152,  261,  262,
  263,  264,  227,  218,   90,  171,   -1,   -1,  261,  262,
  263,  264,   41,   42,   43,  240,   45,  242,   47,   -1,
  245,   -1,   -1,  248,   82,   -1,  251,   85,  253,   -1,
   88,   89,   90,  256,  257,   -1,   -1,   95,   96,   -1,
   41,   42,   43,   -1,   45,   -1,   47,   -1,  256,  257,
  273,  274,   -1,   -1,  277,  278,  279,  280,  116,  117,
   -1,  284,  285,  286,  287,   -1,  274,  256,  257,  277,
  278,  279,  280,   -1,   -1,   -1,  284,  285,  286,  287,
   -1,  270,  271,  272,  273,  274,  275,  276,   -1,   -1,
   -1,   -1,   -1,  282,  256,  257,  154,  155,   -1,  157,
  158,   -1,   -1,  161,  162,  163,  164,  256,  257,   -1,
  168,  273,  170,   -1,   -1,  277,  278,  279,  280,   -1,
  178,   -1,  284,  285,  273,  256,  257,   -1,  277,  278,
  279,  280,   -1,  256,  257,  284,   -1,   -1,   -1,   -1,
   -1,   -1,  273,  256,  257,   -1,  277,  278,  279,  280,
  273,   -1,   -1,  284,  277,  278,  279,  280,  256,  257,
  273,  284,   -1,   -1,  277,  278,  279,  280,  256,  257,
   -1,  284,  230,   -1,   -1,  273,  256,  257,   -1,  277,
  278,  279,  280,   -1,   -1,  273,  284,  256,  257,  277,
  278,  279,  280,  273,   -1,   -1,  284,  277,  278,  279,
  280,   -1,  256,  257,  284,   -1,   -1,   -1,  277,  278,
  279,  280,   -1,   -1,   -1,  284,  270,  271,  272,  273,
   -1,  275,  276,  256,  257,   41,   42,   43,  282,   45,
   -1,   47,   -1,  256,  257,   -1,   -1,  270,  271,  272,
  273,   -1,  275,  276,  256,  257,   -1,  270,   -1,  282,
  273,  274,  275,  276,  256,  257,   -1,   -1,  270,  282,
   -1,  273,  274,  275,  276,  256,  257,   -1,  270,   -1,
  282,  273,  274,  275,  276,  256,  257,   -1,   -1,  270,
  282,   -1,  273,  274,  275,  276,  256,  257,   -1,  270,
   -1,  282,  273,  274,  275,  276,   -1,  256,  257,   -1,
  270,  282,   -1,  273,  274,  275,  276,  256,  257,   -1,
   -1,  270,  282,  272,  273,   -1,  275,  276,  256,  257,
   -1,  270,   -1,  282,  273,  274,  275,  276,  256,  257,
   -1,   -1,  270,  282,   -1,  273,  274,  275,  276,   -1,
   -1,   -1,  270,   -1,  282,  273,   -1,  275,  276,   41,
   42,   43,   -1,   45,  282,   47,   41,   42,   43,   -1,
   45,   -1,   47,
};
}
final static short YYFINAL=10;
final static short YYMAXTOKEN=289;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
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
null,null,null,null,null,null,null,"ID","CTE_ENTERA","DOUBLEF_VAL","CADENA",
"MAYOR_IGUAL","MENOR_IGUAL","IGUAL_IGUAL","DISTINTO","ASIGNACION",
"LOWER_THAN_ELSE","UMINUS",null,null,"IF","ELSE","END_IF","BEGIN","END","POUT",
"RET","CLASS","FUNCTION","USHORTINT","DOUBLEF","THEN","WHILE","REPEAT",
"TYPEDEF","PRIVATE","FRIEND","EXTENDS","TODF","TOUS",
};
final static String yyrule[] = {
"$accept : programa",
"programa : ID opt_punto_y_coma sentencias_declarativas bloque_principal opt_punto_y_coma",
"programa : primera_declaracion_sin_id sentencias_declarativas bloque_principal opt_punto_y_coma",
"programa : bloque_principal opt_punto_y_coma",
"opt_punto_y_coma : ';'",
"opt_punto_y_coma :",
"bloque_principal : BEGIN sentencias_ejecutables END",
"sentencias_declarativas :",
"sentencias_declarativas : sentencias_declarativas sentencia_declarativa",
"sentencia_declarativa : declaracion_variables ';'",
"sentencia_declarativa : declaracion_tipo ';'",
"sentencia_declarativa : declaracion_clase opt_punto_y_coma",
"sentencia_declarativa : declaracion_funcion opt_punto_y_coma",
"sentencia_declarativa : error ';'",
"primera_declaracion_sin_id : declaracion_tipo ';'",
"primera_declaracion_sin_id : declaracion_clase opt_punto_y_coma",
"primera_declaracion_sin_id : declaracion_funcion_sin_id opt_punto_y_coma",
"primera_declaracion_sin_id : tipo_basico lista_variables ';'",
"primera_declaracion_sin_id : PRIVATE sentencia_declarativa",
"primera_declaracion_sin_id : error ';'",
"declaracion_variables : tipo lista_variables",
"tipo : USHORTINT",
"tipo : DOUBLEF",
"tipo : ID",
"tipo_basico : USHORTINT",
"tipo_basico : DOUBLEF",
"lista_variables : ID",
"lista_variables : lista_variables ',' ID",
"declaracion_tipo : TYPEDEF ID '=' '[' lista_constantes ']'",
"lista_constantes : constante",
"lista_constantes : lista_constantes ',' constante",
"constante : CTE_ENTERA",
"constante : DOUBLEF_VAL",
"constante : '-' DOUBLEF_VAL",
"declaracion_clase : CLASS ID BEGIN miembros_clase END",
"miembros_clase :",
"miembros_clase : miembros_clase miembro_clase",
"miembro_clase : sentencia_declarativa",
"miembro_clase : PRIVATE sentencia_declarativa",
"miembro_clase : FRIEND ID ';'",
"miembro_clase : EXTENDS lista_ids ';'",
"lista_ids : ID",
"lista_ids : lista_ids ',' ID",
"declaracion_funcion : tipo FUNCTION ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END",
"declaracion_funcion : FUNCTION tipo ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END",
"declaracion_funcion : tipo ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END",
"declaracion_funcion_sin_id : tipo_basico FUNCTION ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END",
"declaracion_funcion_sin_id : FUNCTION tipo ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END",
"declaracion_funcion_sin_id : tipo_basico ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END",
"lista_parametros :",
"lista_parametros : lista_parametros_no_vacia",
"lista_parametros_no_vacia : parametro",
"lista_parametros_no_vacia : lista_parametros_no_vacia ',' parametro",
"parametro : tipo ID",
"sentencias_ejecutables :",
"sentencias_ejecutables : sentencias_ejecutables sentencia_ejecutable",
"sentencia_ejecutable : sentencia_asignacion ';'",
"sentencia_ejecutable : llamada_funcion ';'",
"sentencia_ejecutable : sentencia_if",
"sentencia_ejecutable : sentencia_while",
"sentencia_ejecutable : sentencia_pout ';'",
"sentencia_ejecutable : sentencia_ret ';'",
"sentencia_ejecutable : bloque_compuesto",
"sentencia_ejecutable : error ';'",
"bloque_compuesto : BEGIN sentencias_ejecutables END opt_punto_y_coma",
"sentencia_asignacion : lado_izquierdo ASIGNACION expresion",
"sentencia_asignacion : lado_izquierdo '=' expresion",
"llamada_funcion : invocacion_funcion",
"lado_izquierdo : ID",
"lado_izquierdo : ID '.' ID",
"lado_izquierdo : ID '[' expresion ']'",
"lado_izquierdo : ID '.' ID '[' expresion ']'",
"sentencia_if : IF '(' condicion ')' opt_then sentencias_ejecutables END_IF ';'",
"sentencia_if : IF '(' condicion ')' opt_then sentencias_ejecutables ELSE sentencias_ejecutables END_IF ';'",
"opt_then : THEN",
"opt_then :",
"sentencia_while : WHILE '(' condicion ')' REPEAT sentencia_ejecutable",
"sentencia_pout : POUT '(' CADENA ')'",
"sentencia_pout : POUT '(' expresion ')'",
"sentencia_pout : POUT CADENA",
"sentencia_ret : RET '(' expresion ')'",
"sentencia_ret : RET",
"condicion : expresion comparador expresion",
"condicion : expresion",
"comparador : MAYOR_IGUAL",
"comparador : MENOR_IGUAL",
"comparador : IGUAL_IGUAL",
"comparador : DISTINTO",
"comparador : '>'",
"comparador : '<'",
"expresion : expresion '+' expresion",
"expresion : expresion '-' expresion",
"expresion : expresion '*' expresion",
"expresion : expresion '/' expresion",
"expresion : '-' expresion",
"expresion : ID '=' expresion",
"expresion : '(' expresion ')'",
"expresion : invocacion_funcion",
"expresion : conversion_tipo",
"expresion : elemento_primario",
"elemento_primario : ID",
"elemento_primario : ID '.' ID",
"elemento_primario : ID '[' expresion ']'",
"elemento_primario : ID '.' ID '[' expresion ']'",
"elemento_primario : CTE_ENTERA",
"elemento_primario : DOUBLEF_VAL",
"invocacion_funcion : ID '(' lista_argumentos ')' opt_orden_evaluacion",
"invocacion_funcion : ID '(' ')' opt_orden_evaluacion",
"invocacion_funcion : ID '.' ID '(' lista_argumentos ')' opt_orden_evaluacion",
"invocacion_funcion : ID '.' ID '(' ')' opt_orden_evaluacion",
"lista_argumentos : expresion",
"lista_argumentos : lista_argumentos ',' expresion",
"opt_orden_evaluacion : '[' lista_constantes ']'",
"opt_orden_evaluacion :",
"conversion_tipo : TODF '(' expresion ')'",
"conversion_tipo : TOUS '(' expresion ')'",
};

//#line 420 "../gramatica.y"

/* ========================================================================= */
/*                         CÓDIGO DE INTERFAZ DEL PARSER                     */
/* ========================================================================= */

private AnalizadorLexico lexico;
private TablaDeSimbolos tablaDeSimbolos;
private boolean huboError = false;
private List<String> estructurasDetectadas = new ArrayList<>();
private List<String> erroresSintacticos = new ArrayList<>();

public Parser(AnalizadorLexico lexico, TablaDeSimbolos tablaDeSimbolos) {
    this.lexico = lexico;
    this.tablaDeSimbolos = tablaDeSimbolos;
}

public Parser(AnalizadorLexico lexico) {
    this(lexico, null);
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

public void yyerror(String s) {
    huboError = true;
    String err = "Línea " + lexico.getYylineno() + ": Error Sintáctico: " + s;
    erroresSintacticos.add(err);
    System.err.println(err);
}

public void registrarEstructura(String s) {
    String msg = "Línea " + lexico.getYylineno() + ": Estructura sintáctica detectada -> " + s;
    estructurasDetectadas.add(msg);
    System.out.println(msg);
}

public boolean huboError() {
    return huboError;
}

public List<String> getEstructurasDetectadas() {
    return estructurasDetectadas;
}

public List<String> getErroresSintacticos() {
    return erroresSintacticos;
}
//#line 597 "Parser.java"
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
//#line 57 "../gramatica.y"
{
            registrarEstructura("Programa completo con nombre '" + val_peek(4).sval + "'");
        }
break;
case 2:
//#line 61 "../gramatica.y"
{
            registrarEstructura("Programa completo (sin nombre explícito)");
        }
break;
case 3:
//#line 65 "../gramatica.y"
{
            registrarEstructura("Programa completo (solo bloque principal)");
        }
break;
case 6:
//#line 77 "../gramatica.y"
{
            registrarEstructura("Bloque principal (BEGIN ... END)");
        }
break;
case 9:
//#line 93 "../gramatica.y"
{
            registrarEstructura("Declaración de variable(s)");
        }
break;
case 10:
//#line 97 "../gramatica.y"
{
            registrarEstructura("Declaración de tipo enumerado (TYPEDEF)");
        }
break;
case 11:
//#line 101 "../gramatica.y"
{
            registrarEstructura("Declaración de clase");
        }
break;
case 12:
//#line 105 "../gramatica.y"
{
            registrarEstructura("Declaración de función/método");
        }
break;
case 13:
//#line 109 "../gramatica.y"
{
            yyerror("Declaración no válida. Resincronización exitosa en ';'");
        }
break;
case 14:
//#line 117 "../gramatica.y"
{
            registrarEstructura("Declaración de tipo enumerado (TYPEDEF)");
        }
break;
case 15:
//#line 121 "../gramatica.y"
{
            registrarEstructura("Declaración de clase");
        }
break;
case 16:
//#line 125 "../gramatica.y"
{
            registrarEstructura("Declaración de función");
        }
break;
case 17:
//#line 129 "../gramatica.y"
{
            registrarEstructura("Declaración de variable(s)");
        }
break;
case 18:
//#line 133 "../gramatica.y"
{
            registrarEstructura("Declaración privada");
        }
break;
case 19:
//#line 137 "../gramatica.y"
{
            yyerror("Declaración inicial no válida. Resincronización exitosa en ';'");
        }
break;
case 33:
//#line 176 "../gramatica.y"
{
            String lexPos = val_peek(0).sval;
            try {
                double val = -Double.parseDouble(lexPos.replace("d", "e").replace("D", "e"));
                if (tablaDeSimbolos != null && !tablaDeSimbolos.verificarRangoDoublef(val)) {
                    yyerror("Constante doublef negativa fuera de rango permitido: -" + lexPos);
                } else if (tablaDeSimbolos != null) {
                    RegistroTS regNeg = tablaDeSimbolos.registrarConstanteNegativa(lexPos, lexico.getYylineno(), val);
                    yyval = new ParserVal(regNeg);
                    registrarEstructura("Detección de constante doublef negativa en enumeración -> " + regNeg.getLexema());
                }
            } catch (Exception e) {
                yyerror("Error procesando constante negativa doublef: " + lexPos);
            }
        }
break;
case 38:
//#line 206 "../gramatica.y"
{
            registrarEstructura("Miembro privado (PRIVATE)");
        }
break;
case 39:
//#line 210 "../gramatica.y"
{
            registrarEstructura("Declaración de amistad (FRIEND)");
        }
break;
case 40:
//#line 214 "../gramatica.y"
{
            registrarEstructura("Herencia múltiple (EXTENDS)");
        }
break;
case 56:
//#line 262 "../gramatica.y"
{
            registrarEstructura("Asignación");
        }
break;
case 57:
//#line 266 "../gramatica.y"
{
            registrarEstructura("Invocación a función/método");
        }
break;
case 58:
//#line 270 "../gramatica.y"
{
            registrarEstructura("Sentencia de control IF");
        }
break;
case 59:
//#line 274 "../gramatica.y"
{
            registrarEstructura("Sentencia iterativa WHILE-REPEAT");
        }
break;
case 60:
//#line 278 "../gramatica.y"
{
            registrarEstructura("Salida (POUT)");
        }
break;
case 61:
//#line 282 "../gramatica.y"
{
            registrarEstructura("Retorno de función (RET)");
        }
break;
case 63:
//#line 287 "../gramatica.y"
{
            yyerror("Sentencia ejecutable no válida. Resincronización exitosa en ';'");
        }
break;
case 94:
//#line 361 "../gramatica.y"
{
            if (val_peek(0) != null && val_peek(0).obj instanceof RegistroTS) {
                RegistroTS reg = (RegistroTS) val_peek(0).obj;
                if (reg.getTipoToken() == AnalizadorLexico.DOUBLEF_VAL) {
                    String lexPos = reg.getLexema();
                    try {
                        double val = -Double.parseDouble(lexPos.replace("d", "e").replace("D", "e"));
                        if (tablaDeSimbolos != null && !tablaDeSimbolos.verificarRangoDoublef(val)) {
                            yyerror("Constante doublef negativa fuera de rango permitido: -" + lexPos);
                        } else if (tablaDeSimbolos != null) {
                            RegistroTS regNeg = tablaDeSimbolos.registrarConstanteNegativa(lexPos, lexico.getYylineno(), val);
                            yyval = new ParserVal(regNeg);
                            registrarEstructura("Detección de constante doublef negativa -> " + regNeg.getLexema());
                        }
                    } catch (Exception e) {
                        yyerror("Error procesando constante negativa doublef: " + lexPos);
                    }
                }
            }
        }
break;
//#line 937 "Parser.java"
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
