%{
import java.io.*;
import java.util.*;
%}

/* DECLARACIONES Y CONFIGURACIÓN DE TOKENS */

%token ID CTE_USHORT CTE_DOUBLEF CADENA
%token IF ELSE END_IF BEGIN END POUT
%token WHILE REPEAT TYPEDEF PRIVATE FRIEND EXTENDS CLASS FUNCTION RET
%token USHORTINT DOUBLEF 
%token ASIGNACION COMP_IGUAL COMP_DIST CORCHETE_IZQ CORCHETE_DER
%token COMP_MAYOR_IGUAL COMP_MENOR_IGUAL

%right '=' ASIGNACION
%left COMP_IGUAL COMP_DIST COMP_MAYOR_IGUAL COMP_MENOR_IGUAL '>' '<'
%left '+' '-'
%left '*' '/'
%right UMINUS

%start programa

%%

/* REGLAS GRAMATICALES Y ACCIONES SINTÁCTICAS */

programa
    : ID ';' bloque_declarativo BEGIN lista_sentencias END ';'
      {
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $1.obj);
        NodoArbol cuerpoProg = (NodoArbol) $5.obj;
        NodoArbol nodoProg = GeneradorCodigoIntermedio.crearNodoAST("PROGRAMA", hojaID, cuerpoProg);
        $$ = new ParserVal(nodoProg);
      }
    | ID bloque_declarativo BEGIN lista_sentencias END ';'
      {
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $1.obj);
        NodoArbol cuerpoProg = (NodoArbol) $4.obj;
        NodoArbol nodoProg = GeneradorCodigoIntermedio.crearNodoAST("PROGRAMA", hojaID, cuerpoProg);
        $$ = new ParserVal(nodoProg);
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

// VER EL RETORNO POR DEFECTO EN CASO DE SER NULO
declaracion_funcion
    : type_specifier FUNCTION ID '(' parametros ')' bloque_declarativo BEGIN lista_sentencias retorno_opcional END ';'
      {
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $3.obj);
        NodoArbol cuerpoFunc = (NodoArbol) $9.obj;
        NodoArbol retFunc = (NodoArbol) $10.obj;

        NodoArbol cuerpoCompleto = GeneradorCodigoIntermedio.crearNodoAST(";", cuerpoFunc, retFunc);
        NodoArbol nodoFunc = GeneradorCodigoIntermedio.crearNodoAST("FUNCTION", hojaID, cuerpoCompleto);
        $$ = new ParserVal(nodoFunc);
      }
    ;

parametros
    : type_specifier ID
    | parametros ',' type_specifier ID
    ;

retorno_opcional
    : RET '(' expresion ')' ';'
      {
        NodoArbol expr = (NodoArbol) $3.obj;
        NodoArbol nodoRet = GeneradorCodigoIntermedio.crearNodoAST("RET", expr, null);
        $$ = new ParserVal(nodoRet);
      }
    | /* vacio */
      {
        $$ = new ParserVal((Object) null);
      }
    ;

bloque_compuesto
    : sentencia
      { $$ = $1; }
    | BEGIN lista_sentencias END ';'
      { $$ = $2; }
    ;

sentencia_simple
    : asignacion       { $$ = $1; }
    | salida_pout ';'  { $$ = $1; }
    | error ';'        { $$ = new ParserVal((Object) null); }
    ;

lista_sentencias
    : lista_sentencias sentencia
      {
        NodoArbol izq = (NodoArbol) $1.obj;
        NodoArbol der = (NodoArbol) $2.obj;
        if (izq == null) {
            $$ = $2;
        } else if (der == null) {
            $$ = $1;
        } else {
            NodoArbol seq = GeneradorCodigoIntermedio.crearNodoAST(";", izq, der);
            $$ = new ParserVal(seq);
        }
      }
    | /* vacio */
      {
        $$ = new ParserVal((Object) null);
      }
    ;

sentencia
    : sentencia_simple { $$ = $1; }
    | sentencia_while  { $$ = $1; }
    | sentencia_if     { $$ = $1; }
    ;

