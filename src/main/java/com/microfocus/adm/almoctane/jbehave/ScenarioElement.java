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

/**
 * Created by intract on 23/06/2016.
 */
class ScenarioElement implements GherkinSerializer {
    private final String name;
    private final List<StepElement> steps = new ArrayList<>();
    private final Integer outlineIndex;

    public ScenarioElement(String name, int outlineIndex) {
        this.name = name;
        this.outlineIndex = outlineIndex;
    }

    public void addStep(StepElement step) {
        steps.add(step);
    }


    public Element toXMLElement(Document doc) {
        // Adding the feature members
        Element scenario = doc.createElement(SCENARIO_TAG_NAME);
        scenario.setAttribute("name", name);
        if (outlineIndex > 0) {
            scenario.setAttribute("outlineIndex", outlineIndex.toString());
        }

        // Serializing the steps
        Element steps = doc.createElement(STEPS_TAG_NAME);
        for (StepElement step : this.steps) {
            steps.appendChild(step.toXMLElement(doc));
        }

        scenario.appendChild(steps);

        return scenario;
    }
}