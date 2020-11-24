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
 * Story reporter that outputs an Octane test result file as XML. It extends
 * {@link org.jbehave.core.reporters.NullStoryReporter}
 * </p>
 */
public class OctaneGherkinReporter extends NullStoryReporter {
    private final ThreadLocal<TestTracker> testTrackerHolder = new ThreadLocal<>();
    private final ClassLoader classLoader;
    private final Path resultDir;

    /**
     * Creates an {@code OctaneGherkinReporter} object.
     *
     * @param classLoader your embedder class loader
     * @param resultDir   (optional) a path to write the reports to. If no path provided {@code OctaneGherkinReporter} will write the reports to {@code target/jbehave/octane/}
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
