%{
import java.io.*;
%}

/* DECLARACIONES Y CONFIGURACIÓN DE TOKENS */

%token ID CTE_USHORT CTE_DOUBLEF CADENA
%token IF THEN ELSE END_IF BEGIN END POUT
%token WHILE REPEAT TYPEDEF PRIVATE FRIEND EXTENDS

%right ':='
%left '+' '-'
%left '*' '/'

%start programa

%%

/* REGLAS GRAMATICALES Y ACCIONES SINTÁCTICAS

programa 
    : ID bloque_declarativo BEGIN bloque_ejecutable END ';'
      { System.out.println("Programa estructurado correctamente."); }
    ;

bloque_declarativo
    : /* vacío */
    | bloque_declarativo declaracion
    ;

declaracion
    : type_specifier lista_variables ';'
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

bloque_ejecutable
    : /* vacío */
    | bloque_ejecutable sentencia
    ;

sentencia
    : asignacion ';'
    | sentencia_while ';'
    | error ';' { System.out.println("Error sintáctico en sentencia ejecutable: resincronizado en ';'"); }
    ;

asignacion
    : ID ':=' expresion
      { System.out.println("Línea " + lexico.getNroLinea() + ": Asignación reconocida"); }
    ;

sentencia_while
    : WHILE '(' expresion ')' REPEAT bloque_ejecutable END
      { System.out.println("Línea " + lexico.getNroLinea() + ": Sentencia WHILE-REPEAT reconocida"); }
    ;

expresion
    : expresion '+' expresion
    | expresion '-' expresion
    | ID
    | CTE_USHORT
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
    System.err.println("Error sintáctico en línea " + lexico.getNroLinea() + ": " + msg);
}

public boolean huboError() {
    return huboError;
}