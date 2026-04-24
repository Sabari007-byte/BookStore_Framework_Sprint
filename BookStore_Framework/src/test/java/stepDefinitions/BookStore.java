package stepDefinitions;

import static io.restassured.RestAssured.given;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.ConfigReader;
import utils.ResponseValidator;

public class BookStore {

    static String isbn;
    static String secondIsbn;
    static final String invalidIsbn = "12345";
    static String replaceIsbn; // target ISBN for PUT body

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
