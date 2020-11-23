package jbehave.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;

public class NameValidationSteps {
    String myName;
    String otherName;

    @Given("My name is $name")
    public void myNameIs(String name) {
        this.myName = name;
    }

    @When("Someone calls me $name")
    public void setOtherName(String name) {
        this.otherName = name;
    }

    @Then("I should answer")
    public void iShouldAnswer(){
        Assert.assertEquals(myName, otherName);
    }

    @Then("I shouldn't answer")
    public void iShouldntAnswer(){
        Assert.assertFalse(myName.equals(otherName));
    }
}
