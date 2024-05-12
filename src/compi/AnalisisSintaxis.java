package compi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AnalisisSintaxis {

    private final int[][] matrizSintaxis;
    private LinkedList<Token> listaTokens;
    private LinkedList<Error> listaErrores;
    private JTable tblErrores, tblContadores;
    private int[] cntDiagramas;
            
    private Stack<Integer> pilaSintaxis;
    
    private final HashMap<Integer, Integer> diagramasPos = new HashMap<Integer, Integer>() {{
            put(1, 0); put(17, 1); put(19, 2); put(21, 3); put(23, 4); put(25, 5); put(27, 6); put(29, 7); put(37, 8); put(38, 9); put(40, 10);
            put(41, 11); put(52, 12); put(62, 13); put(65, 14); put(66, 15); put(67, 16); put(73, 17); put(76, 18); put(78, 19); put(79, 20); put(81, 21);
    }};
    
    private final int[][] prods = {
        {-51, 2, 17, -50, -110}, // STATU → Console.read ( OR A0 )
        {-51, 17, -50, -68}, // STATU → Console.log ( OR )
        {4, 3, 1, -51, 17, -50, -62}, // STATU → if ( OR ) STATU A1 A2
        {-47, 5, 1, -46}, // STATU → { STATU A4 }
        {1, -51, 17, -50, -67}, // STATU → while ( OR ) STATU
        {17}, // STATU → OR
        {-17, 6, -114}, // STATU → return A5 ;
        {-17, -51, 17, -50, -67, 1, -66}, // STATU → do STATU while ( OR ) ;
        {1, -51, 7, -50, -65}, // STATU → for ( A6 ) STATU
        {-47, 16, -46, -51, 17, -50, -64}, // STATU → switch ( OR ) { A15 }
        {2, 17, -15}, // A0  → , OR A0
        {3, 1, -51, 17, -50, -111}, // A1 → elseif ( OR ) STATU A1
        {1, -63}, // A2 → else STATU
        {5, 1, -17}, // A4  → ; STATU A4
        {17}, // A5 → OR
        {62}, // A5 → DEC_RETURN
        {8}, // A6 → A7
        {11, -72}, // A6 → let A10
        {10, 17, -17, 1, -17, 9, 17}, // A7 → OR A8 ; STATU ; OR A9
        {9, 17, -15}, // A8  → , OR A8
        {10, 17, -15}, // A9  → , OR A9
        {8}, // A10 → A7
        {-59, 12, -59, -57}, // A10 → $ id A11 id
        {-97}, // A11 → in
        {-98}, // A11 → of
        {13, 1, -17}, // A12  → ; STATU A12
        {16, -70}, // A13 → break A15
        {16}, // A13 → A15
        {15, 1, -17}, // A14  → ; STATU A14
        {14, 13, 1, -18, 17, -112}, // A15 → case OR : STATU A12 A13
        {15, 1, -18, -113}, // A15 → default : STATU A14
        {18, 25}, // OR → AND B1
        {18, 25, -9}, // B1 → || AND B1
        {18, 25, -8}, // B1 → | AND B1
        {20, 21}, // EXP_PAS → SIMPLEEXPPASCAL C1
        {20, 21, -27}, // C1 → < SIMPLEEXPPASCAL C1
        {20, 21, -28}, // C1 → <= SIMPLEEXPPASCAL C1
        {20, 21, -40}, // C1 → == SIMPLEEXPPASCAL C1
        {20, 21, -43}, // C1 → != SIMPLEEXPPASCAL C1
        {20, 21, -33}, // C1 → >= SIMPLEEXPPASCAL C1
        {20, 21, -32}, // C1 → > SIMPLEEXPPASCAL C1
        {20, 21, -41}, // C1 → === SIMPLEEXPPASCAL C1
        {20, 21, -44}, // C1 → !== SIMPLEEXPPASCAL C1
        {22, 23}, // SIMPLEEXPPASCAL → TERMINOPASCAL D1
        {22, 23, -4}, // D1  → - TERMINOPASCAL D1
        {22, 23, -1}, // D1 → + TERMINOPASCAL D1
        {22, 23, -30}, // D1 → << TERMINOPASCAL D1
        {22, 23, -34}, // D1 → >> TERMINOPASCAL D1
        {22, 23, -36}, // D1 → >>> TERMINOPASCAL D1
        {24, 27}, // TERMINOPASCAL → ELEVACION E1
        {24, 27, -19}, // E1 → * ELEVACION E1
        {24, 27, -22}, // E1 → / ELEVACION E1
        {24, 27, -25}, // E1 → % ELEVACION E1
        {26, 19}, // AND → EXP_PAS F1
        {26, 19, -11}, // F1 → && EXP_PAS F1
        {26, 19, -10}, // F1 → & EXP_PAS F1
        {26, 19, -13}, // F1 → ^ EXP_PAS F1
        {28, 29}, // ELEVACION → FACTOR G1
        {28, 29, -20}, // G1 → ** FACTOR G1
        {66}, // FACTOR → CONSTIPO
        {31, -59, 30}, // FACTOR → H1 id H2
        {-51, 17, -50, 36}, // FACTOR → H7 ( OR )
        {38}, // FACTOR → FUNCION
        {-2}, // H1 → ++
        {-5}, // H1 → --
        {32, 78}, // H2 → ARR H3
        {-51, 34, -50}, // H2 → ( H5 )
        {33, 17, 40}, // H3 → ASIG OR H4
        {17, -18, 17, -45}, // H4 → ? OR : OR
        {35, 17}, // H5 → OR H6
        {35, 17, -15}, // H6  → , OR H6
        {-42}, // H7 → !
        {-7}, // H7 → ~
        {-51, -50, -83}, // MET_CAD → toLowerCase ( )
        {-51, -50, -84}, // MET_CAD → toUpperCase ( )
        {-85}, // MET_CAD → length
        {-51, -50, -86}, // MET_CAD → trim ( )
        {-51, 17, -50, -87}, // MET_CAD → charAt ( OR )
        {-51, 17, -50, -88}, // MET_CAD → startsWith ( OR )
        {-51, 17, -50, -89}, // MET_CAD → endsWith ( OR )
        {-51, 17, -50, -90}, // MET_CAD → indexOf ( OR )
        {-51, 17, -50, -91}, // MET_CAD → includes ( OR )
        {-51, 17, -15, 17, -50, -92}, // MET_CAD → slice ( OR , OR )
        {-51, 17, -15, 17, -50, -93}, // MET_CAD → replace ( OR , OR )
        {-51, 17, -50, -94}, // MET_CAD → split ( OR )
        {-51, 17, -15, 17, -50, -115}, // FUNCION → expo ( OR , OR )
        {-51, 17, -15, 17, -50, -116}, // FUNCION → sqrtv ( OR , OR )
        {-51, 39, 17, -50, -117}, // FUNCION → fromCharCode ( OR I1 )
        {39, 17, -15}, // I1  → , OR I1 
        {-51, 17, -50, -118}, // FUNCION → asc ( OR )
        {-51, 17, -50, -119}, // FUNCION → sen ( OR )
        {-51, 17, -50, -120}, // FUNCION → val ( OR )
        {-51, 17, -50, -121}, // FUNCION → cos ( OR )
        {-51, 17, -50, -122}, // FUNCION → tan ( OR )
        {37}, // FUNCION → MET_CAD
        {-38}, // ASIG → =
        {-3}, // ASIG → +=
        {-23}, // ASIG → /=
        {-21}, // ASIG → *=
        {-6}, // ASIG → -=
        {-26}, // ASIG → %=
        {-12}, // ASIG → &=
        {-14}, // ASIG → ^=
        {-31}, // ASIG → <<=
        {-35}, // ASIG → >>=
        {-37}, // ASIG → >>>=
        {43, -59, 42}, // LET → J1 id J2
        {-72}, // J1 → let
        {-73}, // J1 → const
        {-123}, // J1 → var
        {44, -18}, // J2  → : J3
        {45, 65}, // J3 → TIPO J4
        {66, -38, -59}, // J3 → id = CONSTIPO
        {66, -38}, // J4 → = CONSTIPO
        {46, -38}, // J2 → = J5
        {52}, // J5 → DEC_FUN
        {1, -39, -51, 47, 77, -50}, // J5 → ( DEC_VAR J6 ) => STATU
        {-51, 49, 17, -50, -32, 48, -27, -125, -124}, // J5 → new Array < J7 > ( OR J8 )
        {-49, 50, -48}, // J5 → [ J9 ]
        {47, 77, -15}, // J6  → , DEC_VAR J6
        {65}, // J7 → TIPO
        {-59}, // J7 → id
        {49, 17, -15}, // J8  → , OR J8
        {51, 17}, // J9 → OR J10
        {51, 17, -15}, // J10  → , OR J10
        {-47, 57, -46, 56, -51, 54, -50, 53, -129}, // DEC_FUN → function K1 ( K2 ) K4 { K5 }
        {-59}, // K1 → id
        {55, 77}, // K2 → DEC_VAR K3
        {55, 77, -15}, // K3  → , DEC_VAR K3
        {65, -18}, // K4  → : TIPO
        {59, -17, 58, 77, -123}, // K5 → var DEC_VAR K6 ; K7
        {59}, // K5 → K7
        {58, 77, -15}, // K6  → , DEC_VAR K6 
        {61, 1, 60, 52}, // K7 → DEC_FUN K8 STATU K9
        {61, 1}, // K7 → STATU K9
        {60, 52}, // K8 → DEC_FUN K8
        {61, 1, -17}, // K9  → ; STATU K9
        {-47, 64, 52, -18, -59, 63, -46}, // DEC_RETURN → { L1 id : DEC_FUN L2 }
        {-80}, // L1 → set
        {-81}, // L1 → get
        {64, 52, -18, -59, 63, -15}, // L2  → , L1 id : DEC_FUN L2
        {-77}, // TIPO → string
        {-76}, // TIPO → number
        {-126}, // TIPO → boolean
        {-127}, // TIPO → real
        {-128}, // TIPO → exp
        {-109}, // TIPO → null
        {-59, -58}, // TIPO → # id
        {-52}, // CONSTIPO → cadena
        {-54}, // CONSTIPO → numérica
        {-107}, // CONSTIPO → true
        {-108}, // CONSTIPO → false
        {-55}, // CONSTIPO → real
        {-109}, // CONSTIPO → null
        {-56}, // CONSTIPO → exponencial
        {-47, 71, 1, -46, 68}, // PPAL → M1 { STATU M5 }
        {68, 75}, // M1 → INTERF M1
        {68, 72}, // M1 → CLASE M1
        {69, 41}, // M1 → LET M3
        {70, 52}, // M1 → DEC_FUN M4
        {69, 41, -17}, // M3  → ; LET M3
        {70, 52}, // M3 → DEC_FUN M4
        {70, 52, -17}, // M4  → ; DEC_FUN M4
        {71, 1, -17}, // M5  → ; STATU M5
        {-47, 74, 80, 73, 77, -46, -59, -82}, // CLASE → class id { DEC_VAR N1 DEC_MET N2 }
        {73, 77, -17}, // N1  → ; DEC_VAR N1
        {74, 80}, // N2 → DEC_MET N2
        {-47, 76, 77, -46, -59, -79}, // INTERF → interface id { DEC_VAR O1 }
        {76, 77, -17}, // O1 → ; DEC_VAR O1
        {65, -18, -59}, // DEC_VAR → id : TIPO
        {-49, 79, 17, -48}, // ARR → [ OR P1 ]
        {79, 17, -15}, // P1  → , OR P1
        {-47, 84, -46, 83, -51, 81, -50, -59, -130}, // DEC_MET → Method id ( Q1 ) Q3 { Q4 }
        {82, 77}, // Q1 → DEC_VAR Q2
        {82, 77, -15}, // Q2  → , DEC_VAR Q2
        {65, -18}, // Q3  → : TIPO
        {86, -17, 85, 77, -123}, // Q4  → var DEC_VAR Q5 ; Q6
        {86}, // Q4 → Q6
        {85, 77, -15}, // Q5  → , DEC_VAR Q5 
        {88, 1, 87, 80}, // Q6 → DEC_MET Q7 STATU Q8
        {88, 1}, // Q6 → STATU Q8
        {87, 80}, // Q7 → DEC_MET Q7
        {88, 1, -17}, // Q8  → ; STATU Q8
    };
    
    public AnalisisSintaxis(int[][] matrizSintaxis, LinkedList<Token> listaTokens, LinkedList<Error> listaErrores, JTable tblErrores, JTable tblContadores, int[] cntDiagramas) {
        this.matrizSintaxis = matrizSintaxis;
        this.listaTokens = listaTokens;
        this.listaErrores = listaErrores;
        this.tblErrores = tblErrores;
        this.tblContadores = tblContadores;
        this.cntDiagramas = cntDiagramas;
        
        pilaSintaxis = new Stack<>();
        pilaSintaxis.push(67); // Agregamos el PPAL
    }
    
    public void analizarSintaxis () {
        int cntErrores = 0;
        
        while (!listaTokens.isEmpty()) {
            System.out.println("");
            if (!pilaSintaxis.isEmpty()) {
                int elemPS = pilaSintaxis.peek();
                Token token = listaTokens.getFirst();
                
                System.out.print("Se compara: " + elemPS + " vs " + token.getLexema());
                
                if (elemPS > 0) { // El tope es NT
                    int row = elemPS, col = Math.abs(token.getToken()) - 1;
                    int matVal = matrizSintaxis[row][col];
                    
                    if (matVal >= 1 && matVal <= 183) { // Se encontró producción
                        int prod = pilaSintaxis.pop();
                        
                        if (diagramasPos.containsKey(prod)) cntDiagramas[diagramasPos.get(prod)]++; // Contadores
                        
                        System.out.print("\nSe ingresan: ");
                        for (int elem : prods[matVal - 1]) {
                            System.out.print(elem + " ");
                            pilaSintaxis.push(elem);
                        }
                    } else if (matVal == 184) { // Se encontró epsilon
                        pilaSintaxis.pop();
                    } else { // Se encontró error
                        Error error = new Error(matVal, clasificarError(matVal), token.getLexema(), "Sintaxis", token.getLinea(), 0);
                        System.out.println("ERROR " + token.getLexema() + " " + matVal);
                        listaErrores.add(error);
                        cntErrores++;
                        
                        listaTokens.removeFirst();
                    }
                } else if (elemPS < 0 && elemPS == token.getToken()) { // El tope es T y son iguales
                    pilaSintaxis.pop();
                    listaTokens.removeFirst();
                } else if (elemPS < 0 && elemPS != token.getToken()) { // El tope es T y NO son iguales
                    Error error = new Error(499, "Fuerza bruta", token.getLexema(), "Sintaxis", token.getLinea(), 0);
                    listaErrores.add(error);
                    cntErrores++;
                    
                    pilaSintaxis.pop();
                    listaTokens.removeFirst();
                }    
            } else {
                break;
            }
        }
        
        DefaultTableModel tblErroresModel = (DefaultTableModel) tblErrores.getModel();
        for (var error : listaErrores) {
            tblErroresModel.addRow(new Object[]{
                error.getToken(), error.getDescripcion(), error.getLexema(), error.getTipo(), error.getLinea(), error.getColError()
            });
        }
        
        int val = (int) tblContadores.getValueAt(0, 0);
        tblContadores.setValueAt(val + cntErrores, 0, 0);
    }
    
    private String clasificarError (int estado) {
        return switch (estado) {
            case 508 -> "Se esperaba Console.read Console.log if { while cadena numérica true false real null exponencial ++ -- id ! ~ ( expo sqrtv fromCharCode asc sen val cos tan toLowerCase toUpperCase length trim charAt startsWith endsWith indexOf includes slice replace split return do for switch";
            case 509 -> "Se esperaba ,";
            case 510 -> "Se esperaba elseif";
            case 511 -> "Se esperaba else";
            case 512 -> "Se esperaba ;";
            case 513 -> "Se esperaba cadena numérica true false real null exponencial ++ -- id ! ~ ( expo sqrtv fromCharCode asc sen val cos tan toLowerCase toUpperCase length trim charAt startsWith endsWith indexOf includes slice replace split {";
            case 514 -> "Se esperaba cadena numérica true false real null exponencial ++ -- id ! ~ ( expo sqrtv fromCharCode asc sen val cos tan toLowerCase toUpperCase length trim charAt startsWith endsWith indexOf includes slice replace split let";
            case 515 -> "Se esperaba cadena numérica true false real null exponencial ++ -- id ! ~ ( expo sqrtv fromCharCode asc sen val cos tan toLowerCase toUpperCase length trim charAt startsWith endsWith indexOf includes slice replace split";
            case 516 -> "Se esperaba cadena numérica true false real null exponencial ++ -- id ! ~ ( expo sqrtv fromCharCode asc sen val cos tan toLowerCase toUpperCase length trim charAt startsWith endsWith indexOf includes slice replace split $";
            case 517 -> "Se esperaba in of";
            case 518 -> "Se esperaba break case default";
            case 519 -> "Se esperaba case default";
            case 520 -> "Se esperaba || |";
            case 521 -> "Se esperaba < <= == != >= > === !==";
            case 522 -> "Se esperaba - + << >> >>>";
            case 523 -> "Se esperaba * / %";
            case 524 -> "Se esperaba && & ^";
            case 525 -> "Se esperaba **";
            case 526 -> "Se esperaba ++ --";
            case 527 -> "Se esperaba [ (";
            case 528 -> "Se esperaba = += /= *= -= %= &= ^= <<= >>= >>>=";
            case 529 -> "Se esperaba ?";
            case 530 -> "Se esperaba ! ~";
            case 531 -> "Se esperaba toLowerCase toUpperCase length trim charAt startsWith endsWith indexOf includes slice replace split";
            case 532 -> "Se esperaba expo sqrtv fromCharCode asc sen val cos tan toLowerCase toUpperCase length trim charAt startsWith endsWith indexOf includes slice replace split";
            case 533 -> "Se esperaba = += /= *= -= %= &= ^= <<= >>= >>>=";
            case 534 -> "Se esperaba let const var";
            case 535 -> "Se esperaba : =";
            case 536 -> "Se esperaba string number boolean real exp null # id";
            case 537 -> "Se esperaba =";
            case 538 -> "Se esperaba function ( new [";
            case 539 -> "Se esperaba function";
            case 540 -> "Se esperaba id";
            case 541 -> "Se esperaba :";
            case 542 -> "Se esperaba var function Console.read Console.log if { while cadena numérica true false real null exponencial ++ -- id ! ~ ( expo sqrtv fromCharCode asc sen val cos tan toLowerCase toUpperCase length trim charAt startsWith endsWith indexOf includes slice replace split return do for switch";
            case 543 -> "Se esperaba function Console.read Console.log if { while cadena numérica true false real null exponencial ++ -- id ! ~ ( expo sqrtv fromCharCode asc sen val cos tan toLowerCase toUpperCase length trim charAt startsWith endsWith indexOf includes slice replace split return do for switch";
            case 544 -> "Se esperaba {";
            case 545 -> "Se esperaba set get";
            case 546 -> "Se esperaba string number boolean real exp null #";
            case 547 -> "Se esperaba cadena numérica true false real null exponencial";
            case 548 -> "Se esperaba interface class let const var function";
            case 549 -> "Se esperaba ; function";
            case 550 -> "Se esperaba class";
            case 551 -> "Se esperaba Method";
            case 552 -> "Se esperaba interface";
            case 553 -> "Se esperaba [";
            case 554 -> "Se esperaba var Method Console.read Console.log if { while cadena numérica true false real null exponencial ++ -- id ! ~ ( expo sqrtv fromCharCode asc sen val cos tan toLowerCase toUpperCase length trim charAt startsWith endsWith indexOf includes slice replace split return do for switch";
            case 555 -> "Se esperaba Method Console.read Console.log if { while cadena numérica true false real null exponencial ++ -- id ! ~ ( expo sqrtv fromCharCode asc sen val cos tan toLowerCase toUpperCase length trim charAt startsWith endsWith indexOf includes slice replace split return do for switch";
            default -> "";
        };
    }
    
}
