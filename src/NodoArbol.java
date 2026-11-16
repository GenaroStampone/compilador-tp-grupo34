
/**
 * Representa un nodo dentro del Árbol Sintáctico Abstracto (AST) para el TP3.
 */
public class NodoArbol implements Operando{
    private String etiqueta; // Operador, palabra reservada o valor (ej: "+", ":=", "IF", "var1")
    private final RegistroTS ptrTS; // Referencia a la Tabla de Símbolos (sólo si es nodo hoja)
    private NodoArbol hijoIzq; // Subárbol o expresión izquierda
    private NodoArbol hijoDer; // Subárbol o expresión derecha
    private String tipoToken; // Tipo de dato resultante ("ushortint", "doublef", "VOID", "ERROR")

    //Constructor para Nodos Internos (Operadores, Sentencias de Control, Conversiones)
    public NodoArbol(String etiqueta, NodoArbol hijoIzq, NodoArbol hijoDer) {
        this.etiqueta = etiqueta;
        this.hijoIzq = hijoIzq;
        this.hijoDer = hijoDer;
        this.ptrTS = null;
        this.tipoToken = null;
    }

    //Constructor para Nodos Hoja (Variables, Constantes desde la Tabla de Símbolos)
    public NodoArbol(RegistroTS ptrTS) {
        if (ptrTS != null) {
            this.etiqueta = ptrTS.getLexema();
            this.tipoToken = ptrTS.getTipoToken();
        } else {
            this.etiqueta = "NULL_TS";
            this.tipoToken = null;
        }
        this.ptrTS = ptrTS;
        this.hijoIzq = null;
        this.hijoDer = null;
    }

    // --- GETTERS Y SETTERS ---
    @Override 
    public String getRepresentacion() { 
        return etiqueta; 
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public RegistroTS getPtrTS() {
        return ptrTS;
    }

    public NodoArbol getHijoIzq() {
        return hijoIzq;
    }

    public void setHijoIzq(NodoArbol hijoIzq) {
        this.hijoIzq = hijoIzq;
    }

    public NodoArbol getHijoDer() {
        return hijoDer;
    }

    public void setHijoDer(NodoArbol hijoDer) {
        this.hijoDer = hijoDer;
    }

    @Override 
    public String getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(String tipoToken) {
        this.tipoToken = tipoToken;
    }

    public boolean esHoja() {
        return hijoIzq == null && hijoDer == null;
    }

    //Recorre e imprime el árbol en consola con formato jerárquico.
    public void imprimir(String prefijo, boolean esUltimo) {
        String infoTipo = (tipoToken != null) ? " [" + tipoToken + "]" : "";
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ") + etiqueta + infoTipo);
        String nuevoPrefijo = prefijo + (esUltimo ? " " : "│ ");
        if (hijoIzq != null && hijoDer != null) {
            hijoIzq.imprimir(nuevoPrefijo, false);
            hijoDer.imprimir(nuevoPrefijo, true);
        } else if (hijoIzq != null) {
            hijoIzq.imprimir(nuevoPrefijo, true);
        } else if (hijoDer != null) {
            hijoDer.imprimir(nuevoPrefijo, true);
        }
    }

}