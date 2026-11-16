//Representa una instrucción de Código Intermedio en formato de Terceto.
// Formato: número ( operador , operando1 , operando2 )

public class Terceto implements Operando {
    private int numero; // Índice o dirección del terceto (ej: 0, 1, 2...)
    private String operador; // Operador o instrucción (ej: "+", ":=", "BF", "BI")
    private Operando operando1; // Primer operando (RegistroTS, String "[i]", otro Terceto, etc.)
    private Operando operando2; // Segundo operando 

    public Terceto(int numero, String operador, Operando operando1, Operando operando2) {
        this.numero = numero;
        this.operador = operador;
        this.operando1 = operando1;
        this.operando2 = operando2;
    }

    // --- GETTERS Y SETTERS ---
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public Operando getOperando1() {
        return operando1;
    }

    public void setOperando1(Operando operando1) {
        this.operando1 = operando1;
    }

    public Operando getOperando2() {
        return operando2;
    }

    public void setOperando2(Operando operando2) {
        this.operando2 = operando2;
    }

    @Override
    public String getRepresentacion() {
        return "[" + numero + "]";
    }

    @Override 
    public String getTipoToken() { 
        return "TERCETO"; // O el tipo de retorno si lo manejaras 
    }

    //Auxiliar para formatear la salida legible de cada operando.
    private String formatearOperando(Operando op) {
        return (op != null) ? op.getRepresentacion() : "-";
    }

    @Override
    public String toString() {
        return String.format("%d ( %s , %s , %s )",
                numero,
                operador,
                formatearOperando(operando1),
                formatearOperando(operando2)
        );
    }
}