/* Tema 29: Acceso posicional a atributos con corchetes y asignación */
asignacion
    : ID ASIGNACION expresion ';'
      {
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $1.obj);
        NodoArbol expr = (NodoArbol) $3.obj;
        NodoArbol nodoAsig = GeneradorCodigoIntermedio.verificarYConvertirTipos(":=", hojaID, expr, lexico.getNroLinea());
        $$ = new ParserVal(nodoAsig);
      }
    | ID CORCHETE_IZQ expresion CORCHETE_DER ASIGNACION expresion ';'
      {
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $1.obj);
        NodoArbol indice = (NodoArbol) $3.obj;
        NodoArbol nodoAcceso = GeneradorCodigoIntermedio.crearNodoAST("[]", hojaID, indice);
        NodoArbol expr = (NodoArbol) $6.obj;
        NodoArbol nodoAsig = GeneradorCodigoIntermedio.verificarYConvertirTipos(":=", nodoAcceso, expr, lexico.getNroLinea());
        $$ = new ParserVal(nodoAsig);
      }
    ;

/* Tema 11: Sentencia WHILE-REPEAT */
sentencia_while
    : WHILE '(' condicion ')' REPEAT bloque_compuesto
      {
        NodoArbol condNode = GeneradorCodigoIntermedio.crearNodoAST("cond", (NodoArbol) $3.obj, null);
        NodoArbol cuerpoNode = GeneradorCodigoIntermedio.crearNodoAST("cuerpo", (NodoArbol) $6.obj, null);
        NodoArbol whileNode = GeneradorCodigoIntermedio.crearNodoAST("WHILE", condNode, cuerpoNode);
        $$ = new ParserVal(whileNode);
      }
    ;

sentencia_if
    : IF '(' condicion ')' bloque_compuesto rama_else END_IF ';'
      {
        NodoArbol condNode = GeneradorCodigoIntermedio.crearNodoAST("cond", (NodoArbol) $3.obj, null);
        NodoArbol bloqThen = (NodoArbol) $5.obj; // Subárbol directo de sentencias
        NodoArbol bloqElse = (NodoArbol) $6.obj; // Subárbol de la rama else (o null)

        NodoArbol cuerpoNode = GeneradorCodigoIntermedio.crearNodoAST("cuerpo", bloqThen, bloqElse);
        NodoArbol ifNode = GeneradorCodigoIntermedio.crearNodoAST("IF", condNode, cuerpoNode);

        $$ = new ParserVal(ifNode);
      }
    ;

rama_else
    : ELSE bloque_compuesto
      {
        NodoArbol elseBlock = (NodoArbol) $2.obj;
        NodoArbol elseNode = GeneradorCodigoIntermedio.crearNodoAST("else", elseBlock, null);
        $$ = new ParserVal(elseNode);
      }
    | /* vacio */
      {
        $$ = new ParserVal((Object) null);
      }
    ;

salida_pout
    : POUT '(' CADENA ')'
      {
        NodoArbol hojaCadena = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $3.obj);
        NodoArbol nodoPout = GeneradorCodigoIntermedio.crearNodoAST("POUT", hojaCadena, null);
        $$ = new ParserVal(nodoPout);
      }
    | POUT '(' expresion ')'
      {
        NodoArbol expr = (NodoArbol) $3.obj;
        NodoArbol nodoPout = GeneradorCodigoIntermedio.crearNodoAST("POUT", expr, null);
        $$ = new ParserVal(nodoPout);
      }
    ;

condicion
    : expresion comparador expresion
      {
        NodoArbol izq = (NodoArbol) $1.obj;
        String op = (String) $2.obj;
        NodoArbol der = (NodoArbol) $3.obj;
        NodoArbol nodoComp = GeneradorCodigoIntermedio.verificarYConvertirTipos(op, izq, der, lexico.getNroLinea());
        $$ = new ParserVal(nodoComp);
      }
    ;

comparador
    : COMP_IGUAL        { $$ = new ParserVal("=="); }
    | COMP_DIST         { $$ = new ParserVal("!="); }
    | COMP_MAYOR_IGUAL  { $$ = new ParserVal(">="); }
    | COMP_MENOR_IGUAL  { $$ = new ParserVal("<="); }
    | '>'               { $$ = new ParserVal(">"); }
    | '<'               { $$ = new ParserVal("<"); }
    ;

