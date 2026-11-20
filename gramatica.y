%{
import java.util.*;
%}

/* ============================================================================
   COMPILADOR GRUPO 34 - TP1/TP2/TP3/TP4
   Gramática depurada. No se utilizan ENTONCES, TODF ni TOUS.
   Las conversiones ushortint <-> doublef son implícitas (Tema 35).
   ========================================================================== */

%token ID CTE_USHORT CTE_DOUBLEF CADENA
%token IF ELSE END_IF BEGIN END POUT
%token WHILE REPEAT TYPEDEF PRIVATE FRIEND EXTENDS CLASS FUNCTION RET
%token USHORTINT DOUBLEF
%token ASIGNACION COMP_IGUAL COMP_DIST CORCHETE_IZQ CORCHETE_DER
%token COMP_MAYOR_IGUAL COMP_MENOR_IGUAL

%right ASIGNACION
%right '='
%left COMP_IGUAL COMP_DIST COMP_MAYOR_IGUAL COMP_MENOR_IGUAL '>' '<'
%left '+' '-'
%left '*' '/'
%right UMINUS

%start programa

%%

programa
    : ID fin_cabecera bloque_declarativo BEGIN lista_sentencias END ';'
      {
        NodoArbol nombre = gen.crearHojaAST((RegistroTS) $1.obj);
        NodoArbol decls = (NodoArbol) $3.obj;
        NodoArbol cuerpo = (NodoArbol) $5.obj;
        NodoArbol raiz = gen.crearNodoAST("PROGRAMA", nombre, decls);
        raiz.setTer(cuerpo);
        gen.setRaizAST(raiz);
        $$ = new ParserVal(raiz);
      }
    ;

fin_cabecera
    : ';'
    | /* vacío: se admite el nombre sin ';', como en el enunciado TP2 */
    ;

bloque_declarativo
    : bloque_declarativo declaracion
      { $$ = new ParserVal(secuencia((NodoArbol) $1.obj, (NodoArbol) $2.obj)); }
    | /* vacío */
      { $$ = new ParserVal((Object) null); }
    ;

declaracion
    : declaracion_variables
    | declaracion_typedef
    | declaracion_clase
    | declaracion_funcion
    | error ';'
      {
        yyerror("Declaración inválida; se resincroniza en ';'.");
        $$ = new ParserVal((Object) null);
      }
    ;

type_specifier
    : USHORTINT { $$ = new ParserVal((Object) "USHORTINT"); }
    | DOUBLEF   { $$ = new ParserVal((Object) "DOUBLEF"); }
    | ID        { $$ = new ParserVal((Object) ((RegistroTS) $1.obj).getLexema()); }
    ;

lista_variables
    : ID
      {
        ArrayList<RegistroTS> lista = new ArrayList<>();
        lista.add((RegistroTS) $1.obj);
        $$ = new ParserVal(lista);
      }
    | lista_variables ',' ID
      {
        @SuppressWarnings("unchecked")
        ArrayList<RegistroTS> lista = (ArrayList<RegistroTS>) $1.obj;
        lista.add((RegistroTS) $3.obj);
        $$ = $1;
      }
    ;

declaracion_variables
    : type_specifier lista_variables ';'
      {
        ArrayList<RegistroTS> regs = declararListaVariables((String) $1.obj,
                (List<RegistroTS>) $2.obj, lexico.getNroLinea(), "public");
        registrarEstructura("Declaración de variables");
        NodoArbol nodo = gen.crearNodoAST("VAR_DECL", null, null);
        nodo.setInfo(regs);
        $$ = new ParserVal(nodo);
      }
    ;

/* Tema 23: typedef ID = [lista de valores]; */
declaracion_typedef
    : TYPEDEF ID '=' CORCHETE_IZQ lista_valores CORCHETE_DER ';'
      {
        RegistroTS regID = (RegistroTS) $2.obj;
        String nombreTipo = regID.getLexema();
        String clave = nombreTipo + ":" + controlAmbitos.getAmbitoActual();
        if (ts.buscar(clave) != null) {
          errorSemantico("El tipo '" + nombreTipo + "' ya fue definido en el ámbito actual.");
          $$ = new ParserVal((Object) null);
        } else {
          List<RegistroTS> valores = (List<RegistroTS>) $5.obj;
          if (valores == null || valores.isEmpty()) {
            errorSemantico("La enumeración '" + nombreTipo + "' debe tener al menos un valor.");
          }
          String base = null;
          for (RegistroTS v : valores) {
            String baseValor = (v.getIdToken() == CTE_DOUBLEF) ? "DOUBLEF" : "USHORTINT";
            if (base != null && !base.equals(baseValor)) {
              errorSemantico("La enumeración '" + nombreTipo + "' mezcla valores ushortint y doublef.");
              break;
            }
            base = baseValor;
          }
          if (base == null) base = "USHORTINT";
          RegistroTS regTipo = ts.registrarTipoPersonalizado(clave, "type_name", valores);
          regTipo.setNroLinea(regID.getNroLinea());
          regTipo.setTipoBaseEnum(base);
          registrarEstructura("Enumeración (typedef)");
          NodoArbol nodo = gen.crearNodoAST("TYPEDEF", gen.crearHojaAST(regTipo), null);
          $$ = new ParserVal(nodo);
        }
      }
    ;

