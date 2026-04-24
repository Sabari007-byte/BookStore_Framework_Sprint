package stepDefinitions;

import static io.restassured.RestAssured.given;

import org.testng.asserts.SoftAssert;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import config.ConfigReader;
import utils.ExcelReader;
import utils.ResponseValidator;


public class AccountSteps {

    public static Response response;
    public static String   userId;
    public static String   token;
    public static String   username;
    public static String   csvUsername;
    public static String   csvPassword;
   
    private String requestBody = "";

  
    static void createFreshUser() {
        String base = (csvUsername != null) ? csvUsername : "auto";
        String pass = (csvPassword != null) ? csvPassword : "Test@123";
        username = base + "_" + System.currentTimeMillis();
        String body = buildBody(username, pass);
        Response r = given().contentType(ContentType.JSON).body(body)
                .when().post(ConfigReader.get("createUser"));
        if (r.getContentType() != null && r.getContentType().contains("json")) {
            String uid = r.jsonPath().getString("userID");
            if (uid != null) userId = uid;
        }
       
    }

    public static void generateFreshToken() {
        String pass = (csvPassword != null) ? csvPassword : "Test@123";
        Response r = given().contentType(ContentType.JSON)
                .body(buildBody(username, pass))
                .when().post(ConfigReader.get("generateToken"));
        if (r.getContentType() != null && r.getContentType().contains("json")) {
            String t = r.jsonPath().getString("token");
            if (t != null && !t.isEmpty()) token = t;
        }
    }

    private static String buildBody(String u, String p) {
        return "{ \"userName\": \"" + u + "\", \"password\": \"" + p + "\" }";
    }

    private void markDefect(String tcId, String message) {
        Hooks.forceFailWithDefect = true;
        Hooks.defectMessage = "[DEFECT] " + tcId + ": " + message;
        System.out.println(Hooks.defectMessage);
    }

  
    @Given("The user API base URL is set")
    public void setBaseURI() {
        io.restassured.RestAssured.baseURI = ConfigReader.get("baseUrl");
    }


    @Given("valid user data is loaded from CSV file")
    public void loadUserDataFromCsv() {
        csvUsername = ExcelReader.getUsername(Hooks.rowIndex);
        csvPassword = ExcelReader.getPassword(Hooks.rowIndex);
        Hooks.rowIndex++;
        System.out.println("Loaded user: " + csvUsername);
    }

    @Given("user is created and token is generated from CSV")
    public void createUserAndTokenFromCsv() {
        String pass = (csvPassword != null) ? csvPassword : "Test@123";
        username = csvUsername + "_" + System.currentTimeMillis();
        String body = buildBody(username, pass);
        Response r = given().contentType(ContentType.JSON).body(body)
                .when().post(ConfigReader.get("createUser"));
        if (r.getContentType() != null && r.getContentType().contains("json")) {
            String uid = r.jsonPath().getString("userID");
            if (uid != null) userId = uid;
        }
        r = given().contentType(ContentType.JSON).body(body)
                .when().post(ConfigReader.get("generateToken"));
        if (r.getContentType() != null && r.getContentType().contains("json")) {
            String t = r.jsonPath().getString("token");
            if (t != null && !t.isEmpty()) token = t;
        }
        requestBody = body;
        Hooks.sc.set("userId", userId);
        Hooks.sc.set("token", token);
    }

   

    @Given("user payload is prepared with {string} and {string}")
    public void userPayloadPrepared(String usernameParam, String passwordParam) {
        String u = "CSV_Name".equals(usernameParam) && csvUsername != null
                ? csvUsername + "_" + System.currentTimeMillis() : usernameParam;
        String p = "CSV_pass".equals(passwordParam) && csvPassword != null ? csvPassword : passwordParam;
        username = u;
        requestBody = buildBody(u, p);
    }

    @Given("duplicate user payload is prepared")
    public void duplicateUserPayload() {
        
        String body = buildBody(username, "Test@123");
        given().contentType(ContentType.JSON).body(body)
               .when().post(ConfigReader.get("createUser"));
        requestBody = body;
    }

    @Given("empty user payload is prepared")
    public void emptyUserPayload() {
        requestBody = "{ \"userName\": \" \", \"password\": \"Test@123\" }";
    }

    @Given("invalid JSON payload is prepared")
    public void invalidJsonPayload() {
        requestBody = "{ invalid_json }";
    }

    @Given("user payload with extra invalid field is prepared")
    public void userPayloadWithExtraField() {
        username = "auto_" + System.currentTimeMillis();
        requestBody = "{ \"userName\": \"" + username
                + "\", \"password\": \"Test@123\", \"extra\": \"field\" }";
    }

    @When("I send a POST request to create a user")
    public void createUser() {
        response = given().contentType(ContentType.JSON).body(requestBody)
                .when().post(ConfigReader.get("createUser"));
         if(response.getStatusCode()==201) {
            String uid = response.jsonPath().getString("userID");
            userId = uid;
            Hooks.sc.set("userId", uid);
          
         }
    }

   
    @Given("token request payload is prepared with {string} and {string}")
    public void tokenRequestPayloadPrepared(String usernameParam, String passwordParam) {

        csvUsername = ExcelReader.getUsername(Hooks.rowIndex);
        csvPassword = ExcelReader.getPassword(Hooks.rowIndex);

        createFreshUser();

        String u = username;
        String p = (csvPassword != null) ? csvPassword : "Test@123";

        requestBody = buildBody(u, p);
    }

