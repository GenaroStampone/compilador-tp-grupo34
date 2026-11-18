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
        registrarEstructura("Programa");
        System.out.println("Programa estructurado correctamente.");
      }
    | ID bloque_declarativo BEGIN lista_sentencias END ';'
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
    : declaracion_variables
    | declaracion_typedef
    | declaracion_clase
    | declaracion_funcion
    | error ';' { System.out.println("Error sintáctico en declaración: resincronizado en ';'"); }
    ;

/* Tema 23: tipos definidos por el usuario y clases */

type_specifier
    : USHORTINT
    | DOUBLEF
    | ID             
    ;

lista_variables
    : ID
        {
            ArrayList<RegistroTS> lista = new ArrayList<>();
            lista.add((RegistroTS) $1.obj);
            $$ = new ParserVal(lista)
        }
    | lista_variables ',' ID
        {
            ArrayList<RegistroTS> lista = (ArrayList<RegistroTS>) $1.obj
            lista.add((RegistroTS) $3.obj);
            $$ = $1;
        }
    ;

/* Tema 23: Enumeraciones */

lista_valores
    : valor_enum
    | lista_valores ',' valor_enum
    ;

valor_enum
    : constante
    | '-' constante { aplicarSignoNegativo($2); $$ = $2; }
    ;

declaracion_variables
    : type_specifier lista_variables ';'
      {
        String tipoDato = ((RegistroTS) $1.obj).getLexema();
        ArrayList<RegistroTS> lista = (ArrayList<RegistroTS>) $2.obj;
        for (RegistroTS reg : lista) {
            String lexemaOriginal = reg.getLexema();

            // 1. Name Mangling según el ámbito actual (ej. "a:main" o "a:main:consultar_clave")
            String nameMangled = lexemaOriginal + ":" + controlAmbitos.getAmbitoActual();

            // 2. Control de Redeclaración en el MISMO ámbito
            if (ts.existeSimbolo(claveMangled)) {
                yyerror("Error Semántico: Variable redeclarada '" + lexemaOriginal + 
                        "' en el ámbito " + controlAmbitos.getAmbitoActual());
            } else {
                // 3. Registrar tipo y uso en la Tabla de Símbolos
                ts.registrarVariable(nameMangled, tipoDato, "variable");
            }
        }
        declararListaVariables(listaVars, tipoDato);
     }
    ;

declaracion_typedef
    : TYPEDEF ID '=' CORCHETE_IZQ lista_valores CORCHETE_DER ';'
     {
        // $1 = TYPEDEF, $2 = ID del nuevo tipo
        RegistroTS reg = (RegistroTS) $2.obj;
        String nombreTipo = reg.getLexema();

        // 1. Name Mangling del nombre del tipo (ej. "codigos_control:main")
        String nameMangled = nombreTipo + ":" + controlAmbitos.getAmbitoActual();

        // 2. Control de Redeclaración en el MISMO ámbito
        if (ts.existeSimbolo(nameMangled)) {
            yyerror("Error Semántico: El tipo de dato '" + nombreTipo + "' ya fue definido en el ámbito " + controlAmbitos.getAmbitoActual());
        } else {
            // 3. Registrar en la TS con Uso = "nombre_tipo" y asociar sus valores
            ArrayList<RegistroTS> valores = (ArrayList<RegistroTS>) $5.obj;
            ts.registrarTipoPersonalizado(nameMangled, "nombre_tipo", valores);
        }
     }
    ;

declaracion_funcion
    : encabezado_funcion cuerpo_funcion
      { registrarEstructura("Declaración de función"); }
    ;

encabezado_funcion 
    : type_specifier FUNCTION ID { 
        // 1. Abrir nuevo ámbito con el nombre de la función 
        controlAmbitos.abrirAmbito($3.getLexema()); 
        // 2. Registrar la función en la Tabla de Símbolos (en el ámbito padre) 
        String nameMangled = $3.getLexema() + ":" + controlAmbitos.getAmbitoPadre(); 
        ts.registrarFuncion(nameMangled, $1.getTipo(), "nombre_funcion"); 
        } 
    ; 

cuerpo_funcion 
    : '(' parametros ')' bloque_declarativo BEGIN lista_sentencias retorno_opcional END ';' { 
        // Al terminar el bloque de la función, se cierra el ámbito 
        controlAmbitos.cerrarAmbito(); 
        } 
    ;