lista_valores
    : valor_enum
      {
        ArrayList<RegistroTS> lista = new ArrayList<>();
        lista.add((RegistroTS) $1.obj);
        $$ = new ParserVal(lista);
      }
    | lista_valores ',' valor_enum
      {
        ArrayList<RegistroTS> lista = (ArrayList<RegistroTS>) $1.obj;
        lista.add((RegistroTS) $3.obj);
        $$ = $1;
      }
    ;

valor_enum
    : constante
      {
        NodoArbol n = (NodoArbol) $1.obj;
        $$ = new ParserVal(n.getRegTS());
      }
    | '-' constante
      {
        aplicarSignoNegativo($2);
        NodoArbol n = (NodoArbol) $2.obj;
        $$ = new ParserVal(n.getRegTS());
      }
    ;

/* Funciones: al menos un parámetro, como exige la consigna TP2. */
declaracion_funcion
    : cabecera_funcion bloque_declarativo BEGIN lista_sentencias END ';'
      {
        InfoFuncion info = (InfoFuncion) $1.obj;
        NodoArbol decls = (NodoArbol) $2.obj;
        NodoArbol cuerpo = (NodoArbol) $4.obj;
        $$ = new ParserVal(cerrarFuncion(info, decls, cuerpo));
      }
    ;

cabecera_funcion
    : type_specifier FUNCTION ID '(' lista_parametros_formales ')'
      {
        $$ = new ParserVal(abrirFuncion((String) $1.obj, (RegistroTS) $3.obj,
                (List<ParametroFormal>) $5.obj, "nombre_funcion", "public"));
      }
    ;

lista_parametros_formales
    : type_specifier ID
      {
        ArrayList<ParametroFormal> lista = new ArrayList<>();
        lista.add(new ParametroFormal(((RegistroTS) $2.obj).getLexema(), (String) $1.obj));
        $$ = new ParserVal(lista);
      }
    | lista_parametros_formales ',' type_specifier ID
      {
        ArrayList<ParametroFormal> lista = (ArrayList<ParametroFormal>) $1.obj;
        lista.add(new ParametroFormal(((RegistroTS) $4.obj).getLexema(), (String) $3.obj));
        $$ = $1;
      }
    ;

/* Métodos y funciones comparten la misma apertura de ámbito; la diferencia es
   el uso ("metodo" dentro de una clase, "nombre_funcion" fuera de ella). */
declaracion_clase
    : encabezado_clase herencia_opcional cuerpo_clase
      {
        RegistroTS regClase = (RegistroTS) $1.obj;
        controlAmbitos.cerrarAmbito();
        if (!pilaClases.isEmpty()) pilaClases.pop();
        if (!pilaAmbitosClase.isEmpty()) pilaAmbitosClase.pop();
        NodoArbol metodos = null;
        for (NodoArbol m : regClase.getMetodos()) {
          metodos = secuencia(metodos, m);
        }
        NodoArbol nodo = gen.crearNodoAST("CLASS", gen.crearHojaAST(regClase), metodos);
        $$ = new ParserVal(nodo);
      }
    ;

encabezado_clase
    : CLASS ID
      {
        String nombre = ((RegistroTS) $2.obj).getLexema();
        String clave = nombre + ":" + controlAmbitos.getAmbitoActual();
        RegistroTS regClase = ts.buscar(clave);
        if (regClase != null && "nombre_clase".equalsIgnoreCase(regClase.getUso())) {
          errorSemantico("La clase '" + nombre + "' ya fue declarada en el ámbito actual.");
        } else {
          regClase = ts.registrarClase(clave, "nombre_clase");
          regClase.setNroLinea(((RegistroTS) $2.obj).getNroLinea());
        }
        pilaClases.push(regClase);
        controlAmbitos.abrirAmbito(nombre);
        pilaAmbitosClase.push(controlAmbitos.getAmbitoActual());
        $$ = new ParserVal(regClase);
      }
    ;

herencia_opcional
    : EXTENDS lista_variables ';'
      { procesarHerencia((List<RegistroTS>) $2.obj); }
    | /* vacío */
      { }
    ;

cuerpo_clase
    : BEGIN lista_miembros END ';'
    ;

lista_miembros
    : lista_miembros miembro_clase
    | /* vacío */
    ;

miembro_clase
    : type_specifier lista_variables ';'
      {
        declararListaVariables((String) $1.obj, (List<RegistroTS>) $2.obj, lexico.getNroLinea(), "public");
        registrarEstructura("Atributo de clase");
      }
    | PRIVATE type_specifier lista_variables ';'
      {
        declararListaVariables((String) $2.obj, (List<RegistroTS>) $3.obj, lexico.getNroLinea(), "private");
        registrarEstructura("Atributo privado de clase");
      }
    | cabecera_metodo bloque_declarativo BEGIN lista_sentencias END ';'
      {
        InfoFuncion info = (InfoFuncion) $1.obj;
        cerrarFuncion(info, (NodoArbol) $2.obj, (NodoArbol) $4.obj);
        registrarEstructura("Método de clase");
      }
    | FRIEND ID ';'
      {
        registrarAmigo(((RegistroTS) $2.obj).getLexema());
        registrarEstructura("Declaración friend");
      }
    | EXTENDS lista_variables ';'
      {
        procesarHerencia((List<RegistroTS>) $2.obj);
        registrarEstructura("Herencia");
      }
    | error ';'
      {
        yyerror("Miembro de clase inválido; se resincroniza en ';'.");
      }
    ;

