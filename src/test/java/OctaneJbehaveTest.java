import jbehave.stories.OctaneStoriesRunner;
import org.jbehave.core.junit.JUnitStories;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OctaneJbehaveTest {

    @Test
    public void testSingleFeatureFileRun() throws FileNotFoundException {
        JUnitStories runner = new OctaneStoriesRunner(
            null,
            Collections.singletonList("features/number_addition.feature"));
        runner.run();
        validate(Collections.singletonList("number_addition.xml"));
    }

    @Test
    public void testMultipleFeatureFilesRun() throws FileNotFoundException {
        JUnitStories runner = new OctaneStoriesRunner(
            null,
            Arrays.asList("features/number_addition.feature", "features/name validation.feature")
        );
        runner.run();
        validate(Arrays.asList("number_addition.xml", "name validation.xml"));
    }

    @Test
    public void testCustomizedResultsFolderRun() throws FileNotFoundException {
        String customizedFolderPath = "gherkin-results/a/b";
        JUnitStories runner = new OctaneStoriesRunner(
            Paths.get(customizedFolderPath),
            Collections.singletonList("features/number_addition.feature")
        );
        runner.run();
        validate(Collections.singletonList("number_addition.xml"), customizedFolderPath);
    }

    @Test
    public void testStepsNotImplemented() throws FileNotFoundException {
        JUnitStories runner = new OctaneStoriesRunner(
            null,
            Collections.singletonList("features/not_implemented.feature")
        );
        runner.run();
        validate(Collections.singletonList("not_implemented.xml"));
    }

    @Test
    public void testStepFailed() throws FileNotFoundException {
        JUnitStories runner = new OctaneStoriesRunner(
            null,
            Collections.singletonList("features/step_failed.feature")
        );
        try {
            runner.run();
        } catch (Exception ignored) {
            System.out.println("catch");
        }
        validate(Collections.singletonList("step_failed.xml"));
    }

    @Test
    public void testMultiThreadedRun() throws FileNotFoundException {
        JUnitStories multiThreadedRunner = new OctaneStoriesRunner(
            null,
            Arrays.asList("features/name validation.feature", "features/number_addition.feature","features/number_reduction.feature"),
            3
        );
        multiThreadedRunner.run();
        validate(Arrays.asList("name validation.xml","number_addition.xml", "number_reduction.xml"));
    }

    private void validate(List<String> resultFileNames) throws FileNotFoundException {
        validate(resultFileNames, "target/jbehave/octane");
    }

    private void validate(List<String> resultFileNames, String resultFolder) throws FileNotFoundException {

        for (String resultFileName : resultFileNames) {
            File expectedFile = Paths.get("src", "test", "resources", "expectedResults", resultFileName).toFile();
            String expectedXml = "";
            if (expectedFile.canRead()) {
                FileReader fileReader = new FileReader(expectedFile);
                BufferedReader expectedResultFileReader = new BufferedReader(fileReader);
                expectedXml = expectedResultFileReader.lines().collect(Collectors.joining());
            }


            BufferedReader actualResultFileReader = new BufferedReader(new FileReader(resultFolder + "/" + resultFileName));
            String actualXml = actualResultFileReader.lines().collect(Collectors.joining());

            validatePath(expectedXml, actualXml);

            expectedXml = expectedXml
                .replaceAll("\\s+", "");

            actualXml = actualXml
                .replaceAll(" duration=\"\\d*\"", "")
                .replaceAll(" started=\"\\d*\"", "")
                .replaceAll("\\s+", "");

            Assert.assertEquals(expectedXml, actualXml);
        }


    }

    private void validatePath(String expected, String actual) {
        int expectedPathStart = expected.indexOf("path=");
        int expectedPathEnd = expected.indexOf("\"", expectedPathStart + 7);
        String expectedPath = expected.substring(expectedPathStart + 6, expectedPathEnd);

        int actualPathStart = actual.indexOf("path=");
        int actualPathEnd = actual.indexOf("\"", actualPathStart + 7);
        String actualPath = actual.substring(actualPathStart, actualPathEnd);
        String actualPathSuffix = actualPath.substring(actualPath.length() - expectedPath.length());

        Assert.assertEquals("Path suffix not equal", expectedPath, actualPathSuffix);
    }
}