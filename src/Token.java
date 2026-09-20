public class Token {
    private idToken int;
    private RegistroTS punteroTS;

    public Token(int idToken, RegistroTS punteroTS) { // Constructor para tokens que representan multiples lexemas con atributo, por ejemplo para identificadores y literales.
        this.idToken = idToken;
        this.punteroTS = punteroTS;
    }

    public Token(int idToken) { // Constructor para tokens que representan un unico lexema (sin atributo), por ejemplo para palabras reservadas.
        this.idToken = idToken;
        this.punteroTS = null;
    }

    public int getIdToken() {
        return idToken;
    }

    public RegistroTS getPunteroTS() {
        return punteroTS;
    }

    @Override
    public String toString() {
        if (punteroTS != null) { 
            return "Token [" + idToken + ", Lexema: '" + punteroTS.getLexema() + "']"; 
            } 
        return "Token [" + idToken + "]";
        }
}    