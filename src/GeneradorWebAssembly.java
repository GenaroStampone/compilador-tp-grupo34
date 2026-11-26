import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Generador de código WebAssembly textual (.wat) a partir del AST.
 *
 * Grupo 34 - TP4. Controles incluidos: a (división por cero entera y
 * flotante), b (overflow de suma entera), c (overflow de suma flotante),
 * f (resta negativa ushortint) y h (valor inválido de enumeración).
 */
public class GeneradorWebAssembly {
    private static final double MAX_DOUBLEF = 1.7976931348623157e+308;

    private final TablaDeSimbolos ts;
    private final Map<String, NodoArbol> cuerposFunciones;
    private final NodoArbol raiz;

    private final List<RegistroTS> todasLasEntradas = new ArrayList<>();
    private final List<RegistroTS> clases = new ArrayList<>();
    private final List<RegistroTS> objetos = new ArrayList<>();
    private final List<RegistroTS> globales = new ArrayList<>();
    private final List<RegistroTS> funciones = new ArrayList<>();
    private final Map<RegistroTS, Integer> slotsAtributos = new HashMap<>();
    private final Map<String, List<RegistroTS>> atributosDeClase = new HashMap<>();
    private final Map<String, Integer> baseDeObjeto = new HashMap<>();
    private final Map<String, String> nombreGlobal = new HashMap<>();
    private final Map<String, String> nombreLocal = new HashMap<>();
    private final Map<String, String> nombreFuncion = new HashMap<>();
    private final Set<String> nombresUsados = new LinkedHashSet<>();
    private final Map<String, Integer> datos = new LinkedHashMap<>();
    private int siguienteObjeto = 0;
    private int siguienteDato = 0;

    public GeneradorWebAssembly(TablaDeSimbolos ts, Map<String, NodoArbol> cuerposFunciones, NodoArbol raiz) {
        this.ts = ts;
        this.cuerposFunciones = (cuerposFunciones != null) ? cuerposFunciones : new HashMap<>();
        this.raiz = raiz;
    }

    public String generar() {
        prepararEntradas();
        prepararNombres();
        prepararObjetos();
        siguienteDato = alinear8(siguienteObjeto);
        StringBuilder funcionesWat = new StringBuilder();
        StringBuilder globalsWat = new StringBuilder();

        for (RegistroTS reg : globales) {
            String tipoWat = tipoWatDe(reg);
            String inicial = tipoWat.equals("f64") ? "f64.const 0.0" : "i32.const 0";
            globalsWat.append("  (global ").append(nombreGlobal.get(reg.getLexema()))
                    .append(" (mut ").append(tipoWat).append(") (").append(inicial).append("))\n");
        }

        for (RegistroTS reg : funciones) {
            NodoArbol cuerpo = cuerposFunciones.get(reg.getLexema());
            if (cuerpo != null) {
                funcionesWat.append(generarFuncion(reg, cuerpo));
            }
        }

        Ctx ctxMain = new Ctx(null, null);
        String cuerpoMain = compilarSecuencia(raiz != null ? raiz.getTer() : null, ctxMain);
        ctxMain.code.append(cuerpoMain);
        ctxMain.code.append("  i32.const 0\n");
        String main = "  (func $__main (export \"main\") (result i32)\n"
                + declararLocales(ctxMain)
                + ctxMain.code
                + "  )\n";

        StringBuilder dataWat = new StringBuilder();
        for (Map.Entry<String, Integer> e : datos.entrySet()) {
            dataWat.append("  (data (i32.const ").append(e.getValue()).append(") \"")
                    .append(escaparWat(e.getKey())).append("\")\n");
        }

        int bytesNecesarios = Math.max(1, siguienteDato);
        int paginas = Math.max(1, (bytesNecesarios + 65535) / 65536);

        StringBuilder out = new StringBuilder();
        out.append("(module\n");
        out.append("  (import \"env\" \"log\" (func $log (param i32)))\n");
        out.append("  (import \"env\" \"logf\" (func $logf (param f64)))\n");
        out.append("  (import \"env\" \"logs\" (func $logs (param i32 i32)))\n");
        out.append("  (import \"env\" \"abort\" (func $abort (param i32)))\n");
        out.append("  (memory (export \"memory\") ").append(paginas).append(")\n");
        out.append(globalsWat);
        out.append(funcionesWat);
        out.append(main);
        out.append(dataWat);
        out.append(")\n");
        return out.toString();
    }

    // =========================================================================
    // PREPARACIÓN DE METADATOS
    // =========================================================================

    private void prepararEntradas() {
        List<RegistroTS> entradas = new ArrayList<>(ts.getTabla().values());
        entradas.sort((a, b) -> String.valueOf(a.getLexema()).compareTo(String.valueOf(b.getLexema())));
        for (RegistroTS reg : entradas) {
            if (reg == null || reg.getLexema() == null) continue;
            todasLasEntradas.add(reg);
            String uso = reg.getUso() == null ? "" : reg.getUso();
            if ("nombre_clase".equalsIgnoreCase(uso)) {
                clases.add(reg);
            } else if ("objeto".equalsIgnoreCase(uso)) {
                objetos.add(reg);
            } else if ("variable".equalsIgnoreCase(uso) && "main".equals(reg.getAmbito())) {
                globales.add(reg);
            } else if ("nombre_funcion".equalsIgnoreCase(uso) || "metodo".equalsIgnoreCase(uso)) {
                funciones.add(reg);
            }
        }

        for (RegistroTS clase : clases) {
            List<RegistroTS> attrs = ts.obtenerTodosLosAtributosHeredados(clase.getNombreSimple());
            atributosDeClase.put(clase.getNombreSimple(), attrs);
            for (RegistroTS attr : attrs) {
                if (!slotsAtributos.containsKey(attr)) {
                    slotsAtributos.put(attr, slotsAtributos.size());
                }
            }
        }
    }

