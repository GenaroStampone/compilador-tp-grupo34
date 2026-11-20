public class ParametroFormal {
    private final String nombre;
    private final String tipo;

    public ParametroFormal(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return tipo + " " + nombre;
    }
}
