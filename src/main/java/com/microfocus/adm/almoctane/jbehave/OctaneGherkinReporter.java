/*
 * MIT License
 *
 * Copyright (c) 2020 Micro Focus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.microfocus.adm.almoctane.jbehave;

import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;
import org.jbehave.core.steps.StepCollector;

import java.nio.file.Path;
import java.util.Map;

/**
 * <p>
 * A thread-safe story reporter that outputs an Octane test result file as XML. It extends
 * {@link org.jbehave.core.reporters.NullStoryReporter}
 * </p>
 */
public class OctaneGherkinReporter extends NullStoryReporter {
    private final ThreadLocal<TestTracker> testTrackerHolder = new ThreadLocal<>();
    private final ClassLoader classLoader;
    private final Path resultDir;

    /**
     * Creates an {@code OctaneGherkinReporter} object
     *
     * @param classLoader Your embedder class loader
     * @param resultDir   (optional) A path to write the reports to. If no path provided {@code OctaneGherkinReporter} will write the reports to {@code target/jbehave/octane/}
     * @throws Exception If the specified classLoader is null
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

    /**
     * A JBehave event that occurs before a story is being executed
     *
     * @param story A story to run
     * @param givenStory A given story indicator
     */
    @Override
    public void beforeStory(Story story, boolean givenStory) {
        if (story.getScenarios().isEmpty()) {
            return;
        }
        testTrackerHolder.set(new TestTracker(classLoader, resultDir));
        testTrackerHolder.get().setCurrentFeature(story);
    }

    /**
     * A JBehave event that occurs before a scenario is being executed
     *
     * @param scenario A scenario to run
     */
    @Override
    public void beforeScenario(Scenario scenario) {
        testTrackerHolder.get().handleBeforeScenario(scenario);
    }

    /**
     * A JBehave event that occurs before a step is being executed
     *
     * @param step A step to run
     */
    @Override
    public void beforeStep(String step) {
        testTrackerHolder.get().setStepStarted();
    }

    /**
     * A JBehave event that occurs before a story/feature with parameters is being executed
     *
     * @param tableRow A map of parameters and values
     * @param exampleIndex A map of placeholders and values
     */
    @Override
    public void example(Map<String, String> tableRow, int exampleIndex) {
        testTrackerHolder.get().setScenarioWithParameters(tableRow, exampleIndex);
    }

    /**
     * A JBehave event that occurs after a step has been successfully executed
     *
     * @param step A successful step
     */
    @Override
    public void successful(String step) {
        testTrackerHolder.get().addStepToCurrentScenario(step, Constants.PASSED);
    }

    /**
     * A JBehave event that occurs after an unimplemented step has been discovered
     *
     * @param step An unimplemented step
     */
    @Override
    public void pending(String step) {
        testTrackerHolder.get().addStepToCurrentScenario(step, Constants.PENDING, "step not implemented");
    }

    /**
     * A JBehave event that occurs when a step is skipped
     *
     * @param step A skipped step
     */
    @Override
    public void notPerformed(String step) {
        testTrackerHolder.get().addStepToCurrentScenario(step, Constants.SKIPPED);
    }

    /**
     * A JBehave event that occurs after a step has been failed
     *
     * @param step A failed step
     */
    @Override
    public void failed(String step, Throwable cause) {
        testTrackerHolder.get().addStepToCurrentScenario(step, Constants.FAILED, cause);
    }
    /**
     * A JBehave event that occurs after a scenario was executed
     *
     * @param stage An execution stage
     */
    @Override
    public void afterScenarioSteps(StepCollector.Stage stage) {
        testTrackerHolder.get().addScenarioToCurrentFeature(stage);
    }

    /**
     * A JBehave event that occurs after a story/feature was executed
     *
     * @param givenStory A given story indicator
     */
    @Override
    public void afterStory(boolean givenStory) {
        TestTracker testTracker = testTrackerHolder.get();
        if (testTracker != null) {
            testTracker.generateXML();
        }
        testTrackerHolder.remove();
    }
}
