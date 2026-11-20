// Representa un nodo en el Árbol Sintáctico Abstracto (AST).
public class NodoArbol {
    private String etiqueta;
    private NodoArbol izq;
    private NodoArbol der;
    private NodoArbol ter;
    private final RegistroTS regTS; // Referencia a la TS si el nodo es una hoja (variable/constante)
    private String tipoToken; // Tipo de dato resultante ("USHORTINT", "DOUBLEF", "VOID")
    private Object info; // Metadatos semánticos (InfoFuncion, listas de declaración, etc.)

    // Constructor para nodos internos (operadores, conversiones, sentencias)
    public NodoArbol(String etiqueta, NodoArbol izq, NodoArbol der) {
        this.etiqueta = etiqueta;
        this.izq = izq;
        this.der = der;
        this.regTS = null;
        this.tipoToken = null;
    }

    // Constructor para nodos hoja (apuntan a una entrada de la Tabla de Símbolos)
    public NodoArbol(RegistroTS regTS) {
        this.regTS = regTS;
        this.etiqueta = (regTS != null) ? regTS.getLexema() : "-";
        this.izq = null;
        this.der = null;
        String tipo = (regTS != null) ? regTS.getTipoToken() : null;
        if ("CTE_USHORT".equalsIgnoreCase(tipo)) {
            tipo = "USHORTINT";
        } else if ("CTE_DOUBLEF".equalsIgnoreCase(tipo)) {
            tipo = "DOUBLEF";
        }
        this.tipoToken = tipo;
    }

    // --- GETTERS Y SETTERS ---
    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public NodoArbol getIzq() {
        return izq;
    }

    public void setIzq(NodoArbol izq) {
        this.izq = izq;
    }

    public NodoArbol getDer() {
        return der;
    }

    public void setDer(NodoArbol der) {
        this.der = der;
    }

    public RegistroTS getRegTS() {
        return regTS;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(String tipoToken) {
        this.tipoToken = tipoToken;
    }

    public NodoArbol getTer() {
        return ter;
    }

    public void setTer(NodoArbol ter) {
        this.ter = ter;
    }

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    // Muestra el Árbol Sintáctico de forma jerárquica e indentada en consola.
    public void imprimir(String prefijo, boolean esUltimo) {
        String infoTipo = (tipoToken != null) ? " [" + tipoToken + "]" : "";
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ") + etiqueta + infoTipo);
        String nuevoPrefijo = prefijo + (esUltimo ? " " : "│ ");
        if (ter != null) {
            if (izq != null) izq.imprimir(nuevoPrefijo, false);
            if (der != null) der.imprimir(nuevoPrefijo, false);
            ter.imprimir(nuevoPrefijo, true);
        } else if (izq != null && der != null) {
            izq.imprimir(nuevoPrefijo, false);
            der.imprimir(nuevoPrefijo, true);
        } else if (izq != null) {
            izq.imprimir(nuevoPrefijo, true);
        } else if (der != null) {
            der.imprimir(nuevoPrefijo, true);
        }
    }
}