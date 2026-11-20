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
        {
            $$ = $1;
        }
    | lista_valores ',' valor_enum
        {
            NodoArbol izq = (NodoArbol) $1.obj;
            NodoArbol der = (NodoArbol) $3.obj;
            NodoArbol list = GeneradorCodigoIntermedio.crearNodoAST(",", izq, der);
            $$ = new ParserVal(list);
        }
    ;

valor_enum
    : constante
    | '-' constante { aplicarSignoNegativo($2); $$ = $2; }
    ;

declaracion_variables
    : type_specifier lista_variables ';'
      {
        String tipoDato = ((RegistroTS) $1.obj).getLexema();
        ArrayList<RegistroTS> listaVars = (ArrayList<RegistroTS>) $2.obj;
        String ambitoActual = controlAmbitos.getAmbitoActual();
        if (listaVars != null) {
          for (RegistroTS reg : listaVars) {
            String lexemaOriginal = reg.getLexema();
            // Name Mangling según el ámbito actual (ej. "a:main" o "a:main:consultar_clave")
            String nameMangled = lexemaOriginal + ":" + controlAmbitos.getAmbitoActual();
            // Control de Redeclaración en el MISMO ámbito
            if (ts.buscar(nameMangled)) {
                yyerror("Error Semántico: Variable redeclarada '" + lexemaOriginal + "' en el ámbito " + controlAmbitos.getAmbitoActual());
            } else {
                // Registrar tipo y uso en la Tabla de Símbolos
                RegistroTS regNuevo = ts.registrarVariable(nameMangled, tipoDato, "variable");
            }
          }
        }
      
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
      {
        controlAmbitos.cerrarAmbito(); 
        // Armamos el nodo del AST de la función conectando la hoja del encabezado con el subárbol del cuerpo 
        NodoArbol hojaFunc = (NodoArbol) $1.obj; 
        NodoArbol subArbolCuerpo = (NodoArbol) $2.obj; 
        NodoArbol nodoFuncion = GeneradorCodigoIntermedio.crearNodoAST("FUNC", hojaFunc, subArbolCuerpo); 
        $$ = new ParserVal(nodoFuncion); 
      }
    ;

encabezado_funcion 
    : type_specifier FUNCTION ID '(' lista_parametros_formales ')'
     { 
        String lexemaFunc = ((RegistroTS) $3.obj).getLexema();
        String ambitoPadre = controlAmbitos.getAmbitoActual();
        String nameMangled = lexemaFunc + ":" + ambitoPadre;
        List<String> tiposParametros = (List<String>) $5.obj;
        if (ts.buscar(nameMangled) != null) { 
          // Error semántico: Redeclaración en el mismo ámbito 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": La función o símbolo '" + lexemaFunc + "' ya fue declarado/a en el ámbito '" + ambitoPadre + "'"); 
          controlAmbitos.abrirAmbito(lexemaFunc); 
          $$ = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) $3.obj)); 
        } else {
          // Registrar la función en la Tabla de Símbolos (en el ámbito padre)
          RegistroTS regID = (RegistroTS) $3.obj;
          String tipoFunc = ((RegistroTS) $1.obj).getLexema();
          RegistroTS regFuncion = ts.registrarFuncion(nameMangled, tipoFunc, "nombre_funcion", tiposParametros);
          controlAmbitos.abrirAmbito(lexemaFunc);
          NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST(regFuncion);
          $$ = new ParserVal(hojaID);
        }
      } 
    ; 

cuerpo_funcion 
    :   bloque_declarativo BEGIN lista_sentencias retorno_opcional END ';' { 
        NodoArbol cuerpoFunc = (NodoArbol) $6.obj; 
        NodoArbol retFunc = (NodoArbol) $7.obj;

        if (retFunc == null) {
            String ambito = controlAmbitos.getAmbitoActual(); // Ej: "main:miFuncion"
            int idx = ambito.lastIndexOf(':');
            String claveFunc = ambito.substring(idx + 1) + ":" + ambito.substring(0, idx);

            RegistroTS regFunc = ts.buscar(claveFunc);
            String tipoFunc = (regFunc != null) ? regFunc.getTipoToken() : null;

            RegistroTS regDefecto = ts.obtenerOCrearConstantePorDefecto(tipoFunc);
            NodoArbol hojaDefecto = GeneradorCodigoIntermedio.crearHojaAST(regDefecto);
            retFunc = GeneradorCodigoIntermedio.crearNodoAST("RETURN", hojaDefecto, null);
        }

        NodoArbol cuerpoCompleto = GeneradorCodigoIntermedio.crearNodoAST(";", cuerpoFunc, retFunc);
        $$ = new ParserVal(cuerpoCompleto);

        controlAmbitos.cerrarAmbito();
      }
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
       $$ = new ParserVal(null);
     }
    ;

