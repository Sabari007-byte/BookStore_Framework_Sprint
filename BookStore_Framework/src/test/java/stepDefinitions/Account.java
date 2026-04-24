package stepDefinitions;

import io.cucumber.java.en.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import config.ConfigReader;

public class Account {

    static Response response;
    static String username;
    static String userId;
    static String token;

    String requestBody;



    @Given("The user API base URL is set")
    public void setBaseURI() {
        io.restassured.RestAssured.baseURI = ConfigReader.get("baseUrl");
    }



    @Given("valid user payload is prepared")
    public void validUserPayload() {
        username = "User" + System.currentTimeMillis();
        requestBody = "{ \"userName\": \"" + username + "\", \"password\": \"Test@123\" }";
    }

    @Given("duplicate user payload is prepared")
    public void duplicateUserPayload() {
        validUserPayload();
        createUser(); // first user
        requestBody = "{ \"userName\": \"" + username + "\", \"password\": \"Test@123\" }";
    }

    @Given("user payload with extra fields is prepared")
    public void extraFieldPayload() {
        requestBody = "{ \"userName\": \"user1\", \"password\": \"Test@123\", \"extra\": \"field\" }";
    }

    @Given("empty user payload is prepared")
    public void emptyPayload() {
        requestBody = "{ \"userName\": \"\", \"password\": \"\" }";
    }

    @Given("invalid JSON payload is prepared")
    public void invalidJsonPayload() {
        requestBody = "{ userName: tester, password: }";
    }

    @When("I send a POST request to create a user")
    public void createUser() {
        response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post(ConfigReader.get("createUser"));

        response.then().log().all();

        if (response.getStatusCode() == 201) {
            userId = response.jsonPath().getString("userID");
        }
    }

  

    @Given("valid credentials are prepared")
    public void validCredentials() {

        if (username == null) {
            validUserPayload();
            createUser();
        }

        requestBody = "{ \"userName\": \"" + username + "\", \"password\": \"Test@123\" }";
    }

    @Given("invalid username is prepared")
    public void invalidUsername() {
        requestBody = "{ \"userName\": \"wrongUser\", \"password\": \"Test@123\" }";
    }

    @Given("invalid password is prepared")
    public void invalidPassword() {
        requestBody = "{ \"userName\": \"" + username + "\", \"password\": \"wrongPass\" }";
    }

    @Given("user payload with extra invalid field is prepared")
    public void extraInvalidFieldPayload() {
        requestBody = "{ \"userName\": \"" + username + "\", \"password\": \"Test@123\", \"extra\": \"value\" }";
    }

    @When("I send a POST request to generate token")
    public void generateToken() {
        response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post(ConfigReader.get("generateToken"));

        response.then().log().all();

        if (response.getStatusCode() == 200) {
            token = response.jsonPath().getString("token");
        }
    }

   

    @Given("valid token is available")
    public void validToken() {
        validUserPayload();
        createUser();
        validCredentials();
        generateToken();
    }

    @Given("invalid token is prepared")
    public void invalidToken() {
        token = "invalid_token";
    }

    @Given("no token is provided")
    public void noToken() {
        token = null;
    }

    @When("I send a POST request to authorize user")
    public void authorizeUser() {

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token != null ? "Bearer " + token : "")
                .body("{ \"userName\": \"" + username + "\", \"password\": \"Test@123\" }")
        .when()
                .post(ConfigReader.get("authorized"));

        response.then().log().all();
    }

 

    @Given("valid user ID is available")
    public void validUserId() {
        validToken();
    }

    @Given("invalid user ID is prepared")
    public void invalidUserId() {
        userId = "invalid-id";
    }

    @When("I send a GET request to fetch user")
    public void getUser() {

        response = given()
                .header("Authorization", token != null ? "Bearer " + token : "")
        .when()
                .get(ConfigReader.get("getUser") + userId);

        response.then().log().all();
    }

  

    @Given("valid user ID and token are available")
    public void validUserAndToken() {
        validToken();
    }

    @When("I send a DELETE request to delete user")
    public void deleteUser() {

        response = given()
                .header("Authorization", token != null ? "Bearer " + token : "")
        .when()
                .delete(ConfigReader.get("deleteUser") + userId);

        response.then().log().all();
    }



    @Then("the response status should be {int}")
    public void validateStatus(int code) {
        response.then().statusCode(code);
    }
}