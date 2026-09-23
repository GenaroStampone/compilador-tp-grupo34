import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalizadorLexico {

    // --- CONSTANTES DE TOKENS ---
    public static final int ID = 257;
    public static final int CTE_ENTERA = 258;
    public static final int DOUBLEF_VAL = 259;
    public static final int CADENA = 260;
    public static final int MAYOR_IGUAL = 261;
    public static final int MENOR_IGUAL = 262;
    public static final int IGUAL_IGUAL = 263;
    public static final int DISTINTO = 264;
    public static final int ASIGNACION = 265;

    // Palabras Reservadas
    public static final int IF = 270;
    public static final int ELSE = 271;
    public static final int END_IF = 272;
    public static final int BEGIN = 273;
    public static final int END = 274;
    public static final int POUT = 275;
    public static final int RET = 276;
    public static final int CLASS = 277;
    public static final int FUNCTION = 278;
    public static final int USHORTINT = 279;
    public static final int DOUBLEF = 280;
    public static final int THEN = 281;
    public static final int WHILE = 282;
    public static final int REPEAT = 283;
    public static final int TYPEDEF = 284;
    public static final int PRIVATE = 285;
    public static final int FRIEND = 286;
    public static final int EXTENDS = 287;
    public static final int TODF = 288;
    public static final int TOUS = 289;

    // Símbolos con valor ASCII directo (referencias opcionales)
    public static final int CORCHETE_ABRE = '[';
    public static final int CORCHETE_CIERRA = ']';
    public static final int PAREN_ABRE = '(';
    public static final int PAREN_CIERRA = ')';
    public static final int COMA = ',';
    public static final int PUNTO_Y_COMA = ';';
    public static final int DOS_PUNTOS = ':';
    public static final int PUNTO = '.';
    public static final int IGUAL = '=';

    // --- Atributos del Autómata y Lectura ---
    private BufferedReader lectorFuente;
    private TablaDeSimbolos tablaSimbolos;
    private int estadoActual;
    private int nroLinea;
    public int yylineno; // Contador estándar de líneas para interfaz con Parser / Yacc
    private StringBuilder buffer;
    private int ultimoCaracter; // Control de lookahead / ungetc

    private List<String> errores = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    // Matrices del Autómata
    private int[][] TE_mat;
    private AccionSemantica[][] AS_mat;

    // Arreglo de mapeo O(1) ASCII -> Columna
    private int[] mapaCaracteres;

    // Atributo entregado al Parser (contiene la referencia RegistroTS en .obj)
    public ParserVal yylval;

    private static final int CANT_ESTADOS = 19;
    private static final int CANT_COLUMNAS = 21;
    private static final int COLUMNA_C = 17; // Índice de la columna 'C' (Cualquier otro)
    private static final int F = -1; // Estado Final / Emisión de token
    private static final int LARGO_MAX_ID = 22; // Límite de 22 caracteres

    public AnalizadorLexico(String rutaArchivo, TablaDeSimbolos tablaSimbolos) {
        try {
            this.lectorFuente = new BufferedReader(new FileReader(rutaArchivo));
        } catch (FileNotFoundException e) {
            System.err.println("Error: No se encontró el archivo fuente en " + rutaArchivo);
        }

        this.tablaSimbolos = tablaSimbolos;
        this.nroLinea = 1; // La numeración de líneas comienza en 1
        this.yylineno = 1;
        this.estadoActual = 0;
        this.buffer = new StringBuilder();
        this.ultimoCaracter = -1;

        inicializarMapeoCaracteres();
        inicializarMatrices();
    }

    private void inicializarMapeoCaracteres() {
        mapaCaracteres = new int[256]; // Arreglo de 256 posiciones para ASCII
        Arrays.fill(mapaCaracteres, COLUMNA_C); // Por defecto: todo se asigna a 'C' (columna 17)

        // Columna 0: Símbolos simples '*', '/', '(', ')', ',', ';', '[', ']'
        mapaCaracteres['*'] = 0;
        mapaCaracteres['/'] = 0;
        mapaCaracteres['('] = 0;
        mapaCaracteres[')'] = 0;
        mapaCaracteres[','] = 0;
        mapaCaracteres[';'] = 0;
        mapaCaracteres['['] = 0;
        mapaCaracteres[']'] = 0;

        // Columna 1: '{'
        mapaCaracteres['{'] = 1;
        // Columna 2: '}'
        mapaCaracteres['}'] = 2;
        // Columna 3: '.'
        mapaCaracteres['.'] = 3;
        // Columna 4: '$'
        mapaCaracteres['$'] = 4;
        // Columna 8: '_'
        mapaCaracteres['_'] = 8;
        // Columna 9: '='
        mapaCaracteres['='] = 9;
        // Columna 10: '<'
        mapaCaracteres['<'] = 10;
        // Columna 11: '>'
        mapaCaracteres['>'] = 11;
        // Columna 12: '!'
        mapaCaracteres['!'] = 12;
        // Columna 13: ':'
        mapaCaracteres[':'] = 13;

        // Columna 14: '+' y '-'
        mapaCaracteres['+'] = 14;
        mapaCaracteres['-'] = 14;

        // Columna 15: Letras minúsculas 'L' (a-z excepto 'u', 's', 'd')
        for (char c = 'a'; c <= 'z'; c++) mapaCaracteres[c] = 15;
        
        // Columna 5: 'u'
        mapaCaracteres['u'] = 5;
        // Columna 6: 's'
        mapaCaracteres['s'] = 6;
        // Columna 7: 'd' (letra d)
        mapaCaracteres['d'] = 7;

        // Columna 16: Letras mayúsculas 'M' (A-Z)
        for (char c = 'A'; c <= 'Z'; c++) mapaCaracteres[c] = 16;

        // Columna 18: Dígitos '0-9'
        for (char c = '0'; c <= '9'; c++) mapaCaracteres[c] = 18;

        // Columna 19: Saltos de línea
        mapaCaracteres['\n'] = 19;
        mapaCaracteres['\r'] = 19;

        // Columna 20: Blancos y tabuladores (' ', '\t')
        mapaCaracteres[' '] = 20;
        mapaCaracteres['\t'] = 20;
    }

    private int mapearColumna(char c) {
        if (c >= 0 && c < 256) {
            return mapaCaracteres[c];
        }
        return COLUMNA_C; // Retorna columna 17 para cualquier otro carácter
    }

    private void inicializarMatrices() {
        TE_mat = new int[CANT_ESTADOS][CANT_COLUMNAS];
        AS_mat = new AccionSemantica[CANT_ESTADOS][CANT_COLUMNAS];

        for (int i = 0; i < CANT_ESTADOS; i++) {
            Arrays.fill(TE_mat[i], F);
        }

        // =========================================================
        // ESTADO 0 (Estado Inicial)
        // =========================================================
                            AS_mat[0][0] = this::AS13; // '*', '/', '(', ')', ',', ';'
        TE_mat[0][1] = 11;  AS_mat[0][1] = this::AS22; // '{'
        TE_mat[0][3] = 5;   AS_mat[0][3] = this::AS8;  // '.'
        TE_mat[0][5] = 1;   AS_mat[0][5] = this::AS1;  // 'u'
        TE_mat[0][6] = 1;   AS_mat[0][6] = this::AS1;  // 's'
        TE_mat[0][7] = 1;   AS_mat[0][7] = this::AS1;  // 'd'
        TE_mat[0][9] = 15;  AS_mat[0][9] = null; // '='
        TE_mat[0][10] = 17;  AS_mat[0][10] = null; // '<'
        TE_mat[0][11] = 18;  AS_mat[0][11] = null; // '>'
        TE_mat[0][12] = 16;  AS_mat[0][12] = null; // '!'
        TE_mat[0][13] = 6;   AS_mat[0][13] = null; // ':'
                             AS_mat[0][14] = this::AS13; // '+', '-'
        TE_mat[0][15] = 1;   AS_mat[0][15] = this::AS1;  // L
        TE_mat[0][16] = 1;   AS_mat[0][16] = this::AS1;  // M
        TE_mat[0][18] = 2;   AS_mat[0][18] = this::AS4;  // d
        TE_mat[0][19] = 0;   AS_mat[0][19] = this::AS_Descartar; // \n, \r
        TE_mat[0][20] = 0;   AS_mat[0][20] = this::AS_Descartar; // ' ', \t

        TE_mat[0][2] = 0;   AS_mat[0][2] = this::AS_ErrorLexico; // '}'
        TE_mat[0][4] = 0;   AS_mat[0][4] = this::AS_ErrorLexico; // '$'
        TE_mat[0][8] = 0;   AS_mat[0][8] = this::AS_ErrorLexico; // '_'
        TE_mat[0][COLUMNA_C] = 0;   AS_mat[0][COLUMNA_C] = this::AS_ErrorLexico; // Cualquier otro carácter
        
        // =========================================================
        // ESTADO 1 (Identificadores y Palabras Reservadas)
        // =========================================================
        TE_mat[1][8] = 1;   AS_mat[1][8] = this::AS2; // '_'
        TE_mat[1][15] = 1;   AS_mat[1][15] = this::AS2; // L
        TE_mat[1][16] = 1;   AS_mat[1][16] = this::AS2; // M
        TE_mat[1][18] = 1;   AS_mat[1][18] = this::AS2; // d
        TE_mat[1][5] = 1;   AS_mat[1][5] = this::AS2; // "u"
        TE_mat[1][6] = 1;   AS_mat[1][6] = this::AS2; // "s"
        TE_mat[1][7] = 1;   AS_mat[1][7] = this::AS2; // "d"

        // Cualquier otro carácter (incluyendo C=17) emite token
        for (int col = 0; col < CANT_COLUMNAS; col++) {
            if (col != 8 && col != 15 && col != 16 && col != 18 && col != 5 && col != 6 && col != 7) {
                AS_mat[1][col] = this::AS3;
            }
        }

        // =========================================================
        // ESTADO 2 (Constantes / Enteros)
        // =========================================================
        TE_mat[2][3] = 5; AS_mat[2][3] = this::AS30; // '.'
        TE_mat[2][4] = 3; AS_mat[2][4] = this::AS9; // '$'
        TE_mat[2][18] = 2; AS_mat[2][18] = this::AS5; // dígitos

        for (int col = 0; col < CANT_COLUMNAS; col++) {
            if (col != 3 && col != 4 && col != 18) {
                TE_mat[2][col] = 0;
                AS_mat[2][col] = this::AS_ErrorLexico;
            }
        }

        // =========================================================
        // ESTADOS 3 Y 4 (Exponentes)
        // =========================================================
        TE_mat[3][5] = 4; AS_mat[3][5] = this::AS10; // 'u'
                          AS_mat[4][6] = this::AS11; // 's'

        for (int col = 0; col < CANT_COLUMNAS; col++) {
            if (col != 5) {
                TE_mat[3][col] = 0;
                AS_mat[3][col] = this::AS_ErrorLexico;
            }
            if (col != 6) {
                TE_mat[4][col] = 0;
                AS_mat[4][col] = this::AS_ErrorLexico;
            }
        }

        // =========================================================
        // ESTADOS 5 Y 6 (Punto / Flotante y Dos Puntos / Asignación)
        // =========================================================
        TE_mat[5][18] = 7; AS_mat[5][18] = this::AS5; // dígito
        TE_mat[6][9] = F;  AS_mat[6][9] = this::AS15; // ':='

        for (int col = 0; col < CANT_COLUMNAS; col++) {
            if (col != 18) {
                TE_mat[5][col] = F;
                AS_mat[5][col] = this::AS_Punto;
            }
            if (col != 9) {
                TE_mat[6][col] = F;
                AS_mat[6][col] = this::AS_DosPuntos;
            }
        }

        // =========================================================
        // ESTADO 7
        // =========================================================
        TE_mat[7][7] = 8; AS_mat[7][7] = this::AS12; // 'd'
        TE_mat[7][18] = 7; AS_mat[7][18] = this::AS5; // dígito

        for (int col = 0; col < CANT_COLUMNAS; col++) {
            if (col != 7 && col != 18) {
                AS_mat[7][col] = this::AS6;
            }
        }

        // =========================================================
        // ESTADOS 8, 9, 10
        // =========================================================
        TE_mat[8][14] = 9;  AS_mat[8][14] = this::AS7; // '+' o '-'
        TE_mat[8][18] = 10; AS_mat[8][18] = this::AS5; // dígito

        TE_mat[9][18] = 10; AS_mat[9][18] = this::AS5; // dígito
        
        TE_mat[10][18] = 10; AS_mat[10][18] = this::AS5; // dígito
        
        for (int col = 0; col < CANT_COLUMNAS; col++) {
            if (col != 18) {
                TE_mat[9][col] = 0;
                AS_mat[9][col] = this::AS_ErrorLexico;
                AS_mat[10][col] = this::AS6;
            }
        }

        for (int col = 0; col < CANT_COLUMNAS; col++) {
            if (col != 18 && col != 14) {
                TE_mat[8][col] = 0;
                AS_mat[8][col] = this::AS_ErrorLexico;
            }
        }

        // =========================================================
        // ESTADOS 11, 12, 13, 14 (Cadenas y Comentarios)
        // =========================================================
        for (int col = 0; col < CANT_COLUMNAS; col++) {
            TE_mat[11][col] = 13; AS_mat[11][col] = this::AS24;
            TE_mat[12][col] = 12; AS_mat[12][col] = this::AS26;
            TE_mat[13][col] = 13; AS_mat[13][col] = this::AS24;
            TE_mat[14][col] = 12; AS_mat[14][col] = this::AS26;
        }

        TE_mat[11][1] = 12; AS_mat[11][1] = this::AS25; // '{'
                            AS_mat[11][2] = this::AS23; // '}'
        TE_mat[11][19] = 0; AS_mat[11][19] = this::AS29; // '\n' o '\r'

        TE_mat[12][2] = 14; AS_mat[12][2] = this::AS27; // '}'

                            AS_mat[13][2] = this::AS23; // '}'
        TE_mat[13][19] = 0;  AS_mat[13][19] = this::AS29; // '\n' o '\r'

        TE_mat[14][2] = 0;  AS_mat[14][2] = this::AS28; // '}'

        // =========================================================
        // ESTADOS 15, 16, 17, 18 (Comparadores)
        // =========================================================
        for (int col = 0; col < CANT_COLUMNAS; col++) {
            AS_mat[15][col] = this::AS16;
            TE_mat[16][col] = 0;    
            AS_mat[16][col] = this::AS_ErrorLexico;
            AS_mat[17][col] = this::AS18;
            AS_mat[18][col] = this::AS20;
        }
        AS_mat[15][9] = this::AS17; // ==
        TE_mat[16][9] = F; AS_mat[16][9] = this::AS14; // !=
        AS_mat[17][9] = this::AS19; // <=
        AS_mat[18][9] = this::AS21; // >=
    }

    public int yylex() {
        this.yylval = null; // Evita arrastrar atributos de tokens anteriores
        estadoActual = 0;
        buffer.setLength(0);

        while (true) {
            int caracterLeido = obtenerSiguienteCaracter();

            if (caracterLeido == -1) {
                return 0; // EOF
            }

            char c = (char) caracterLeido;
        
            int columna = mapearColumna(c);

            int tokenDevuelto = -1;
            if (AS_mat[estadoActual][columna] != null) {
                tokenDevuelto = AS_mat[estadoActual][columna].ejecutar(c);
            }

            estadoActual = TE_mat[estadoActual][columna];

            if (tokenDevuelto != -1) {
                return tokenDevuelto;
            }

            if (estadoActual == F || estadoActual < 0) {
                estadoActual = 0;
            }
        }
    }

    // =========================================================================
    //                          ACCIONES SEMÁNTICAS 
    // =========================================================================

    // --- IDENTIFICADORES Y PALABRAS RESERVADAS ---

    // AS1: Inicializar string y añadir letra leída
    private int AS1(char c) {
        buffer.setLength(0); // Vacía el buffer acumulador para eliminar lecturas anteriores
        buffer.append(c);    // Agrega el primer carácter (letra) al buffer del lexema
        return -1;           // Retorna -1 para indicar que el token no está completo y seguir leyendo
    }

    // AS2: Concatenar {L, d, "_", M} al string
    private int AS2(char c) {
        buffer.append(c);    // Concatena el carácter leído (letra, dígito o '_') al buffer existente
        return -1;           // Retorna -1 para continuar acumulando caracteres en el lexema
    }

    // AS3: Devolver el último carácter leído, verificar longitud (truncamiento a 22 chars),
    // buscar en TS/TPR y retornar el token (ID o Palabra Reservada)
    private int AS3(char c) {
        ungetc(c);                           // Devuelve el carácter delimitador (ej. espacio, operador) al flujo de entrada
        String lexema = buffer.toString();   // Convierte todo el texto acumulado en el buffer a un String
        
        // Control de longitud máxima (22 caracteres)
        if (lexema.length() > LARGO_MAX_ID) { // Verifica si la longitud supera los 22 caracteres permitidos
            String warn = String.format("Línea %d: Warning: El identificador '%s' fue truncado a '%s'",
                    nroLinea, lexema, lexema.substring(0, LARGO_MAX_ID));
            warnings.add(warn);
            System.out.println(warn); // Imprime en pantalla una advertencia de truncamiento
            lexema = lexema.substring(0, LARGO_MAX_ID); // Recorta la cadena quedándose solo con los primeros 22 caracteres
        }
        
        // Consulta en Tabla de Símbolos / Palabras Reservadas
        if (tablaSimbolos.esPalabraReservada(lexema)) { // Consulta si la cadena coincide con alguna Palabra Reservada del lenguaje
            return obtenerTokenPalabraReservada(lexema); // Devuelve la constante entera específica asignada a esa Palabra Reservada
        }
        
        RegistroTS reg = tablaSimbolos.alta(lexema, ID, nroLinea); // Da de alta el Identificador en la Tabla de Símbolos
        this.yylval = new ParserVal(reg);                          // Asigna objeto ParserVal con referencia directa al RegistroTS
        this.yylval.sval = reg.getLexema();
        return ID;                                                 // Retorna la constante entera del token ID
    }

    // --- CONSTANTES NUMÉRICAS ---

    // AS4: Inicializar string para la constante y agregar dígito al string
    private int AS4(char c) {
        buffer.setLength(0); // Reinicia el buffer acumulador limpiando datos previos
        buffer.append(c);    // Agrega el primer dígito del número al buffer
        return -1;           // Retorna -1 para continuar procesando los siguientes dígitos
    }

    // AS5: Agregar dígito al string
    private int AS5(char c) {
        buffer.append(c);    // Concatena el nuevo dígito leído al número en construcción
        return -1;           // Retorna -1 para seguir acumulando dígitos
    }

    // AS6: Devolver el último carácter leído, verificar rango flotante de 64 bits,
    // dar de alta en TS y retornar token DOUBLEF (o Error Léxico si supera rango)
    private int AS6(char c) {
        ungetc(c);                          // Devuelve el carácter leído de más que provocó el corte del número
        String lexema = buffer.toString();  // Obtiene el texto completo de la constante acumulada
        try {
            double val = Double.parseDouble(lexema.replace("d", "e").replace("D", "e")); // Convierte la notación 'd' a 'e' y la transforma a double
            double absVal = Math.abs(val);  // Calcula el valor absoluto para evaluar los límites numéricos
            double minVal = 2.2250738585072014e-308; // Límite inferior positivo para punto flotante de 64 bits
            double maxVal = 1.7976931348623157e+308; // Límite superior máximo para punto flotante de 64 bits
            
            if (val == 0.0 || (absVal >= minVal && absVal <= maxVal)) { // Verifica que el valor esté dentro del rango permitido o sea 0.0
                RegistroTS reg = tablaSimbolos.alta(lexema, DOUBLEF_VAL, nroLinea, val); // Registra la constante en la Tabla de Símbolos
                this.yylval = new ParserVal(reg);                               // Pasa la referencia del RegistroTS en yylval para Yacc
                this.yylval.sval = reg.getLexema();
                return DOUBLEF_VAL;                                             // Retorna la constante entera del token DOUBLEF
            } else {
                String err = String.format("Línea %d: Error Léxico: Constante flotante fuera de rango '%s'", nroLinea, lexema);
                errores.add(err);
                System.err.println(err);
                buffer.setLength(0); // Vacía el buffer por haber detectado un valor inválido
                return -1;           // Retorna -1 sin emitir ningún token al Parser
            }
        } catch (NumberFormatException e) {
            String err = String.format("Línea %d: Error Léxico: Constante flotante mal formada '%s'", nroLinea, lexema);
            errores.add(err);
            System.err.println(err);
            buffer.setLength(0);     // Limpia el buffer
            return -1;               // Retorna -1 para continuar la lectura
        }
    }

    // AS7: Concatenar {"+", "-"} al string
    private int AS7(char c) {
        buffer.append(c);    // Concatena el signo '+' o '-' correspondiente al exponente
        return -1;           // Retorna -1 para continuar leyendo los dígitos del exponente
    }

    // AS8: Inicializar string para la constante y agregar "." al string
    private int AS8(char c) {
        buffer.setLength(0); // Vacía el buffer de acumulaciones previas
        buffer.append(c);    // Agrega el punto '.' como carácter inicial del número
        return -1;           // Retorna -1 para continuar acumulando la parte decimal
    }

    // AS9: Concatenar "$" al string
    private int AS9(char c) {
        buffer.append(c);    // Agrega el separador especial '$' al buffer
        return -1;           // Retorna -1 para continuar la lectura
    }

    // AS10: Concatenar "u" al string
    private int AS10(char c) {
        buffer.append(c);    // Agrega el sufijo 'u' al buffer
        return -1;           // Retorna -1 para continuar
    }

    // AS11: Acción semántica ejecutada al leer el carácter 's' que completa el sufijo "$us"
    private int AS11(char c) {
        // 1. Agrega el carácter 's' al acumulador para completar el lexema (ejemplo: "100$us")
        buffer.append(c);
        
        // 2. Obtiene el texto completo del lexema acumulado hasta el momento
        String lexemaCompleto = buffer.toString();
        
        // 3. Busca la posición del símbolo '$' dentro del lexema para separar el número del sufijo
        int posDollar = lexemaCompleto.indexOf('$');
        
        // 4. Extrae únicamente los dígitos antes del '$' (de "100$us" obtiene la cadena "100")
        String numeroStr = (posDollar != -1) ? lexemaCompleto.substring(0, posDollar) : lexemaCompleto;

        try {
            // 5. Convierte la subcadena de dígitos a un valor numérico entero en Java
            int val = Integer.parseInt(numeroStr);
            
            // 6. Controla la regla de negocio: el entero sin signo (us) debe estar entre 0 y 255
            if (val >= 0 && val <= 255) {
                
                // 7. Registra el lexema completo en la Tabla de Símbolos como constante entera
                RegistroTS reg = tablaSimbolos.alta(lexemaCompleto, CTE_ENTERA, nroLinea, val);
                
                // 8. Asigna la referencia al elemento de la TS en la variable de atributo del parser (YACC)
                this.yylval = new ParserVal(reg);
                this.yylval.sval = reg.getLexema();
                
                // 9. Devuelve el identificador del token (CTE_ENTERA) para entregárselo al parser
                return CTE_ENTERA;
                
            } else {
                // Caso de error: El número se pudo parsear pero excede el límite permitido (ejemplo: "300$us")
                String err = String.format("Línea %d: Error Léxico: Constante entera fuera de rango (0 a 255) '%s'", 
                        nroLinea, lexemaCompleto);
                errores.add(err);
                System.err.println(err);
                buffer.setLength(0);
                return -1;
            }
        } catch (NumberFormatException e) {
            String err = String.format("Línea %d: Error Léxico: Constante entera mal formada '%s'", 
                    nroLinea, lexemaCompleto);
            errores.add(err);
            System.err.println(err);
            buffer.setLength(0);
            return -1;
        }
    }




    // AS12: Concatenar "d" al string
    private int AS12(char c) {
        buffer.append(c);    // Concatena el sufijo 'd' al buffer
        return -1;           // Retorna -1 para continuar la lectura
    }

    // --- OPERADORES Y LITERALES ---

    // AS13: Reconocer literal y devolver token
    private int AS13(char c) {
        return (int) c;      // Convierte directamente el carácter simple a su código ASCII y lo retorna como ID del token
    }

    // AS14: Consumir el carácter "=" y devolver el token "!="
    private int AS14(char c) {
        return DISTINTO;     // Retorna la constante entera del token que representa el operador '!='
    }

    // AS15: Consumir el carácter "=" y devolver el token ":="
    private int AS15(char c) {
        return ASIGNACION;   // Retorna la constante entera del token que representa el operador ':='
    }

    // AS16: Devolver el último carácter leído a la entrada y devolver el token "="
    private int AS16(char c) {
        ungetc(c);           // Devuelve el carácter posterior no esperado al flujo de entrada
        return (int) '=';    // Retorna el código ASCII del carácter '=' para representar la igualdad simple
    }

    // AS17: Consumir el carácter "=" y devolver el token "=="
    private int AS17(char c) {
        return IGUAL_IGUAL;  // Retorna la constante entera del token que representa la comparación '=='
    }

    // AS18: Devolver el último carácter leído a la entrada y devolver el token "<"
    private int AS18(char c) {
        ungetc(c);           // Devuelve el carácter posterior al flujo de entrada
        return (int) '<';    // Retorna el código ASCII del carácter '<'
    }

    // AS19: Consumir el carácter "=" y devolver el token "<="
    private int AS19(char c) {
        return MENOR_IGUAL;  // Retorna la constante entera del token que representa el operador '<='
    }

    // AS20: Devolver el último carácter leído a la entrada y devolver el token ">"
    private int AS20(char c) {
        ungetc(c);           // Devuelve el carácter posterior al flujo de entrada
        return (int) '>';    // Retorna el código ASCII del carácter '>'
    }

    // AS21: Consumir el carácter "=" y devolver el token ">="
    private int AS21(char c) {
        return MAYOR_IGUAL;  // Retorna la constante entera del token que representa el operador '>='
    }

    // --- CADENAS DE CARACTERES Y COMENTARIOS ---

    // AS22: Inicializar string para cadena de 1 línea y agregar una "{"
    private int AS22(char c) {
        buffer.setLength(0); // Limpia el buffer acumulador para dar inicio al texto
        buffer.append(c);    // Guarda la llave de apertura '{' inicial en el buffer
        return -1;           // Retorna -1 para continuar acumulando el cuerpo de la cadena
    }

    // AS23: Concatenar un "}" al string y devolver el token para la cadena
    private int AS23(char c) {
        buffer.append(c);                                             // Concatena la llave de cierre '}' al buffer
        String lexema = buffer.toString();                            // Convierte el contenido completo del buffer a String
        RegistroTS reg = tablaSimbolos.alta(lexema, CADENA, nroLinea); // Registra el texto completo de la cadena en la Tabla de Símbolos
        this.yylval = new ParserVal(reg);                             // Asigna la referencia de la cadena (RegistroTS) en yylval para Yacc
        this.yylval.sval = reg.getLexema();
        return CADENA;                                                // Retorna la constante del token CADENA
    }

    // AS24: Concatenar cualquier C - {"\n", "\r", "}"} al string
    private int AS24(char c) {
        buffer.append(c);    // Agrega el carácter correspondiente al cuerpo interno de la cadena
        return -1;           // Retorna -1 para continuar acumulando texto
    }

    // AS25: Agregar una "{" al string. Paso de formar una cadena de 1 línea a un comentario multilínea
    private int AS25(char c) {
        buffer.append(c);    // Concatena la segunda '{' indicando que se trata de un comentario '{{'
        return -1;           // Retorna -1 para continuar dentro del cuerpo del comentario
    }

    // AS26: Concatenar cualquier C - "}" al string. Si hubo salto de línea, sumarlo al contador de líneas.
    private int AS26(char c) {
        buffer.append(c);    // Agrega el carácter al texto del comentario multilínea
        if (c == '\n') {     // Evalúa si el carácter procesado es un salto de línea '\n'
            nroLinea++;      // Incrementa en 1 el contador global de líneas
            yylineno = nroLinea;
        }
        return -1;           // Retorna -1 para seguir leyendo dentro del comentario
    }

    // AS27: Concatenar un "}" al string
    private int AS27(char c) {
        buffer.append(c);    // Agrega la primera llave '}' de cierre del comentario
        return -1;           // Retorna -1 a la espera del segundo '}' para confirmar el cierre '}}'
    }

    // AS28: Concatenar un "}" al string. Retornar al estado inicial (no se devuelve token)
    private int AS28(char c) {
        buffer.append(c);    // Concatena la segunda llave '}' completando el cierre '}}'
        buffer.setLength(0); // Vacía y elimina todo el contenido del comentario del buffer
        return -1;           // Retorna -1 para regresar al Estado 0 sin entregar ningún token al Parser
    }

    // AS29: Emitir "Error Léxico: Cadena de 1 línea no cerrada". Limpiar la cadena mal formada. Incrementar contador de líneas.
    private int AS29(char c) {
        System.err.printf("Línea %d: Error Léxico: Cadena de 1 línea no cerrada%n", nroLinea); // Informa en consola el error de la cadena no cerrada
        buffer.setLength(0); // Vacía el buffer que guardaba la cadena incompleta
        if (c == '\n') {     // Revisa si el carácter que provocó el corte fue un salto de línea
            nroLinea++;      // Incrementa el contador de líneas
            yylineno = nroLinea;
        }
        return -1;           // Retorna -1 para recuperarse y continuar escaneando los siguientes tokens
    }

    // AS30: Agregar "." al string
    private int AS30(char c) {
        buffer.append(c);    // Agrega el punto '.' como carácter inicial del número
        return -1;           // Retorna -1 para continuar acumulando la parte decimal
    }

    // --- ACCIONES AUXILIARES Y SÍMBOLOS NUEVOS ---

    // AS_DosPuntos: Devolver el último carácter leído y retornar el token ':'
    private int AS_DosPuntos(char c) {
        ungetc(c);
        return (int) ':';
    }

    // AS_Punto: Devolver el último carácter leído. Si el buffer contenía solo '.',
    // retornar el token literal '.', de lo contrario emitir error léxico por float mal formado.
    private int AS_Punto(char c) {
        ungetc(c);
        if (buffer.length() == 1 && buffer.charAt(0) == '.') {
            buffer.setLength(0);
            return (int) '.';
        } else {
            System.err.printf("Línea %d: Error Léxico: Constante flotante mal formada (se esperaba dígito tras el punto) '%s'%n",
                    nroLinea, buffer.toString());
            buffer.setLength(0);
            return -1;
        }
    }

    // AS_Descartar: Saltea espacios, tabuladores y saltos de línea actualizando el número de línea.
    private int AS_Descartar(char c) {
        if (c == '\n') {     // Verifica si el carácter descartado es un salto de línea
            nroLinea++;      // Incrementa el contador de líneas del analizador
            yylineno = nroLinea;
        }
        return -1;           // Retorna -1 sin entregar token para que yylex() continúe con la siguiente iteración
    }

    // AS_ErrorLexico: Maneja transiciones no válidas o símbolos no permitidos en el autómata.
    private int AS_ErrorLexico(char c) {
        String err = String.format("Línea %d: Error Léxico: Carácter no esperado '%c' (ASCII %d)", 
                nroLinea, c, (int) c);
        errores.add(err);
        System.err.println(err);
        buffer.setLength(0);            // Vacía el buffer de lecturas parciales erróneas
        return -1;                      // Retorna -1 para permitir que el autómata vuelva a Estado 0 y siga leyendo
    }

    private int obtenerTokenPalabraReservada(String lexema) {
        switch (lexema.toLowerCase()) {
            case "if": return IF;
            case "then": return THEN;
            case "else": return ELSE;
            case "end_if": return END_IF;
            case "begin": return BEGIN;
            case "end": return END;
            case "pout": return POUT;
            case "ret": return RET;
            case "class": return CLASS;
            case "function": return FUNCTION;
            case "ushortint": return USHORTINT;
            case "doublef": return DOUBLEF;
            case "while": return WHILE;
            case "repeat": return REPEAT;
            case "typedef": return TYPEDEF;
            case "private": return PRIVATE;
            case "friend": return FRIEND;
            case "extends": return EXTENDS;
            case "todf": return TODF;
            case "tous": return TOUS;
            default: return ID;
        }
    }

    private int obtenerSiguienteCaracter() {
        if (ultimoCaracter != -1) {
            int c = ultimoCaracter;
            ultimoCaracter = -1;
            return c;
        }
        try {
            return lectorFuente.read();
        } catch (IOException e) {
            return -1;
        }
    }

    private void ungetc(char c) {
        this.ultimoCaracter = c;
    }

    public int getNroLinea() {
        return nroLinea;
    }

    public int getYylineno() {
        return yylineno;
    }

    public List<String> getErrores() {
        return errores;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}