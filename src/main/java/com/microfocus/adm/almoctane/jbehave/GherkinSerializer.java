package com.microfocus.adm.almoctane.jbehave;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

interface GherkinSerializer {
    String FEATURE_TAG_NAME = "feature";
    String ROOT_TAG_NAME = "features";
    String SCENARIO_TAG_NAME = "scenario";
    String SCENARIOS_TAG_NAME = "scenarios";
    String FILE_TAG_NAME = "file";
    String STEP_TAG_NAME = "step";
    String STEPS_TAG_NAME = "steps";
    String ERROR_MESSAGE_TAG_NAME = "error_message";

    Element toXMLElement(Document doc);

}
