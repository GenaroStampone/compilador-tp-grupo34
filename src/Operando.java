//Interfaz polimórfica para cualquier elemento que actúe como operando en un Terceto.
public interface Operando {

    //Retorna la representación en formato String del operando para la impresión del Terceto.
    String getRepresentacion();

    String getTipoToken();
}