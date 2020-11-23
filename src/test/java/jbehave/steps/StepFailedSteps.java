package jbehave.steps;

import junit.framework.AssertionFailedError;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class StepFailedSteps {
    @Given("step should fail")
    public void stepShouldFail() {

    }

    @When("step fails")
    public void stepFails() {
        throw new AssertionError("expected:<2> but was:<5000>");
    }

    @Then("this should be skipped")
    public void shouldBeSkipped() {

    }
}
