import java.util.ArrayList;
import java.util.List;

// Análisis semántico, construcción del AST y soporte para la generación de código.
public class GeneradorCodigoIntermedio {

    private NodoArbol raizAST;
    private final List<String> erroresSemanticos;
    private final TablaDeSimbolos tablaSimbolos;

    public GeneradorCodigoIntermedio(TablaDeSimbolos tablaSimbolos) {
        this.raizAST = null;
        this.erroresSemanticos = new ArrayList<>();
        this.tablaSimbolos = tablaSimbolos;
    }

    public NodoArbol crearNodoAST(String etiqueta, NodoArbol izq, NodoArbol der) {
        return new NodoArbol(etiqueta, izq, der);
    }

    public NodoArbol crearNodoAST(String etiqueta, NodoArbol izq, NodoArbol der, NodoArbol ter) {
        NodoArbol nodo = new NodoArbol(etiqueta, izq, der);
        nodo.setTer(ter);
        return nodo;
    }

    public NodoArbol crearHojaAST(RegistroTS reg) {
        return new NodoArbol(reg);
    }

    public void setRaizAST(NodoArbol raiz) {
        this.raizAST = raiz;
    }

    public NodoArbol getRaizAST() {
        return raizAST;
    }

    public List<String> getErroresSemanticos() {
        return new ArrayList<>(erroresSemanticos);
    }

    public void registrarErrorSemantico(int nroLinea, String mensaje) {
        String texto = (nroLinea > 0)
                ? "Línea " + nroLinea + ": Error Semántico: " + mensaje
                : "Error Semántico: " + mensaje;
        if (!erroresSemanticos.contains(texto)) {
            erroresSemanticos.add(texto);
        }
    }

    public String obtenerTipoDato(NodoArbol nodo) {
        if (nodo == null || nodo.getTipoToken() == null || nodo.getTipoToken().isEmpty()) {
            return "DESCONOCIDO";
        }
        return nodo.getTipoToken();
    }

    public RegistroTS resolverTipo(String nombreTipo, List<String> candidatos) {
        if (nombreTipo == null) {
            return null;
        }
        if ("USHORTINT".equalsIgnoreCase(nombreTipo) || "DOUBLEF".equalsIgnoreCase(nombreTipo)) {
            RegistroTS primitivo = new RegistroTS(nombreTipo);
            primitivo.setTipoToken(nombreTipo.toUpperCase());
            primitivo.setUso("tipo_basico");
            return primitivo;
        }
        return tablaSimbolos.buscarTipoVisible(nombreTipo, candidatos);
    }

    public boolean esTipoNumerico(String tipo) {
        if (tipo == null) return false;
        String t = tipo.toUpperCase();
        if (t.equals("USHORTINT") || t.equals("DOUBLEF")) {
            return true;
        }
        RegistroTS reg = tablaSimbolos.buscar(tipo + ":main");
        if (reg == null) {
            reg = tablaSimbolos.buscar(tipo);
        }
        return reg != null && reg.esTipoEnum();
    }

    public String tipoBaseDe(String tipo) {
        if (tipo == null) return "DESCONOCIDO";
        if (tipo.equalsIgnoreCase("USHORTINT") || tipo.equalsIgnoreCase("DOUBLEF")) {
            return tipo.toUpperCase();
        }
        RegistroTS reg = tablaSimbolos.buscar(tipo + ":main");
        if (reg == null) reg = tablaSimbolos.buscar(tipo);
        if (reg != null && reg.esTipoEnum()) {
            return reg.getTipoBaseEnum();
        }
        return tipo;
    }

    private RegistroTS obtenerTipoEnumDeVariable(RegistroTS regVar) {
        if (regVar == null || regVar.getTipoToken() == null) return null;
        RegistroTS reg = tablaSimbolos.buscar(regVar.getTipoToken() + ":main");
        if (reg == null) reg = tablaSimbolos.buscar(regVar.getTipoToken());
        return (reg != null && reg.esTipoEnum()) ? reg : null;
    }

    private RegistroTS obtenerRegistroConstante(NodoArbol nodo) {
        if (nodo == null) return null;
        if (nodo.getRegTS() != null) return nodo.getRegTS();
        if ("UMINUS".equals(nodo.getEtiqueta())) {
            return obtenerRegistroConstante(nodo.getIzq());
        }
        return null;
    }

    public boolean esConstante(NodoArbol nodo) {
        return obtenerRegistroConstante(nodo) != null;
    }

