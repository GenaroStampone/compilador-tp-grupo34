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