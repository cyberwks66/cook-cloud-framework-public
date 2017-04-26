Feature: Receive a JSON Greeting response
	In order to receive a greeting
	As a vain world
	I want to be greeted with my name

	Scenario: Call rest service to get a response by greeting
		Given HelloWorldController service is running
		When my name is "World"
		And I request a greeting for greeting: "Hello"
		Then I should get a response with 'Hello World'