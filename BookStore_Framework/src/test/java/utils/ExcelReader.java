package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelReader {

	private static String automationFilePath() {
	    return new File("").getAbsolutePath() + File.separator
	            + "src" + File.separator + "test" + File.separator
	            + "resources" + File.separator + "testdata" + File.separator
	            + "testcase.xlsx";
	}

    public static final int COL_TC_ID    = 0;
    public static final int COL_METHOD   = 1;
    public static final int COL_ENDPOINT = 2;
    public static final int COL_BODY     = 3;
    public static final int COL_STATUS   = 4;
    public static final int COL_RESULT   = 5;

    public static String getData(String sheet, int row, int col) {
        try (FileInputStream fis = new FileInputStream(automationFilePath());
             Workbook wb = WorkbookFactory.create(fis)) {
            Sheet ws = wb.getSheet(sheet);
            if (ws == null) return "";
            Row r = ws.getRow(row);
            if (r == null) return "";
            Cell cell = r.getCell(col);
            if (cell == null) return "";
            if (cell.getCellType() == CellType.NUMERIC)
                return String.valueOf((int) cell.getNumericCellValue());
            return cell.toString().trim();
        } catch (Exception e) { e.printStackTrace(); return ""; }
    }

    public static int getRowByTcId(String sheet, String tcTag) {
        try (FileInputStream fis = new FileInputStream(automationFilePath());
             Workbook wb = WorkbookFactory.create(fis)) {
            Sheet ws = wb.getSheet(sheet);
            if (ws == null) return -1;
            String hookTcNum = extractTcNum(tcTag);
            for (Row row : ws) {
                if (row.getRowNum() == 0) continue;
                Cell idCell = row.getCell(0);
                if (idCell == null) continue;
                String excelTcNum = extractTcNum(idCell.toString());
                if (!excelTcNum.isEmpty() && excelTcNum.equals(hookTcNum))
                    return row.getRowNum();
            }
            return -1;
        } catch (Exception e) { e.printStackTrace(); return -1; }
    }

    public static String getSheetByTcId(String tcTag) {
        for (String sheet : new String[]{"Account", "Book"})
            if (getRowByTcId(sheet, tcTag) != -1) return sheet;
        return null;
    }

    public static void writeResult(String tcId, String status) {
        if (tcId == null || tcId.isEmpty()) return;
        String filePath = automationFilePath();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = WorkbookFactory.create(fis)) {
            boolean found = false;
            String hookTcNum = extractTcNum(tcId);
            for (String sn : new String[]{"Account", "Book"}) {
                Sheet ws = wb.getSheet(sn);
                if (ws == null) continue;
                for (Row row : ws) {
                    Cell idCell = row.getCell(0);
                    if (idCell == null) continue;
                    if (extractTcNum(idCell.toString()).equals(hookTcNum)) {
                        Cell c = row.getCell(COL_RESULT);
                        if (c == null) c = row.createCell(COL_RESULT);
                        c.setCellValue(status);
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            if (!found) System.err.println("[ExcelReader] writeResult: no row for " + tcId);
            try (FileOutputStream fos = new FileOutputStream(filePath)) { wb.write(fos); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static String extractTcNum(String value) {
        if (value == null) return "";
        int idx = value.lastIndexOf("TC_");
        if (idx == -1) return "";
        return value.substring(idx + 3).trim();
    }
}