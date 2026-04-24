package base;

import io.restassured.RestAssured;
import config.ConfigReader;

public class BaseTest {

    public static void setup() {
        RestAssured.baseURI = ConfigReader.get("baseUrl");
    }
}