cabecera_metodo
    : type_specifier FUNCTION ID '(' lista_parametros_formales ')'
      {
        $$ = new ParserVal(abrirFuncion((String) $1.obj, (RegistroTS) $3.obj,
                (List<ParametroFormal>) $5.obj, "metodo", "public"));
      }
    | PRIVATE type_specifier FUNCTION ID '(' lista_parametros_formales ')'
      {
        $$ = new ParserVal(abrirFuncion((String) $2.obj, (RegistroTS) $4.obj,
                (List<ParametroFormal>) $6.obj, "metodo", "private"));
      }
    | type_specifier ID '(' lista_parametros_formales ')'
      {
        $$ = new ParserVal(abrirFuncion((String) $1.obj, (RegistroTS) $2.obj,
                (List<ParametroFormal>) $4.obj, "metodo", "public"));
      }
    | PRIVATE type_specifier ID '(' lista_parametros_formales ')'
      {
        $$ = new ParserVal(abrirFuncion((String) $2.obj, (RegistroTS) $3.obj,
                (List<ParametroFormal>) $5.obj, "metodo", "private"));
      }
    ;

/* ===================== SENTENCIAS EJECUTABLES ===================== */

bloque_compuesto
    : sentencia
      { $$ = $1; }
    | BEGIN lista_sentencias END ';'
      {
        $$ = $2;
        registrarEstructura("Bloque compuesto");
      }
    ;

lista_sentencias
    : lista_sentencias sentencia
      { $$ = new ParserVal(secuencia((NodoArbol) $1.obj, (NodoArbol) $2.obj)); }
    | /* vacío */
      { $$ = new ParserVal((Object) null); }
    ;

sentencia
    : asignacion
    | salida_pout ';'
      { $$ = $1; }
    | sentencia_if
    | sentencia_while
    | sentencia_retorno
    | error ';'
      {
        yyerror("Sentencia inválida; se resincroniza en ';'.");
        $$ = new ParserVal((Object) null);
      }
    ;

asignacion
    : ID ASIGNACION expresion ';'
      {
        RegistroTS reg = buscarVariableVisibilidad(((RegistroTS) $1.obj).getLexema());
        if (reg == null) {
          errorSemantico("La variable '" + ((RegistroTS) $1.obj).getLexema() + "' no está declarada o no es visible.");
          $$ = new ParserVal((Object) null);
        } else if (!ts.esAsignable(reg)) {
          errorSemantico("El símbolo '" + reg.getNombreSimple() + "' no es asignable (uso: " + reg.getUso() + ").");
          $$ = new ParserVal((Object) null);
        } else {
          NodoArbol nodo = gen.verificarAsignacion(gen.crearHojaAST(reg), (NodoArbol) $3.obj, lexico.getNroLinea());
          registrarEstructura("Asignación");
          $$ = new ParserVal(nodo);
        }
      }
    | ID CORCHETE_IZQ expresion CORCHETE_DER ASIGNACION expresion ';'
      {
        String nombreObjeto = ((RegistroTS) $1.obj).getLexema();
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto);
        if (regObjeto == null) {
          errorSemantico("El objeto '" + nombreObjeto + "' no está declarado o no es visible.");
          $$ = new ParserVal((Object) null);
        } else if (!gen.esTipoObjeto(regObjeto.getTipoToken())) {
          errorSemantico("El identificador '" + nombreObjeto + "' no es un objeto con acceso posicional.");
          $$ = new ParserVal((Object) null);
        } else {
          NodoArbol indice = (NodoArbol) $3.obj;
          NodoArbol expr = (NodoArbol) $6.obj;
          boolean permitido = verificarPermisoPosicional(regObjeto, indice);
          if (!permitido) {
            errorSemantico("Acceso denegado a un atributo privado de la clase '" + regObjeto.getTipoToken() + "'.");
            $$ = new ParserVal((Object) null);
          } else {
            NodoArbol nodo = gen.verificarEscrituraAtributo(regObjeto, indice, expr, lexico.getNroLinea());
            registrarEstructura("Asignación a atributo posicional");
            $$ = new ParserVal(nodo);
          }
        }
      }
    ;

sentencia_if
    : IF '(' condicion ')' bloque_compuesto rama_else END_IF ';'
      {
        NodoArbol cond = gen.crearNodoAST("cond", (NodoArbol) $3.obj, null);
        NodoArbol cuerpo = gen.crearNodoAST("cuerpo", (NodoArbol) $5.obj, (NodoArbol) $6.obj);
        NodoArbol nodo = gen.crearNodoAST("IF", cond, cuerpo);
        registrarEstructura("Sentencia IF");
        $$ = new ParserVal(nodo);
      }
    ;

rama_else
    : ELSE bloque_compuesto
      { $$ = $2; }
    | /* vacío */
      { $$ = new ParserVal((Object) null); }
    ;

