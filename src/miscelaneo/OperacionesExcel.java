/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package miscelaneo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author aguir
 */
public class OperacionesExcel {

    public static int[][] readExcel (String filePath, String sheetName) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheet(sheetName);

            int numRows = 71, numCols = 35;
            int[][] mat = new int[numRows][numCols];
            for (int i = 1; i <= numRows; i++) {
                for (int j = 1; j <= numCols; j++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    mat[i - 1][j - 1] = (int) cell.getNumericCellValue();
                }
            }

            return mat;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public static void createExcel (String filePath, JTable tblContadores, JTable tblTokens, JTable tblErrores) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        XSSFSheet tokens = workbook.createSheet("Tokens");
        XSSFRow sheetRowTitulosTokens = tokens.createRow(0);
        int NUM_COLS_TOKENS = 3;
        for (int i = 0; i < NUM_COLS_TOKENS; i++) {
            XSSFCell sheetCellTitulo = sheetRowTitulosTokens.createCell(i);
            sheetCellTitulo.setCellValue(tblTokens.getColumnName(i));
        }
        for (int i = 0; i < tblTokens.getRowCount(); i++) {
            XSSFRow sheetRow = tokens.createRow(i + 1);
            
            XSSFCell linea = sheetRow.createCell(0);
            linea.setCellValue((int) tblTokens.getValueAt(i, 0));
            
            XSSFCell token = sheetRow.createCell(1);
            token.setCellValue((String) tblTokens.getValueAt(i, 1));
            
            XSSFCell lexema = sheetRow.createCell(2);
            lexema.setCellValue((int) tblTokens.getValueAt(i, 2));
        }
        
        XSSFSheet errores = workbook.createSheet("Errores");
        XSSFRow sheetRowTitulosErrores = errores.createRow(0);
        int NUM_COLS_ERRORES = 6;
        for (int i = 0; i < NUM_COLS_ERRORES; i++) {
            XSSFCell sheetCellTitulo = sheetRowTitulosErrores.createCell(i);
            sheetCellTitulo.setCellValue(tblErrores.getColumnName(i));
        }
        for (int i = 0; i < tblErrores.getRowCount(); i++) {
            XSSFRow sheetRow = errores.createRow(i + 1);
            
            XSSFCell token = sheetRow.createCell(0);
            token.setCellValue((int) tblErrores.getValueAt(i, 0));
            
            XSSFCell linea = sheetRow.createCell(1);
            linea.setCellValue((String) tblErrores.getValueAt(i, 1));
            
            XSSFCell descripcion = sheetRow.createCell(2);
            descripcion.setCellValue((String) tblErrores.getValueAt(i, 2));
            
            XSSFCell lexema = sheetRow.createCell(3);
            lexema.setCellValue((String) tblErrores.getValueAt(i, 3));
            
            XSSFCell colError = sheetRow.createCell(4);
            colError.setCellValue((int) tblErrores.getValueAt(i, 4));
            
            XSSFCell tipo = sheetRow.createCell(5);
            tipo.setCellValue((int) tblErrores.getValueAt(i, 5));
        }
        
        XSSFSheet contadores = workbook.createSheet("Contadores");
        XSSFRow sheetRowTitulosContadores = contadores.createRow(0), sheetRowDatosContadores = contadores.createRow(1);
        int NUM_COLS_CONTADORES = 22;
        for (int i = 0; i < NUM_COLS_CONTADORES; i++) {
            XSSFCell sheetCellTitulo = sheetRowTitulosContadores.createCell(i), sheetCellDatoContador = sheetRowDatosContadores.createCell(i);
            sheetCellTitulo.setCellValue(tblContadores.getColumnName(i));
            sheetCellDatoContador.setCellValue((int) tblContadores.getValueAt(0, i));
        }
        
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
