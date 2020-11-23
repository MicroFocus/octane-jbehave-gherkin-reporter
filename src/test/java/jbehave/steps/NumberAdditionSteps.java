package jbehave.steps;

import org.junit.Assert;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;


public class NumberAdditionSteps {
    private int number;

    @Given("i know math")
    public void knowMath() {
    }

    @Then("i can calculate")
    public void canCalculate() {
    }

    @Given("a number with a value of $number")
    public void aNumber(int number) {
        this.number = number;
    }


    @When("we add $addition to the number")
    public void add(int addition) {
        number += addition;
    }

    @Then("the number value should be $result")
    public void theAlertStatusShouldBe(int result) {
        Assert.assertEquals(result, number);
    }
}
