package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue     = {"stepDefinitions", "hooks"},
        plugin   = {
                "pretty",
                "html:target/cucumber-report-book.html"
        },
        monochrome = false
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
