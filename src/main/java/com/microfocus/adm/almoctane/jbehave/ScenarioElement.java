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