    private void prepararNombres() {
        for (RegistroTS reg : todasLasEntradas) {
            String uso = reg.getUso() == null ? "" : reg.getUso();
            if ("variable".equalsIgnoreCase(uso) && "main".equals(reg.getAmbito())) {
                nombreGlobal.put(reg.getLexema(), nombreUnico("g_", reg.getLexema()));
            } else if (("variable".equalsIgnoreCase(uso) || "parametro".equalsIgnoreCase(uso))
                    && !"main".equals(reg.getAmbito())) {
                nombreLocal.put(reg.getLexema(), nombreUnico("l_", reg.getLexema()));
            } else if ("nombre_funcion".equalsIgnoreCase(uso) || "metodo".equalsIgnoreCase(uso)) {
                nombreFuncion.put(reg.getLexema(), nombreUnico("fn_", reg.getLexema()));
            }
        }
    }

    private void prepararObjetos() {
        for (RegistroTS obj : objetos) {
            List<RegistroTS> attrs = atributosDeClase.get(obj.getTipoToken());
            if (attrs == null) {
                attrs = ts.obtenerTodosLosAtributosHeredados(obj.getTipoToken());
            }
            int maxSlot = -1;
            for (RegistroTS attr : attrs) {
                Integer slot = slotsAtributos.get(attr);
                if (slot != null && slot > maxSlot) maxSlot = slot;
            }
            int bytes = Math.max(8, (maxSlot + 1) * 8);
            baseDeObjeto.put(obj.getLexema(), siguienteObjeto);
            siguienteObjeto += bytes;
        }
    }

    private String nombreUnico(String prefijo, String clave) {
        String base = sanitizar(clave);
        String nombre = "$" + prefijo + base;
        int n = 1;
        while (nombresUsados.contains(nombre)) {
            nombre = "$" + prefijo + base + "_" + n;
            n++;
        }
        nombresUsados.add(nombre);
        return nombre;
    }