encabezado_clase 
    : CLASS ID
     {  
        String nombreClase = ((RegistroTS) $2.obj).getLexema();
        String ambitoPadre = controlAmbitos.getAmbitoActual();
        String nameMangled = nombreClase + ":" + ambitoPadre;
        RegistroTS regClase = null; 
        if (ts.buscar(nameMangled) != null) { 
            yyerror("Error Semántico: La clase '" + nombreClase + "' ya fue declarada en el ámbito " + controlAmbitos.getAmbitoActual());
            controlAmbitos.abrirAmbito(nombreClase); 
            regClase = (RegistroTS) $2.obj;
        } else { 
            regClase = ts.registrarClase(nameMangled, "nombre_clase");
            controlAmbitos.abrirAmbito(nombreClase); 
        }
        $$ = new ParserVal(regClase);
     }
    ; 

cuerpo_clase
    : BEGIN cuerpo_clase_definicion END ';' 
     { 
        // Al terminar el bloque de la clase, se cierra el ámbito 
        controlAmbitos.cerrarAmbito(); 
     } 
    ;

herencia_opcional
    : EXTENDS lista_variables 
     { 
        registrarEstructura("Herencia");
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
    : visibilidad_opcional declaracion
    | declaracion
    | declaracion_friend   /* Tema 27: clases/funciones amigas */
      { registrarEstructura("Declaración friend"); }
    | EXTENDS lista_variables ';'  /* Tema 30: herencia multiple dentro del cuerpo */
      { registrarEstructura("Herencia dentro de clase"); }
    ;

visibilidad_opcional
    : PRIVATE 
     { 
      $$ = new ParserVal("private");
     }
    | /* vacío */ 
     { 
      $$ = new ParserVal("public"); 
     }
    ;

declaracion_friend
    : FRIEND ID ';'
     { 
      String nombreAmigo = ((RegistroTS) $2.obj).getLexema(); 
      // Obtenemos el registro de la clase actual que está abierta en el ámbito 
      String nombreClaseActual = controlAmbitos.getAmbitoActual(); 
      RegistroTS regClaseActual = ts.buscar(nombreClaseActual + ":main"); 
      if (regClaseActual != null) { 
        // Registramos la clase o función como amiga de la clase actual 
        regClaseActual.agregarAmigo(nombreAmigo); 
        } 
     }

lista_parametros_formales
    : type_specifier ID
     {
      List<String> lista = new ArrayList<>();
        lista.add((String) $1.obj); // Agrega el tipo (ej: "USHORTINT")
        $$ = new ParserVal(lista);
     }
    | lista_parametros_formales ',' type_specifier ID 
     { 
        List<String> lista = (List<String>) $1.obj;
        lista.add((String) $3.obj);
        $$ = new ParserVal(lista);
     }
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
        registrarEstructura("Asignación");
        String lexemaVar = ((RegistroTS) $1.obj).getLexema(); 
        RegistroTS regVariable = buscarVariableVisibilidad(lexemaVar);
        if (regVariable == null) { 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": La variable '" + lexemaVar + "' no está declarada o no es visible en este ámbito"); 
          $$ = new ParserVal(null); 
        } else if (!ts.esAsignable(regVariable)) { 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": Uso incorrecto de '" + lexemaVar + "'. No se puede asignar a un símbolo de uso '" + regVariable.getUso() + "'");
          $$ = new ParserVal(null); 
        } else {
          NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST(regVariable);
          NodoArbol expr = (NodoArbol) $3.obj;
          NodoArbol nodoAsig = GeneradorCodigoIntermedio.verificarAsignacion(hojaID, expr, lexico.getNroLinea());
          $$ = new ParserVal(nodoAsig);
        }
      }
    | ID CORCHETE_IZQ expresion CORCHETE_DER ASIGNACION expresion ';'
      {
        registrarEstructura("Asignación a atributo posicional"); 
        // Extraer el lexema del objeto ($1.obj) 
        String nombreObjeto = ((RegistroTS) $1.obj).getLexema(); 
        // Resolver la visibilidad y ámbito del objeto (Local -> Clase -> Global) 
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto); 
        if (regObjeto == null) { 
          // Error semántico: Objeto o variable no declarada 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El objeto o variable '" + nombreObjeto + "' no está declarado o no es visible"); 
          $$ = new ParserVal(null);
        } else if (!"objeto".equalsIgnoreCase(regObjeto.getUso())) { 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El identificador '" + nombreObjeto + "' es de uso '" + regObjeto.getUso() + "' y no admite acceso posicional a atributos"); 
          $$ = new ParserVal(null);
        } else {
          NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST(regObjeto);
          NodoArbol indice = (NodoArbol) $3.obj;
          if (indice != null && indice.getTipo() != null && !indice.getTipo().toLowerCase().contains("int")) { 
            yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El índice para el acceso posicional debe ser de tipo entero, pero se recibió '" + indice.getTipo() + "'"); 
          }
          String tipoClase = regObjeto.getTipo(); // Ej: "ca" 
          RegistroTS regClaseDuenia = ts.buscar(tipoClase + ":main"); 
          String tipoAtributo = ts.obtenerTipoAtributoPosicional(tipoClase, indice);
          NodoArbol nodoAcceso = GeneradorCodigoIntermedio.crearNodoAST("[]", hojaID, indice);
          nodoAcceso.setTipo(tipoAtributo);
          RegistroTS regAtributo = ts.buscarAtributoPosicional(tipoClase, indice); 
          if (regAtributo != null && !ts.tienePermisoAcceso(regClaseDuenia, regAtributo, controlAmbitos.getAmbitoActual())) { 
            yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": Acceso Denegado. El atributo posicional en '" + tipoClase + "' es PRIVATE y el ámbito actual no es FRIEND"); 
            $$ = new ParserVal(null); 
          } else {
            NodoArbol expr = (NodoArbol) $6.obj;
            NodoArbol nodoAsig = GeneradorCodigoIntermedio.verificarEscrituraAtributo(regObjeto, indice, expr, lexico.getNroLinea());
            $$ = new ParserVal(nodoAsig);
          }
        }
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
    | ID '.' ID '(' lista_variables ')'
     {
        String nombreObjeto = ((RegistroTS) $1.obj).getLexema(); 
        String nombreMetodo = ((RegistroTS) $3.obj).getLexema(); 
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto); 
        if (regObjeto == null) { 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El objeto '" + nombreObjeto + "' no está declarado o no es visible"); 
          $$ = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) null)); 
        } else { 
          String tipoClase = regObjeto.getTipo();
          RegistroTS regClaseDuenia = ts.buscar(tipoClase + ":main");
          String claveMetodoDirecto = nombreMetodo + ":main:" + tipoClase; RegistroTS regMetodo = ts.buscar(claveMetodoDirecto); 
          if (regMetodo == null) { 
            regMetodo = ts.buscarEnHerencia(tipoClase, nombreMetodo); 
          } 
          if (regMetodo == null) { 
            yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El método '" + nombreMetodo + "' no existe en la clase '" + tipoClase + "' ni en su herencia"); 
            $$ = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) null)); 
          } else if (!"metodo".equals(regMetodo.getUso()) && !"nombre_funcion".equals(regMetodo.getUso())) { 
            yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El identificador '" + nombreMetodo + "' no es un método de la clase '" + tipoClase + "'"); 
            $$ = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) null)); 
          } else if (!ts.tienePermisoAcceso(regClaseDuenia, regMetodo, controlAmbitos.getAmbitoActual())) { 
            yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": Acceso Denegado. El método '" + nombreMetodo + "' es PRIVATE en '" + tipoClase + "'"); 
            $$ = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) null)); 
          } else { 
            NodoArbol subArbolArgs = (NodoArbol) $5.obj; 
            List<String> tiposFormales = regMetodo.getTiposParametros();
            NodoArbol hojaMetodo = GeneradorCodigoIntermedio.crearHojaAST(regMetodo);
            NodoArbol nodoCall = GeneradorCodigoIntermedio.crearNodoAST("CALL", hojaMetodo, subArbolArgs);
            nodoCall.setTipo(regMetodo.getTipoToken());
            $$ = new ParserVal(nodoCall);
          }
        }
     }
    
    | ID invocacion_ordenada
      {
        String lexemaFunc = ((RegistroTS) $1.obj).getLexema();
        RegistroTS regFuncion = buscarVariableVisibilidad(lexemaFunc);
        if (regFuncion == null) { 
          // Error 1: No declarada o fuera de alcance 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": La función o método '" + lexemaFunc + "' no está declarado o no es visible en este ámbito"); 
          $$ = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS)null)); 
        } else if (!"nombre_funcion".equalsIgnoreCase(regFuncion.getUso()) && !"metodo".equalsIgnoreCase(regFuncion.getUso())) { 
          // Error 2: El símbolo existe pero no es una función (ej: es una variable o clase) 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El identificador '" + lexemaFunc + "' es de uso '" + regFuncion.getUso() + "' y no puede ser invocado como función"); 
          $$ = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS)null)); 
        } else {
          NodoArbol infoInvoc = (NodoArbol) $2.obj;
          NodoArbol nodoCall = GeneradorCodigoIntermedio.procesarInvocacion(regFuncion, infoInvoc, lexico.getNroLinea());
          nodoCall.setTipo(regFuncion.getTipoToken());
          $$ = new ParserVal(nodoCall);
        }
      }
    | ID CORCHETE_IZQ expresion CORCHETE_DER
      { registrarEstructura("Acceso posicional");
        String nombreObjeto = ((RegistroTS) $1.obj).getLexema();
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto);
        if (regObjeto == null) { 
          // Error semántico: el objeto o variable no está declarado 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El objeto o variable '" + nombreObjeto + "' no está declarado o no es visible"); 
          $$ = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS)null)); 
        } else {
          NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST(regObjeto);
          NodoArbol indice = (NodoArbol) $3.obj;
          NodoArbol nodoAcceso = GeneradorCodigoIntermedio.verificarLecturaAtributo(regObj, indice, lexico.getNroLinea());
          $$ = new ParserVal(nodoAcceso);
        }
      }
    | ID
     {
        String lexemaVar = ((RegistroTS) $1.obj).getLexema(); 
        // Resolver visibilidad de adentro hacia afuera usando Name Mangling 
        RegistroTS regVariable = buscarVariableVisibilidad(lexemaVar); 
        if (regVariable == null) { 
          // Error 1: Variable no declarada o fuera de alcance 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": La variable '" + lexemaVar + "' no está declarada o no es visible en este ámbito"); 
          $$ = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS)null)); 
        } else if (!"variable".equalsIgnoreCase(regVariable.getUso()) && !"parametro".equalsIgnoreCase(regVariable.getUso()) && !"atributo".equalsIgnoreCase(regVariable.getUso()) && !"objeto".equalsIgnoreCase(regVariable.getUso())) { 
          // Error 2: Uso incorrecto de contexto (ej. usar un nombre de función o clase como operando) 
          yyerror("Error Semántico en línea " + lexico.getNroLinea() + ": El identificador '" + lexemaVar + "' es de uso '" + regVariable.getUso() + "' y no puede ser utilizado como una variable"); 
          $$ = new ParserVal(GeneradorCodigoIntermedio.crearHojaAST((RegistroTS) null)); 
        } else {
          NodoArbol hojaID = GeneradorCodigoIntermedio.crearHojaAST(registrarVariable);
          hojaID.setTipo(regVariable.getTipoToken());
          $$ = new ParserVal(hojaID);
        }
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
        NodoArbol orden = (NodoArbol) $5.obj;
        NodoArbol inv = GeneradorCodigoIntermedio.crearNodoAST("INVOC_ORDENADA", args, orden);
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

    // Si estamos dentro de una clase y no se encontró localmente, buscar en la Herencia 
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