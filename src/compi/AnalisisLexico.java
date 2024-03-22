package compi;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author aguir
 */
public class AnalisisLexico {

    private final Set<String> PALABRAS_RESERVADAS = new HashSet<String>() {{
        add("if"); add("else"); add("switch"); add("for"); add("do"); add("while");
        add("console.log"); add("forEach"); add("break"); add("continue"); add("let");
        add("const"); add("undefined"); add("typeof"); add("Number"); add("String");
        add("any"); add("interface"); add("set"); add("get"); add("class"); add("toLowerCase");
        add("toUpperCase"); add("length"); add("trim"); add("charAt"); add("startsWith");
        add("endsWith"); add("indexOf"); add("Includes"); add("slice"); add("replace");
        add("split"); add("push"); add("shift"); add("in"); add("of"); add("splice");
        add("concat"); add("find"); add("findIndex"); add("filter"); add("map");
        add("sort"); add("reverse"); add("true"); add("false"); add("null");
    }};
    
    private final String text;
    private final int[][] matrizLexico;
    private JTable tblContadores, tblTokens, tblErrores;
    
    private LinkedList<Token> listaTokens;
    private LinkedList<Error> listaErrores;
    
    public AnalisisLexico(String text, int[][] matrizLexico, JTable tblContadores, JTable tblTokens, JTable tblErrores) {
        this.matrizLexico = matrizLexico;
        this.text = text;
        this.tblContadores = tblContadores;
        this.tblTokens = tblTokens;
        this.tblErrores = tblErrores;
        
        listaTokens = new LinkedList<>();
        listaErrores = new LinkedList<>();
    }
    
    public void analizarLexico () {
        int estado = 0, col, linea = 1, currColumn = 1;
        String lexema = "";
        boolean isConsoleLogEsperado = false;
        
        for (int i = 0; i < text.length(); i++) {
            char caracter = text.charAt(i);
            col = calcularColumna(caracter);
            estado = matrizLexico[estado][col];
            
            if (estado < 0) {
                Token token;
                String lastLexema = (!listaTokens.isEmpty()) ? listaTokens.getLast().getLexema() : null;
                
                /* console.log condition */
                if (isConsoleLogEsperado) {
                    isConsoleLogEsperado = false;
                    if (lexema.equals("log")) {
                        listaTokens.removeLast();
                        listaTokens.removeLast();

                        lexema = "console.log";

                        int IDENTIFICADOR_COL = 1, CONTROL_COL = 12;
                        int val = (int) tblContadores.getValueAt(0, IDENTIFICADOR_COL);
                        tblContadores.setValueAt(val - 1, 0, IDENTIFICADOR_COL);
                        val = (int) tblContadores.getValueAt(0, CONTROL_COL);
                        tblContadores.setValueAt(val - 1, 0, CONTROL_COL);
                    }
                }
                if (lastLexema != null && lastLexema.equals("console") && estado == -16) isConsoleLogEsperado = true;
                /* console.log condition */
                
                if (estado != -24 && estado != -61) { // A prueba de comentarios ñyejeje
                    token = new Token(estado, lexema, linea);
                    listaTokens.add(token);
                }
                
                int colClasif = clasificarToken(estado, lexema);
                int val = (int) tblContadores.getValueAt(0, colClasif);
                tblContadores.setValueAt(val + 1, 0, colClasif);
                
                estado = 0;
                lexema = "";
                
                i--;
            } else if (estado >= 500 && estado <= 507) {
                lexema += caracter;
                
                String descripcion = clasificarError(estado);
                Error error = new Error(estado, descripcion, lexema, "Léxico", linea, currColumn);
                listaErrores.add(error);
                
                currColumn++;
                
                int colClasif = clasificarToken(estado, lexema);
                int val = (int) tblContadores.getValueAt(0, colClasif);
                tblContadores.setValueAt(val + 1, 0, colClasif);
                
                estado = 0;
                lexema = "";
            } else {
                currColumn++;
                
                if (caracter == '\n') { 
                    linea++;
                    currColumn = 1;
                } else if (estado == 25 || estado == 26 || (caracter != '\t' && caracter != '\s')) {
                    lexema += caracter;
                }
            }
        }

        fillTables();
    }
    
