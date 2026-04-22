package hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.testng.asserts.SoftAssert;
import utils.ExcelReader;

public class Hooks {

    public static ThreadLocal<SoftAssert> softAssert = new ThreadLocal<>();

    public static String tcId;
    public static String sheet;
    public static int rowNum;

    public static String excelMethod;
    public static String excelEndpoint;
    public static String excelBody;
    public static int excelExpectedStatus;

    public static int actualStatusCode = -1;
    public static boolean forceFailWithDefect = false;
    public static String defectMessage = "";
    @Before
    public void beforeScenario(Scenario scenario) {

        softAssert.set(new SoftAssert());

        actualStatusCode = -1;
        forceFailWithDefect = false;
        defectMessage = "";

        String tag = scenario.getSourceTagNames()
                .stream()
                .filter(t -> t.contains("TC_"))
                .findFirst()
                .orElse(null);

        if (tag != null) {
            setTcId(tag.replace("@", ""));
        }
    }

    public static void setTcId(String fullTcId) {

        tcId = fullTcId;
        sheet = ExcelReader.getSheetByTcId(tcId);
        rowNum = (sheet != null) ? ExcelReader.getRowByTcId(sheet, tcId) : -1;

        if (sheet == null || rowNum == -1) {
            System.err.println("Excel row not found for TC: " + tcId);
            excelMethod = excelEndpoint = excelBody = "";
            excelExpectedStatus = -1;
        } else {
            excelMethod = ExcelReader.getData(sheet, rowNum, ExcelReader.COL_METHOD);
            excelEndpoint = ExcelReader.getData(sheet, rowNum, ExcelReader.COL_ENDPOINT);
            excelBody = ExcelReader.getData(sheet, rowNum, ExcelReader.COL_BODY);

            try {
                excelExpectedStatus = Integer.parseInt(
                        ExcelReader.getData(sheet, rowNum, ExcelReader.COL_STATUS));
            } catch (Exception e) {
                excelExpectedStatus = -1;
            }
        }

        System.out.println("TC=" + tcId + " Method=" + excelMethod +
                " Endpoint=" + excelEndpoint + " Expected=" + excelExpectedStatus);
    }

    @After
    public void afterScenario(Scenario scenario) {

        try {
            softAssert.get().assertAll(); 
        } catch (AssertionError e) {
            forceFailWithDefect = true;
            defectMessage = e.getMessage();
        }

        boolean passed = !scenario.isFailed() && !forceFailWithDefect;
        String status = passed ? "PASS" : "FAIL";

        if (tcId != null) {
            ExcelReader.writeResult(tcId, status);
        }

        if (forceFailWithDefect) {
            throw new AssertionError("[DEFECT] " + defectMessage);
        }

        softAssert.remove();

        System.out.println(tcId + " → " + status +
                " | ActualHTTP=" + actualStatusCode);
    }
}