sentencia_while
    : WHILE '(' condicion ')' REPEAT bloque_compuesto
      {
        NodoArbol cond = gen.crearNodoAST("cond", (NodoArbol) $3.obj, null);
        NodoArbol cuerpo = gen.crearNodoAST("cuerpo", (NodoArbol) $6.obj, null);
        NodoArbol nodo = gen.crearNodoAST("WHILE", cond, cuerpo);
        registrarEstructura("Sentencia WHILE-REPEAT");
        $$ = new ParserVal(nodo);
      }
    ;

sentencia_retorno
    : RET '(' expresion ')' ';'
      {
        if (pilaTiposRetorno.isEmpty()) {
          errorSemantico("Sentencia 'ret' fuera de una función.");
          $$ = new ParserVal((Object) null);
        } else {
          String tipoFuncion = pilaTiposRetorno.peek();
          NodoArbol expr = (NodoArbol) $3.obj;
          NodoArbol nodo = crearRetorno(expr, tipoFuncion);
          registrarEstructura("Sentencia RET");
          $$ = new ParserVal(nodo);
        }
      }
    ;

salida_pout
    : POUT '(' CADENA ')'
      {
        NodoArbol hoja = gen.crearHojaAST((RegistroTS) $3.obj);
        NodoArbol nodo = gen.crearNodoAST("POUT", hoja, null);
        registrarEstructura("Sentencia POUT (cadena)");
        $$ = new ParserVal(nodo);
      }
    | POUT '(' expresion ')'
      {
        NodoArbol nodo = gen.crearNodoAST("POUT", (NodoArbol) $3.obj, null);
        registrarEstructura("Sentencia POUT (expresión)");
        $$ = new ParserVal(nodo);
      }
    ;

/* ===================== EXPRESIONES ===================== */

condicion
    : expresion comparador expresion
      {
        NodoArbol nodo = gen.verificarYConvertirTipos((String) $2.obj,
                (NodoArbol) $1.obj, (NodoArbol) $3.obj, lexico.getNroLinea());
        $$ = new ParserVal(nodo);
      }
    ;

comparador
    : COMP_IGUAL       { $$ = new ParserVal((Object) "=="); }
    | COMP_DIST        { $$ = new ParserVal((Object) "!="); }
    | COMP_MAYOR_IGUAL { $$ = new ParserVal((Object) ">="); }
    | COMP_MENOR_IGUAL { $$ = new ParserVal((Object) "<="); }
    | '>'              { $$ = new ParserVal((Object) ">"); }
    | '<'              { $$ = new ParserVal((Object) "<"); }
    ;

