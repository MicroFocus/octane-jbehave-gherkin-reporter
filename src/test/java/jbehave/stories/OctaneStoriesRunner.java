package jbehave.stories;

import com.alm.octane.OctaneGherkinReporter;
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
        System.out.println(featuresProperty);
        if (featuresProperty != null) {
            featureFilesPaths = Arrays.asList(featuresProperty.split(","));
        }
        String include = String.join(",", featureFilesPaths);
        return new StoryFinder().findPaths(
            codeLocationFromClass(embeddableClass),
            include,
            "");
    }

    // Here we specify the steps classes
    @Override
    public InjectableStepsFactory stepsFactory() {
        // varargs, can have more that one steps classes
        return new InstanceStepsFactory(
            configuration(),
            new NameValidationSteps(),
            new NotImplementedSteps(),
            new NumberAdditionSteps(),
            new NumberReductionSteps(),
            new StepFailedSteps()
        );
    }

}
