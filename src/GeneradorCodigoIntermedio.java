import java.util.ArrayList;
import java.util.List;

// Administrador central para la Generación de Código Intermedio y Análisis Semántico (TP3).
public class GeneradorCodigoIntermedio {

    private NodoArbol raizAST;

    // Reporte consolidado de errores semánticos
    private final List<String> erroresSemanticos;

    // Referencia a la Tabla de Símbolos
    private final TablaDeSimbolos tablaSimbolos;

    public GeneradorCodigoIntermedio(TablaDeSimbolos tablaSimbolos) {
        this.raizAST = null;
        this.erroresSemanticos = new ArrayList<>();
        this.tablaSimbolos = tablaSimbolos;
    }

    // ==========================================
    // GESTIÓN DEL ÁRBOL SINTÁCTICO (AST)
    // ==========================================

    public NodoArbol crearNodoAST(String etiqueta, NodoArbol izq, NodoArbol der) {
        return new NodoArbol(etiqueta, izq, der);
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

    // ==========================================
    // METODOS SEMANTICOS TP3 (TEMA 20)
    // ==========================================

    public NodoArbol generarInvocacionFuncionOrdenada(RegistroTS regFunc, List<NodoArbol> paramsReales, List<Integer> orden, int nroLinea) {
        // Buscar la declaración de la función en la TS
        RegistroTS funcDeclarada = tablaSimbolos.buscar(regFunc.getLexema());
        if (funcDeclarada == null || !"FUNCION".equals(funcDeclarada.getUso())) {
            registrarErrorSemantico(nroLinea, "La función '" + regFunc.getLexema() + "' no está declarada.");
            return null;
        }

        List<String> paramsFormales = funcDeclarada.getListaParametros();

        // Si por algún error de declaración la lista viniera nula o vacía:
        if (paramsFormales == null || paramsFormales.isEmpty()) {
            registrarErrorSemantico(nroLinea, "La función '" + regFunc.getLexema() + "' no posee parámetros formales declarados.");
            return null;
        }

        // Chequeo de cantidad de parámetros reales vs formales
        if (paramsReales.size() != paramsFormales.size()) {
            registrarErrorSemantico(nroLinea, String.format(
                    "Cantidad incorrecta de parámetros en invocación a '%s': esperados %d, recibidos %d",
                    regFunc.getLexema(), paramsFormales.size(), paramsReales.size()));
            return null;
        }

        // Chequeo Tema 20: El número de constantes de orden debe coincidir con los parámetros
        if (orden.size() != paramsReales.size()) {
            registrarErrorSemantico(nroLinea, String.format(
                    "Error en orden de evaluación (Tema 20): La lista de orden tiene %d elementos, pero la función tiene %d parámetros",
                    orden.size(), paramsReales.size()));
            return null;
        }

        // Crear subárboles de copia en el orden especificado por la lista de constantes
        NodoArbol nodoCopias = null;
        for (int pos : orden) {
            int idxReal = pos - 1; 
            if (idxReal < 0 || idxReal >= paramsReales.size()) {
                registrarErrorSemantico(nroLinea, "Índice de orden fuera de rango: " + pos);
                return null;
            }

            NodoArbol paramReal = paramsReales.get(idxReal);
            // Obtenemos el nombre/lexema del parámetro formal
            String paramFormal = paramsFormales.get(idxReal);
            RegistroTS regFormal = tablaSimbolos.buscar(paramFormal);
            
            NodoArbol hojaFormal;
            if (regFormal != null) {
                hojaFormal = crearHojaAST(regFormal);
            } else {
                // Fallback si en listaParametros se guardó el tipo o lexema directo
                hojaFormal = crearNodoAST(paramFormal, null, null);
            }

            // Generar asignación implícita de copia: paramFormal := paramReal
            NodoArbol copia = verificarAsignacion(hojaFormal, paramReal, nroLinea);
            nodoCopias = crearNodoAST("COPIA_PARAM", nodoCopias, copia);
        }

        // 5. Retornar el nodo final de llamada a función
        NodoArbol nodoFunc = crearHojaAST(funcDeclarada);
        return crearNodoAST("CALL", nodoFunc, nodoCopias);
    }

    // Desempaqueta el subárbol recibido de la gramática e invoca a generarInvocacionFuncionOrdenada.
    public NodoArbol procesarInvocacion(RegistroTS regFunc, NodoArbol infoInvoc, int nroLinea) {
        List<NodoArbol> paramsReales = new ArrayList<>();
        List<Integer> orden = new ArrayList<>();

        if (infoInvoc != null && "INVOC_ORDENADA".equals(infoInvoc.getEtiqueta())) {
            desarmarExpresiones(infoInvoc.getIzq(), paramsReales);
            desarmarOrden(infoInvoc.getDer(), orden);
        } else {
            desarmarExpresiones(infoInvoc, paramsReales);
            // Si no hay corchetes, genera el orden estándar: 1, 2, ..., N
            for (int i = 1; i <= paramsReales.size(); i++) {
                orden.add(i);
            }
        }

        // Invoca directamente a tu método intacto:
        return generarInvocacionFuncionOrdenada(regFunc, paramsReales, orden, nroLinea);
    }

    // Métodos auxiliares para recorrer el árbol del AST:
    private void desarmarExpresiones(NodoArbol nodo, List<NodoArbol> lista) {
        if (nodo == null) return;
        if (",".equals(nodo.getEtiqueta())) {
            desarmarExpresiones(nodo.getIzq(), lista);
            desarmarExpresiones(nodo.getDer(), lista);
        } else {
            lista.add(nodo);
        }
    }

    // Metodo auxiliar para obtener el orden del llamado a funcion (Ej. f(a, b, c)[1, 3, 2])
    private void desarmarOrden(NodoArbol nodo, List<Integer> lista) {
        if (nodo == null) return;
        if (",".equals(nodo.getEtiqueta())) {
            desarmarOrden(nodo.getIzq(), lista);
            desarmarOrden(nodo.getDer(), lista);
        } else {
            if (nodo.getRegTS() != null) {
                lista.add((int)nodo.getRegTS().getValorNum());
            }
        }
    }

    // ==========================================
    // ANÁLISIS SEMÁNTICO Y TEMA 35 (CONVERSIONES)
    // ==========================================

    //Obtiene el tipo de dato de un nodo ("ushortint", "doublef", "DESCONOCIDO", "TERCETO", "LITERAL").
    public String obtenerTipoDato(NodoArbol nodo) { 
        if (nodo == null || nodo.getTipoToken() == null) { 
            return "DESCONOCIDO"; 
        } 
        return nodo.getTipoToken(); 
    }

    // Valida compatibilidad entre tipos e inserta conversión implícita (ItoD) si aplica.
    // Retorna el tipo de dato resultante ("ushortint", "doublef" o "ERROR").
    public NodoArbol verificarYConvertirTipos(String operador, NodoArbol izq, NodoArbol der, int nroLinea) {
        String t1 = obtenerTipoDato(izq);
        String t2 = obtenerTipoDato(der);

        // Mismo tipo: no requiere conversión
        if (t1.equalsIgnoreCase(t2)) {
            NodoArbol nodoOp = crearNodoAST(operador, izq, der);
            nodoOp.setTipoToken(t1);
            return nodoOp;
        }

        // Tema 35: Conversión implícita de ushortint a doublef
        if (t1.equalsIgnoreCase("USHORTINT") && t2.equalsIgnoreCase("DOUBLEF")) {
            NodoArbol nodoConv = crearNodoAST("ItoD", izq, null);
            nodoConv.setTipoToken("DOUBLEF");
            NodoArbol nodoOp = crearNodoAST(operador, nodoConv, der);
            nodoOp.setTipoToken("DOUBLEF");
            return nodoOp;
        }

        if (t1.equalsIgnoreCase("DOUBLEF") && t2.equalsIgnoreCase("USHORTINT")) {
            NodoArbol nodoConv = crearNodoAST("ItoD", der, null);
            nodoConv.setTipoToken("DOUBLEF");
            NodoArbol nodoOp = crearNodoAST(operador, izq, nodoConv);
            nodoOp.setTipoToken("DOUBLEF");
            return nodoOp;
        }

        // Incompatibilidad de tipos
        registrarErrorSemantico(nroLinea, "Tipos incompatibles para '" + operador + "': " + t1 + " y " + t2);
        NodoArbol nodoError = crearNodoAST(operador, izq, der);
        nodoError.setTipoToken("ERROR");
        return nodoError;
    }

    // Valida la compatibilidad en asignaciones.
    public NodoArbol verificarAsignacion(NodoArbol izq, NodoArbol der, int nroLinea) {
        String tIzq = obtenerTipoDato(izq);
        String tDer = obtenerTipoDato(der);

        if (tIzq.equalsIgnoreCase(tDer)) {
            return crearNodoAST(":=", izq, der);
        }

        // doublef := ushortint (válido con conversión implícita)
        if (tIzq.equalsIgnoreCase("DOUBLEF") && tDer.equalsIgnoreCase("USHORTINT")) {
            NodoArbol nodoConv = crearNodoAST("ItoD", der, null);
            nodoConv.setTipoToken("DOUBLEF");
            return crearNodoAST(":=", izq, nodoConv);
        }

        // ushortint := doublef (inválido)
        if (tIzq.equalsIgnoreCase("USHORTINT") && tDer.equalsIgnoreCase("DOUBLEF")) {
            registrarErrorSemantico(nroLinea, "Error de tipo: No se puede asignar 'doublef' a 'ushortint' sin conversión explícita.");
        }

        return crearNodoAST(":=", izq, der);
    }

    public void registrarErrorSemantico(int nroLinea, String mensaje) {
        erroresSemanticos.add("Línea " + nroLinea + ": Error Semántico: " + mensaje);
    }

    public List<String> getErroresSemanticos() {
        return new ArrayList<>(erroresSemanticos);
    }

    // ==========================================
    // VISUALIZACIÓN / INFORMES
    // ==========================================

    public void imprimirAST() {
        System.out.println("=== ÁRBOL SINTÁCTICO ABSTRACTO (AST) ===");
        if (raizAST != null) {
            raizAST.imprimir("", true);
        } else {
            System.out.println("(Árbol no generado)");
        }
    }
}