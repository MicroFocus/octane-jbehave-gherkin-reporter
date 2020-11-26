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
        }
        validate(Collections.singletonList("step_failed.xml"));
    }

    @Test
    public void testStepFailedOnScenarioOutline() throws FileNotFoundException {
        JUnitStories runner = new OctaneStoriesRunner(
            null,
            Collections.singletonList("features/failed_step_on_scenario_outline.feature")
        );
        try {
            runner.run();
        } catch (Exception ignored) {
        }
        validate(Collections.singletonList("failed_step_on_scenario_outline.xml"));
    }

    @Test
    public void testStepFailedOnBackground() throws FileNotFoundException {
        JUnitStories runner = new OctaneStoriesRunner(
            null,
            Collections.singletonList("features/failed_step_on_background.feature")
        );
        try {
            runner.run();
        } catch (Exception ignored) {
        }
        validate(Collections.singletonList("failed_step_on_background.xml"));
    }

    @Test
    public void testMultiThreadedRun() throws FileNotFoundException {
        JUnitStories runner = new OctaneStoriesRunner(
            null,
            Arrays.asList(
                "features/name validation.feature",
                "features/number_addition.feature",
                "features/number_reduction.feature",
                "features/threaded1.feature",
                "features/threaded2.feature",
                "features/threaded3.feature",
                "features/threaded4.feature"
            ),
            2
        );
        runner.run();
        validate(
            Arrays.asList(
                "name validation.xml",
                "number_addition.xml",
                "number_reduction.xml",
                "threaded1.xml",
                "threaded2.xml",
                "threaded3.xml",
                "threaded4.xml"
            )
        );
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
