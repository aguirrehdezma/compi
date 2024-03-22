package compi;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author aguir
 */
public class Error {
    
    private final int token, linea, colError;
    private final String lexema, descripcion, tipo;

    public Error(int token, String descripcion, String lexema, String tipo, int linea, int colError) {
        this.token = token;
        this.descripcion = descripcion;
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.colError = colError;
    }

    public int getToken() {
        return token;
    }

    public String getDescripcion() {
        return descripcion;
    }
    
    public String getLexema() {
        return lexema;
    }

    public String getTipo() {
        return tipo;
    }
    
    public int getLinea() {
        return linea;
    }

    public int getColError() {
        return colError;
    }
    
}
