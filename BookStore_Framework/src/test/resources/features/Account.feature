#Author: ARISH ,VEERESHWAR
#Created On: 21-04-2026
#Module: Account

Feature: BookStore Account API Tests

 

  @BookStoreWebAPI01TC_01 @Valid
  Scenario: Create user with valid username and password
    Given valid user data is loaded from CSV file
    And user payload is prepared with "CSV_Name" and "CSV_pass"
    When I send a POST request to create a user
    Then the response status should be 201
    And the response JSON should contain field "userID"
    And the response JSON should contain field "username"

  @BookStoreWebAPI01TC_02 @Invalid
  Scenario: Create user with existing username/password
    Given duplicate user payload is prepared
    When I send a POST request to create a user
    Then the response status should be 406

  @BookStoreWebAPI01TC_03 @Invalid
  Scenario Outline: Create user with extra json body
    Given payload is prepared with "<username>" and "<password>" and "<mobile>"
    When I send a POST request to create a user
    Then the response status should be <expectedStatus>

    Examples:
      | username | password | expectedStatus |mobile|
      | user1    | Test@123 | 400            |234213|

  @BookStoreWebAPI01TC_04 @Invalid
  Scenario: Create user with space username
    Given empty user payload is prepared
    When I send a POST request to create a user
    Then the response status should be 406

  @BookStoreWebAPI01TC_05 @Invalid
  Scenario: Create user with invalid JSON body
    Given invalid JSON payload is prepared
    When I send a POST request to create a user
    Then the response status should be 400

  @BookStoreWebAPI01TC_06 @Valid
  Scenario: Generate token with valid credentials
    Given valid user data is loaded from CSV file
    And token request payload is prepared with "CSV" and "CSV"
    When I send a POST request to generate token
    Then the response status should be 200
    And the response JSON should contain field "token"
    And the response JSON should contain field "status"

  @BookStoreWebAPI01TC_07 @Invalid
  Scenario Outline: Generate token with invalid credentials
    Given token request payload is prepared with "<username>" and "<password>"
    When I send a POST request to generate token
    Then the response status should be <expectedStatus>

    Examples:
      | username  | password   | expectedStatus |
      | wrongUser | Test@123   | 401            |
      | user1677  | wrongPass  | 401            |
      | user1677  |            | 400            |

  @BookStoreWebAPI01TC_09 @Invalid
  Scenario: Generate token with extra field
    Given user payload with extra invalid field is prepared
    When I send a POST request to generate token
    Then the response status should be 400

  @BookStoreWebAPI01TC_10 @Valid
  Scenario: Validate authorization with valid token
    Given valid user data is loaded from CSV file
    And user is created and token is generated from CSV
    When I send a POST request to authorize user
    Then the response status should be 200

  @BookStoreWebAPI01TC_11 @Invalid
  Scenario Outline: Validate authorization with invalid/missing token
    Given token "<token>" is prepared for authorization
    When I send a POST request to authorize user
    Then the response status should be <expectedStatus>

    Examples:
      | token               | expectedStatus |
      | invalid_token_12345 | 401            |
      |                     | 401            |

  @BookStoreWebAPI01TC_13 @Valid
  Scenario: Fetch user with valid UUID
  Given previously created user is used
  When I send a GET request to fetch user
  Then the response status should be 200

  @BookStoreWebAPI01TC_14 @Invalid
  Scenario Outline: Fetch user with invalid UUID
    Given user ID "<userId>" is prepared
    When I send a GET request to fetch user
    Then the response status should be <expectedStatus>

    Examples:
      | userId                               | expectedStatus |
      | invalid-uuid-12345                   | 404            |
      | 00000000-0000-0000-0000-000000000000 | 404            |

  @BookStoreWebAPI01TC_15 @Valid
  Scenario: Delete user with valid UUID and token
  Given previously created user is used
  When I send a DELETE request to delete user
  Then the response status should be 204

  @BookStoreWebAPI01TC_16 @Invalid
  Scenario: Delete user with invalid UUID
    Given invalid user ID is prepared
    When I send a DELETE request to delete user
    Then the response status should be 404

  @BookStoreWebAPI01TC_17 @Invalid
  Scenario: Delete user without token
    Given no token is provided
    When I send a DELETE request to delete user
    Then the response status should be 401

