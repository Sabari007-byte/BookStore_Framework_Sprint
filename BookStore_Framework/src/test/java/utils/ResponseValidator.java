package utils;

import io.restassured.response.Response;
import org.testng.asserts.SoftAssert;

public class ResponseValidator {

    private static final long DEFAULT_MAX_RESPONSE_TIME_MS = 10000;

    public static void validateJsonResponse(Response response,
                                            int expectedStatus,
                                            long maxMs,
                                            SoftAssert softAssert) {

        softAssert.assertEquals(response.getStatusCode(), expectedStatus,
                "Status code mismatch");

        softAssert.assertTrue(response.getContentType().contains("application/json"),
                "Invalid Content-Type: " + response.getContentType());

        softAssert.assertTrue(response.getTime() <= maxMs,
                "Response time too high: " + response.getTime());
    }

    public static void validateJsonResponse(Response response,
                                            int expectedStatus,
                                            SoftAssert softAssert) {
        validateJsonResponse(response, expectedStatus, DEFAULT_MAX_RESPONSE_TIME_MS, softAssert);
    }

    public static void validateFieldPresent(Response response,
                                            String jsonPath,
                                            SoftAssert softAssert) {

        Object value = response.jsonPath().get(jsonPath);

        softAssert.assertNotNull(value,
                "Missing field: " + jsonPath);
    }

    public static void validateFieldValue(Response response,
                                          String jsonPath,
                                          String expected,
                                          SoftAssert softAssert) {

        String actual = response.jsonPath().getString(jsonPath);

        softAssert.assertEquals(actual, expected,
                "Mismatch in field: " + jsonPath);
    }
}