package base;

import config.ConfigReader;
import io.restassured.RestAssured;

public class BaseTest {

    public static void setup() {
        RestAssured.baseURI = ConfigReader.get("baseUrl");
    }
}