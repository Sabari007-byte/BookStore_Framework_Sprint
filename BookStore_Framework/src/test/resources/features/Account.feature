#Author: ARISH
#Created On: 21-04-2026
#Module: Account

Feature: BookStore Account API Tests

  Background:
    Given The user API base URL is set

  @BookStoreWebAPI01TC_01
  Scenario: Create user with valid username and password
    Given valid user payload is prepared
    When I send a POST request to create a user
    Then the response status should be 201

  @BookStoreWebAPI01TC_02
  Scenario: Create user with existing username/password
    Given duplicate user payload is prepared
    When I send a POST request to create a user
    Then the response status should be 406

  @BookStoreWebAPI01TC_03
  Scenario: Create user with extra JSON body 
    Given user payload with extra fields is prepared
    When I send a POST request to create a user
    Then the response status should be 400


  @BookStoreWebAPI01TC_06
  Scenario: Generate token with valid credentials
    Given valid credentials are prepared
    When I send a POST request to generate token
    Then the response status should be 200

  @BookStoreWebAPI01TC_07
  Scenario: Generate token with invalid username
    Given invalid username is prepared
    When I send a POST request to generate token
    Then the response status should be 401


  @BookStoreWebAPI01TC_10
  Scenario: Validate authorization with valid token
    Given valid token is available
    When I send a POST request to authorize user
    Then the response status should be 200

  @BookStoreWebAPI01TC_11
  Scenario: Validate authorization with invalid token 
    Given invalid token is prepared
    When I send a POST request to authorize user
    Then the response status should be 401

  @BookStoreWebAPI01TC_13
  Scenario: Fetch user with valid UUID
    Given valid user ID is available
    When I send a GET request to fetch user
    Then the response status should be 200


  @BookStoreWebAPI01TC_15
  Scenario: Delete user with valid UUID and token
    Given valid user ID and token are available
    When I send a DELETE request to delete user
    Then the response status should be 204

 