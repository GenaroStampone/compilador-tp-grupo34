public class RegistroTS{ //Representa una entrada individual dentro de la TS
    private String lexema;
    private int tipoToken;
    private int nroLinea;
    private double valorNum;
    private int longitud;

    public RegistroTS(String lexema, int tipoToken, int nroLinea) {
        this.lexema = lexema;
        this.tipoToken = tipoToken;
        this.nroLinea = nroLinea;
        this.valorNum = 0.0; // Inicializamos el valor numérico a 0.0 por defecto
        this.longitud = lexema.length();
    }

    public String getLexema() {
        return lexema;
    }

    public int getTipoToken() {
        return tipoToken;
    }

    public int getNroLinea() {
        return nroLinea;
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
}