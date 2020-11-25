# octane-jbehave-gherkin-reporter
ALM Octane JBehave Gherkin reporter enables uploading JBehave tests back into ALM Octane.

## How does it work
1.	You use this reporter in your JBehave tests (see instructions below).
2.	When running the tests, the reporter outputs files with the results.
3.	ALM Octane plugin for CI reads these files and uploads the results back to ALM Octane (see how to configure ALM Octane CI plugin in the [ALM Octane online help](https://admhelp.microfocus.com/octane/en/15.1.20/Online/Content/AdminGuide/how_config_CI_plugin.htm)).
4.	You can see the results in your Gherkin or BDD Scenario test in ALM Octane.

## Prerequisites:
* You are using Java language and the JBehave library to develop Gherkin tests.
* You are writing your tests in Gherkin syntax and use .feature files.

## How to configure octane-jbehave-gherkin-reporter in your project

### Enable reporting to Octane
1. Add a dependency in your pom file:
```xml
<dependencies>
    <dependency>
         <groupId>com.microfocus.adm.almoctane.jbehave</groupId>
         <artifactId>octane-jbehave-gherkin-reporter</artifactId>
         <version>1.0.0</version>
    </dependency>
</dependencies>
```

2. Add the OctaneGherkinReporter to your running configuration as a reporter (using the `withReports` method of `StoryReporterBuilder`) For example:
```java
public class OctaneStoriesRunner extends JUnitStories {
    private final Class<? extends Embeddable> embeddableClass = this.getClass();
    
    @Override
    public Configuration configuration() {
        String resultDir = "gherkin-results";
        return new MostUsefulConfiguration()
            .useStoryParser(new GherkinStoryParser())
            .useStoryLoader(new LoadFromClasspath(embeddableClass))
            .useStoryReporterBuilder(
                new StoryReporterBuilder()
                    .withReporters(new OctaneGherkinReporter(embeddableClass.getClassLoader(), resultDir))
            );
    }
}
```
OctaneGherkinReporter constructor accepts two input parameters:</br>
&nbsp;&nbsp;&nbsp;&nbsp;1. classLoader - your embedder class loader</br>
&nbsp;&nbsp;&nbsp;&nbsp;2. resultDir - (optional) a path to write the reports. If no path provided OctaneGherkinReporter will write the reports to `target/jbehave/octane`.</br>

### Enable executions with test runners
In addition to running test jobs by the CI you can use Octane's Test Runners to run specific feature files.</br>
In order to be able to run your JBehave tests from Octane using the testing framework your embedder should be able to receive a parameter that will indicate what feature files it should run.</br>
In your embedder override `storyPaths` in the following way:
```java
    @Override
    protected List<String> storyPaths() {
        String include = "**/*.feature";
        String featuresProperty = System.getProperty("features"); // -Dfeatures
        if (featuresProperty != null) {
            include = featuresProperty;
        }
        return new StoryFinder().findPaths(
            codeLocationFromClass(embeddableClass),include,"");
        }
```   