    public void registrarEnumeracion(RegistroTS regTipo, NodoArbol subarbolValores) {
        if (regTipo == null) return;
        List<Double> valores = new ArrayList<>();
        desarmarValoresEnum(subarbolValores, valores);
        regTipo.setUso("type_name");
        regTipo.setTipoToken("typedef");
        regTipo.setValoresEnum(valores);
    }

    public void registrarEnumeracion(RegistroTS regTipo, List<RegistroTS> valores) {
        if (regTipo == null) return;
        regTipo.setUso("type_name");
        regTipo.setTipoToken("typedef");
        List<Double> numeros = new ArrayList<>();
        boolean primero = true;
        for (RegistroTS valor : valores) {
            if (valor == null) continue;
            numeros.add(valor.getValorNum());
            if (primero) {
                regTipo.setTipoBaseEnum(valor.getIdToken() == Parser.CTE_DOUBLEF ? "DOUBLEF" : "USHORTINT");
                primero = false;
            }
        }
        regTipo.setValoresEnum(numeros);
    }

    private void desarmarValoresEnum(NodoArbol nodo, List<Double> lista) {
        if (nodo == null) return;
        if (",".equals(nodo.getEtiqueta())) {
            desarmarValoresEnum(nodo.getIzq(), lista);
            desarmarValoresEnum(nodo.getDer(), lista);
        } else if (nodo.getRegTS() != null) {
            lista.add(nodo.getRegTS().getValorNum());
        }
    }

    public NodoArbol verificarYConvertirTipos(String operador, NodoArbol izq, NodoArbol der, int nroLinea) {
        if (izq == null || der == null) {
            registrarErrorSemantico(nroLinea, "Expresión incompleta para el operador '" + operador + "'.");
            NodoArbol error = crearNodoAST(operador, izq, der);
            error.setTipoToken("ERROR");
            return error;
        }

        String t1 = obtenerTipoDato(izq);
        String t2 = obtenerTipoDato(der);
        String b1 = tipoBaseDe(t1);
        String b2 = tipoBaseDe(t2);

        if (!esTipoNumerico(t1) || !esTipoNumerico(t2)) {
            registrarErrorSemantico(nroLinea, "El operador '" + operador + "' requiere operandos numéricos, se recibieron '" + t1 + "' y '" + t2 + "'.");
            NodoArbol error = crearNodoAST(operador, izq, der);
            error.setTipoToken("ERROR");
            return error;
        }

        boolean comparacion = esOperadorComparacion(operador);
        NodoArbol nodoIzq = izq;
        NodoArbol nodoDer = der;

        if (b1.equalsIgnoreCase("USHORTINT") && b2.equalsIgnoreCase("DOUBLEF")) {
            nodoIzq = crearNodoAST("ItoD", izq, null);
            nodoIzq.setTipoToken("DOUBLEF");
        } else if (b1.equalsIgnoreCase("DOUBLEF") && b2.equalsIgnoreCase("USHORTINT")) {
            nodoDer = crearNodoAST("ItoD", der, null);
            nodoDer.setTipoToken("DOUBLEF");
        }

        String tipoResultado = b1.equalsIgnoreCase("DOUBLEF") || b2.equalsIgnoreCase("DOUBLEF") ? "DOUBLEF" : "USHORTINT";
        NodoArbol nodo = crearNodoAST(operador, nodoIzq, nodoDer);
        nodo.setTipoToken(comparacion ? "BOOL" : tipoResultado);
        return nodo;
    }

    private boolean esOperadorComparacion(String operador) {
        return "==".equals(operador) || "!=".equals(operador)
                || ">".equals(operador) || "<".equals(operador)
                || ">=".equals(operador) || "<=".equals(operador);
    }

