package jbehave.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;

public class NumberReductionSteps {
    private int num;

    @Given("number $num")
    public void aNum(int num) {
        this.num = num;
    }

    @When("reducing $num")
    public void reducing(int num) {
        this.num -= num;
    }

    @Then("we should get $result")
    public void answer(int result) {
        Assert.assertEquals(num, result);
    }
}
