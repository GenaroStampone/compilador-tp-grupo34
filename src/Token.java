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
            case Parser.ID:                 return "ID";
            case Parser.CTE_USHORT:         return "CTE_USHORT";
            case Parser.CTE_DOUBLEF:        return "CTE_DOUBLEF";
            case Parser.CADENA:             return "CADENA";
            case Parser.COMP_MAYOR_IGUAL:   return "COMP_MAYOR_IGUAL (>=)";
            case Parser.COMP_MENOR_IGUAL:   return "COMP_MENOR_IGUAL (<=)";
            case Parser.COMP_IGUAL:         return "COMP_IGUAL (==)";
            case Parser.COMP_DIST:          return "COMP_DIST (!=)";
            case Parser.ASIGNACION:         return "ASIGNACION (:=)";
            case Parser.CORCHETE_IZQ:       return "CORCHETE_IZQ ([)";
            case Parser.CORCHETE_DER:       return "CORCHETE_DER (])";
            case Parser.IF:                 return "PR_IF";
            case Parser.ELSE:               return "PR_ELSE";
            case Parser.END_IF:             return "PR_END_IF";
            case Parser.BEGIN:              return "PR_BEGIN";
            case Parser.END:                return "PR_END";
            case Parser.POUT:               return "PR_POUT";
            case Parser.RET:                return "PR_RET";
            case Parser.CLASS:              return "PR_CLASS";
            case Parser.FUNCTION:           return "PR_FUNCTION";
            case Parser.USHORTINT:          return "PR_USHORTINT";
            case Parser.DOUBLEF:            return "PR_DOUBLEF";
            case Parser.WHILE:              return "PR_WHILE";
            case Parser.REPEAT:             return "PR_REPEAT";
            case Parser.TYPEDEF:            return "PR_TYPEDEF";
            case Parser.PRIVATE:            return "PR_PRIVATE";
            case Parser.FRIEND:             return "PR_FRIEND";
            case Parser.EXTENDS:            return "PR_EXTENDS";
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