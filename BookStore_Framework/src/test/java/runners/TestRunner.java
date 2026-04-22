package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;


@CucumberOptions(
    features = "src/test/resources/Features",
    glue     = {"stepDefinitions", "hooks"},
    plugin   = {
        "pretty",
        "html:target/cucumber-report.html",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome = false,
    publish    = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
