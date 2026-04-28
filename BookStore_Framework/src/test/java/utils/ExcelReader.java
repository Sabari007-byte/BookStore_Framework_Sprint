package utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

    private static List<String[]> rows;

    private static List<String[]> load() {
        if (rows != null) return rows;
        rows = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(
                "./src/test/resources/testdata/bookStore.xlsx");
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = wb.getSheetAt(0);
            boolean firstRow = true;

            for (Row row : sheet) {
                if (firstRow) { firstRow = false; continue; } // skip header
                Cell userCell = row.getCell(0);
                Cell passCell = row.getCell(1);
                if (userCell == null || passCell == null) continue;
                String username = userCell.getCellType() == CellType.STRING
                        ? userCell.getStringCellValue().trim() : "";
                String password = passCell.getCellType() == CellType.STRING
                        ? passCell.getStringCellValue().trim() : "";
                if (username.isEmpty()) continue;
                rows.add(new String[]{username, password});
            }

        } catch (Exception e) {
            throw new RuntimeException("[ExcelReader] Failed to load:", e);
        }
        return rows;
    }


    public static String getUsername(int rowIndex) {
        List<String[]> data = load();
        if (data.isEmpty()) throw new RuntimeException("[CsvReader] CSV has no data rows.");
        return data.get(rowIndex % data.size())[0];
    }

    public static String getPassword(int rowIndex) {
        List<String[]> data = load();
        if (data.isEmpty()) throw new RuntimeException("[CsvReader] CSV has no data rows.");
        return data.get(rowIndex % data.size())[1];
    }

    
}