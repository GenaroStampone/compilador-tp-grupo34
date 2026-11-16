import java.util.ArrayList;
import java.util.List;

//Representa la tira de código intermedio en Polaca Inversa (Notación Postfija).
public class PolacaInversa {
    private final List<Operando> elementos;

    public PolacaInversa() {
        this.elementos = new ArrayList<>();
    }

    //Agrega un operando a la tira y retorna la posición/índice donde fue colocado.
    public int agregar(Operando elem) {
        elementos.add(elem);
        return elementos.size() - 1; // Retorna el índice (dirección) del elemento agregado
    }

    //para agregar rápidamente un texto o literal (ej. "+", ":=", "BF").
    public int agregarLiteral(String valor) {
        return agregar(new OperandoLiteral(valor));
    }

    //Permite modificar un elemento en una posición específica (Indispensable para Backpatching).
    public void setElemento(int indice, Operando elem) {
        if (indice >= 0 && indice < elementos.size()) {
            elementos.set(indice, elem);
        }
    }

    //Sobrecarga de setElemento para literales.
    public void setElementoLiteral(int indice, String valor) {
        setElemento(indice, new OperandoLiteral(valor));
    }

    public int getTamanio() {
        return elementos.size();
    }

    public Operando getElemento(int indice) {
        if (indice >= 0 && indice < elementos.size()) {
            return elementos.get(indice);
        }
        return null;
    }

    //Muestra la tira de Polaca Inversa numerada posición por posición.
    public void imprimir() {
        System.out.println("=== POLACA INVERSA ===");
        if (elementos.isEmpty()) {
            System.out.println("(Vacía)");
            return;
        }
        StringBuilder indices = new StringBuilder("Pos: ");
        StringBuilder contenido = new StringBuilder("Elem: ");
        for (int i = 0; i < elementos.size(); i++) {
            Operando op = elementos.get(i);
            String rep = (op != null) ? op.getRepresentacion() : "-";
            indices.append(String.format("%-6d", i));
            contenido.append(String.format("%-6s", rep));
        }
        System.out.println(indices.toString());
        System.out.println(contenido.toString());
    }
}