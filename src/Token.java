public class Token {
    private final int idToken;
    private final RegistroTS punteroTS;

    // Constructor para tokens que representan multiples lexemas con atributo, por ejemplo para identificadores y literales.
    public Token(int idToken, RegistroTS punteroTS) { 
        this.idToken = idToken;
        this.punteroTS = punteroTS;
    }

    // Constructor para tokens que representan un unico lexema (sin atributo), por ejemplo para palabras reservadas.
    public Token(int idToken) { 
        this.idToken = idToken;
        this.punteroTS = null;
    }

    public int getIdToken() {
        return idToken;
    }

    public RegistroTS getPunteroTS() {
        return punteroTS;
    }

    public String getNombreToken() {
        return obtenerNombre(idToken);
    }

    public static String obtenerNombre(int token) {
        switch (token) {
            case 0:                              return "EOF";
            case AnalizadorLexico.ID:            return "ID";
            case AnalizadorLexico.CTE_ENTERA:    return "CTE_ENTERA";
            case AnalizadorLexico.DOUBLEF_VAL:   return "DOUBLEF_VAL";
            case AnalizadorLexico.CADENA:        return "CADENA";
            case AnalizadorLexico.MAYOR_IGUAL:   return "MAYOR_IGUAL (>=)";
            case AnalizadorLexico.MENOR_IGUAL:   return "MENOR_IGUAL (<=)";
            case AnalizadorLexico.IGUAL_IGUAL:   return "IGUAL_IGUAL (==)";
            case AnalizadorLexico.DISTINTO:      return "DISTINTO (!=)";
            case AnalizadorLexico.ASIGNACION:    return "ASIGNACION (:=)";
            case AnalizadorLexico.IF:            return "PR_IF";
            case AnalizadorLexico.ELSE:          return "PR_ELSE";
            case AnalizadorLexico.END_IF:        return "PR_END_IF";
            case AnalizadorLexico.BEGIN:         return "PR_BEGIN";
            case AnalizadorLexico.END:           return "PR_END";
            case AnalizadorLexico.POUT:          return "PR_POUT";
            case AnalizadorLexico.RET:           return "PR_RET";
            case AnalizadorLexico.CLASS:         return "PR_CLASS";
            case AnalizadorLexico.FUNCTION:      return "PR_FUNCTION";
            case AnalizadorLexico.USHORTINT:     return "PR_USHORTINT";
            case AnalizadorLexico.DOUBLEF:       return "PR_DOUBLEF";
            case AnalizadorLexico.WHILE:         return "PR_WHILE";
            case AnalizadorLexico.REPEAT:        return "PR_REPEAT";
            case AnalizadorLexico.TYPEDEF:       return "PR_TYPEDEF";
            case AnalizadorLexico.PRIVATE:       return "PR_PRIVATE";
            case AnalizadorLexico.FRIEND:        return "PR_FRIEND";
            case AnalizadorLexico.EXTENDS:       return "PR_EXTENDS";
            default:
                if (token > 0 && token < 256) {
                    return "LITERAL '" + (char) token + "'";
                }
                return "TOKEN_" + token;
        }
    }

    @Override
    public String toString() {
        if (punteroTS != null) { 
            return String.format("Token [%-4d | %-20s | Lexema: '%s']", idToken, getNombreToken(), punteroTS.getLexema()); 
        } 
        return String.format("Token [%-4d | %-20s]", idToken, getNombreToken());
    }
}