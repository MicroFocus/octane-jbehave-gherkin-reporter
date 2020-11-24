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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

class FeatureElement implements GherkinSerializer {
    private String name = "";
    private String tag = "";
    private String path = "";
    private String fileContent = "";
    private Long started;
    private List<ScenarioElement> scenarios;

    public FeatureElement() {
        scenarios = new ArrayList<>();
    }

    public void addScenario(ScenarioElement scenario) {
        scenarios.add(scenario);
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileContent(String _fileContent) {
        this.fileContent = _fileContent;
    }

    public void setStarted(Long started) {
        this.started = started;
    }

    public Element toXMLElement(Document doc) {
        Element feature = doc.createElement(FEATURE_TAG_NAME);

        // Adding the feature members
        feature.setAttribute("name", name);
        feature.setAttribute("path", path);
        feature.setAttribute("tag", tag);
        if (started != null) {
            feature.setAttribute("started", started.toString());
        }

        // Adding the file to the feature
        Element fileElement = doc.createElement(FILE_TAG_NAME);
        fileElement.appendChild(doc.createCDATASection(fileContent));
        feature.appendChild(fileElement);

        Element scenariosElement = doc.createElement(SCENARIOS_TAG_NAME);

        // Serializing the scenarios
        for (ScenarioElement scenario : scenarios) {
            scenariosElement.appendChild(scenario.toXMLElement(doc));
        }

        feature.appendChild(scenariosElement);

        return feature;
    }
}