/* Tema 18: Asignaciones dentro de expresiones y conversiones (Tema 2 y 8) */
expresion
    : expresion '+' expresion
      {
        NodoArbol izq = (NodoArbol) $1.obj;
        NodoArbol der = (NodoArbol) $3.obj;
        NodoArbol nodo = GeneradorCodigoIntermedio.verificarYConvertirTipos("+", izq, der, lexico.getNroLinea());
        $$ = new ParserVal(nodo);
      }
    | expresion '-' expresion
      {
        NodoArbol izq = (NodoArbol) $1.obj;
        NodoArbol der = (NodoArbol) $3.obj;
        NodoArbol nodo = GeneradorCodigoIntermedio.verificarYConvertirTipos("-", izq, der, lexico.getNroLinea());
        $$ = new ParserVal(nodo);
      }
    | expresion '*' expresion
      {
        NodoArbol izq = (NodoArbol) $1.obj;
        NodoArbol der = (NodoArbol) $3.obj;
        NodoArbol nodo = GeneradorCodigoIntermedio.verificarYConvertirTipos("*", izq, der, lexico.getNroLinea());
        $$ = new ParserVal(nodo);
      }
    | expresion '/' expresion
      {
        NodoArbol izq = (NodoArbol) $1.obj;
        NodoArbol der = (NodoArbol) $3.obj;
        NodoArbol nodo = GeneradorCodigoIntermedio.verificarYConvertirTipos("/", izq, der, lexico.getNroLinea());
        $$ = new ParserVal(nodo);
      }
    | '-' expresion %prec UMINUS
      {
        aplicarSignoNegativo($2);
        NodoArbol expr = (NodoArbol) $2.obj;
        // Usamos una etiqueta explícita "UMINUS" para no confundirlo con la resta binaria
        NodoArbol nodo = GeneradorCodigoIntermedio.crearNodoAST("UMINUS", expr, null);
        $$ = new ParserVal(nodo);
      }
    | ID '=' expresion
      {
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $1.obj);
        NodoArbol expr = (NodoArbol) $3.obj;
        NodoArbol nodoAsig = GeneradorCodigoIntermedio.verificarYConvertirTipos("=", hojaID, expr, lexico.getNroLinea());
        $$ = new ParserVal(nodoAsig);
      }
    | ID invocacion_ordenada
      {
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $1.obj);
        NodoArbol args = (NodoArbol) $2.obj;
        NodoArbol nodoCall = GeneradorCodigoIntermedio.crearNodoAST("CALL", hojaID, args);
        $$ = new ParserVal(nodoCall);
      }
    | ID CORCHETE_IZQ expresion CORCHETE_DER
      {
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $1.obj);
        NodoArbol indice = (NodoArbol) $3.obj;
        NodoArbol nodoAcceso = GeneradorCodigoIntermedio.crearNodoAST("[]", hojaID, indice);
        $$ = new ParserVal(nodoAcceso);
      }
    | ID
      {
        NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $1.obj);
        $$ = new ParserVal(hojaID);
      }
    | constante
      {
        /* $$ hereda directamente el ParserVal con el NodoArbol producido en la regla constante */
        $$ = $1;
      }
    ;

invocacion_ordenada
    : '(' lista_expresiones ')' CORCHETE_IZQ lista_valores CORCHETE_DER
      {
        NodoArbol args = (NodoArbol) $2.obj;
        NodoArbol indices = (NodoArbol) $5.obj;
        NodoArbol inv = GeneradorCodigoIntermedio.crearNodoAST("INVOC", args, indices);
        $$ = new ParserVal(inv);
      }
    | '(' lista_expresiones ')'
      {
        $$ = $2;
      }
    ;

lista_expresiones
    : expresion
      { $$ = $1; }
    | lista_expresiones ',' expresion
      {
        NodoArbol izq = (NodoArbol) $1.obj;
        NodoArbol der = (NodoArbol) $3.obj;
        NodoArbol list = GeneradorCodigoIntermedio.crearNodoAST(",", izq, der);
        $$ = new ParserVal(list);
      }

constante
    : CTE_USHORT
      {
        NodoArbol hojaCte = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $1.obj);
        $$ = new ParserVal(hojaCte);
      }
    | CTE_DOUBLEF
      {
        validarConstanteDoublef($1);
        NodoArbol hojaCte = GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $1.obj);
        $$ = new ParserVal(hojaCte);
      }
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