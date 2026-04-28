package hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import context.ScenarioContext;
import org.testng.asserts.SoftAssert;
import base.BaseTest;

public class Hooks {

    public static ThreadLocal<SoftAssert> softAssert = new ThreadLocal<>();
    public static int    actualStatusCode = -1;
    public static String sharedUserId;
    public static String sharedToken;
    public static String sharedUsername;
    public static int    rowIndex = 0;
    public static ScenarioContext sc;

    @Before
    public void beforeScenario(Scenario scenario) {
        BaseTest.setup();                 
        softAssert.set(new SoftAssert());
        actualStatusCode = -1;
        sc = new ScenarioContext();
    }

    @After
    public void afterScenario(Scenario scenario) {
        softAssert.remove();
        System.out.println(scenario.getName() +
                " | ActualHTTP=" + actualStatusCode);
    }
}