package hooks;

import base.BaseTest;
import io.cucumber.java.Before;
import context.ScenarioContext;

public class Hooks {
	public static ScenarioContext context;

    @Before
    public void setup() {
        BaseTest.setup();
        context = new ScenarioContext();
    }
}