    private String sanitizar(String texto) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_') {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        if (sb.length() == 0) sb.append("v");
        if (Character.isDigit(sb.charAt(0))) sb.insert(0, 'v');
        return sb.toString();
    }

    // =========================================================================
    // FUNCIONES Y CONTEXTO DE GENERACIÓN
    // =========================================================================

    private static class Ctx {
        final StringBuilder code = new StringBuilder();
        final String selfName;
        final String claseActual;
        final Map<String, String> locales = new LinkedHashMap<>();
        int tempCount = 0;
        int labelCount = 0;

        Ctx(String selfName, String claseActual) {
            this.selfName = selfName;
            this.claseActual = claseActual;
        }

        String temp(String tipo) {
            String nombre = "$tmp" + (tempCount++);
            locales.put(nombre, tipo);
            return nombre;
        }

        String label(String prefijo) {
            return "$" + prefijo + (labelCount++);
        }
    }

    private String generarFuncion(RegistroTS reg, NodoArbol cuerpo) {
        String tipoRet = tipoBaseDe(reg.getTipoToken());
        String claseMetodo = "metodo".equalsIgnoreCase(reg.getUso()) ? claseDeMetodo(reg) : null;
        String self = "metodo".equalsIgnoreCase(reg.getUso()) ? "$self" : null;
        Ctx ctx = new Ctx(self, claseMetodo);

        StringBuilder firmas = new StringBuilder();
        if (self != null) {
            firmas.append(" (param $self i32)");
        }

        Set<String> clavesParametros = new LinkedHashSet<>();
        String ambitoFuncion = reg.getAmbito() + ":" + reg.getNombreSimple();
        for (ParametroFormal p : reg.getParametrosFormales()) {
            String clave = p.getNombre() + ":" + ambitoFuncion;
            clavesParametros.add(clave);
            RegistroTS regParam = ts.buscar(clave);
            String nombre = regParam != null ? nombreLocal.get(regParam.getLexema()) : null;
            if (nombre == null) {
                nombre = nombreUnico("p_", p.getNombre());
                nombreLocal.put(clave, nombre);
            }
            String tipo = tipoWatDeTipo(p.getTipo());
            firmas.append(" (param ").append(nombre).append(" ").append(tipo).append(")");
        }

        // Locales de la función (excluye parámetros y funciones anidadas).
        List<RegistroTS> locales = new ArrayList<>();
        for (RegistroTS r : todasLasEntradas) {
            if (r.getAmbito().equals(ambitoFuncion)
                    && ("variable".equalsIgnoreCase(r.getUso()) || "parametro".equalsIgnoreCase(r.getUso()))) {
                if (!clavesParametros.contains(r.getLexema())) {
                    locales.add(r);
                }
            }
        }
        locales.sort((a, b) -> a.getLexema().compareTo(b.getLexema()));
        for (RegistroTS r : locales) {
            String nombre = nombreLocal.get(r.getLexema());
            if (nombre == null) {
                nombre = nombreUnico("l_", r.getLexema());
                nombreLocal.put(r.getLexema(), nombre);
            }
            ctx.locales.put(nombre, tipoWatDe(r));
        }

        String codigoCuerpo = compilarSecuencia(cuerpo != null ? cuerpo.getDer() : null, ctx);
        ctx.code.append(codigoCuerpo);
        String cero = tipoRet.equals("DOUBLEF") ? "f64.const 0.0" : "i32.const 0";
        ctx.code.append("  ").append(cero).append("\n  return\n");

        String nombre = nombreFuncion.get(reg.getLexema());
        String resultado = tipoRet.equals("DOUBLEF") ? "f64" : "i32";
        return "  (func " + nombre + firmas + " (result " + resultado + ")\n"
                + declararLocales(ctx) + ctx.code + "  )\n";
    }

    private String declararLocales(Ctx ctx) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : ctx.locales.entrySet()) {
            sb.append("    (local ").append(e.getKey()).append(" ").append(e.getValue()).append(")\n");
        }
        return sb.toString();
    }

    private String claseDeMetodo(RegistroTS reg) {
        String ambito = reg.getAmbito();
        int idx = ambito.lastIndexOf(':');
        return idx >= 0 ? ambito.substring(idx + 1) : ambito;
    }

    // =========================================================================
    // SENTENCIAS
    // =========================================================================

    private String compilarSecuencia(NodoArbol nodo, Ctx ctx) {
        if (nodo == null) return "";
        if (";".equals(nodo.getEtiqueta())) {
            return compilarSecuencia(nodo.getIzq(), ctx) + compilarSecuencia(nodo.getDer(), ctx);
        }
        String etiqueta = nodo.getEtiqueta();
        if ("FUNC".equals(etiqueta) || "VAR_DECL".equals(etiqueta)
                || "CLASS".equals(etiqueta) || "TYPEDEF".equals(etiqueta)) {
            return "";
        }
        return compilarSentencia(nodo, ctx);
    }

    private String compilarSentencia(NodoArbol nodo, Ctx ctx) {
        if (nodo == null) return "";
        switch (nodo.getEtiqueta()) {
            case ":=":
                return compilarAsignacion(nodo, ctx, false);
            case "POUT":
                return compilarPout(nodo, ctx);
            case "WHILE":
                return compilarWhile(nodo, ctx);
            case "IF":
                return compilarIf(nodo, ctx);
            case "RET":
                return compilarRetorno(nodo, ctx);
            case "CALL":
                return compilarLlamada(nodo, ctx) + "\n  drop\n";
            default:
                return "";
        }
    }

    private String compilarPout(NodoArbol nodo, Ctx ctx) {
        NodoArbol expr = nodo.getIzq();
        if (expr != null && expr.getRegTS() != null && "CADENA".equalsIgnoreCase(expr.getRegTS().getTipoToken())) {
            String texto = expr.getRegTS().getLexema();
            if (texto != null && texto.length() >= 2 && texto.startsWith("{") && texto.endsWith("}")) {
                texto = texto.substring(1, texto.length() - 1);
            }
            int offset = agregarDato(texto == null ? "" : texto);
            return "  i32.const " + offset + "\n  i32.const " + bytesUtf8(texto == null ? "" : texto)
                    + "\n  call $logs\n";
        }
        String tipo = tipoBaseDe(tipoDeNodo(expr));
        String codigo = compilarExpresion(expr, ctx);
        if (tipo.equals("DOUBLEF")) {
            return codigo + "  call $logf\n";
        }
        return codigo + "  call $log\n";
    }

    private String compilarIf(NodoArbol nodo, Ctx ctx) {
        NodoArbol cond = nodo.getIzq() != null ? nodo.getIzq().getIzq() : null;
        NodoArbol cuerpo = nodo.getDer();
        NodoArbol thenNode = cuerpo != null ? cuerpo.getIzq() : null;
        NodoArbol elseNode = cuerpo != null ? cuerpo.getDer() : null;
        String condCode = compilarExpresion(cond, ctx);
        String thenCode = compilarSecuencia(thenNode, ctx);
        String elseCode = compilarSecuencia(elseNode, ctx);
        String bloqueElse = elseCode.isEmpty() ? "" : " else\n" + indentar(elseCode);
        return condCode
                + "  if\n" + indentar(thenCode)
                + bloqueElse
                + "  end\n";
    }

    private String compilarWhile(NodoArbol nodo, Ctx ctx) {
        NodoArbol cond = nodo.getIzq() != null ? nodo.getIzq().getIzq() : null;
        NodoArbol cuerpo = nodo.getDer() != null ? nodo.getDer().getIzq() : null;
        String salida = ctx.label("salida");
        String ciclo = ctx.label("ciclo");
        String condCode = compilarExpresion(cond, ctx);
        String cuerpoCode = compilarSecuencia(cuerpo, ctx);
        return "  block " + salida + "\n"
                + "    loop " + ciclo + "\n"
                + indentar(condCode)
                + "      i32.eqz\n"
                + "      br_if " + salida + "\n"
                + indentar(cuerpoCode)
                + "      br " + ciclo + "\n"
                + "    end\n"
                + "  end\n";
    }

    private String compilarRetorno(NodoArbol nodo, Ctx ctx) {
        NodoArbol expr = nodo.getIzq();
        String tipoFuncion = tipoBaseDe(nodo.getTipoToken());
        String codigo = compilarExpresion(expr, ctx);
        String tipoExpr = tipoBaseDe(tipoDeNodo(expr));
        codigo += convertir(tipoExpr, tipoFuncion);
        return codigo + "  return\n";
    }

    // =========================================================================
    // ASIGNACIONES
    // =========================================================================

    private String compilarAsignacion(NodoArbol nodo, Ctx ctx, boolean dejarValor) {
        NodoArbol izq = nodo.getIzq();
        NodoArbol der = nodo.getDer();

        if (nodo.getInfo() instanceof Object[]) {
            Object[] info = (Object[]) nodo.getInfo();
            String clase = (String) info[0];
            RegistroTS attr = (RegistroTS) info[1];
            RegistroTS regObjeto = info.length > 2 ? (RegistroTS) info[2] : null;
            if (attr != null && regObjeto != null) {
                return compilarGuardarAtributoEstatico(regObjeto, attr, der, nodo, ctx, dejarValor);
            }
            if (izq != null && "[]".equals(izq.getEtiqueta())) {
                return compilarGuardarAtributoDinamico(izq, der, nodo, ctx, dejarValor);
            }
            if (clase != null) {
                return compilarGuardarAtributoDinamico(izq, der, nodo, ctx, dejarValor);
            }
        }

        if (izq != null && "[]".equals(izq.getEtiqueta())) {
            return compilarGuardarAtributoDinamico(izq, der, nodo, ctx, dejarValor);
        }

        if (izq == null || izq.getRegTS() == null) return "";
        RegistroTS destino = izq.getRegTS();
        if ("atributo".equalsIgnoreCase(destino.getUso()) && ctx.selfName != null) {
            return compilarGuardarAtributoPropio(destino, der, nodo, ctx, dejarValor);
        }
        return compilarGuardarVariable(destino, der, nodo, ctx, dejarValor);
    }

    private String compilarGuardarVariable(RegistroTS destino, NodoArbol der, NodoArbol asignacion, Ctx ctx, boolean dejarValor) {
        String tipoDestino = tipoBaseDe(destino.getTipoToken());
        String tipoExpr = tipoBaseDe(tipoDeNodo(der));
        String valor = compilarExpresion(der, ctx) + convertir(tipoExpr, tipoDestino);
        String tmp = ctx.temp(tipoWatDeTipo(tipoDestino));
        StringBuilder sb = new StringBuilder();
        sb.append(valor);
        sb.append("  local.set ").append(tmp).append("\n");
        RegistroTS enumTipo = enumDeTipo(destino.getTipoToken());
        if (enumTipo != null && !esConstante(der)) {
            sb.append(verificarEnum(tmp, tipoWatDeTipo(tipoDestino), enumTipo));
        }
        String nombre = nombreVariable(destino);
        if (esGlobal(destino)) {
            sb.append("  local.get ").append(tmp).append("\n");
            sb.append("  global.set ").append(nombre).append("\n");
        } else {
            sb.append("  local.get ").append(tmp).append("\n");
            sb.append("  local.set ").append(nombre).append("\n");
        }
        if (dejarValor) {
            sb.append("  local.get ").append(tmp).append("\n");
        }
        return sb.toString();
    }

    private String compilarGuardarAtributoPropio(RegistroTS attr, NodoArbol der, NodoArbol asignacion, Ctx ctx, boolean dejarValor) {
        String tipoAttr = tipoBaseDe(attr.getTipoToken());
        String tipoExpr = tipoBaseDe(tipoDeNodo(der));
        String valor = compilarExpresion(der, ctx) + convertir(tipoExpr, tipoAttr);
        String tmp = ctx.temp(tipoWatDeTipo(tipoAttr));
        StringBuilder sb = new StringBuilder();
        sb.append(valor).append("  local.set ").append(tmp).append("\n");
        RegistroTS enumTipo = enumDeTipo(attr.getTipoToken());
        if (enumTipo != null && !esConstante(der)) {
            sb.append(verificarEnum(tmp, tipoWatDeTipo(tipoAttr), enumTipo));
        }
        Integer slot = slotsAtributos.get(attr);
        int off = (slot != null ? slot : 0) * 8;
        sb.append("  local.get $self\n");
        sb.append("  i32.const ").append(off).append("\n  i32.add\n");
        sb.append("  local.get ").append(tmp).append("\n");
        sb.append(tipoAttr.equals("DOUBLEF") ? "  f64.store\n" : "  i32.store\n");
        if (dejarValor) sb.append("  local.get ").append(tmp).append("\n");
        return sb.toString();
    }

    private String compilarGuardarAtributoEstatico(RegistroTS regObjeto, RegistroTS attr, NodoArbol der,
            NodoArbol asignacion, Ctx ctx, boolean dejarValor) {
        String tipoAttr = tipoBaseDe(attr.getTipoToken());
        String tipoExpr = tipoBaseDe(tipoDeNodo(der));
        String valor = compilarExpresion(der, ctx) + convertir(tipoExpr, tipoAttr);
        String tmp = ctx.temp(tipoWatDeTipo(tipoAttr));
        StringBuilder sb = new StringBuilder();
        sb.append(valor).append("  local.set ").append(tmp).append("\n");
        RegistroTS enumTipo = enumDeTipo(attr.getTipoToken());
        if (enumTipo != null && !esConstante(der)) {
            sb.append(verificarEnum(tmp, tipoWatDeTipo(tipoAttr), enumTipo));
        }
        Integer slot = slotsAtributos.get(attr);
        int off = (slot != null ? slot : 0) * 8;
        sb.append(compilarBaseObjeto(regObjeto, ctx));
        sb.append("  i32.const ").append(off).append("\n  i32.add\n");
        sb.append("  local.get ").append(tmp).append("\n");
        sb.append(tipoAttr.equals("DOUBLEF") ? "  f64.store\n" : "  i32.store\n");
        if (dejarValor) sb.append("  local.get ").append(tmp).append("\n");
        return sb.toString();
    }

    private String compilarGuardarAtributoDinamico(NodoArbol acceso, NodoArbol der, NodoArbol asignacion,
            Ctx ctx, boolean dejarValor) {
        NodoArbol objNode = acceso != null ? acceso.getIzq() : null;
        NodoArbol indiceNode = acceso != null ? acceso.getDer() : null;
        RegistroTS regObjeto = objNode != null ? objNode.getRegTS() : null;
        String clase = (regObjeto != null) ? regObjeto.getTipoToken() : null;
        List<RegistroTS> attrs = atributosDeClase.get(clase);
        if (attrs == null && clase != null) attrs = ts.obtenerTodosLosAtributosHeredados(clase);
        if (attrs == null) attrs = new ArrayList<>();

        String tipoExpr = tipoBaseDe(tipoDeNodo(der));
        String valor = compilarExpresion(der, ctx);
        String tmpValor = ctx.temp(tipoWatDeTipo(tipoExpr));
        String tmpIndice = ctx.temp("i32");
        StringBuilder sb = new StringBuilder();
        sb.append(valor).append("  local.set ").append(tmpValor).append("\n");
        sb.append(compilarExpresion(indiceNode, ctx)).append("  local.set ").append(tmpIndice).append("\n");

        String cuerpo = "  " + abortar(5);
        for (int i = 0; i < attrs.size(); i++) {
            RegistroTS attr = attrs.get(i);
            String tipoAttr = tipoBaseDe(attr.getTipoToken());
            Integer slot = slotsAtributos.get(attr);
            int off = (slot != null ? slot : 0) * 8;
            String rama = "  local.get " + tmpIndice + "\n  i32.const " + i + "\n  i32.eq\n"
                    + "  if\n"
                    + "    " + compilarBaseObjeto(regObjeto, ctx).replace("\n", "\n    ")
                    + "    i32.const " + off + "\n    i32.add\n"
                    + "    local.get " + tmpValor + "\n"
                    + convertir(tipoExpr, tipoAttr).replace("\n", "\n    ")
                    + "    " + (tipoAttr.equals("DOUBLEF") ? "f64.store" : "i32.store") + "\n"
                    + "  else\n" + indentar(cuerpo)
                    + "  end\n";
            cuerpo = rama;
        }
        sb.append(cuerpo);
        if (dejarValor) sb.append("  local.get ").append(tmpValor).append("\n");
        return sb.toString();
    }

    // =========================================================================
    // EXPRESIONES
    // =========================================================================

    private String compilarExpresion(NodoArbol nodo, Ctx ctx) {
        if (nodo == null) return "  i32.const 0\n";
        String etiqueta = nodo.getEtiqueta();
        if (nodo.getRegTS() != null) {
            return compilarHoja(nodo, ctx);
        }

        switch (etiqueta) {
            case "ItoD": {
                String code = compilarExpresion(nodo.getIzq(), ctx);
                return code + "  f64.convert_i32_u\n";
            }
            case "UMINUS": {
                String tipo = tipoBaseDe(tipoDeNodo(nodo));
                String code = compilarExpresion(nodo.getIzq(), ctx);
                if (tipo.equals("DOUBLEF")) return code + "  f64.neg\n";
                return "  i32.const 0\n" + code + "  i32.sub\n";
            }
            case ":=":
                return compilarAsignacion(nodo, ctx, true);
            case "[]":
                return compilarLeerAtributo(nodo, ctx);
            case "CALL":
                return compilarLlamada(nodo, ctx);
            case "+":
            case "-":
            case "*":
            case "/":
                return compilarOperacion(nodo, ctx);
            case "==":
            case "!=":
            case ">":
            case "<":
            case ">=":
            case "<=":
                return compilarComparacion(nodo, ctx);
            default:
                return "  i32.const 0\n";
        }
    }

    private String compilarHoja(NodoArbol nodo, Ctx ctx) {
        RegistroTS reg = nodo.getRegTS();
        if (reg.getIdToken() == Parser.CTE_USHORT) {
            return "  i32.const " + (long) reg.getValorNum() + "\n";
        }
        if (reg.getIdToken() == Parser.CTE_DOUBLEF) {
            return "  f64.const " + formatearDouble(reg.getValorNum()) + "\n";
        }
        if ("CADENA".equalsIgnoreCase(reg.getTipoToken())) {
            int off = agregarDato(reg.getLexema());
            return "  i32.const " + off + "\n";
        }
        if ("objeto".equalsIgnoreCase(reg.getUso())) {
            Integer base = baseDeObjeto.get(reg.getLexema());
            return "  i32.const " + (base != null ? base : 0) + "\n";
        }
        if ("atributo".equalsIgnoreCase(reg.getUso()) && ctx.selfName != null) {
            Integer slot = slotsAtributos.get(reg);
            int off = (slot != null ? slot : 0) * 8;
            String tipo = tipoBaseDe(reg.getTipoToken());
            return "  local.get $self\n  i32.const " + off + "\n  i32.add\n"
                    + (tipo.equals("DOUBLEF") ? "  f64.load\n" : "  i32.load\n");
        }
        String nombre = nombreVariable(reg);
        return (esGlobal(reg) ? "  global.get " : "  local.get ") + nombre + "\n";
    }

    private String compilarOperacion(NodoArbol nodo, Ctx ctx) {
        String tipo = tipoBaseDe(tipoDeNodo(nodo));
        String izq = compilarExpresion(nodo.getIzq(), ctx);
        String der = compilarExpresion(nodo.getDer(), ctx);
        String op = nodo.getEtiqueta();

        if (tipo.equals("DOUBLEF")) {
            if (op.equals("+")) {
                String tmp = ctx.temp("f64");
                return izq + der + "  f64.add\n  local.set " + tmp + "\n"
                        + "  local.get " + tmp + "\n  f64.abs\n  f64.const " + formatearDouble(MAX_DOUBLEF)
                        + "\n  f64.gt\n  if\n    " + abortar(3) + "  end\n"
                        + "  local.get " + tmp + "\n";
            }
            if (op.equals("/")) {
                String a = ctx.temp("f64");
                String b = ctx.temp("f64");
                return izq + "  local.set " + a + "\n"
                        + der + "  local.tee " + b + "\n  f64.const 0\n  f64.eq\n"
                        + "  if\n    " + abortar(1) + "  end\n"
                        + "  local.get " + a + "\n  local.get " + b + "\n  f64.div\n";
            }
            return izq + der + "  f64." + (op.equals("*") ? "mul" : "sub") + "\n";
        }

        if (op.equals("+")) {
            String tmp = ctx.temp("i32");
            return izq + der + "  i32.add\n  local.set " + tmp + "\n"
                    + "  local.get " + tmp + "\n  i32.const 255\n  i32.gt_u\n"
                    + "  if\n    " + abortar(2) + "  end\n"
                    + "  local.get " + tmp + "\n";
        }
        if (op.equals("/")) {
            String a = ctx.temp("i32");
            String b = ctx.temp("i32");
            return izq + "  local.set " + a + "\n"
                    + der + "  local.tee " + b + "\n  i32.eqz\n"
                    + "  if\n    " + abortar(1) + "  end\n"
                    + "  local.get " + a + "\n  local.get " + b + "\n  i32.div_u\n";
        }
        return izq + der + "  i32." + (op.equals("*") ? "mul" : "sub") + "\n";
    }

    private String compilarComparacion(NodoArbol nodo, Ctx ctx) {
        String tipo = tipoBaseDe(tipoDeNodo(nodo.getIzq()));
        String izq = compilarExpresion(nodo.getIzq(), ctx);
        String der = compilarExpresion(nodo.getDer(), ctx);
        String op = nodo.getEtiqueta();
        if (tipo.equals("DOUBLEF")) {
            switch (op) {
                case "==": return izq + der + "  f64.eq\n";
                case "!=": return izq + der + "  f64.ne\n";
                case ">": return izq + der + "  f64.gt\n";
                case "<": return izq + der + "  f64.lt\n";
                case ">=": return izq + der + "  f64.ge\n";
                case "<=": return izq + der + "  f64.le\n";
                default: return izq + der + "  f64.eq\n";
            }
        }
        switch (op) {
            case "==": return izq + der + "  i32.eq\n";
            case "!=": return izq + der + "  i32.ne\n";
            case ">": return izq + der + "  i32.gt_u\n";
            case "<": return izq + der + "  i32.lt_u\n";
            case ">=": return izq + der + "  i32.ge_u\n";
            case "<=": return izq + der + "  i32.le_u\n";
            default: return izq + der + "  i32.eq\n";
        }
    }

    private String compilarLeerAtributo(NodoArbol nodo, Ctx ctx) {
        NodoArbol objNode = nodo.getIzq();
        NodoArbol indiceNode = nodo.getDer();
        RegistroTS regObjeto = objNode != null ? objNode.getRegTS() : null;
        if (regObjeto == null) return "  i32.const 0\n";

        Object[] info = (nodo.getInfo() instanceof Object[]) ? (Object[]) nodo.getInfo() : null;
        if (info != null && info.length > 1 && info[1] instanceof RegistroTS) {
            RegistroTS attr = (RegistroTS) info[1];
            String tipo = tipoBaseDe(attr.getTipoToken());
            Integer slot = slotsAtributos.get(attr);
            int off = (slot != null ? slot : 0) * 8;
            return compilarBaseObjeto(regObjeto, ctx)
                    + "  i32.const " + off + "\n  i32.add\n"
                    + (tipo.equals("DOUBLEF") ? "  f64.load\n" : "  i32.load\n");
        }

        String clase = (regObjeto.getTipoToken());
        List<RegistroTS> attrs = atributosDeClase.get(clase);
        if (attrs == null) attrs = ts.obtenerTodosLosAtributosHeredados(clase);
        if (attrs == null) attrs = new ArrayList<>();
        String targetTipo = tipoBaseDe(nodo.getTipoToken());
        String targetWat = tipoWatDeTipo(targetTipo);
        String tmpIndice = ctx.temp("i32");
        StringBuilder sb = new StringBuilder();
        sb.append(compilarExpresion(indiceNode, ctx)).append("  local.set ").append(tmpIndice).append("\n");
        String expresion = "  " + abortar(5);
        for (int i = attrs.size() - 1; i >= 0; i--) {
            RegistroTS attr = attrs.get(i);
            String tipoAttr = tipoBaseDe(attr.getTipoToken());
            Integer slot = slotsAtributos.get(attr);
            int off = (slot != null ? slot : 0) * 8;
            String rama = "  local.get " + tmpIndice + "\n  i32.const " + i + "\n  i32.eq\n"
                    + "  if (result " + targetWat + ")\n"
                    + "    " + compilarBaseObjeto(regObjeto, ctx).replace("\n", "\n    ")
                    + "    i32.const " + off + "\n    i32.add\n"
                    + "    " + (tipoAttr.equals("DOUBLEF") ? "f64.load" : "i32.load") + "\n"
                    + convertir(tipoAttr, targetTipo).replace("\n", "\n    ")
                    + "  else\n" + indentar(expresion)
                    + "  end\n";
            expresion = rama;
        }
        sb.append(expresion);
        return sb.toString();
    }

    private String compilarLlamada(NodoArbol nodo, Ctx ctx) {
        if (!(nodo.getInfo() instanceof RegistroTS)) return "  i32.const 0\n";
        RegistroTS regFunc = (RegistroTS) nodo.getInfo();
        List<NodoArbol> args = new ArrayList<>();
        desarmarLista(nodo.getIzq(), args);
        List<Integer> orden = new ArrayList<>();
        desarmarOrden(nodo.getDer(), orden);
        if (orden.isEmpty()) {
            for (int i = 0; i < args.size(); i++) orden.add(i + 1);
        }

        boolean metodo = "metodo".equalsIgnoreCase(regFunc.getUso());
        String selfTmp = null;
        StringBuilder sb = new StringBuilder();
        if (metodo) {
            selfTmp = ctx.temp("i32");
            NodoArbol obj = nodo.getTer();
            String selfCode;
            if (obj != null) {
                selfCode = compilarExpresion(obj, ctx);
            } else if (ctx.selfName != null) {
                selfCode = "  local.get $self\n";
            } else {
                selfCode = "  i32.const 0\n";
            }
            sb.append(selfCode).append("  local.set ").append(selfTmp).append("\n");
        }

        List<String> temps = new ArrayList<>();
        List<ParametroFormal> formales = regFunc.getParametrosFormales();
        for (int k = 0; k < args.size(); k++) {
            temps.add(null);
        }
        for (int pos : orden) {
            int idx = pos - 1;
            if (idx < 0 || idx >= args.size()) continue;
            String tipoFormal = idx < formales.size() ? tipoBaseDe(formales.get(idx).getTipo()) : tipoBaseDe(tipoDeNodo(args.get(idx)));
            String tipoReal = tipoBaseDe(tipoDeNodo(args.get(idx)));
            String code = compilarExpresion(args.get(idx), ctx) + convertir(tipoReal, tipoFormal);
            String tmp = ctx.temp(tipoWatDeTipo(tipoFormal));
            temps.set(idx, tmp);
            sb.append(code).append("  local.set ").append(tmp).append("\n");
        }
        for (int i = 0; i < temps.size(); i++) {
            if (temps.get(i) == null) {
                String tipoFormal = i < formales.size() ? tipoBaseDe(formales.get(i).getTipo()) : "USHORTINT";
                String tmp = ctx.temp(tipoWatDeTipo(tipoFormal));
                temps.set(i, tmp);
                sb.append(tipoFormal.equals("DOUBLEF") ? "  f64.const 0.0\n" : "  i32.const 0\n")
                        .append("  local.set ").append(tmp).append("\n");
            }
        }
        if (metodo) sb.append("  local.get ").append(selfTmp).append("\n");
        for (String tmp : temps) sb.append("  local.get ").append(tmp).append("\n");
        String nombre = nombreFuncion.get(regFunc.getLexema());
        sb.append("  call ").append(nombre).append("\n");
        return sb.toString();
    }

    private void desarmarLista(NodoArbol nodo, List<NodoArbol> lista) {
        if (nodo == null) return;
        if (",".equals(nodo.getEtiqueta())) {
            desarmarLista(nodo.getIzq(), lista);
            desarmarLista(nodo.getDer(), lista);
        } else {
            lista.add(nodo);
        }
    }

    private void desarmarOrden(NodoArbol nodo, List<Integer> lista) {
        if (nodo == null) return;
        if (",".equals(nodo.getEtiqueta())) {
            desarmarOrden(nodo.getIzq(), lista);
            desarmarOrden(nodo.getDer(), lista);
        } else {
            RegistroTS reg = nodo.getRegTS();
            if (reg != null) lista.add((int) reg.getValorNum());
        }
    }

    // =========================================================================
    // UTILIDADES SEMÁNTICAS / WAT
    // =========================================================================

    private String compilarBaseObjeto(RegistroTS regObjeto, Ctx ctx) {
        if (regObjeto == null) return "  i32.const 0\n";
        if ("objeto".equalsIgnoreCase(regObjeto.getUso())) {
            Integer base = baseDeObjeto.get(regObjeto.getLexema());
            return "  i32.const " + (base != null ? base : 0) + "\n";
        }
        return compilarHoja(new NodoArbol(regObjeto), ctx);
    }

    private String nombreVariable(RegistroTS reg) {
        if (esGlobal(reg)) return nombreGlobal.get(reg.getLexema());
        String nombre = nombreLocal.get(reg.getLexema());
        return nombre != null ? nombre : "$l_desconocido";
    }

    private boolean esGlobal(RegistroTS reg) {
        return "main".equals(reg.getAmbito())
                && ("variable".equalsIgnoreCase(reg.getUso()) || "parametro".equalsIgnoreCase(reg.getUso()));
    }

    private String tipoDeNodo(NodoArbol nodo) {
        if (nodo == null) return "USHORTINT";
        if (nodo.getTipoToken() != null) return nodo.getTipoToken();
        if (nodo.getRegTS() != null) return nodo.getRegTS().getTipoToken();
        return "USHORTINT";
    }

    private String tipoBaseDe(String tipo) {
        if (tipo == null) return "USHORTINT";
        if (tipo.equalsIgnoreCase("CTE_DOUBLEF")) return "DOUBLEF";
        if (tipo.equalsIgnoreCase("CTE_USHORT")) return "USHORTINT";
        if (tipo.equalsIgnoreCase("DOUBLEF")) return "DOUBLEF";
        if (tipo.equalsIgnoreCase("USHORTINT")) return "USHORTINT";
        RegistroTS reg = ts.buscar(tipo + ":main");
        if (reg == null) reg = ts.buscar(tipo);
        if (reg != null && reg.esTipoEnum()) {
            return reg.getTipoBaseEnum();
        }
        return tipo;
    }

    private String tipoWatDe(RegistroTS reg) {
        if (reg == null) return "i32";
        if ("objeto".equalsIgnoreCase(reg.getUso())) return "i32";
        return tipoWatDeTipo(reg.getTipoToken());
    }

    private String tipoWatDeTipo(String tipo) {
        return tipoBaseDe(tipo).equals("DOUBLEF") ? "f64" : "i32";
    }

    private RegistroTS enumDeTipo(String tipo) {
        if (tipo == null) return null;
        RegistroTS reg = ts.buscar(tipo + ":main");
        if (reg == null) reg = ts.buscar(tipo);
        return (reg != null && reg.esTipoEnum()) ? reg : null;
    }

    private boolean esConstante(NodoArbol nodo) {
        if (nodo == null) return false;
        if (nodo.getRegTS() != null) {
            int id = nodo.getRegTS().getIdToken();
            return id == Parser.CTE_USHORT || id == Parser.CTE_DOUBLEF;
        }
        return "UMINUS".equals(nodo.getEtiqueta()) && esConstante(nodo.getIzq());
    }

    private String convertir(String desde, String hasta) {
        String a = tipoBaseDe(desde);
        String b = tipoBaseDe(hasta);
        if (a.equals(b)) return "";
        if (b.equals("DOUBLEF") && a.equals("USHORTINT")) {
            return "  f64.convert_i32_u\n";
        }
        if (b.equals("USHORTINT") && a.equals("DOUBLEF")) {
            return "  i32.trunc_f64_u\n";
        }
        return "";
    }

    private String verificarEnum(String tmp, String tipoWat, RegistroTS enumTipo) {
        List<Double> valores = enumTipo.getValoresEnum();
        if (valores == null || valores.isEmpty()) {
            return "  " + abortar(5);
        }
        StringBuilder cond = new StringBuilder();
        for (int i = 0; i < valores.size(); i++) {
            cond.append("  local.get ").append(tmp).append("\n");
            if (tipoWat.equals("f64")) {
                cond.append("  f64.const ").append(formatearDouble(valores.get(i))).append("\n  f64.eq\n");
            } else {
                cond.append("  i32.const ").append((long) (double) valores.get(i)).append("\n  i32.eq\n");
            }
            if (i > 0) cond.append("  i32.or\n");
        }
        return cond.toString() + "  i32.eqz\n  if\n    " + abortar(5) + "  end\n";
    }

    private String abortar(int codigo) {
        return "i32.const " + codigo + "\n  call $abort\n  unreachable\n";
    }

    private int agregarDato(String texto) {
        Integer existente = datos.get(texto);
        if (existente != null) return existente;
        int offset = alinear8(siguienteDato);
        datos.put(texto, offset);
        siguienteDato = offset + bytesUtf8(texto);
        return offset;
    }

    private int alinear8(int valor) {
        return (valor + 7) & ~7;
    }

    private int bytesUtf8(String texto) {
        try {
            return texto.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        } catch (Exception e) {
            return texto.length();
        }
    }

    private String formatearDouble(double valor) {
        if (Double.isNaN(valor)) return "nan";
        if (Double.isInfinite(valor)) return valor > 0 ? "inf" : "-inf";
        String s = Double.toString(valor).replace('E', 'e');
        if (!s.contains(".") && !s.contains("e")) s += ".0";
        return s;
    }

    private String escaparWat(String texto) {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = texto.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        for (byte b : bytes) {
            int v = b & 0xff;
            if (v == '\\') sb.append("\\\\");
            else if (v == '"') sb.append("\\\"");
            else if (v == '\n') sb.append("\\n");
            else if (v == '\t') sb.append("\\t");
            else if (v >= 32 && v <= 126) sb.append((char) v);
            else sb.append(String.format(Locale.ROOT, "\\%02x", v));
        }
        return sb.toString();
    }

    private String indentar(String codigo) {
        if (codigo == null || codigo.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String linea : codigo.split("\n", -1)) {
            if (!linea.isEmpty()) sb.append("    ").append(linea).append("\n");
        }
        return sb.toString();
    }
}
