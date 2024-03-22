package compi;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author aguir
 */
public class Token {

    private final int token, linea;
    private final String lexema;

    public Token(int token, String lexema, int linea) {
        this.token = token;
        this.lexema = lexema;
        this.linea = linea;
    }

    public int getToken() {
        return token;
    }

    public String getLexema() {
        return lexema;
    }    
    
    public int getLinea() {
        return linea;
    }
    
}
