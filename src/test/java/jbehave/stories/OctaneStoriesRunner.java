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
package jbehave.stories;

import com.microfocus.adm.almoctane.jbehave.OctaneGherkinReporter;
import jbehave.steps.*;
import org.jbehave.core.Embeddable;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.parsers.gherkin.GherkinStoryParser;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

public class OctaneStoriesRunner extends JUnitStories {
    private final Class<? extends Embeddable> embeddableClass = this.getClass();
    private final Path resultDir;
    private List<String> featureFilesPaths;

    public OctaneStoriesRunner(Path resultDir, List<String> featureFilesPaths) {
        this(resultDir, featureFilesPaths, 1);
    }

    public OctaneStoriesRunner(Path resultDir, List<String> featureFilesPaths, int threads) {
        this.resultDir = resultDir;
        this.featureFilesPaths = featureFilesPaths;
        Embedder embedder = configuredEmbedder();
        embedder.embedderControls().useThreads(threads);
    }

    @Override
    public Configuration configuration() {
        try {
            return new MostUsefulConfiguration()
                .useStoryParser(new GherkinStoryParser())
                .useStoryLoader(new LoadFromClasspath(embeddableClass))
                .useStoryReporterBuilder(
                    new StoryReporterBuilder()
                        .withReporters(new OctaneGherkinReporter(embeddableClass.getClassLoader(), resultDir))
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected List<String> storyPaths() {
        String featuresProperty = System.getProperty("features"); //-Dfeatures
        if (featuresProperty != null) {
            featureFilesPaths = Arrays.asList(featuresProperty.split(","));
        }
        String include = String.join(",", featureFilesPaths);
        return new StoryFinder().findPaths(
            codeLocationFromClass(embeddableClass),
            include,
            "");
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(
            configuration(),
            new NameValidationSteps(),
            new NotImplementedSteps(),
            new NumberAdditionSteps(),
            new NumberReductionSteps(),
            new StepFailedSteps(),
            new ThreadedRunSteps()
        );
    }

}
