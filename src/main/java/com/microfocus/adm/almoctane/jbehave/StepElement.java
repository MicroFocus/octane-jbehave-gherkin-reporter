package com.microfocus.adm.almoctane.jbehave;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


class StepElement implements GherkinSerializer {
    private String name;
    private String status = "";
    private Long duration = 0L;
    private String errorMessage = "";

    public StepElement(String name) {
        this.name = name;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setStatus(String status) {
        this.status = status.toLowerCase();
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Element toXMLElement(Document doc) {
        Element step = doc.createElement(STEP_TAG_NAME);

        step.setAttribute("name", name);
        step.setAttribute("status", status);

        String duration = this.duration != null ? this.duration.toString() : "0";
        step.setAttribute("duration", duration);

        if(errorMessage != null && !errorMessage.isEmpty()){
            Element errorElement = doc.createElement(ERROR_MESSAGE_TAG_NAME);
            errorElement.appendChild(doc.createCDATASection(errorMessage));
            step.appendChild(errorElement);
        }

        return step;
    }
}