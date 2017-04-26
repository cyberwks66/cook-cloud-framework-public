package com.cooksys.helloworld.rest;

import com.cooksys.cloud.helloworld.model.HelloResponse;
import com.cooksys.cloud.helloworld.rest.controllers.GreetingCircuitBreaker;
import com.cooksys.cloud.helloworld.rest.controllers.HelloWorldController;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class HelloWorldControllerTests {
    private HelloWorldController controller;
    private String name;
    private String greeting;
    private HelloResponse response;

    @Given("^HelloWorldController service is running$")
    public void helloworldcontroller_service_is_running() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        controller = new HelloWorldController();
        controller.setGreetingCircuitBreaker(new GreetingCircuitBreakerMock());
    }

    @When("^my name is \"(.*?)\"$")
    public void my_name_is_World(String name) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        this.name = name;

    }

    @When("^I request a greeting for greeting: \"(.*?)\"$")
    public void i_request_a_greeting_for_greeting_Hello(String greeting) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        this.greeting = greeting;
        controller.setName(name);
        response = controller.sayHello();
    }

    @Then("^I should get a response with 'Hello World'$")
    public void i_should_get_a_response_with_Hello_World() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        //assertTrue(response.getGreeting().equals(greeting) && response.getName().equals(name));
    }

    class GreetingCircuitBreakerMock implements GreetingCircuitBreaker {
        @Override
        public String getGreeting() {
            return greeting;
        }
    }

}