/* Temas 27 y 30: Clases, Visibilidad y Herencia Multiple */
declaracion_clase
    : encabezado_clase herencia_opcional cuerpo_clase
     {
       registrarEstructura("Clase");
       System.out.println("Línea " + lexico.getNroLinea() + ": Declaración de Clase reconocida");
       RegistroTS regClaseActual = (RegistroTS) $1.obj;
       ArrayList<RegistroTS> listaPadres = (ArrayList<RegistroTS>) $2.obj;
       if (regClaseActual != null && listaPadres != null){
            for (RegistroTS regPadre : listaPadres) {
                String nombrePadre = regPadre.getLexema();
                // 1. Validar que la clase padre exista en la Tabla de Símbolos
                RegistroTS clasePadreEncontrada = buscarVariableVisibilidad(nombrePadre);
                if (clasePadreEncontrada == null || !"nombre_clase".equals(clasePadreEncontrada.getUso())) {
                    yyerror("Error Semántico: La clase base '" + nombrePadre + "' no está declarada o no es visible");
                } else if (ts.esHerenciaCircular(clasePadreEncontrada.getLexema(), regClaseActual.getLexema())){
                    yyerror("Error Semántico: Herencia circular detectada. La clase '" + nombrePadre + "' genera un ciclo de herencia con la clase actual");
                } else {
                    // Cargar el nombre de la clase padre en el RegistroTS de la clase actual
                    regClaseActual.agregarClaseHeredada(clasePadreEncontrada.getLexema());
                }
            }   
       }
     }
    ;

encabezado_clase 
    : CLASS ID
     { 
        // 1. Abrir nuevo ámbito con el nombre de la clase
        controlAmbitos.abrirAmbito($2.getLexema()); 
        // 2. Registrar la clase en la Tabla de Símbolos (en el ámbito padre) 
        String nameMangled = $2.getLexema() + ":" + controlAmbitos.getAmbitoPadre();
        String nombreClase = ((RegistroTS) $2.obj).getLexema();
        RegistroTS regClase = null; 
        if (ts.existeSimbolo(claveMangled)) { 
            yyerror("Error Semántico: La clase '" + nombreClase + "' ya fue declarada en el ámbito " + controlAmbitos.getAmbitoActual()); 
        } else { 
            // Registrar la clase en la TS 
            regClase = ts.registrarClase(nameMangled, "nombre_clase"); 
        }
     }
    ; 

cuerpo_clase
    : BEGIN cuerpo_clase_definicion END ';' { 
        // Al terminar el bloque de la clase, se cierra el ámbito 
        controlAmbitos.cerrarAmbito(); 
        } 
    ;

herencia_opcional
    : EXTENDS lista_variables 
     { registrarEstructura("Herencia"); 
        // $2 contiene la lista de identificadores de las clases padre (ArrayList<RegistroTS>)
        $$ = $2; // Propagamos la lista de clases heredadas
     }
    | /* vacio */
      {
        $$ = new ParserVal(new ArrayList<RegistroTS>());
      }
    ;

cuerpo_clase_deficion
    : cuerpo_clase_definicion miembro_clase
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

parametros
    : type_specifier ID
    | parametros ',' type_specifier ID
    ;

retorno_opcional
    : RET '(' expresion ')' ';'
      { registrarEstructura("Sentencia RET"); }
    | /* vacio */
    ;

sentencia_simple 
    : asignacion
    | salida_pout ';' 
    | error ';' { System.out.println("Error sintáctico en sentencia ejecutable: resincronizado en ';'"); } 
    ;

bloque_compuesto 
    : sentencia
    | BEGIN lista_sentencias END ';' 
    ;

lista_sentencias
    : lista_sentencias sentencia
    | /* vacio */
    ;

sentencia 
    : sentencia_simple 
    | sentencia_while 
    | sentencia_if 
    ;

/* Tema 29: Acceso posicional a atributos con corchetes */
asignacion
    : ID ASIGNACION expresion ';'
      { registrarEstructura("Asignación"); }
    | ID CORCHETE_IZQ expresion CORCHETE_DER ASIGNACION expresion ';'
      {
        System.out.println("Linea " + lexico.getNroLinea() + ": Asignacion a atributo opcional");
        registrarEstructura("Asignación a atributo opcional");
      }
    ;

/* Tema 11: Sentencia WHILE-REPEAT */
sentencia_while 
    : WHILE '(' condicion ')' REPEAT bloque_compuesto
        { registrarEstructura("Sentencia WHILE-REPEAT"); } 
    ;

