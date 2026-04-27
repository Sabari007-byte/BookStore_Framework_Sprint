package hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import context.ScenarioContext;

import org.testng.asserts.SoftAssert;

public class Hooks {

    public static ThreadLocal<SoftAssert> softAssert = new ThreadLocal<>();

    public static int     actualStatusCode    = -1;
    public static boolean forceFailWithDefect = false;
    public static String sharedUserId;
    public static String sharedToken;
    public static String sharedUsername;
    public static String  defectMessage       = "";
    public static String  currentTcTag        = "";   
    public static int rowIndex = 0;
    public static ScenarioContext sc;
    @Before
    public void beforeScenario(Scenario scenario) {
        softAssert.set(new SoftAssert());
        actualStatusCode    = -1;
        forceFailWithDefect = false;
        defectMessage       = "";
        currentTcTag        = "";
        sc=new ScenarioContext();
        
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

        if (forceFailWithDefect) {
            throw new AssertionError("[DEFECT] " + defectMessage);
        }

        softAssert.remove();

        System.out.println(scenario.getName() + " → " + status +
                " | ActualHTTP=" + actualStatusCode);
    }
}