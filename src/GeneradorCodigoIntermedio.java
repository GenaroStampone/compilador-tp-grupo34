import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// Administrador central para la Generación de Código Intermedio y Análisis Semántico (TP3).
public class GeneradorCodigoIntermedio {
    private final List<Terceto> listaTercetos;
    private final PolacaInversa polacaInversa;
    private NodoArbol raizAST;

    // Pilas para el Rellenado Diferido (Backpatching)
    private final Stack<Integer> pilaSaltosTercetos;
    private final Stack<Integer> pilaSaltosPolaca;

    // Reporte consolidado de errores semánticos
    private final List<String> erroresSemanticos;

    // Referencia a la Tabla de Símbolos
    private final TablaDeSimbolos tablaSimbolos;

    public GeneradorCodigoIntermedio(TablaDeSimbolos tablaSimbolos) {
        this.listaTercetos = new ArrayList<>();
        this.polacaInversa = new PolacaInversa();
        this.raizAST = null;
        this.pilaSaltosTercetos = new Stack<>();
        this.pilaSaltosPolaca = new Stack<>();
        this.erroresSemanticos = new ArrayList<>();
        this.tablaSimbolos = tablaSimbolos;
    }

    // ==========================================
    // GESTIÓN DE TERCETOS
    // ==========================================

    // Crea y almacena un nuevo Terceto.
    public Terceto crearTerceto(String operador, Operando op1, Operando op2) {
        int num = listaTercetos.size();
        Terceto t = new Terceto(num, operador, op1, op2);
        listaTercetos.add(t);
        return t;
    }

    public int getIndiceActualTercetos() {
        return listaTercetos.size();
    }

    public void apilarSaltoTerceto(int indice) {
        pilaSaltosTercetos.push(indice);
    }

    public int desapilarSaltoTerceto() {
        return pilaSaltosTercetos.isEmpty() ? -1 : pilaSaltosTercetos.pop();
    }

    public List<Terceto> getListaTercetos() {
        return new ArrayList<>(listaTercetos);
    }
    
    // Realiza Backpatching en un Terceto incompleto (ej: para 'BF' o 'BI').
    public void backpatchTerceto(int indiceTerceto, Operando destino) {
        if (indiceTerceto >= 0 && indiceTerceto < listaTercetos.size()) {
            Terceto t = listaTercetos.get(indiceTerceto);
            if (t.getOperando2() == null) {
                t.setOperando2(destino);
            } else if (t.getOperando1() == null) {
                t.setOperando1(destino);
            }
        }
    }

    // ==========================================
    // GESTIÓN DE POLACA INVERSA
    // ==========================================

    public int emitirPolaca(Operando elem) {
        return polacaInversa.agregar(elem);
    }

    public int emitirLiteralPolaca(String valor) {
        return polacaInversa.agregarLiteral(valor);
    }

    public void apilarSaltoPolaca(int indice) {
        pilaSaltosPolaca.push(indice);
    }

    public int desapilarSaltoPolaca() {
        return pilaSaltosPolaca.isEmpty() ? -1 : pilaSaltosPolaca.pop();
    }

    public void backpatchPolaca(int indicePolaca, Operando destino) {
        polacaInversa.setElemento(indicePolaca, destino);
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

    //Obtiene el tipo de dato de un operando ("ushortint", "doublef", "DESCONOCIDO", "TERCETO", "LITERAL").
    public String obtenerTipoDato(Operando op) { 
        if (op == null || op.getTipoToken() == null) { 
            return "DESCONOCIDO"; 
        } 
        return op.getTipoToken(); 
    }

    // Valida compatibilidad entre tipos e inserta conversión implícita (ItoD) si aplica.
    // Retorna el tipo de dato resultante ("ushortint", "doublef" o "ERROR").
    public String verificarYConvertirTipos(Operando op1, Operando op2, int nroLinea) {
        String t1 = obtenerTipoDato(op1);
        String t2 = obtenerTipoDato(op2);

        if (t1.equals("DESCONOCIDO") || t2.equals("DESCONOCIDO")) {
            return "DESCONOCIDO";
        }

        // Caso 1: Mismo tipo (ushortint con ushortint, o doublef con doublef)
        if (t1.equalsIgnoreCase(t2)) {
            return t1;
        }

        // Caso 2: Mezcla de ushortint y doublef (Tema 35: Conversión Implícita a doublef)
        if ((t1.equalsIgnoreCase("USHORTINT") && t2.equalsIgnoreCase("DOUBLEF")) ||
            (t1.equalsIgnoreCase("DOUBLEF") && t2.equalsIgnoreCase("USHORTINT"))) {

            // Generar terceto de conversión para el operando ushortint
            if (t1.equalsIgnoreCase("USHORTINT")) {
                crearTerceto("ItoD", op1, null);
            } else {
                crearTerceto("ItoD", op2, null);
            }
            return "DOUBLEF"; // La operación resulta en flotante
        }

        // Si los tipos son incompatibles
        registrarErrorSemantico(nroLinea, "Tipos incompatibles para la operación: " + t1 + " y " + t2);
        return "ERROR";
    }

    // Valida una asignación entre variable izquierda y expresión derecha.
    public boolean verificarAsignacion(Operando izq, Operando der, int nroLinea) {
        String tIzq = obtenerTipoDato(izq);
        String tDer = obtenerTipoDato(der);

        if (tIzq.equalsIgnoreCase(tDer)) {
            return true;
        }

        // Asignación válida con conversión: doublef := ushortint
        if (tIzq.equalsIgnoreCase("DOUBLEF") && tDer.equalsIgnoreCase("USHORTINT")) {
            crearTerceto("ItoD", der, null);
            return true;
        }

        // Asignación inválida: ushortint := doublef
        if (tIzq.equalsIgnoreCase("USHORTINT") && tDer.equalsIgnoreCase("DOUBLEF")) {
            registrarErrorSemantico(nroLinea, "Error de tipo: No se puede asignar una expresión de tipo 'doublef' a una variable de tipo 'ushortint' sin conversión explícita.");
            return false;
        }

        return true;
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

    public void imprimirTercetos() {
        System.out.println("=== TERCETOS GENERADOS ===");
        if (listaTercetos.isEmpty()) {
            System.out.println("(No se generaron tercetos)");
            return;
        }
        for (Terceto t : listaTercetos) {
            System.out.println(t);
        }
    }

    public void imprimirPolaca() {
        polacaInversa.imprimir();
    }

    public void imprimirAST() {
        System.out.println("=== ÁRBOL SINTÁCTICO ABSTRACTO (AST) ===");
        if (raizAST != null) {
            raizAST.imprimir("", true);
        } else {
            System.out.println("(Árbol no generado)");
        }
    }
}