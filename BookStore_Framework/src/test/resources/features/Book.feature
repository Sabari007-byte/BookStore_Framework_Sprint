#Author : Ganesh , Joseph
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

  @BookStoreWebAPI01TC_23 @Invalid
  Scenario Outline: Add book with invalid ISBN
    Given valid token and ISBN "<isbn>" are available
    When I send a POST request to add a book
    Then the response status should be <expectedStatus>

    Examples:
      | isbn         | expectedStatus |
      | 12345        | 400            |
      | non-existent | 400            |

  @BookStoreWebAPI01TC_24 @Valid
  Scenario: Replace book with different valid ISBN
    Given valid user data is loaded from CSV file
    And user is created and token is generated from CSV
    And valid ISBN is available
    When I send a PUT request to replace book
    Then the response status should be 200
    And the response JSON should contain field "books"

  @BookStoreWebAPI01TC_25 @Invalid
  Scenario: Replace book with same ISBN
    Given valid token userId and same ISBN are available
    When I send a PUT request to replace book
    Then the response status should be 409

  @BookStoreWebAPI01TC_26 @Invalid
  Scenario Outline: Replace book with invalid data
    Given valid token userId and ISBN "<isbn>" are available for update
    When I send a PUT request to replace book
    Then the response status should be <expectedStatus>

    Examples:
      | isbn  | expectedStatus |
      | 12345 | 400            |
      |       | 400            |

  @BookStoreWebAPI01TC_27 @Invalid
  Scenario Outline: Replace book with invalid token
    Given token "<token>" and valid userId and ISBN are available
    When I send a PUT request to replace book
    Then the response status should be <expectedStatus>

    Examples:
      | token             | expectedStatus |
      | invalid_token_xyz | 401            |
      |                   | 401            |

  @BookStoreWebAPI01TC_28 @Valid
  Scenario: Delete book with valid ISBN and token
    Given valid user data is loaded from CSV file
    And user is created and token is generated from CSV
    And valid token userId and ISBN are available
    When I send a DELETE request to remove book
    Then the response status should be 204

  @BookStoreWebAPI01TC_29 @Valid
  Scenario: Verify user exists after book deletion
    Given valid user data is loaded from CSV file
    And user is created and token is generated from CSV
    And valid token userId and ISBN are available
    When I send a DELETE request to remove book
    And I verify the user still exists
    Then the response status should be 200
    And the response JSON should contain field "userId"
   