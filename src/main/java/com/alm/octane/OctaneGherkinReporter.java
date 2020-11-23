package com.alm.octane;


import com.alm.octane.infra.TestTracker;
import com.alm.octane.infra.Constants;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;
import org.jbehave.core.steps.StepCollector;

import java.nio.file.Path;
import java.util.Map;


public class OctaneGherkinReporter extends NullStoryReporter {
    private final ThreadLocal<TestTracker> testTrackerHolder = new ThreadLocal<>();
    private final ClassLoader classLoader;
    private final Path resultDir;

    /**
     * Creates an {@code OctaneGherkinReporter} object.
     * @param classLoader your embedder class loader
     * @param resultDir (optional) a path to write the reports to. If no path provided {@code OctaneGherkinReporter} will write the reports to {@code target/jbehave/octane/}
     * @throws Exception if the specified classLoader is null
     */
    public OctaneGherkinReporter(ClassLoader classLoader, Path resultDir) throws Exception {
        if (classLoader == null) {
            throw new Exception("Embedder can not be null");
        }
        this.classLoader = classLoader;
        this.resultDir = resultDir;
    }

    public OctaneGherkinReporter(ClassLoader classLoader) throws Exception {
        this(classLoader, null);
    }

    @Override
    public void beforeStory(Story story, boolean givenStory) {
        if (story.getScenarios().isEmpty()) {
            return;
        }
        testTrackerHolder.set(new TestTracker(classLoader, resultDir));
        testTrackerHolder.get().setCurrentFeature(story);
    }

    @Override
    public void beforeScenario(Scenario scenario) {
        testTrackerHolder.get().handleBeforeScenario(scenario);
    }

    @Override
    public void beforeStep(String step) {
        testTrackerHolder.get().setStepStarted();
    }

    @Override
    public void example(Map<String, String> tableRow, int exampleIndex) {
        testTrackerHolder.get().setScenarioWithParameters(tableRow, exampleIndex);
    }

    @Override
    public void successful(String step) {
        testTrackerHolder.get().addStepToCurrentScenario(step, Constants.PASSED);
    }


    @Override
    public void pending(String step) {
        testTrackerHolder.get().addStepToCurrentScenario(step, Constants.PENDING, "step not implemented");
    }

    @Override
    public void notPerformed(String step) {
        testTrackerHolder.get().addStepToCurrentScenario(step, Constants.SKIPPED);
    }

    @Override
    public void failed(String step, Throwable cause) {
        testTrackerHolder.get().addStepToCurrentScenario(step, Constants.FAILED, cause);
    }

    @Override
    public void afterScenarioSteps(StepCollector.Stage stage) {
        testTrackerHolder.get().addScenarioToCurrentFeature(stage);
    }

    @Override
    public void afterStory(boolean givenStory) {
        TestTracker testTracker = testTrackerHolder.get();
        if (testTracker != null) {
            testTracker.generateXML();
        }
        testTrackerHolder.remove();
    }
}
