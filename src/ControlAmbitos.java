import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ControlAmbitos {
    private Stack<String> pilaAmbitos = new Stack<>();

    public ControlAmbitos() {
        // Al iniciar la compilación, el ámbito base es 'main'
        pilaAmbitos.push("main");
    }

    // Al entrar a una función/método/clase
    public void abrirAmbito(String nombreAmbito) {
        pilaAmbitos.push(nombreAmbito);
    }

    // Al salir de la función/método/clase
    public void cerrarAmbito() {
        if (pilaAmbitos.size() > 1) { // Mantiene 'main' siempre en la base
            pilaAmbitos.pop();
        }
    }

    // Devuelve la cadena completa de ámbitos concatenados por ':'
    public String getAmbitoActual() {
        return String.join(":", pilaAmbitos); // Ej: "main:aa:aaa"
    }

    // Devuelve la lista de ámbitos para realizar búsquedas de adentro hacia afuera
    public List<String> getJerarquiaBusqueda(String lexemaOriginal) {
        List<String> candidatos = new ArrayList<>();

        // Construye las opciones desde el ámbito actual hasta el global 'main'
        List<String> ambitosList = new ArrayList<>(pilaAmbitos);
        for (int i = ambitosList.size(); i > 0; i--) {
            String subScope = String.join(":", ambitosList.subList(0, i));
            candidatos.add(lexemaOriginal + ":" + subScope);
        }
        return candidatos; // Ej para 'x': ["x:main:aa:aaa", "x:main:aa", "x:main"]
    }
}