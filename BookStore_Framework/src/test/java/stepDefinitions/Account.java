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


public class Account {

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
    @Given("payload is prepared with {string} and {string} and {string}")
    public void extraJsonBody(String username, String password ,String mobile) {
        
        requestBody = "{ \"userName\": \"" + username + "\", \"password\": \"" + password + "\",\"mobile\":\""+mobile+"\" }";
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
            Hooks.sharedUserId = uid;
            Hooks.sharedUsername = username;
         }
    }

   
    @Given("token request payload is prepared with {string} and {string}")
    public void tokenRequestPayloadPrepared(String usernameParam, String passwordParam) {
        String u = "CSV".equals(usernameParam) ? Hooks.sharedUsername : usernameParam;
        String p = "CSV".equals(passwordParam) ? 
                   (csvPassword != null ? csvPassword : "Test@123") : passwordParam;
        requestBody = buildBody(u, p);
    }

    @When("I send a POST request to generate token")
    public void generateToken() {
        response = given().contentType(ContentType.JSON).body(requestBody)
                .when().post(ConfigReader.get("generateToken"));
     
            String t = response.jsonPath().getString("token");
            token = t;
       Hooks.sc.set("token", t);
       Hooks.sharedToken = t;
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
    @Given("previously created user is used")
    public void usePreviousUser() {

        userId = Hooks.sharedUserId;

        if (Hooks.sharedToken == null || Hooks.sharedToken.isEmpty()) {

            String body = "{ \"userName\": \"" + Hooks.sharedUsername + "\", \"password\": \"Test@123\" }";

            Response r = given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .post(ConfigReader.get("generateToken"));

            Hooks.sharedToken = r.jsonPath().getString("token");
        }

        token = Hooks.sharedToken;

        Hooks.sc.set("userId", userId);
        Hooks.sc.set("token", token);
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
      Hooks.actualStatusCode=response.getStatusCode();
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
