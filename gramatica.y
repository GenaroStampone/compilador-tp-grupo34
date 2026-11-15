%{
import java.io.*;
import java.util.*;
%}

/* DECLARACIONES Y CONFIGURACIÓN DE TOKENS */

%token ID CTE_USHORT CTE_DOUBLEF CADENA
%token IF THEN ELSE END_IF BEGIN END POUT
%token WHILE REPEAT TYPEDEF PRIVATE FRIEND EXTENDS CLASS FUNCTION RET
%token USHORTINT DOUBLEF 
%token ASIGNACION ASIG_EXPR COMP_IGUAL COMP_DIST CORCHETE_IZQ CORCHETE_DER
%token COMP_MAYOR_IGUAL COMP_MENOR_IGUAL

%right '=' ASIGNACION ASIG_EXPR
%left COMP_IGUAL COMP_DIST COMP_MAYOR_IGUAL COMP_MENOR_IGUAL '>' '<'
%left '+' '-'
%left '*' '/'
%right UMINUS

%start programa

%%

/* REGLAS GRAMATICALES Y ACCIONES SINTÁCTICAS */

programa
    : ID ';' bloque_declarativo BEGIN bloque_ejecutable END ';'
      {
        registrarEstructura("Programa");
        System.out.println("Programa estructurado correctamente.");
      }
    | ID bloque_declarativo BEGIN bloque_ejecutable END ';'
      {
        registrarEstructura("Programa");
        System.out.println("Programa estructurado correctamente.");
      }
    ;

bloque_declarativo
    : bloque_declarativo declaracion
    | /* vacio */
    ;

declaracion
    : type_specifier lista_variables ';'
      { registrarEstructura("Declaración de variables"); }
    | declaracion_typedef
    | declaracion_clase
    | declaracion_funcion
    | error ';' { System.out.println("Error sintáctico en declaración: resincronizado en ';'"); }
    ;

type_specifier
    : USHORTINT
    | DOUBLEF
    | ID              /* Tema 23: tipos definidos por el usuario y clases */
    ;

lista_variables
    : ID
    | lista_variables ',' ID
    ;

/* Tema 23: Enumeraciones */
declaracion_typedef
    : TYPEDEF ID '=' CORCHETE_IZQ lista_valores CORCHETE_DER ';'
     {
       registrarEstructura("Enumeración (typedef)");
       System.out.println("Línea " + lexico.getNroLinea() + ": Enumeración (typedef) declarada");
     }
    ;

lista_valores
    : valor_enum
    | lista_valores ',' valor_enum
    ;

valor_enum
    : constante
    | '-' constante { aplicarSignoNegativo($2); $$ = $2; }
    ;

/* Temas 27 y 30: Clases, Visibilidad y Herencia Multiple */
declaracion_clase
    : CLASS ID herencia_opcional BEGIN cuerpo_clase END ';'
     {
       registrarEstructura("Clase");
       System.out.println("Línea " + lexico.getNroLinea() + ": Declaración de Clase reconocida");
     }
    ;

herencia_opcional
    : EXTENDS lista_variables { registrarEstructura("Herencia"); }
    | /* vacio */
    ;

cuerpo_clase
    : cuerpo_clase miembro_clase
    | /* vacio */
    ;

miembro_clase
    : visibilidad declaracion
    | declaracion
    | FRIEND lista_variables ';'   /* Tema 27: clases/funciones amigas */
      { registrarEstructura("Declaración friend"); }
    | EXTENDS lista_variables ';'  /* Tema 30: herencia multiple dentro del cuerpo */
      { registrarEstructura("Herencia dentro de clase"); }
    ;

visibilidad
    : PRIVATE
    ;

declaracion_funcion
    : type_specifier FUNCTION ID '(' parametros ')' bloque_declarativo BEGIN bloque_ejecutable retorno_opcional END ';'
      { registrarEstructura("Declaración de función"); }
    ;

parametros
    : type_specifier ID
    | parametros ',' type_specifier ID
    ;

retorno_opcional
    : RET '(' expresion ')' ';'
      { registrarEstructura("Sentencia RET"); }
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
      { registrarEstructura("Asignación"); }
    | ID CORCHETE_IZQ expresion CORCHETE_DER ASIGNACION expresion
      {
        System.out.println("Linea " + lexico.getNroLinea() + ": Asignacion a atributo opcional");
        registrarEstructura("Asignación a atributo opcional");
      }
    ;

/* Tema 11: Sentencia WHILE-REPEAT */
sentencia_while
    : WHILE '(' condicion ')' REPEAT bloque_ejecutable END
      {
        System.out.println("Linea " + lexico.getNroLinea() + ": Sentencia WHILE-REPEAT reconocida");
        registrarEstructura("Sentencia WHILE-REPEAT");
      }
    ;

sentencia_if
    : IF '(' condicion ')' bloque_ejecutable rama_else END_IF
      { registrarEstructura("Sentencia IF"); }
    ;

rama_else
    : ELSE bloque_ejecutable
    | /* vacio */
    ;

salida_pout
    : POUT '(' CADENA ')'
      { registrarEstructura("Sentencia POUT"); }
    | POUT '(' expresion ')'
      { registrarEstructura("Sentencia POUT"); }
    ;

condicion
    : expresion comparador expresion
      { registrarEstructura("Condición"); }
    ;

comparador
    : COMP_IGUAL
    | COMP_DIST
    | COMP_MAYOR_IGUAL
    | COMP_MENOR_IGUAL
    | '>'
    | '<'
    ;

/* Tema 18: Asignaciones dentro de expresiones y conversiones (Tema 2 y 8) */
expresion
    : expresion '+' expresion
    | expresion '-' expresion
    | expresion '*' expresion
    | expresion '/' expresion
    | '-' expresion %prec UMINUS
      { aplicarSignoNegativo($2); $$ = $2; }
    | ID '=' expresion
    | ID invocacion_ordenada
    | ID CORCHETE_IZQ expresion CORCHETE_DER
      { registrarEstructura("Acceso posicional"); }
    | ID
    | constante
    ;

invocacion_ordenada
    : '(' lista_expresiones ')' CORCHETE_IZQ lista_valores CORCHETE_DER
      { registrarEstructura("Invocación ordenada con índices"); }
    | '(' lista_expresiones ')'
      { registrarEstructura("Invocación ordenada"); }
    ;

lista_expresiones
    : expresion
    | lista_expresiones ',' expresion
    | /* vacio */
    ;

constante
    : CTE_USHORT
    | CTE_DOUBLEF { validarConstanteDoublef($1); $$ = $1; }
    ;

%%

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
