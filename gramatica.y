%{
import java.io.*;
import java.util.List;
import java.util.ArrayList;
%}

/* Declaración de todos los tokens terminales del Analizador Léxico (Grupo 34) */
%token ID 257
%token CTE_ENTERA 258
%token DOUBLEF_VAL 259
%token CADENA 260
%token MAYOR_IGUAL 261
%token MENOR_IGUAL 262
%token IGUAL_IGUAL 263
%token DISTINTO 264
%token ASIGNACION 265

%token IF 270
%token ELSE 271
%token END_IF 272
%token BEGIN 273
%token END 274
%token POUT 275
%token RET 276
%token CLASS 277
%token FUNCTION 278
%token USHORTINT 279
%token DOUBLEF 280
%token THEN 281
%token WHILE 282
%token REPEAT 283
%token TYPEDEF 284
%token PRIVATE 285
%token FRIEND 286
%token EXTENDS 287
%token TODF 288
%token TOUS 289

/* Reglas de precedencia y asociatividad para evitar ambigüedades */
%right '=' ASIGNACION
%nonassoc LOWER_THAN_ELSE
%nonassoc ELSE
%left IGUAL_IGUAL DISTINTO
%left '<' '>' MENOR_IGUAL MAYOR_IGUAL
%left '+' '-'
%left '*' '/'
%right UMINUS

%%

/* ========================================================================= */
/*                         ESTRUCTURA GENERAL DEL PROGRAMA                    */
/* ========================================================================= */

programa
    : ID opt_punto_y_coma sentencias_declarativas bloque_principal opt_punto_y_coma
        {
            registrarEstructura("Programa completo con nombre '" + $1.sval + "'");
        }
    | primera_declaracion_sin_id sentencias_declarativas bloque_principal opt_punto_y_coma
        {
            registrarEstructura("Programa completo (sin nombre explícito)");
        }
    | bloque_principal opt_punto_y_coma
        {
            registrarEstructura("Programa completo (solo bloque principal)");
        }
    ;

opt_punto_y_coma
    : ';'
    | /* vacio */
    ;

bloque_principal
    : BEGIN sentencias_ejecutables END
        {
            registrarEstructura("Bloque principal (BEGIN ... END)");
        }
    ;

/* ========================================================================= */
/*                         SENTENCIAS DECLARATIVAS                           */
/* ========================================================================= */

sentencias_declarativas
    : /* vacio */
    | sentencias_declarativas sentencia_declarativa
    ;

sentencia_declarativa
    : declaracion_variables ';'
        {
            registrarEstructura("Declaración de variable(s)");
        }
    | declaracion_tipo ';'
        {
            registrarEstructura("Declaración de tipo enumerado (TYPEDEF)");
        }
    | declaracion_clase opt_punto_y_coma
        {
            registrarEstructura("Declaración de clase");
        }
    | declaracion_funcion opt_punto_y_coma
        {
            registrarEstructura("Declaración de función/método");
        }
    | error ';'
        {
            yyerror("Declaración no válida. Resincronización exitosa en ';'");
        }
    ;

/* Declaraciones iniciales cuando el programa no especifica un ID de nombre */
primera_declaracion_sin_id
    : declaracion_tipo ';'
        {
            registrarEstructura("Declaración de tipo enumerado (TYPEDEF)");
        }
    | declaracion_clase opt_punto_y_coma
        {
            registrarEstructura("Declaración de clase");
        }
    | declaracion_funcion_sin_id opt_punto_y_coma
        {
            registrarEstructura("Declaración de función");
        }
    | tipo_basico lista_variables ';'
        {
            registrarEstructura("Declaración de variable(s)");
        }
    | PRIVATE sentencia_declarativa
        {
            registrarEstructura("Declaración privada");
        }
    | error ';'
        {
            yyerror("Declaración inicial no válida. Resincronización exitosa en ';'");
        }
    ;

declaracion_variables
    : tipo lista_variables
    ;

tipo
    : USHORTINT
    | DOUBLEF
    | ID
    ;

tipo_basico
    : USHORTINT
    | DOUBLEF
    ;

lista_variables
    : ID
    | lista_variables ',' ID
    ;

/* Declaración de tipos enumerados (Tema 23) */
declaracion_tipo
    : TYPEDEF ID '=' '[' lista_constantes ']'
    ;

lista_constantes
    : constante
    | lista_constantes ',' constante
    ;

constante
    : CTE_ENTERA
    | DOUBLEF_VAL
    | '-' DOUBLEF_VAL %prec UMINUS
        {
            String lexPos = $2.sval;
            try {
                double val = -Double.parseDouble(lexPos.replace("d", "e").replace("D", "e"));
                if (tablaDeSimbolos != null && !tablaDeSimbolos.verificarRangoDoublef(val)) {
                    yyerror("Constante doublef negativa fuera de rango permitido: -" + lexPos);
                } else if (tablaDeSimbolos != null) {
                    RegistroTS regNeg = tablaDeSimbolos.registrarConstanteNegativa(lexPos, lexico.getYylineno(), val);
                    $$ = new ParserVal(regNeg);
                    registrarEstructura("Detección de constante doublef negativa en enumeración -> " + regNeg.getLexema());
                }
            } catch (Exception e) {
                yyerror("Error procesando constante negativa doublef: " + lexPos);
            }
        }
    ;

/* Clases y Objetos (Temas 27 y 30) */
declaracion_clase
    : CLASS ID BEGIN miembros_clase END
    ;

miembros_clase
    : /* vacio */
    | miembros_clase miembro_clase
    ;

