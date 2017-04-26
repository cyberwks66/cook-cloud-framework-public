import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.security.Provider.Service;

public class StepDefinitions_Monday {

 //   private static final Provider NAME = "Redis";
    private static final String NOT = "not";

    private Service service;
    private boolean isWeekend;

    @Given("^There is a service$")
    public void There_is_a_service() throws Throwable {
   //     this.service = new Service(NAME);
    }

    @And("^it is (.*)after815AM$")
    public void it_is_weekend(String isOrIsNotWeekend) throws Throwable {
  //      this.isWeekend = !(NOT.equals(isOrIsNotWeekend));
    }
    @And("^it is (.*)after815AM$")
    public void it_is_Monday(String isOrIsNotWeekend) throws Throwable {
  //      this.isWeekend = !(NOT.equals(isOrIsNotWeekend));
    }
    

    @When("^the alarm rings$")
    public void the_alarm_rings() throws Throwable {
        //TODO: make some irritating noise...
    }

    @Then("^the service should (.*)get up and go to work$")
    public void the_service_should_get_up_and_go_to_work(String isOrIsNotWeekend) throws Throwable {
        final String expectedMessage;
  //      if (NOT.equals(isOrIsNotWeekend)){
  //          expectedMessage = NAME + " goes to work!";
        }
    //    else {
   //         expectedMessage = NAME + " does not go to work!";
   //     }

    //    String actualMessage = this.service.goToWork(this.isWeekend);
    //    Assert.assertEquals(expectedMessage, actualMessage);
    }
//}