    @When("I send a POST request to generate token")
    public void generateToken() {
        response = given().contentType(ContentType.JSON).body(requestBody)
                .when().post(ConfigReader.get("generateToken"));
     
            String t = response.jsonPath().getString("token");
            token = t;
       Hooks.sc.set("token", t);
    }

  
    @Given("token {string} is prepared for authorization")
    public void tokenForAuthorization(String tokenValue) {
        token = tokenValue;
        String u = (csvUsername != null) ? csvUsername : "testuser";
        String p = (csvPassword != null) ? csvPassword : "Test@123";
        requestBody = buildBody(u, p);
    }

    @When("I send a POST request to authorize user")
    public void authorizeUser() {
        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization",
                        (token == null || token.isEmpty()) ? "" : "Bearer " + token)
                .body(requestBody)
                .when().post(ConfigReader.get("authorized"));
    }

 

    @Given("user ID {string} is prepared")
    public void userIdPrepared(String uid) {
        createFreshUser();
        generateFreshToken();
        userId = uid;
    }

    @Given("invalid user ID is prepared")
    public void invalidUserIdPrepared() {
        createFreshUser();
        generateFreshToken();
        userId = "00000000-0000-0000-0000-000000000000";
    }

    @Given("no token is provided")
    public void noTokenProvided() {
        createFreshUser();
        token = "";
    }

    @When("I send a GET request to fetch user")
    public void fetchUser() {
        response = given()
                .header("Authorization",
                        (token == null || token.isEmpty()) ? "" : "Bearer " + token)
                .when().get(ConfigReader.get("getUser") + (userId != null ? userId : ""));
    }

    @When("I send a DELETE request to delete user")
    public void deleteUser() {
        response = given()
                .header("Authorization",
                        (token == null || token.isEmpty()) ? "" : "Bearer " + token)
                .when().delete(ConfigReader.get("deleteUser") + (userId != null ? userId : ""));
    }



    @Then("the response status should be {int}")
    public void validateStatus(int expectedCode) {
        Hooks.actualStatusCode = response.getStatusCode();
        int actual = Hooks.actualStatusCode;

        switch (Hooks.currentTcTag) {
            case "BookStoreWebAPI01TC_03":
                if (actual == 201) {
                    markDefect("TC_03", "Expected 400 Bad Request for extra JSON field but API returned 201 Created. "
                            + "API does not reject unknown fields in request body.");
                    return;
                }
                break;
            case "BookStoreWebAPI01TC_07":
                if (actual == 200) {
                    markDefect("TC_07", "Expected 401 Unauthorized for invalid username but API returned 200 OK. "
                            + "Response body contains 'status: Failed' — HTTP status code is incorrect.");
                    return;
                }
                break;
            case "BookStoreWebAPI01TC_08":
                if (actual == 200) {
                    markDefect("TC_08", "Expected 401 Unauthorized for wrong password but API returned 200 OK. "
                            + "Response body contains 'status: Failed' — HTTP status code is incorrect.");
                    return;
                }
                break;
            case "BookStoreWebAPI01TC_09":
                if (actual == 200) {
                    markDefect("TC_09", "Expected 400 Bad Request for extra field in token request but API returned 200 OK. "
                            + "API ignores unknown fields and generates token anyway.");
                    return;
                }
                break;
            case "BookStoreWebAPI01TC_11":
                if (actual == 200) {
                    markDefect("TC_11", "Expected 401 Unauthorized for invalid Bearer token on /Authorized but API returned 200 OK. "
                            + "API does not validate the Authorization header for this endpoint.");
                    return;
                }
                break;
            case "BookStoreWebAPI01TC_12":
                if (actual == 200) {
                    markDefect("TC_12", "Expected 401 Unauthorized for missing token on /Authorized but API returned 200 OK. "
                            + "API ignores the Authorization header entirely for this endpoint.");
                    return;
                }
                break;
            case "BookStoreWebAPI01TC_14":
                if (actual == 401) {
                    markDefect("TC_14", "Expected 404 Not Found for invalid UUID on GET /User/{UUID} but API returned 401 Unauthorized. "
                            + "API enforces ownership check before existence check.");
                    return;
                }
                break;
            case "BookStoreWebAPI01TC_16":
                if (actual == 200) {
                    markDefect("TC_16", "Expected 404 Not Found for invalid UUID on DELETE /User/{UUID} but API returned 200 OK. "
                            + "API does not validate that the UUID belongs to the authenticated user.");
                    return;
                }
                break;
            case "BookStoreWebAPI01TC_25":
                if (actual == 400) {
                    markDefect("TC_25", "Expected 409 Conflict when replacing book with the same ISBN but API returned 400 Bad Request. "
                            + "'ISBN supplied is not available in User's Collection' — wrong error code.");
                    return;
                }
                break;
            case "BookStoreWebAPI01TC_31":
                if (actual == 400) {
                    markDefect("TC_31", "Expected 404 Not Found when deleting an already-deleted book but API returned 400 Bad Request. "
                            + "API should return 404 when the resource no longer exists.");
                    return;
                }
                break;
            default:
                break;
        }

        response.then().statusCode(expectedCode);
    }

    @Then("the JSON response status should be {int} within {long} ms")
    public void validateJsonResponseWithTime(int expectedStatus, long maxMs) {
        SoftAssert softAssert = Hooks.softAssert.get();
        ResponseValidator.validateJsonResponse(response, expectedStatus, maxMs, softAssert);
        Hooks.actualStatusCode = response.getStatusCode();
    }

    @Then("the response JSON should contain field {string}")
    public void validateJsonFieldPresent(String jsonPath) {
        SoftAssert softAssert = Hooks.softAssert.get();
        ResponseValidator.validateFieldPresent(response, jsonPath, softAssert);
    }
}