    public NodoArbol verificarAsignacion(NodoArbol izq, NodoArbol der, int nroLinea) {
        if (izq == null || der == null) {
            registrarErrorSemantico(nroLinea, "Asignación incompleta.");
            return null;
        }

        String tIzq = obtenerTipoDato(izq);
        String tDer = obtenerTipoDato(der);
        RegistroTS regIzq = izq.getRegTS();

        RegistroTS enumTipo = obtenerTipoEnumDeVariable(regIzq);
        if (enumTipo != null) {
            String baseEnum = enumTipo.getTipoBaseEnum();
            String baseDer = tipoBaseDe(tDer);
            if (!baseDer.equalsIgnoreCase(baseEnum)) {
                registrarErrorSemantico(nroLinea, "La enumeración '" + enumTipo.getNombreSimple()
                        + "' es de tipo base " + baseEnum + " y no admite un valor " + tDer + ".");
            }

            RegistroTS cte = obtenerRegistroConstante(der);
            if (cte != null && (cte.getIdToken() == Parser.CTE_USHORT || cte.getIdToken() == Parser.CTE_DOUBLEF)) {
                double valor = cte.getValorNum();
                if ("UMINUS".equals(der.getEtiqueta())) {
                    valor = -valor;
                }
                if (!enumTipo.getValoresEnum().contains(valor)) {
                    registrarErrorSemantico(nroLinea, "El valor '" + cte.getLexema()
                            + "' no pertenece a los valores válidos de la enumeración '" + enumTipo.getNombreSimple() + "'.");
                }
            }

            NodoArbol nodoConv = der;
            if (baseEnum.equalsIgnoreCase("DOUBLEF") && baseDer.equalsIgnoreCase("USHORTINT")) {
                nodoConv = crearNodoAST("ItoD", der, null);
                nodoConv.setTipoToken("DOUBLEF");
            }
            NodoArbol asig = crearNodoAST(":=", izq, nodoConv);
            asig.setTipoToken(baseEnum);
            asig.setInfo(enumTipo);
            return asig;
        }

        if (tIzq.equalsIgnoreCase(tDer)) {
            NodoArbol asig = crearNodoAST(":=", izq, der);
            asig.setTipoToken(tIzq);
            return asig;
        }

        if (tIzq.equalsIgnoreCase("DOUBLEF") && tDer.equalsIgnoreCase("USHORTINT")) {
            NodoArbol conv = crearNodoAST("ItoD", der, null);
            conv.setTipoToken("DOUBLEF");
            NodoArbol asig = crearNodoAST(":=", izq, conv);
            asig.setTipoToken("DOUBLEF");
            return asig;
        }

        if (tIzq.equalsIgnoreCase("USHORTINT") && tDer.equalsIgnoreCase("DOUBLEF")) {
            registrarErrorSemantico(nroLinea, "No se puede asignar 'doublef' a 'ushortint' sin conversión explícita.");
        } else if (esTipoObjeto(tIzq) || esTipoObjeto(tDer)) {
            registrarErrorSemantico(nroLinea, "Asignación incompatible entre objetos de tipo '" + tIzq + "' y '" + tDer + "'.");
        } else {
            registrarErrorSemantico(nroLinea, "Tipos incompatibles en asignación: '" + tIzq + "' y '" + tDer + "'.");
        }

        NodoArbol asig = crearNodoAST(":=", izq, der);
        asig.setTipoToken(tIzq);
        return asig;
    }

    public boolean esTipoObjeto(String tipo) {
        if (tipo == null) return false;
        RegistroTS reg = tablaSimbolos.buscar(tipo + ":main");
        if (reg == null) reg = tablaSimbolos.buscar(tipo);
        return reg != null && "nombre_clase".equalsIgnoreCase(reg.getUso());
    }

    public NodoArbol verificarAsignacionEnExpresion(NodoArbol izq, NodoArbol der, int nroLinea) {
        if (izq == null || izq.getRegTS() == null || !tablaSimbolos.esAsignable(izq.getRegTS())) {
            registrarErrorSemantico(nroLinea, "El lado izquierdo de '=' debe ser un identificador asignable.");
            return null;
        }
        if (!esConstante(der) && (der == null || der.getRegTS() == null)) {
            registrarErrorSemantico(nroLinea, "Tema 18: la asignación interna solo admite un identificador o una constante como valor.");
        }
        return verificarAsignacion(izq, der, nroLinea);
    }

    // ==========================================
    // TEMA 29: ACCESO POSICIONAL A ATRIBUTOS
    // ==========================================

