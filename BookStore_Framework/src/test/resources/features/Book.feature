#Author : Ganesh ,Sabarinathan,Joseph 
#Created On : 21-04-2026
#Module : Book

Feature: BookStore Book API Tests

  Background:
    Given The BookStore API base URL is set

  @BookStoreWebAPI01TC_18
  Scenario: Retrieve all books
    When I send a GET request to fetch all books
    Then the response status should be 200

  @BookStoreWebAPI01TC_19
  Scenario: Retrieve book with valid ISBN
    Given valid ISBN is available
    When I send a GET request to fetch book by ISBN
    Then the response status should be 200

  @BookStoreWebAPI01TC_20
  Scenario: Retrieve book with invalid ISBN
    Given invalid ISBN is prepared
    When I send a GET request to fetch book by ISBN
    Then the response status should be 400

  @BookStoreWebAPI01TC_21
  Scenario: Add book with valid ISBN and token
    Given valid token and ISBN are available
    When I send a POST request to add a book
    Then the response status should be 201

  @BookStoreWebAPI01TC_22
  Scenario: Add multiple books with valid token
    Given valid token and multiple ISBNs are available
    When I send a POST request to add multiple books
    Then the response status should be 201

  @BookStoreWebAPI01TC_23
  Scenario: Add book with invalid ISBN
    Given valid token and invalid ISBN are available
    When I send a POST request to add a book
    Then the response status should be 400

  @BookStoreWebAPI01TC_24
  Scenario:  Replace book with different valid ISBN
    Given valid token userId and ISBN are available
    When I send a PUT request to replace book
    Then the response status should be 200

  @BookStoreWebAPI01TC_25
  Scenario: Replace book with same ISBN 
    Given valid token userId and same ISBN are available
    When I send a PUT request to replace book
    Then the response status should be 409

  @BookStoreWebAPI01TC_26
  Scenario: Replace book with invalid ISBN
    Given valid token userId and invalid ISBN are available for update
    When I send a PUT request to replace book
    Then the response status should be 400

  @BookStoreWebAPI01TC_27
  Scenario: Replace book with invalid token
    Given invalid token with valid userId and ISBN are available
    When I send a PUT request to replace book
    Then the response status should be 401

  @BookStoreWebAPI01TC_28
  Scenario: Delete book with valid ISBN and token
    Given valid token userId and ISBN are available
    When I send a DELETE request to remove book
    Then the response status should be 204

  @BookStoreWebAPI01TC_29
  Scenario: Verify user exists after book deletion
    Given valid token userId and ISBN are available
    When I send a DELETE request to remove book
    And I verify the user still exists
    Then the response status should be 200

  @BookStoreWebAPI01TC_30
  Scenario: Delete book with invalid ISBN
    Given valid token userId and invalid ISBN are available for delete
    When I send a DELETE request to remove book
    Then the response status should be 400

  @BookStoreWebAPI01TC_31
  Scenario: Delete book second time 
    Given valid token userId and ISBN are available
    When I send a DELETE request to remove book
    And I send a DELETE request to remove book again
    Then the response status should be 404

  @BookStoreWebAPI01TC_32
  Scenario: Delete all books without token
    Given valid userId without token is available
    When I send a DELETE request to remove all books
    Then the response status should be 401

  @BookStoreWebAPI01TC_33
  Scenario: Delete all books with valid token
    Given valid token and userId are available
    When I send a DELETE request to remove all books
    Then the response status should be 204