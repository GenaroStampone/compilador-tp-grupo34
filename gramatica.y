%{
import java.io.*;
%}

/* DECLARACIONES Y CONFIGURACIÓN DE TOKENS */

%token ID CTE_USHORT CTE_DOUBLEF CADENA
%token IF THEN ELSE END_IF BEGIN END POUT
%token WHILE REPEAT TYPEDEF PRIVATE FRIEND EXTENDS CLASS FUNCTION RET
%token USHORTINT DOUBLEF 
%token ASIGNACION ASIG_EXPR COMP_IGUAL COMP_DIST CORCHETE_IZQ CORCHETE_DER

%right '=' ASIGNACION ASIG_EXPR
%left '+' '-'
%left '*' '/'

%start programa

%%

/* REGLAS GRAMATICALES Y ACCIONES SINTÁCTICAS */

programa 
    : ID bloque_declarativo BEGIN bloque_ejecutable END ';'
      { System.out.println("Programa estructurado correctamente."); }
    ;

bloque_declarativo
    : bloque_declarativo declaracion
    | /* vacio */
    ;

declaracion
    : type_specifier lista_variables ';'
    | declaracion_typedef
    | declaracion_clase
    | declaracion_funcion
    | error ';' { System.out.println("Error sintáctico en declaración: resincronizado en ';'"); }
    ;

type_specifier
    : USHORTINT
    | DOUBLEF
    ;

lista_variables
    : ID
    | lista_variables ',' ID
    ;

/* Tema 23: Enumeraciones */
declaracion_typedef
    : TYPEDEF ID '=' '[' lista_valores ']' ';'
     { System.out.println("Línea " + lexico.getNroLinea() + ": Enumeración (typedef) declarada"); }
    ;

lista_valores
    : constante
    | lista_valores ',' constante
    ;

/* Temas 27 y 30: Clases, Visibilidad y Herencia Multiple */
declaracion_clase
    : CLASS ID herencia_opcional BEGIN cuerpo_clase END ';'
     { System.out.println("Línea " + lexico.getNroLinea() + ": Declaración de Clase reconocida"); } ;
    ;

herencia_opcional
    : EXTENDS lista_variables
    | /* vacio */
    ;

cuerpo_clase
    : cuerpo_clase miembro_clase
    | /* vacio */
    ;

miembro_clase
    : visibilidad declaracion
    | declaracion
    ;

visibilidad
    : PRIVATE
    | FRIEND
    ;

declaracion_funcion
    : type_specifier FUNCTION ID '(' parametros ')' bloque_declarativo BEGIN bloque_ejecutable retorno_opcional END ';'
    ;

parametros
    : type_specifier ID
    | parametros ',' type_specifier ID
    | /* vacio */
    ;

retorno_opcional
    : RET '(' expresion ')' ';'
    | /* vacio */
    ;

bloque_ejecutable
    : bloque_ejecutable sentencia
    | /* vacio */
    ;

sentencia
    : asignacion ';'
    | sentencia_while ';'
    | sentencia_if ';'
    | salida_pout ';'
    | error ';' { System.out.println("Error sintáctico en sentencia ejecutable: resincronizado en ';'"); }
    ;

/* Tema 29: Acceso posicional a atributos con corchetes */
asignacion
    : ID ASIGNACION expresion
    | ID '[' expresion ']' ASIGNACION expresion
      { System.out.println("Linea " + lexico.getNroLinea() + ": Asignacion a atributo opcional"); }
    ;

/* Tema 11: Sentencia WHILE-REPEAT */
sentencia_while
    : WHILE '(' condicion ')' REPEAT bloque_ejecutable END
      { System.out.println("Linea " + lexico.getNroLinea() + ": Sentencia WHILE-REPEAT reconocida"); }
    ;

sentencia_if
    : IF '(' condicion ')' bloque_ejecutable rama_else END_IF
    ;

rama_else
    : ELSE bloque_ejecutable
    | /* vacio */
    ;

salida_pout
    : POUT '(' CADENA ')'
    | POUT '(' expresion ')'
    ;

condicion
    : expresion comparador expresion
    ;

comparador
    : COMP_IGUAL
    | COMP_DIST
    | '>'
    | '<'
    ;

/* Tema 18: Asignaciones dentro de expresiones y conversiones (Tema 2 y 8) */
expresion
    : expresion '+' expresion
    | expresion '-' expresion
    | expresion '*' expresion
    | expresion '/' expresion
    | ID '=' expresion
    | ID invocacion_ordenada
    | ID '[' expresion ']'
    | ID
    | constante
    ;

invocacion_ordenada
    : '(' lista_expresiones ')' '[' lista_valores ']'
    | '(' lista_expresiones ')'
    ;

lista_expresiones
    : expresion
    | lista_expresiones ',' expresion
    | /* vacio */
    ;

constante
    : CTE_USHORT
    | CTE_DOUBLEF
    ;

%%

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