    public NodoArbol verificarLecturaAtributo(RegistroTS regObjeto, NodoArbol nodoIndice, int nroLinea) {
        if (regObjeto == null) {
            registrarErrorSemantico(nroLinea, "Objeto no identificado en el acceso posicional.");
            return null;
        }
        String tipoClase = regObjeto.getTipoToken();
        if (!esTipoObjeto(tipoClase)) {
            registrarErrorSemantico(nroLinea, "La variable '" + regObjeto.getNombreSimple() + "' (tipo '" + tipoClase + "') no es una instancia de una clase válida.");
            return null;
        }
        validarIndicePosicional(nodoIndice, nroLinea);

        NodoArbol nodoAcceso = crearNodoAST("[]", crearHojaAST(regObjeto), nodoIndice);
        nodoAcceso.setInfo(tipoClase);

        RegistroTS cteIndice = obtenerRegistroConstante(nodoIndice);
        if (cteIndice != null) {
            int pos = (int) cteIndice.getValorNum();
            if ("UMINUS".equals(nodoIndice.getEtiqueta())) pos = -pos;
            RegistroTS attr = tablaSimbolos.obtenerAtributoPosicional(tipoClase, pos);
            if (attr == null) {
                registrarErrorSemantico(nroLinea, "La clase '" + tipoClase + "' no posee ningún atributo en el índice " + pos + ".");
                nodoAcceso.setTipoToken("ERROR");
            } else {
                // La clase puede tener atributos heredados; obtenerAtributoPosicional ya resuelve el orden.
                nodoAcceso.setTipoToken(attr.getTipoToken());
                nodoAcceso.setInfo(new Object[] { tipoClase, attr });
            }
        } else {
            // El tipo real solo se conoce en ejecución. Se usa el tipo base del primer
            // atributo y el generador WAT hace la selección por índice.
            List<RegistroTS> attrs = tablaSimbolos.obtenerTodosLosAtributosHeredados(tipoClase);
            if (attrs.isEmpty()) {
                registrarErrorSemantico(nroLinea, "La clase '" + tipoClase + "' no posee atributos para el acceso posicional.");
                nodoAcceso.setTipoToken("ERROR");
            } else {
                nodoAcceso.setTipoToken(attrs.get(0).getTipoToken());
            }
        }
        return nodoAcceso;
    }

    public NodoArbol verificarEscrituraAtributo(RegistroTS regObjeto, NodoArbol nodoIndice, NodoArbol nodoExpr, int nroLinea) {
        if (regObjeto == null) {
            registrarErrorSemantico(nroLinea, "Objeto no identificado en la asignación posicional.");
            return null;
        }
        String tipoClase = regObjeto.getTipoToken();
        if (!esTipoObjeto(tipoClase)) {
            registrarErrorSemantico(nroLinea, "La variable '" + regObjeto.getNombreSimple() + "' (tipo '" + tipoClase + "') no es una instancia de una clase válida.");
            return null;
        }
        validarIndicePosicional(nodoIndice, nroLinea);
        RegistroTS cteIndice = obtenerRegistroConstante(nodoIndice);
        if (cteIndice != null) {
            int pos = (int) cteIndice.getValorNum();
            if ("UMINUS".equals(nodoIndice.getEtiqueta())) pos = -pos;
            RegistroTS attr = tablaSimbolos.obtenerAtributoPosicional(tipoClase, pos);
            if (attr == null) {
                registrarErrorSemantico(nroLinea, "La clase '" + tipoClase + "' no posee ningún atributo en el índice " + pos + ".");
            } else if ("private".equalsIgnoreCase(attr.getVisibilidad())) {
                // La comprobación de friend se realiza en gramatica.y, que conoce el ámbito emisor.
            }
            NodoArbol asig = verificarAsignacion(crearHojaAST(attr), nodoExpr, nroLinea);
            if (asig != null) {
                asig.setInfo(new Object[] { tipoClase, attr, regObjeto });
            }
            return asig;
        }

        NodoArbol asig = crearNodoAST(":=", crearNodoAST("[]", crearHojaAST(regObjeto), nodoIndice), nodoExpr);
        asig.setTipoToken(nodoExpr != null ? nodoExpr.getTipoToken() : "DESCONOCIDO");
        asig.setInfo(new Object[] { tipoClase, null, regObjeto });
        return asig;
    }

    private void validarIndicePosicional(NodoArbol nodoIndice, int nroLinea) {
        if (nodoIndice == null) {
            registrarErrorSemantico(nroLinea, "Falta el índice del acceso posicional.");
            return;
        }
        String tipo = tipoBaseDe(obtenerTipoDato(nodoIndice));
        if (!tipo.equalsIgnoreCase("USHORTINT")) {
            registrarErrorSemantico(nroLinea, "El índice del acceso posicional debe ser de tipo entero (USHORTINT).");
        }
    }

    // ==========================================
    // INVOCACIONES DE FUNCIONES / MÉTODOS (TEMA 20)
    // ==========================================