    private int calcularColumna (char caracter) {
        if (Character.isDigit(caracter)) return 29;
        if (Character.isLowerCase(caracter)) return 30;
        if (Character.isUpperCase(caracter)) return 31;
        return switch (caracter) {
            case '+' -> 0;
            case '-' -> 1;
            case '~' -> 2;
            case '|' -> 3;
            case '&' -> 4;
            case '^' -> 5;
            case ',' -> 6;
            case '.' -> 7;
            case ';' -> 8;
            case ':' -> 9;
            case '*' -> 10;
            case '/' -> 11;
            case '%' -> 12;
            case '<' -> 13;
            case '>' -> 14;
            case '=' -> 15;
            case '!' -> 16;
            case '?' -> 17;
            case '{' -> 18;
            case '}' -> 19;
            case '[' -> 20;
            case ']' -> 21;
            case '(' -> 22;
            case ')' -> 23;
            case '\n' -> 24;
            case '\s' -> 25;
            case '\t' -> 26;
            case '"' -> 27;
            case '\'' -> 28;
            case '@' -> 32;
            case '_' -> 33;
            default -> 34;
        };
    }
    
    private int clasificarToken (int estado, String lexema) {
        int col = -1;
        
        // ERRORES
        if (estado >= 500 && estado <= 507) col = 0;
        // IDENTIFICADORES
        else if (estado == -59) col = 1;
        // COMENTARIOS
        else if (estado == -24 || estado == -61) col = 2;
        // CADENA
        else if (estado == -52 || estado == -53) col = 4;
        // NUMERICO
        else if (estado == -54) col = 5;
        // REAL
        else if (estado == -55) col = 6;
        // EXPONENCIAL
        else if (estado == -56 || estado == -57 || estado == -58) col = 7;
        // POSTFIX
        else if (estado == -2 || estado == -5) col = 10;
        // LOG BIN
        else if (estado == -7 || estado == -8 || estado == -10 || estado == -13) col = 11;
        // CONTROL
        else if (estado == -15 || estado == -16 || estado == -17 || estado == -18) col = 12;
        // MATEMATICOS
        else if (estado == -1 || estado == -4 || estado == -19 || estado == -22 || estado == -25) col = 13;
        // EXPONENTE
        else if (estado == -20) col = 14;
        // TURNO
        else if (estado == -30 || estado == -34 || estado == -36) col = 15;
        // RELACIONALES
        else if (estado == -27 || estado == -32 || estado == -28 || estado == -33 || estado == -40 || estado == -43 || estado == -29) col = 16;
        // SIN IGUALDAD
        else if (estado == -41 || estado == -44) col = 17;
        // LOGICOS
        else if (estado == -42 || estado == -11 || estado == -9) col = 18;
        // TERNARIO
        else if (estado == -45) col = 19;
        // ASIGNACION
        else if (estado == -38 || estado == -3 || estado == -6 || estado == -21 || estado == -23 || estado == -26 || estado == -12 || estado == -14 || estado == -31 || estado == -35 || estado == -37 || estado == -39) col = 20;
        // AGRUPAMIENTO
        else if (estado == -46 || estado == -47 || estado == -48 || estado == -49 || estado == -50 || estado == -51) col = 21;
        
        else if (estado == -60) {
            if (PALABRAS_RESERVADAS.contains(lexema)) {
                switch (lexema) {
                    case "true", "false" -> col = 8; // BOOL
                    case "null" -> col = 9; // NULL
                    default -> col = 3; // RESERVADAS
                }
            } else {
                col = 1; // IDENTIFICADORES
            }
        }
        
        return col;
    }
    
    private String clasificarError (int estado) {
        return switch (estado) {
            case 500 -> "Se esperaba un inicio de expresión con caracter válido";
            case 501 -> "No se esperaba un salto de línea";
            case 502 -> "No se esperaba un salto de línea";
            case 503 -> "Se esperaba un número después del caracter '.'";
            case 504 -> "Se esperaba un caracter '+' ó '-' después del caracter '^'";
            case 505 -> "Se esperaba un número después del caracter '+'";
            case 506 -> "Se esperaba un número después del caracter '-'";
            case 507 -> "Se esperaba un número después del caracter '^'";
            default -> "";
        };
    }
    
    private void fillTables () {
        DefaultTableModel tblTokensModel = (DefaultTableModel) tblTokens.getModel();
        for (var token : listaTokens) {
            tblTokensModel.addRow(new Object[]{token.getToken(), token.getLexema(), token.getLinea()});
        }
        
        DefaultTableModel tblErroresModel = (DefaultTableModel) tblErrores.getModel();
        for (var error : listaErrores) {
            tblErroresModel.addRow(new Object[]{
                error.getToken(), error.getDescripcion(), error.getLexema(), error.getTipo(), error.getLinea(), error.getColError()
            });
        }
    }
    
}