expresion
    : expresion '+' expresion
      { $$ = new ParserVal(gen.verificarYConvertirTipos("+", (NodoArbol) $1.obj, (NodoArbol) $3.obj, lexico.getNroLinea())); }
    | expresion '-' expresion
      { $$ = new ParserVal(gen.verificarYConvertirTipos("-", (NodoArbol) $1.obj, (NodoArbol) $3.obj, lexico.getNroLinea())); }
    | expresion '*' expresion
      { $$ = new ParserVal(gen.verificarYConvertirTipos("*", (NodoArbol) $1.obj, (NodoArbol) $3.obj, lexico.getNroLinea())); }
    | expresion '/' expresion
      { $$ = new ParserVal(gen.verificarYConvertirTipos("/", (NodoArbol) $1.obj, (NodoArbol) $3.obj, lexico.getNroLinea())); }
    | '-' expresion %prec UMINUS
      {
        NodoArbol expr = (NodoArbol) $2.obj;
        if (expr != null && expr.getRegTS() != null) {
          aplicarSignoNegativo($2);
          $$ = $2;
        } else {
          NodoArbol nodo = gen.crearNodoAST("UMINUS", expr, null);
          if (expr != null) nodo.setTipoToken(expr.getTipoToken());
          $$ = new ParserVal(nodo);
        }
      }
    | '(' expresion ')'
      { $$ = $2; }
    | ID '=' valor_asignable
      {
        RegistroTS reg = buscarVariableVisibilidad(((RegistroTS) $1.obj).getLexema());
        if (reg == null) {
          errorSemantico("Variable '" + ((RegistroTS) $1.obj).getLexema() + "' no declarada en asignación interna.");
          $$ = new ParserVal((Object) null);
        } else if (!ts.esAsignable(reg)) {
          errorSemantico("El símbolo '" + reg.getNombreSimple() + "' no es asignable en una expresión.");
          $$ = new ParserVal((Object) null);
        } else {
          NodoArbol nodo = gen.verificarAsignacionEnExpresion(gen.crearHojaAST(reg), (NodoArbol) $3.obj, lexico.getNroLinea());
          registrarEstructura("Asignación interna (Tema 18)");
          $$ = new ParserVal(nodo);
        }
      }
    | ID '(' lista_expresiones ')' orden_opcional
      {
        RegistroTS regFunc = buscarFuncionVisible(((RegistroTS) $1.obj).getLexema());
        if (regFunc == null) {
          errorSemantico("La función '" + ((RegistroTS) $1.obj).getLexema() + "' no está declarada o no es visible.");
          $$ = new ParserVal((Object) null);
        } else {
          NodoArbol nodo = gen.crearInvocacion(regFunc, (NodoArbol) $3.obj, (NodoArbol) $5.obj, lexico.getNroLinea());
          registrarEstructura("Invocación de función");
          $$ = new ParserVal(nodo);
        }
      }
    | ID '.' ID '(' lista_expresiones ')' orden_opcional
      {
        String nombreObjeto = ((RegistroTS) $1.obj).getLexema();
        String nombreMetodo = ((RegistroTS) $3.obj).getLexema();
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto);
        if (regObjeto == null || !gen.esTipoObjeto(regObjeto.getTipoToken())) {
          errorSemantico("El objeto '" + nombreObjeto + "' no está declarado o no es de un tipo con métodos.");
          $$ = new ParserVal((Object) null);
        } else {
          String tipoClase = regObjeto.getTipoToken();
          RegistroTS regMetodo = ts.buscarMetodoEnHerencia(tipoClase, nombreMetodo);
          if (regMetodo == null) {
            errorSemantico("El método '" + nombreMetodo + "' no existe en la clase '" + tipoClase + "' ni en su herencia.");
            $$ = new ParserVal((Object) null);
          } else {
            RegistroTS regClase = ts.buscar(tipoClase + ":main");
            if (regClase != null && !ts.tienePermisoAcceso(regClase, regMetodo, controlAmbitos.getAmbitoActual())) {
              errorSemantico("Acceso denegado: el método '" + nombreMetodo + "' es privado en '" + tipoClase + "'.");
              $$ = new ParserVal((Object) null);
            } else {
              NodoArbol nodo = gen.crearInvocacion(regMetodo, (NodoArbol) $5.obj, (NodoArbol) $7.obj, lexico.getNroLinea());
              registrarEstructura("Invocación de método");
              $$ = new ParserVal(nodo);
            }
          }
        }
      }
    | ID CORCHETE_IZQ expresion CORCHETE_DER
      {
        String nombreObjeto = ((RegistroTS) $1.obj).getLexema();
        RegistroTS regObjeto = buscarVariableVisibilidad(nombreObjeto);
        if (regObjeto == null || !gen.esTipoObjeto(regObjeto.getTipoToken())) {
          errorSemantico("El objeto '" + nombreObjeto + "' no admite acceso posicional.");
          $$ = new ParserVal((Object) null);
        } else {
          boolean permitido = verificarPermisoPosicional(regObjeto, (NodoArbol) $3.obj);
          if (!permitido) {
            errorSemantico("Acceso denegado a un atributo privado de la clase '" + regObjeto.getTipoToken() + "'.");
            $$ = new ParserVal((Object) null);
          } else {
            NodoArbol nodo = gen.verificarLecturaAtributo(regObjeto, (NodoArbol) $3.obj, lexico.getNroLinea());
            registrarEstructura("Lectura de atributo posicional");
            $$ = new ParserVal(nodo);
          }
        }
      }
    | ID
      {
        String lexemaVar = ((RegistroTS) $1.obj).getLexema();
        RegistroTS reg = buscarVariableVisibilidad(lexemaVar);
        if (reg == null) {
          errorSemantico("La variable '" + lexemaVar + "' no está declarada o no es visible.");
          $$ = new ParserVal((Object) null);
        } else if ("nombre_clase".equalsIgnoreCase(reg.getUso())
                || "nombre_funcion".equalsIgnoreCase(reg.getUso())
                || "metodo".equalsIgnoreCase(reg.getUso())
                || "type_name".equalsIgnoreCase(reg.getUso())) {
          errorSemantico("El identificador '" + lexemaVar + "' no puede usarse como variable.");
          $$ = new ParserVal((Object) null);
        } else {
          $$ = new ParserVal(gen.crearHojaAST(reg));
        }
      }
    | constante
      { $$ = $1; }
    ;

valor_asignable
    : ID
      {
        RegistroTS reg = buscarVariableVisibilidad(((RegistroTS) $1.obj).getLexema());
        if (reg == null) {
          errorSemantico("Variable '" + ((RegistroTS) $1.obj).getLexema() + "' no declarada.");
          $$ = new ParserVal((Object) null);
        } else {
          $$ = new ParserVal(gen.crearHojaAST(reg));
        }
      }
    | constante
      { $$ = $1; }
    | '-' constante
      {
        aplicarSignoNegativo($2);
        $$ = $2;
      }
    ;

lista_expresiones
    : expresion
      { $$ = $1; }
    | lista_expresiones ',' expresion
      {
        NodoArbol nodo = gen.crearNodoAST(",", (NodoArbol) $1.obj, (NodoArbol) $3.obj);
        $$ = new ParserVal(nodo);
      }
    ;

orden_opcional
    : CORCHETE_IZQ lista_orden CORCHETE_DER
      { $$ = $2; }
    | /* vacío */
      { $$ = new ParserVal((Object) null); }
    ;

lista_orden
    : constante
      { $$ = $1; }
    | lista_orden ',' constante
      {
        NodoArbol nodo = gen.crearNodoAST(",", (NodoArbol) $1.obj, (NodoArbol) $3.obj);
        $$ = new ParserVal(nodo);
      }
    ;

