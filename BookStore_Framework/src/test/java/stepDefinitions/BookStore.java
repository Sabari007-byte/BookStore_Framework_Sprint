package stepDefinitions;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class BookStore {

    Response response;
    String requestBody;

    String baseUrl = "https://bookstore.toolsqa.com";

    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6IkJvb2tTdG9yZTE2MDcyNTYiLCJwYXNzd29yZCI6IlBhc3N3b3JkIzEwIiwiaWF0IjoxNzc2NzQyMDQxfQ.Dc-pIPkuv-TIyP-mSoYjH6y4FVNdgaSrFRNZnCj5uwU";
    String userId = "96b6339e-2ec8-4d64-9b2d-bd99ae62ed8b";

    String originalToken = token;

    String isbn;
    String secondIsbn;
    String invalidIsbn = "12345";

    @Given("The BookStore API base URL is set")
    public void setBaseURL() {
        RestAssured.baseURI = baseUrl;
    }


    public void fetchISBNs() {
        Response res = given().when().get("/BookStore/v1/Books");

        isbn = res.jsonPath().getString("books[0].isbn");
        secondIsbn = res.jsonPath().getString("books[1].isbn");
    }

    public void cleanUserBooks() {
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/BookStore/v1/Books?UserId=" + userId);
    }

    public void addBookToUser() {

        requestBody = "{ \"userId\": \"" + userId + "\"," +
                "\"collectionOfIsbns\": [{ \"isbn\": \"" + isbn + "\" }] }";

        Response res = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
        .when()
                .post("/BookStore/v1/Books");

        if (res.getStatusCode() != 201) {
            throw new RuntimeException("Book not added. Status: " + res.getStatusCode());
        }
    }




    @Given("invalid ISBN is prepared")
    public void invalidISBN() {
        isbn = invalidIsbn;
    }

    @When("I send a GET request to fetch book by ISBN")
    public void getBookByISBN() {
        response = given()
                .queryParam("ISBN", isbn)
        .when()
                .get("/BookStore/v1/Book");
    }


    @Given("valid token and multiple ISBNs are available")
    public void validMultipleISBN() {
        fetchISBNs();
    }

    @Given("valid token and invalid ISBN are available")
    public void invalidISBNAdd() {
        isbn = invalidIsbn;
    }

    @When("I send a POST request to add a book")
    public void addBook() {

        requestBody = "{ \"userId\": \"" + userId + "\"," +
                "\"collectionOfIsbns\": [{ \"isbn\": \"" + isbn + "\" }] }";

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
        .when()
                .post("/BookStore/v1/Books");
    }

    @When("I send a POST request to add multiple books")
    public void addMultipleBooks() {

        requestBody = "{ \"userId\": \"" + userId + "\"," +
                "\"collectionOfIsbns\": [" +
                "{ \"isbn\": \"" + isbn + "\" }," +
                "{ \"isbn\": \"" + secondIsbn + "\" } ] }";

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
        .when()
                .post("/BookStore/v1/Books");
    }

  


    @Given("valid token userId and same ISBN are available")
    public void sameISBN() {
        setupForUpdate();
        secondIsbn = isbn;
    }

    @Given("valid token userId and invalid ISBN are available for update")
    public void invalidUpdate() {
        setupForUpdate();
        secondIsbn = invalidIsbn;
    }

    @Given("invalid token with valid userId and ISBN are available")
    public void invalidTokenUpdate() {
        setupForUpdate();
        token = "invalid_token";
    }

    @When("I send a PUT request to replace book")
    public void replaceBook() {

        requestBody = "{ \"userId\": \"" + userId + "\"," +
                "\"isbn\": \"" + secondIsbn + "\" }";

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
        .when()
                .put("/BookStore/v1/Books/" + isbn);

        token = originalToken; 
    }

   
    @When("I send a DELETE request to remove book")
    public void deleteBook() {

        requestBody = "{ \"isbn\": \"" + isbn + "\", \"userId\": \"" + userId + "\" }";

        response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
        .when()
                .delete("/BookStore/v1/Book");
    }
   
    @When("I send a DELETE request to remove book again")
    public void deleteAgain() {
        deleteBook();
    }

    @When("I verify the user still exists")
    public void verifyUserExists() {

        response = given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/Account/v1/User/" + userId);
    }

  
    @Given("valid userId without token is available")
    public void noToken() {
        token = "";
    }

    @Given("valid token and userId are available")
    public void validDeleteAll() {
        token = originalToken;
    }

    @When("I send a DELETE request to remove all books")
    public void deleteAllBooks() {

        response = given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete("/BookStore/v1/Books?UserId=" + userId);
    }


    @Then("the response status should be {int}")
    public void validateStatus(Integer expectedStatusCode) {

        System.out.println("Response:\n" + response.asPrettyString());

        assertEquals(response.getStatusCode(), expectedStatusCode.intValue());
    }
}