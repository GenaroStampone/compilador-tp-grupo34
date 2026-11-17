/*
Para representar CUALQUIER texto o valor que NO sea:
    1. Una variable o constante de la Tabla de Símbolos
    2. Una referencia al resultado de un Terceto previo como [15]

Puede ser:
    - Operadores e instrucciones ejecutables: "+", "-", "*", ":=", "BF", "BI", "CMP", "POUT".
    - Nombres de conversiones implícitas o funciones del sistema: "ItoD"
    - Direcciones o etiquetas de salto directo: "15", "0", "LABEL_FIN"
    - Cualquier símbolo o constante auxiliar no gestionada por la Tabla de Símbolos.
*/
public class OperandoLiteral implements Operando {
    private final String valor;

    public OperandoLiteral(String valor) {
        this.valor = valor;
    }

    @Override
    public String getRepresentacion() {
        return (valor != null) ? valor : "-";
    }

    @Override 
    public String getTipoToken() { 
        return "LITERAL"; 
    }
}