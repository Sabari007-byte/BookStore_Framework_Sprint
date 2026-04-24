#Author : Ganesh 
#Created On : 24-04-2026
#Module : Book

Feature: BookStore Book API Tests

  Background:
    Given The BookStore API base URL is set

  @BookStoreWebAPI01TC_18 @Valid
  Scenario: Retrieve all books
    When I send a GET request to fetch all books
    Then the response status should be 200
    And the response JSON should contain field "books"

  @BookStoreWebAPI01TC_19 @Valid
  Scenario: Retrieve book with valid ISBN
    Given valid ISBN is available
    When I send a GET request to fetch book by ISBN
    Then the response status should be 200
    And the response JSON should contain field "isbn"
    And the response JSON should contain field "title"

  @BookStoreWebAPI01TC_20 @Invalid
  Scenario Outline: Retrieve book with invalid ISBN
    Given ISBN "<isbn>" is prepared
    When I send a GET request to fetch book by ISBN
    Then the response status should be <expectedStatus>

    Examples:
      | isbn    | expectedStatus |
      | 12345   | 400            |
      |         | 504            |
      | abc-xyz | 400            |

  @BookStoreWebAPI01TC_21 @Valid
  Scenario: Add book with valid ISBN and token
    Given valid user data is loaded from CSV file
    And user is created and token is generated from CSV
    And valid ISBN is available
    When I send a POST request to add a book
    Then the response status should be 201
    And the response JSON should contain field "books"

  @BookStoreWebAPI01TC_22 @Valid
  Scenario: Add multiple books with valid token
    Given valid user data is loaded from CSV file
    And user is created and token is generated from CSV
    And valid ISBN is available
    When I send a POST request to add multiple books
    Then the response status should be 201
    And the response JSON should contain field "books"