constante
    : CTE_USHORT
      { $$ = new ParserVal(gen.crearHojaAST((RegistroTS) $1.obj)); }
    | CTE_DOUBLEF
      {
        validarConstanteDoublef($1);
        $$ = new ParserVal(gen.crearHojaAST((RegistroTS) $1.obj));
      }
    ;

%%

/* ============================================================================
   CÓDIGO JAVA DE SOPORTE DEL PARSER
   ========================================================================== */

private AnalizadorLexico lexico;
private TablaDeSimbolos ts;
private GeneradorCodigoIntermedio gen;
private ControlAmbitos controlAmbitos;
private boolean huboError = false;
private boolean modoSilencioso = false;
private final List<String> erroresSintacticos = new ArrayList<>();
private final List<String> estructurasDetectadas = new ArrayList<>();
private final Deque<String> pilaTiposRetorno = new ArrayDeque<>();
private final Deque<RegistroTS> pilaClases = new ArrayDeque<>();
private final Deque<String> pilaAmbitosClase = new ArrayDeque<>();
private final Map<String, NodoArbol> cuerposFunciones = new LinkedHashMap<>();

public Parser(AnalizadorLexico lexico, TablaDeSimbolos ts) {
    this.lexico = lexico;
    this.ts = ts;
    this.gen = new GeneradorCodigoIntermedio(ts);
    this.controlAmbitos = new ControlAmbitos();
}

private int yylex() {
    int token = lexico.yylex();
    this.yylval = (lexico.yylval != null) ? lexico.yylval : new ParserVal();
    return token;
}

public void yyerror(String msg) {
    this.huboError = true;
    String lexema = "";
    if (yylval != null && yylval.obj instanceof RegistroTS) {
        lexema = ((RegistroTS) yylval.obj).getLexema();
    }
    String tokenNombre;
    if (yychar >= 0 && yychar < yyname.length && yyname[yychar] != null) {
        tokenNombre = yyname[yychar];
    } else if (!lexema.isEmpty()) {
        tokenNombre = lexema;
    } else {
        tokenNombre = "TOKEN_INESPERADO";
    }
    String err = "Línea " + lexico.getNroLinea() + ": Error Sintáctico (token: "
            + tokenNombre + "): " + msg;
    erroresSintacticos.add(err);
    if (!modoSilencioso) System.err.println(err);
}

public void setModoSilencioso(boolean valor) {
    this.modoSilencioso = valor;
}

public boolean huboError() {
    return huboError || !erroresSintacticos.isEmpty();
}

public List<String> getErroresSintacticos() {
    return new ArrayList<>(erroresSintacticos);
}

public List<String> getEstructurasDetectadas() {
    return new ArrayList<>(estructurasDetectadas);
}

public GeneradorCodigoIntermedio getGenerador() {
    return gen;
}

public TablaDeSimbolos getTablaSimbolos() {
    return ts;
}

public Map<String, NodoArbol> getCuerposFunciones() {
    return new LinkedHashMap<>(cuerposFunciones);
}

private void registrarEstructura(String nombre) {
    estructurasDetectadas.add(nombre);
}

private void errorSemantico(String mensaje) {
    gen.registrarErrorSemantico(lexico.getNroLinea(), mensaje);
    huboError = true;
}

private NodoArbol secuencia(NodoArbol a, NodoArbol b) {
    if (a == null) return b;
    if (b == null) return a;
    return gen.crearNodoAST(";", a, b);
}

private NodoArbol crearRetorno(NodoArbol expr, String tipoFuncion) {
    NodoArbol ret = gen.crearNodoAST("RET", expr, null);
    if (expr == null) {
        ret.setTipoToken(tipoFuncion);
        return ret;
    }
    String tExpr = gen.obtenerTipoDato(expr);
    String baseEsperada = gen.tipoBaseDe(tipoFuncion);
    String baseExpr = gen.tipoBaseDe(tExpr);
    if (baseEsperada.equals("DOUBLEF") && baseExpr.equals("USHORTINT")) {
        NodoArbol conv = gen.crearNodoAST("ItoD", expr, null);
        conv.setTipoToken("DOUBLEF");
        ret.setIzq(conv);
        ret.setTipoToken("DOUBLEF");
    } else if (baseEsperada.equals("USHORTINT") && baseExpr.equals("DOUBLEF")) {
        errorSemantico("La función retorna ushortint y se encontró una expresión doublef.");
        ret.setTipoToken("USHORTINT");
    } else if (!baseEsperada.equals(baseExpr)) {
        errorSemantico("Tipo de retorno incompatible: se esperaba '" + tipoFuncion + "' y se recibió '" + tExpr + "'.");
        ret.setTipoToken(tipoFuncion);
    } else {
        ret.setTipoToken(tipoFuncion);
    }
    return ret;
}

private String tipoCanonico(String tipo) {
    if (tipo == null) return null;
    if (tipo.equalsIgnoreCase("USHORTINT")) return "USHORTINT";
    if (tipo.equalsIgnoreCase("DOUBLEF")) return "DOUBLEF";
    return tipo;
}