sentencia_if 
    : IF '(' condicion ')' bloque_compuesto rama_else END_IF ';'
        { registrarEstructura("Sentencia IF"); } 
    ;

rama_else 
    : ELSE bloque_compuesto 
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
     { 
        // Nodo operador para la suma 
        $$ = new ParserVal(new NodoOperador("+", (NodoArbol)$1.obj, (NodoArbol)$3.obj)); 
     }
    | expresion '-' expresion
     { 
        // Nodo operador para la resta
        $$ = new ParserVal(new NodoOperador("-", (NodoArbol)$1.obj, (NodoArbol)$3.obj)); 
     }
    | expresion '*' expresion
     { 
        // Nodo operador para la multiplicacion
        $$ = new ParserVal(new NodoOperador("\*", (NodoArbol)$1.obj, (NodoArbol)$3.obj)); 
     }
    | expresion '/' expresion
     { 
        // Nodo operador para la division
        $$ = new ParserVal(new NodoOperador("/", (NodoArbol)$1.obj, (NodoArbol)$3.obj)); 
     }
    | '-' expresion %prec UMINUS
     { 
        Nodo para la aplicacion del signo negativo
        aplicarSignoNegativo($2); 
        $$ = $2; 
     }
    | ID '=' expresion
     { 
        // Asignación: $1 = ID (LHS), $3 = expresion (RHS) 
        String lexema = ((RegistroTS) $1.obj).getLexema(); 
        RegistroTS simboloResuelto = buscarVariableVisibilidad(lexema); 
        NodoArbol nodoLHS = new NodoArbol(simboloResuelto); 
        $$ = new ParserVal(new NodoOperador(":=", nodoLHS, (NodoArbol)$3.obj));
     }
    | ID invocacion_ordenada
    | ID CORCHETE_IZQ expresion CORCHETE_DER
      { registrarEstructura("Acceso posicional"); }
    | ID '.' ID
     {
        String nombreObjeto = ((RegistroTS) $1.obj).getLexema(); 
        String nombreAtributo = ((RegistroTS) $3.obj).getLexema(); 
        // Resolver el objeto a1 (obteniendo su RegistroTS) 
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto); 
        if (regObjeto == null) { 
            yyerror("Error Semántico: Objeto '" + nombreObjeto + "' no declarado"); 
            $$ = new ParserVal(new NodoArbol((RegistroTS)null)); 
        } else { 
            String tipoClase = regObjeto.getTipo(); // Tipo del objeto (ej. "ca") 
            // Buscar si el atributo está en la clase directa del objeto 
            String claveAtributoDirecto = nombreAtributo + ":main:" + tipoClase; 
            RegistroTS atributoResuelto = ts.buscar(claveAtributoDirecto); 
            // Si no está en la clase directa, buscar en la Herencia 
            if (atributoResuelto == null) { 
                atributoResuelto = ts.buscarEnHerencia(tipoClase, nombreAtributo); 
            } 
            if (atributoResuelto == null) { 
                yyerror("Error Semántico: El atributo '" + nombreAtributo + "' no pertenece a la clase '" + tipoClase + "' ni a sus clases heredadas"); 
                $$ = new ParserVal(new NodoArbol((RegistroTS)null)); 
            } else { 
                // Generar hoja con el atributo resuelto en la clase/padre correspondiente 
                $$ = new ParserVal(new NodoArbol(atributoResuelto)); 
            } 
        } 
     }
    | ID
     {
        String lexemaOriginal = ((RegistroTS) $1.obj).getLexema(); 
        // Resolver visibilidad de adentro hacia afuera usando Name Mangling 
        RegistroTS simboloResuelto = buscarVariableVisibilidad(lexemaOriginal); 
        // Crear el NodoArbol con el símbolo resuelto y asignarlo a $$ 
        if (simboloResuelto != null) { 
            $$ = new ParserVal(new NodoArbol(simboloResuelto)); 
        } else { 
            $$ = new ParserVal(new NodoArbol(null)); 
            // Para evitar NullPointerException 
        }
     }
    | constante
     { 
        // Las constantes ya fueron cargadas en la TS por el Léxico 
        RegistroTS regCte = (RegistroTS) $1.obj; 
        $$ = new ParserVal(new NodoArbol(regCte)); 
     }
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

    // 2\. Si estamos dentro de una clase y no se encontró localmente, buscar en la Herencia 
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