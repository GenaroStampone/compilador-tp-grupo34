public class InfoFuncion {
    private final RegistroTS registro;
    private final String tipoRetorno;
    private final String nombreVisible;
    private final String ambitoPadre;

    public InfoFuncion(RegistroTS registro, String tipoRetorno, String nombreVisible, String ambitoPadre) {
        this.registro = registro;
        this.tipoRetorno = tipoRetorno;
        this.nombreVisible = nombreVisible;
        this.ambitoPadre = ambitoPadre;
    }

    public RegistroTS getRegistro() {
        return registro;
    }

    public String getTipoRetorno() {
        return tipoRetorno;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    public String getAmbitoPadre() {
        return ambitoPadre;
    }
}
