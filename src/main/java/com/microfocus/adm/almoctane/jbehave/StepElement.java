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