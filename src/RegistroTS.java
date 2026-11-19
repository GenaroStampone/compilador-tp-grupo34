import java.util.ArrayList;
import java.util.List;

public class RegistroTS {
    private String lexema;
    private int idToken;
    private String uso;                 // Ej: "VARIABLE", "CONSTANTE", "CLASE", "FUNCION", "ATRIBUTO"
    private String tipoToken;           // Ej: "USHORTINT", "DOUBLEF", "VOID"
    private int nroLinea;
    private double valorNum;
    private int longitud;

    // --- ATRIBUTOS SEMÁNTICOS NUEVOS PARA TP3 ---
    private String visibilidad = "public"; // "public" o "private"
    private List<String> listaAmigos = new ArrayList<>(); // Clases o funciones amigas
    private final List<String> listaParametros; // Tema 20: Tipos/nombres de parámetros formales
    private final List<RegistroTS> listaAtributos; // Tema 29: Atributos miembros de una clase
    private final List<String> clasesHeredadas; // Tema 30: Clases padre ordenadas según 'extends'

    // Constructor para ID, Cadenas y Tokens sin valor numérico inicial
    public RegistroTS(String lexema, int idToken, String tipoToken, int nroLinea) {
        this.lexema = lexema;
        this.idToken = idToken;
        this.tipoToken = tipoToken;
        this.nroLinea = nroLinea;
        this.valorNum = 0.0;
        this.longitud = (lexema != null) ? lexema.length() : 0;
        this.uso = null;
        this.listaParametros = new ArrayList<>();
        this.listaAtributos = new ArrayList<>();
        this.clasesHeredadas = new ArrayList<>();
    }

    // Constructor para constantes numéricas
    public RegistroTS(String lexema, int idToken, String tipoToken, int nroLinea, double valorNum) {
        this.lexema = lexema;
        this.idToken = idToken;
        this.tipoToken = tipoToken;
        this.nroLinea = nroLinea;
        this.valorNum = valorNum;
        this.longitud = (lexema != null) ? lexema.length() : 0;
        this.uso = null;
        this.listaParametros = new ArrayList<>();
        this.listaAtributos = new ArrayList<>();
        this.clasesHeredadas = new ArrayList<>();
    }

    public RegistroTS(String nombreMangled){
        this.lexema = nombreMangled;
        this.idToken = -1;
        this.uso = null;
        this.tipoToken = null;
        this.nroLinea = -1;
        this.valorNum = 0.0;
        this.longitud = (nombreMangled != null) ? nombreMangled.length() : 0;
        this.listaParametros = new ArrayList<>();
        this.listaAtributos = new ArrayList<>();
        this.clasesHeredadas = new ArrayList<>();
    }

    // --- GETTERS Y SETTERS ---
    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
        this.longitud = (lexema != null) ? lexema.length() : 0;
    }

    public int getIdToken() {
        return idToken;
    }

    public void setIdToken(int idToken) {
        this.idToken = idToken;
    }

    public String getUso() {
        return uso;
    }

    public void setUso(String uso) {
        this.uso = uso;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(String tipoToken) {
        this.tipoToken = tipoToken;
    }

    public int getNroLinea() {
        return nroLinea;
    }

    public void setNroLinea(int nroLinea) {
        this.nroLinea = nroLinea;
    }

    public double getValorNum() {
        return valorNum;
    }

    public void setValorNum(double valorNum) {
        this.valorNum = valorNum;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    // --- GETTERS Y MÉTODOS PARA ATRIBUTOS TP3 ---
    
    public void agregarParametro(String tipoParam) {
        this.listaParametros.add(tipoParam);
    }

    public List<String> getListaParametros() {
        return new ArrayList<>(listaParametros); // Copia defensiva
    }

    public void agregarAtributo(RegistroTS atributo) {
        this.listaAtributos.add(atributo);
    }

    public List<RegistroTS> getListaAtributos() {
        return new ArrayList<>(listaAtributos); // Copia defensiva
    }

    public void agregarClaseHeredada(String nombreClasePadre) {
        this.clasesHeredadas.add(nombreClasePadre);
    }

    public List<String> getClasesHeredadas() {
        return new ArrayList<>(clasesHeredadas); // Copia defensiva
    }

    public void setVisibilidad(String visibilidad) {
        this.visibilidad = visibilidad;
    }

    public String getVisibilidad() { 
        return visibilidad; 
    }

    public boolean esPrivado() { 
        return "private".equalsIgnoreCase(visibilidad); 
    }

    public void agregarAmigo(String nombreAmigo) { 
        if (!listaAmigos.contains(nombreAmigo)) { 
            listaAmigos.add(nombreAmigo); 
        } 
    }

    public List<String> getListaAmigos() { 
        return listaAmigos; 
    }
}