private RegistroTS resolverTipoDeclaracion(String tipo) {
    String canonico = tipoCanonico(tipo);
    if ("USHORTINT".equals(canonico) || "DOUBLEF".equals(canonico)) {
        return null;
    }
    RegistroTS reg = ts.buscarTipoVisible(tipo, controlAmbitos.getJerarquiaBusqueda(tipo));
    if (reg == null) {
        errorSemantico("El tipo '" + tipo + "' no está declarado o no es visible.");
    }
    return reg;
}

private ArrayList<RegistroTS> declararListaVariables(String tipo, List<RegistroTS> lista, int nroLinea, String visibilidad) {
    ArrayList<RegistroTS> resultado = new ArrayList<>();
    if (lista == null) return resultado;

    String tipoResuelto = tipoCanonico(tipo);
    RegistroTS tipoReg = resolverTipoDeclaracion(tipo);
    boolean esClase = tipoReg != null && "nombre_clase".equalsIgnoreCase(tipoReg.getUso());
    if (esClase) {
        tipoResuelto = tipoReg.getNombreSimple();
    } else if (tipoReg != null && tipoReg.esTipoEnum()) {
        tipoResuelto = tipoReg.getNombreSimple();
    }

    String ambito = controlAmbitos.getAmbitoActual();
    boolean dentroDeClase = !pilaAmbitosClase.isEmpty()
            && pilaAmbitosClase.peek().equals(ambito);

    for (RegistroTS original : lista) {
        String nombre = original.getLexema();
        String clave = nombre + ":" + ambito;
        if (ts.buscar(clave) != null) {
            errorSemantico("El símbolo '" + nombre + "' ya fue declarado en el ámbito actual.");
            continue;
        }
        String uso;
        if (dentroDeClase) {
            uso = "atributo";
        } else if (esClase) {
            uso = "objeto";
        } else {
            uso = "variable";
        }
        RegistroTS reg = ts.registrarVariable(clave, tipoResuelto, uso);
        reg.setNroLinea(original.getNroLinea() > 0 ? original.getNroLinea() : nroLinea);
        if (dentroDeClase) {
            reg.setVisibilidad(visibilidad);
            RegistroTS clase = pilaClases.peek();
            if (clase != null) clase.agregarAtributo(reg);
        }
        resultado.add(reg);
    }
    return resultado;
}

private InfoFuncion abrirFuncion(String tipoRetorno, RegistroTS regID,
        List<ParametroFormal> parametros, String uso, String visibilidad) {
    String nombre = regID.getLexema();
    String ambitoPadre = controlAmbitos.getAmbitoActual();
    String clave = nombre + ":" + ambitoPadre;

    if (ts.buscar(clave) != null) {
        errorSemantico("La función o método '" + nombre + "' ya fue declarado en el ámbito actual.");
    }

    RegistroTS regFunc = ts.registrarFuncion(clave, tipoRetorno, uso, null);
    regFunc.setNroLinea(regID.getNroLinea());
    regFunc.setVisibilidad(visibilidad);
    for (ParametroFormal p : parametros) {
        regFunc.agregarParametroFormal(p);
    }

    pilaTiposRetorno.push(tipoRetorno);
    controlAmbitos.abrirAmbito(nombre);

    for (ParametroFormal p : parametros) {
        String claveParam = p.getNombre() + ":" + controlAmbitos.getAmbitoActual();
        if (ts.buscar(claveParam) != null) {
            errorSemantico("Parámetro redeclarado '" + p.getNombre() + "'.");
        } else {
            RegistroTS regParam = ts.registrarVariable(claveParam, tipoCanonico(p.getTipo()), "parametro");
            regParam.setNroLinea(regID.getNroLinea());
        }
    }

    return new InfoFuncion(regFunc, tipoRetorno, nombre, ambitoPadre);
}

private NodoArbol cerrarFuncion(InfoFuncion info, NodoArbol decls, NodoArbol cuerpo) {
    if (!pilaTiposRetorno.isEmpty()) pilaTiposRetorno.pop();
    controlAmbitos.cerrarAmbito();
    NodoArbol nodo = gen.crearNodoAST("FUNC", decls, cuerpo);
    nodo.setInfo(info);
    nodo.setTipoToken(info.getTipoRetorno());
    cuerposFunciones.put(info.getRegistro().getLexema(), nodo);
    if (!pilaClases.isEmpty() && !pilaAmbitosClase.isEmpty()
            && pilaAmbitosClase.peek().equals(info.getAmbitoPadre())) {
        pilaClases.peek().agregarMetodo(nodo);
    }
    return nodo;
}

private void procesarHerencia(List<RegistroTS> padres) {
    if (pilaClases.isEmpty() || padres == null) return;
    RegistroTS clase = pilaClases.peek();
    String claveActual = clase.getNombreSimple();
    for (RegistroTS padre : padres) {
        String nombrePadre = padre.getLexema();
        RegistroTS regPadre = ts.buscar(nombrePadre + ":main");
        if (regPadre == null) regPadre = ts.buscar(nombrePadre);
        if (regPadre == null || !"nombre_clase".equalsIgnoreCase(regPadre.getUso())) {
            errorSemantico("La clase base '" + nombrePadre + "' no está declarada o no es visible.");
        } else if (claveActual.equals(nombrePadre)
                || ts.esHerenciaCircular(regPadre.getLexema(), clase.getLexema())) {
            errorSemantico("Herencia circular detectada con la clase '" + nombrePadre + "'.");
        } else if (!clase.getClasesHeredadas().contains(nombrePadre)) {
            clase.agregarClaseHeredada(nombrePadre);
        }
    }
}

