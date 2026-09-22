package lexico;

@FunctionalInterface 
public interface AccionSemantica { 
    int ejecutar(char c); 
}