miembro_clase
    : sentencia_declarativa
    | PRIVATE sentencia_declarativa
        {
            registrarEstructura("Miembro privado (PRIVATE)");
        }
    | FRIEND ID ';'
        {
            registrarEstructura("Declaración de amistad (FRIEND)");
        }
    | EXTENDS lista_ids ';'
        {
            registrarEstructura("Herencia múltiple (EXTENDS)");
        }
    ;

lista_ids
    : ID
    | lista_ids ',' ID
    ;

/* Declaración de funciones */
declaracion_funcion
    : tipo FUNCTION ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END
    | FUNCTION tipo ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END
    | tipo ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END
    ;

declaracion_funcion_sin_id
    : tipo_basico FUNCTION ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END
    | FUNCTION tipo ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END
    | tipo_basico ID '(' lista_parametros ')' sentencias_declarativas BEGIN sentencias_ejecutables END
    ;

lista_parametros
    : /* vacio */
    | lista_parametros_no_vacia
    ;

lista_parametros_no_vacia
    : parametro
    | lista_parametros_no_vacia ',' parametro
    ;

parametro
    : tipo ID
    ;

/* ========================================================================= */
/*                         SENTENCIAS EJECUTABLES                            */
/* ========================================================================= */

sentencias_ejecutables
    : /* vacio */
    | sentencias_ejecutables sentencia_ejecutable
    ;

sentencia_ejecutable
    : sentencia_asignacion ';'
        {
            registrarEstructura("Asignación");
        }
    | llamada_funcion ';'
        {
            registrarEstructura("Invocación a función/método");
        }
    | sentencia_if
        {
            registrarEstructura("Sentencia de control IF");
        }
    | sentencia_while
        {
            registrarEstructura("Sentencia iterativa WHILE-REPEAT");
        }
    | sentencia_pout ';'
        {
            registrarEstructura("Salida (POUT)");
        }
    | sentencia_ret ';'
        {
            registrarEstructura("Retorno de función (RET)");
        }
    | bloque_compuesto
    | error ';'
        {
            yyerror("Sentencia ejecutable no válida. Resincronización exitosa en ';'");
        }
    ;

bloque_compuesto
    : BEGIN sentencias_ejecutables END opt_punto_y_coma
    ;

sentencia_asignacion
    : lado_izquierdo ASIGNACION expresion
    | lado_izquierdo '=' expresion
    ;

llamada_funcion
    : invocacion_funcion
    ;

lado_izquierdo
    : ID
    | ID '.' ID
    | ID '[' expresion ']'
    | ID '.' ID '[' expresion ']'
    ;

sentencia_if
    : IF '(' condicion ')' opt_then sentencias_ejecutables END_IF ';' %prec LOWER_THAN_ELSE
    | IF '(' condicion ')' opt_then sentencias_ejecutables ELSE sentencias_ejecutables END_IF ';'
    ;

opt_then
    : THEN
    | /* vacio */
    ;

sentencia_while
    : WHILE '(' condicion ')' REPEAT sentencia_ejecutable
    ;

sentencia_pout
    : POUT '(' CADENA ')'
    | POUT '(' expresion ')'
    | POUT CADENA
    ;

sentencia_ret
    : RET '(' expresion ')'
    | RET
    ;

condicion
    : expresion comparador expresion
    | expresion
    ;

comparador
    : MAYOR_IGUAL
    | MENOR_IGUAL
    | IGUAL_IGUAL
    | DISTINTO
    | '>'
    | '<'
    ;

/* ========================================================================= */
/*                         EXPRESIONES ARITMÉTICAS Y TÉRMINOS                */
/* ========================================================================= */

expresion
    : expresion '+' expresion
    | expresion '-' expresion
    | expresion '*' expresion
    | expresion '/' expresion
    | '-' expresion %prec UMINUS
        {
            if ($2 != null && $2.obj instanceof RegistroTS) {
                RegistroTS reg = (RegistroTS) $2.obj;
                if (reg.getTipoToken() == AnalizadorLexico.DOUBLEF_VAL) {
                    String lexPos = reg.getLexema();
                    try {
                        double val = -Double.parseDouble(lexPos.replace("d", "e").replace("D", "e"));
                        if (tablaDeSimbolos != null && !tablaDeSimbolos.verificarRangoDoublef(val)) {
                            yyerror("Constante doublef negativa fuera de rango permitido: -" + lexPos);
                        } else if (tablaDeSimbolos != null) {
                            RegistroTS regNeg = tablaDeSimbolos.registrarConstanteNegativa(lexPos, lexico.getYylineno(), val);
                            $$ = new ParserVal(regNeg);
                            registrarEstructura("Detección de constante doublef negativa -> " + regNeg.getLexema());
                        }
                    } catch (Exception e) {
                        yyerror("Error procesando constante negativa doublef: " + lexPos);
                    }
                }
            }
        }
    | ID '=' expresion
    | '(' expresion ')'
    | invocacion_funcion
    | conversion_tipo
    | elemento_primario
    ;

elemento_primario
    : ID
    | ID '.' ID
    | ID '[' expresion ']'
    | ID '.' ID '[' expresion ']'
    | CTE_ENTERA
    | DOUBLEF_VAL
    ;

invocacion_funcion
    : ID '(' lista_argumentos ')' opt_orden_evaluacion
    | ID '(' ')' opt_orden_evaluacion
    | ID '.' ID '(' lista_argumentos ')' opt_orden_evaluacion
    | ID '.' ID '(' ')' opt_orden_evaluacion
    ;

lista_argumentos
    : expresion
    | lista_argumentos ',' expresion
    ;

opt_orden_evaluacion
    : '[' lista_constantes ']'
    | /* vacio */
    ;

conversion_tipo
    : TODF '(' expresion ')'
    | TOUS '(' expresion ')'
    ;

%%

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