private void registrarAmigo(String nombreAmigo) {
    if (pilaClases.isEmpty()) {
        errorSemantico("La declaración 'friend' solo es válida dentro de una clase.");
        return;
    }
    pilaClases.peek().agregarAmigo(nombreAmigo);
}

private boolean verificarPermisoPosicional(RegistroTS regObjeto, NodoArbol indice) {
    RegistroTS cte = obtenerConstante(indice);
    if (cte == null) return true; // el chequeo exacto de índice variable se hace al ejecutar
    int pos = (int) cte.getValorNum();
    if ("UMINUS".equals(indice.getEtiqueta())) pos = -pos;
    RegistroTS attr = ts.obtenerAtributoPosicional(regObjeto.getTipoToken(), pos);
    if (attr == null) return true;
    RegistroTS clase = ts.buscar(regObjeto.getTipoToken() + ":main");
    return ts.tienePermisoAcceso(clase, attr, controlAmbitos.getAmbitoActual());
}

private RegistroTS obtenerConstante(NodoArbol nodo) {
    if (nodo == null) return null;
    RegistroTS reg = nodo.getRegTS();
    if (reg != null && (reg.getIdToken() == CTE_USHORT || reg.getIdToken() == CTE_DOUBLEF)) {
        return reg;
    }
    if ("UMINUS".equals(nodo.getEtiqueta())) return obtenerConstante(nodo.getIzq());
    return null;
}

private RegistroTS buscarVariableVisibilidad(String lexemaOriginal) {
    for (String clave : controlAmbitos.getJerarquiaBusqueda(lexemaOriginal)) {
        RegistroTS reg = ts.buscar(clave);
        if (reg != null && !"nombre_clase".equalsIgnoreCase(reg.getUso())
                && !"nombre_funcion".equalsIgnoreCase(reg.getUso())
                && !"metodo".equalsIgnoreCase(reg.getUso())
                && !"type_name".equalsIgnoreCase(reg.getUso())) {
            return reg;
        }
    }
    String ambito = controlAmbitos.getAmbitoActual();
    String[] partes = ambito.split(":");
    if (partes.length >= 2) {
        RegistroTS heredado = ts.buscarEnHerencia(partes[1], lexemaOriginal);
        if (heredado != null) return heredado;
    }
    return null;
}

private RegistroTS buscarFuncionVisible(String lexemaOriginal) {
    for (String clave : controlAmbitos.getJerarquiaBusqueda(lexemaOriginal)) {
        RegistroTS reg = ts.buscar(clave);
        if (reg != null && ("nombre_funcion".equalsIgnoreCase(reg.getUso())
                || "metodo".equalsIgnoreCase(reg.getUso()))) {
            return reg;
        }
    }
    String ambito = controlAmbitos.getAmbitoActual();
    String[] partes = ambito.split(":");
    if (partes.length >= 2) {
        RegistroTS heredado = ts.buscarMetodoEnHerencia(partes[1], lexemaOriginal);
        if (heredado != null) return heredado;
    }
    return null;
}

/* TP2 punto c: el léxico no conoce el signo. El Parser revalida el rango. */
private void aplicarSignoNegativo(ParserVal v) {
    if (v == null || !(v.obj instanceof NodoArbol)) return;
    NodoArbol nodo = (NodoArbol) v.obj;
    RegistroTS reg = nodo.getRegTS();
    if (reg == null) return;

    if (reg.getIdToken() == CTE_USHORT) {
        errorSemantico("La constante entera sin signo '" + reg.getLexema() + "' no admite valor negativo.");
        return;
    }
    if (reg.getIdToken() != CTE_DOUBLEF) return;

    String lexema = reg.getLexema();
    if (lexema.startsWith("-")) return;
    try {
        double valorNegativo = -Double.parseDouble(lexema.replace("d", "e").replace("D", "e"));
        if (!ts.verificarRangoDoublef(valorNegativo)) {
            errorSemantico("Constante doublef negativa fuera de rango: -" + lexema);
            return;
        }
        RegistroTS regNeg = ts.registrarConstanteNegativa(lexema, lexico.getNroLinea(), valorNegativo);
        NodoArbol hoja = new NodoArbol(regNeg);
        v.obj = hoja;
        v.sval = regNeg.getLexema();
    } catch (NumberFormatException e) {
        errorSemantico("Constante doublef negativa mal formada: -" + lexema);
    }
}

private void validarConstanteDoublef(ParserVal v) {
    if (v == null || !(v.obj instanceof RegistroTS)) return;
    RegistroTS reg = (RegistroTS) v.obj;
    try {
        double valor = Double.parseDouble(reg.getLexema().replace("d", "e").replace("D", "e"));
        if (!ts.verificarRangoDoublef(valor)) {
            errorSemantico("Constante doublef fuera de rango: " + reg.getLexema());
        }
    } catch (NumberFormatException e) {
        errorSemantico("Constante doublef mal formada: " + reg.getLexema());
    }
}
