package com.microfocus.adm.almoctane.jbehave.infra;


import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.steps.StepCollector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;

public class TestTracker {
    private final ClassLoader embedder;
    private JbFeatureElement currentFeature;
    private ScenarioElement currentScenario;
    private String lastScenarioName;
    private long stepStarted = 0L;
    private final Path resultDir;


    public TestTracker(ClassLoader embedder, Path resultDir) {
        this.embedder = embedder;
        if (resultDir == null) {
            this.resultDir = Paths.get("target", "jbehave", "octane");
        } else {
            this.resultDir = resultDir;
        }
    }

    public void setCurrentFeature(Story story) {
        if (currentFeature == null) {
            currentFeature = new JbFeatureElement(embedder, story);
        }
    }

    public void handleBeforeScenario(Scenario scenario) {
        lastScenarioName = scenario.getTitle();
        setCurrentScenario(lastScenarioName, 0);
    }

    public void setScenarioWithParameters(Map<String, String> tableRow, int exampleIndex) {
        String scenarioNameWithValues = replaceParamsWithActualValues(lastScenarioName, tableRow);
        setCurrentScenario(scenarioNameWithValues, exampleIndex + 1);
    }

    public void addScenarioToCurrentFeature(StepCollector.Stage stage) {

        if (isStageValid(stage) && currentScenario != null) {
            currentFeature.addScenario(currentScenario);
            dismissCurrentScenario();
        }
    }


    public void addStepToCurrentScenario(String stepName, String status) {
        addStepToCurrentScenario(stepName, status, "");
    }

    public void addStepToCurrentScenario(String stepName, String status, Throwable cause) {
        addStepToCurrentScenario(stepName, status, getRootStackTrace(cause));
    }

    public void addStepToCurrentScenario(String stepName, String status, String exceptionMessage) {
        String sanitizedStepName = stepName.replaceAll(Constants.JB_LEFT_PARAM_CHAR + "|" + Constants.JB_RIGHT_PARAM_CHAR, "");
        StepElement step = new StepElement(sanitizedStepName);

        if (!exceptionMessage.isEmpty()) {
            step.setErrorMessage(exceptionMessage);
        }

        step.setStatus(status);
        long elapsedTime = Instant.now().toEpochMilli() - stepStarted;
        step.setDuration(elapsedTime);
        currentScenario.addStep(step);
    }

    public void generateXML() {
        if (currentFeature == null) {
            return;
        }
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = doc.createElement(GherkinSerializer.ROOT_TAG_NAME);
            rootElement.setAttribute("version", Constants.XML_VERSION);
            doc.appendChild(rootElement);

            Element featureElement = currentFeature.toXMLElement(doc);
            rootElement.appendChild(featureElement);

            URL resultFileUrl = buildUrlReportFileName();
            OutputFile out = new OutputFile(resultFileUrl);
            out.write(doc);
        } catch (Exception e) {
            ErrorHandler.error("Failed to write the result XML to the file system.", e);
        }
    }

    public void setStepStarted() {
        this.stepStarted = Instant.now().toEpochMilli();
    }

    private void setCurrentScenario(String title, Integer outlineIndex) {
        currentScenario = new ScenarioElement(title, outlineIndex);
    }

    private String getRootStackTrace(Throwable cause) {
        String[] stacktrace = ExceptionUtils.getRootCauseStackTrace(cause);
        StringBuilder sb = new StringBuilder();
        int stackTraceEntryLimitation = 20;
        for (int i = 0; i < stackTraceEntryLimitation; i++) {
            sb.append(stacktrace[i]).append(Constants.LINE_SEPARATOR);
        }
        if (stacktrace.length > stackTraceEntryLimitation) {
            sb.append("\t...");
        }
        return sb.toString();
    }

    private String replaceParamsWithActualValues(String scenarioName, Map<String, String> parameters) {
        String[] tokens = scenarioName.split(" ");
        for (String word : tokens) {
            if (word.matches(Constants.GHERKIN_LEFT_PARAM_CHAR + "(.*?)" + Constants.GHERKIN_RIGHT_PARAM_CHAR)) {
                String sanitizedWord = word.substring(1, word.length() - 1);
                String actualValue = parameters.get(sanitizedWord);
                if (actualValue == null) {
                    continue;
                }
                scenarioName = scenarioName.replace(word, actualValue);
            }
        }
        return scenarioName;
    }

    private boolean isStageValid(StepCollector.Stage stage) {
        return stage != null && stage.equals(StepCollector.Stage.AFTER);
    }

    private URL buildUrlReportFileName() throws MalformedURLException {
        String xmlFileName = currentFeature.getFileName().replace(".feature", ".xml");
        return new URL("file:" + resultDir + File.separator + xmlFileName);
    }

    private void dismissCurrentScenario() {
        currentScenario = null;
    }
}
