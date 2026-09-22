%{
    package compilador.sintactico;
%}

/* Tokens con referencia a RegistroTS en la TablaDeSimbolos */
%token ID
%token CTE_SHORTINT
%token CTE_DOUBLEF
%token CADENA 

/* Palabras reservadas */
%token IF ELSE END_IF BEGIN END POUT RET CLASS FUNCTION

/* Palabras Reservadas Específicas del Grupo 34 */
%token USHORTINT            /*T2: Enteros de 8 bits sin signo - $us*/
%token DOUBLEF              /*T8: Números de punto flotante de 64 bits $df*/
%token WHILE REPEAT         /* T11: Iteración WHILE-REPEAT */
%token TYPEDEF              /* T23: Enumeraciones */
%token FRIEND               /* T27: Clases amigas */
%token PRIVATE              /* T27: Clases Amigas */
%token EXTENDS              /* T30: Herencia múltiple */

/* Operadores compuestos y comparadores */
%token ASIG                 /* := */                 
%token OP_IGUAL             /* = */
%token OP_DISTINTO          /* != */
%token OP_MAYORIGUAL        /* >= */
%token OP_MENORIGUAL        /* <= */

%start programa

%%

programa
    : /* a completar */
    | programa sentencia
    ;

%%

private AnalizadorLexico lexico;