    public NodoArbol crearInvocacion(RegistroTS regFunc, NodoArbol args, NodoArbol orden, int nroLinea) {
        if (regFunc == null) {
            registrarErrorSemantico(nroLinea, "Función o método no identificado.");
            return null;
        }
        if (!"nombre_funcion".equalsIgnoreCase(regFunc.getUso()) && !"metodo".equalsIgnoreCase(regFunc.getUso())) {
            registrarErrorSemantico(nroLinea, "El identificador '" + regFunc.getNombreSimple() + "' no es una función o método invocable.");
            return null;
        }

        List<NodoArbol> reales = new ArrayList<>();
        desarmarExpresiones(args, reales);
        List<ParametroFormal> formales = regFunc.getParametrosFormales();

        if (reales.size() != formales.size()) {
            registrarErrorSemantico(nroLinea, "Cantidad incorrecta de parámetros en invocación a '"
                    + regFunc.getNombreSimple() + "': esperados " + formales.size()
                    + ", recibidos " + reales.size() + ".");
        }

        int cantidad = Math.min(reales.size(), formales.size());
        for (int i = 0; i < cantidad; i++) {
            String tipoFormal = formales.get(i).getTipo();
            String tipoReal = obtenerTipoDato(reales.get(i));
            String baseFormal = tipoBaseDe(tipoFormal);
            String baseReal = tipoBaseDe(tipoReal);
            if (baseFormal.equalsIgnoreCase("DOUBLEF") && baseReal.equalsIgnoreCase("USHORTINT")) {
                NodoArbol conv = crearNodoAST("ItoD", reales.get(i), null);
                conv.setTipoToken("DOUBLEF");
                reales.set(i, conv);
            } else if (tipoFormal.equalsIgnoreCase(tipoReal)) {
                // compatible
            } else if (esTipoObjeto(tipoFormal) && esTipoObjeto(tipoReal)) {
                if (!tipoFormal.equalsIgnoreCase(tipoReal)) {
                    registrarErrorSemantico(nroLinea, "El parámetro " + (i + 1) + " de '"
                            + regFunc.getNombreSimple() + "' espera un objeto de tipo '" + tipoFormal
                            + "' y se recibió '" + tipoReal + "'.");
                }
            } else if (baseFormal.equalsIgnoreCase(baseReal)) {
                // enums compatibles por tipo base
            } else {
                registrarErrorSemantico(nroLinea, "El parámetro " + (i + 1) + " de '"
                        + regFunc.getNombreSimple() + "' espera '" + tipoFormal
                        + "' y se recibió '" + tipoReal + "'.");
            }
        }

        if (orden != null) {
            List<Integer> posiciones = new ArrayList<>();
            desarmarOrden(orden, posiciones);
            if (posiciones.size() != reales.size()) {
                registrarErrorSemantico(nroLinea, "La lista de orden tiene " + posiciones.size()
                        + " elementos, pero la invocación tiene " + reales.size() + " parámetros.");
            }
            for (int pos : posiciones) {
                if (pos < 1 || pos > reales.size()) {
                    registrarErrorSemantico(nroLinea, "Índice de orden fuera de rango: " + pos + ".");
                }
            }
        }

        NodoArbol seq = null;
        for (NodoArbol real : reales) {
            seq = (seq == null) ? real : crearNodoAST(",", seq, real);
        }

        NodoArbol nodo = crearNodoAST("CALL", seq, orden);
        nodo.setInfo(regFunc);
        nodo.setTipoToken(regFunc.getTipoToken());
        return nodo;
    }

    private void desarmarExpresiones(NodoArbol nodo, List<NodoArbol> lista) {
        if (nodo == null) return;
        if (",".equals(nodo.getEtiqueta())) {
            desarmarExpresiones(nodo.getIzq(), lista);
            desarmarExpresiones(nodo.getDer(), lista);
        } else {
            lista.add(nodo);
        }
    }

    private void desarmarOrden(NodoArbol nodo, List<Integer> lista) {
        if (nodo == null) return;
        if (",".equals(nodo.getEtiqueta())) {
            desarmarOrden(nodo.getIzq(), lista);
            desarmarOrden(nodo.getDer(), lista);
        } else {
            RegistroTS reg = obtenerRegistroConstante(nodo);
            if (reg != null) {
                lista.add((int) reg.getValorNum());
            }
        }
    }

    public void imprimirAST() {
        System.out.println("=== ÁRBOL SINTÁCTICO ABSTRACTO (AST) / CÓDIGO INTERMEDIO ===");
        if (raizAST != null) {
            raizAST.imprimir("", true);
        } else {
            System.out.println("(Árbol no generado)");
        }
    }
}
