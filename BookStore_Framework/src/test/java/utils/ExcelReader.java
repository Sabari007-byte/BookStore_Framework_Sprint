package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

   
    private static List<String[]> rows;

    private static List<String[]> load() {
        if (rows != null) return rows;
        rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("./src/test/resources/testdata/bookStore.csv"))) {
            String header = br.readLine(); 
            if (header == null) return rows;
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
               
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;
                rows.add(new String[]{parts[0].trim(), parts[1].trim()});
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "[CsvReader] Failed to load:", e);
        }
        
        return rows;
    }

   
    public static int getRowCount() {
        return load().size();
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

    public static String resolveUsername(String csvKey) {
       
        try {
            int idx = Integer.parseInt(csvKey);
            return getUsername(idx);
        } catch (NumberFormatException ignored) {  }

        
        for (String[] row : load()) {
            if (row[0].equalsIgnoreCase(csvKey)) return row[0];
        }
        
        return csvKey;
    }


    public static String resolvePassword(String csvKey) {
        try {
            int idx = Integer.parseInt(csvKey);
            return getPassword(idx);
        } catch (NumberFormatException ignored) {  }

        for (String[] row : load()) {
            if (row[0].equalsIgnoreCase(csvKey)) return row[1];
        }
        return "Test@123"; 
    }
}
