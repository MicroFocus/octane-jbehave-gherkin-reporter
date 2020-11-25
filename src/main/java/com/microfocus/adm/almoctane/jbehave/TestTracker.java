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

class TestTracker {
    private final ClassLoader classLoader;
    private JbFeatureElement currentFeature;
    private ScenarioElement currentScenario;
    private String lastScenarioName;
    private long stepStarted = 0L;
    private final Path resultDir;


    public TestTracker(ClassLoader classLoader, Path resultDir) {
        this.classLoader = classLoader;
        if (resultDir == null) {
            this.resultDir = Paths.get("target", "jbehave", "octane");
        } else {
            this.resultDir = resultDir;
        }
    }

    public void setCurrentFeature(Story story) {
        if (currentFeature == null) {
            currentFeature = new JbFeatureElement(classLoader, story);
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
        long elapsedTime = Instant.now().toEpochMilli() - stepStarted;
        String sanitizedStepName = stepName.replaceAll(Constants.JB_LEFT_PARAM_CHAR + "|" + Constants.JB_RIGHT_PARAM_CHAR, "");
        StepElement step = new StepElement(sanitizedStepName);

        if (!exceptionMessage.isEmpty()) {
            step.setErrorMessage(exceptionMessage);
        }

        step.setStatus(status);
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
