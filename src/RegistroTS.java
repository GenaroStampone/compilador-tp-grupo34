public class RegistroTS {
    private String lexema;
    private int idToken;
    private String uso; // Indica el rol del lexema (ej: "VARIABLE", "CONSTANTE")
    private String tipoToken;
    private int nroLinea;
    private double valorNum;
    private int longitud;

    // Constructor para ID, Cadenas y Tokens sin valor numérico inicial
    public RegistroTS(String lexema, int idToken, String tipoToken, int nroLinea) {
        this.lexema = lexema;
        this.idToken = idToken;
        this.tipoToken = tipoToken;
        this.nroLinea = nroLinea;
        this.valorNum = 0.0;
        this.longitud = (lexema != null) ? lexema.length() : 0;
        this.uso = null;
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
}