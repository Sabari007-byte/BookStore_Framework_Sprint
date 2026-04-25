package stepDefinitions;

import static io.restassured.RestAssured.given;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import config.ConfigReader;
import utils.ResponseValidator;

public class BookStore {

    static String isbn;
    static String secondIsbn;
    static final String invalidIsbn = "12345";
    static String replaceIsbn; 

    private void fetchISBNs() {
        Response res = given().when().get(ConfigReader.get("getBooks"));
        isbn       = res.jsonPath().getString("books[0].isbn");
        secondIsbn = res.jsonPath().getString("books[1].isbn");
        replaceIsbn = secondIsbn;
    }

    private void setupUserAndToken() {
        Account.username = "auto_" + System.currentTimeMillis();
        String body = "{ \"userName\": \"" + Account.username
                + "\", \"password\": \"Test@123\" }";
        Response r = given().contentType(ContentType.JSON).body(body)
                .when().post(ConfigReader.get("createUser"));
        if (r.getContentType() != null && r.getContentType().contains("json")) {
            String uid = r.jsonPath().getString("userID");
            if (uid != null) Account.userId = uid;
        }
        r = given().contentType(ContentType.JSON).body(body)
                .when().post(ConfigReader.get("generateToken"));
        if (r.getContentType() != null && r.getContentType().contains("json")) {
            String t = r.jsonPath().getString("token");
            if (t != null && !t.isEmpty()) Account.token = t;
        }
        Hooks.sc.set("userId",Account.userId);
        Hooks.sc.set("token", Account.token);
    }

    private void addBookToUser(String isbnToAdd) {
        if (isbnToAdd == null || isbnToAdd.isEmpty()) return;
        String body = "{ \"userId\": \"" + Account.userId
                + "\", \"collectionOfIsbns\": [{ \"isbn\": \"" + isbnToAdd + "\" }] }";
        given().contentType(ContentType.JSON)
               .header("Authorization", "Bearer " + Account.token)
               .body(body).when().post(ConfigReader.get("addBook"));
    }

  

    @Given("The BookStore API base URL is set")
    public void setBaseURI() {
        io.restassured.RestAssured.baseURI = ConfigReader.get("baseUrl");
    }

   
    @When("I send a GET request to fetch all books")
    public void getAllBooks() {
        Account.response = given().when().get("/BookStore/v1/Books");
    }

    @Given("valid ISBN is available")
    public void validISBN() {
        fetchISBNs();
    }

    @Given("ISBN {string} is prepared")
    public void isbnPrepared(String isbnParam) {
        isbn = isbnParam;
    }

    @When("I send a GET request to fetch book by ISBN")
    public void getBookByIsbn() {
        String query = (isbn == null || isbn.isEmpty()) ? "" : "?ISBN=" + isbn;
        Account.response = given().when().get("/BookStore/v1/Book" + query);
    }

   

    @Given("valid token and ISBN {string} are available")
    public void validTokenAndSpecificIsbn(String isbnParam) {
        setupUserAndToken();
        isbn = isbnParam;
    }

    @When("I send a POST request to add a book")
    public void addBook() {

        String userId = (String) Hooks.sc.get("userId");
        String token  = (String) Hooks.sc.get("token");

        String body = "{ \"userId\": \"" + userId
                + "\", \"collectionOfIsbns\": [{ \"isbn\": \"" + isbn + "\" }] }";

        Account.response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when().post("/BookStore/v1/Books");
    }

    @When("I send a POST request to add multiple books")
    public void addMultipleBooks() {
        String body = "{ \"userId\": \"" + Account.userId
                + "\", \"collectionOfIsbns\": [{ \"isbn\": \"" + isbn
                + "\" }, { \"isbn\": \"" + secondIsbn + "\" }] }";
        Account.response = given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + Account.token)
                .body(body).when().post("/BookStore/v1/Books");
    }


    @Given("valid token userId and same ISBN are available")
    public void sameIsbnSetup() {
        setupUserAndToken();
        fetchISBNs();
        addBookToUser(isbn);
        replaceIsbn = isbn; 
    }

    @Given("valid token userId and ISBN {string} are available for update")
    public void specificIsbnForUpdate(String isbnParam) {
        setupUserAndToken();
        fetchISBNs();
        addBookToUser(isbn);
        replaceIsbn = isbnParam;     }

    @Given("token {string} and valid userId and ISBN are available")
    public void specificTokenForReplace(String tokenParam) {
        setupUserAndToken();
        fetchISBNs();
        addBookToUser(isbn);
        Account.token = tokenParam; 
    }

    @Given("valid token userId and ISBN are available")
    public void validTokenUserIdAndIsbn() {
        if (Account.userId == null) setupUserAndToken();
        fetchISBNs();
        addBookToUser(isbn);
        replaceIsbn = secondIsbn;
    }

    @When("I send a PUT request to replace book")
    public void replaceBook() {
        
        if (Account.userId != null && Account.token != null) {
            addBookToUser(isbn);
        }
        String useReplace = (replaceIsbn != null) ? replaceIsbn : secondIsbn;
        String body = "{ \"userId\": \"" + Account.userId
                + "\", \"isbn\": \"" + useReplace + "\" }";
        Account.response = given().contentType(ContentType.JSON)
                .header("Authorization",
                        (Account.token == null || Account.token.isEmpty())
                                ? "" : "Bearer " + Account.token)
                .body(body).when().put("/BookStore/v1/Books/" + isbn);
    }

   

    @Given("valid token userId and ISBN {string} are available for delete")
    public void specificIsbnForDelete(String isbnParam) {
        setupUserAndToken();
        isbn = isbnParam;
    }

    @When("I send a DELETE request to remove book")
    public void deleteBook() {
        String body = "{ \"isbn\": \"" + isbn
                + "\", \"userId\": \"" + Account.userId + "\" }";
        Account.response = given().contentType(ContentType.JSON)
                .header("Authorization",
                        (Account.token == null || Account.token.isEmpty())
                                ? "" : "Bearer " + Account.token)
                .body(body).when().delete("/BookStore/v1/Book");
    }

    @When("I send a DELETE request to remove book again")
    public void deleteBookAgain() {
        deleteBook();
    }

    @When("I verify the user still exists")
    public void verifyUserExists() {
        Account.response = given()
                .header("Authorization", "Bearer " + Account.token)
                .when().get("/Account/v1/User/" + Account.userId);
    }

  

    @Given("userId {string} and token {string} conditions")
    public void setUserIdAndTokenConditions(String userIdParam, String tokenParam) {
        Account.userId = userIdParam;
        Account.token  = tokenParam;
    }

    @When("I send a DELETE request to remove all books")
    public void deleteAllBooks() {
        Account.response = given()
                .header("Authorization",
                        (Account.token == null || Account.token.isEmpty())
                                ? "" : "Bearer " + Account.token)
                .when().delete("/BookStore/v1/Books?UserId=" + Account.userId);
    }

   

    @Then("the book JSON response status should be {int} within {long} ms")
    public void validateBookJsonResponse(int expectedStatus, long maxMs) {
        ResponseValidator.validateJsonResponse(Account.response, expectedStatus, maxMs, null);
        Hooks.actualStatusCode = Account.response.getStatusCode();
    }

    @Then("the book response JSON should contain field {string}")
    public void validateBookJsonFieldPresent(String jsonPath) {
        ResponseValidator.validateFieldPresent(Account.response, jsonPath, null);
    }
}
