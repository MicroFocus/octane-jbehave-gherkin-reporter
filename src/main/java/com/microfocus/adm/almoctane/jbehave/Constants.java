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

class Constants {
    public static final String errorPrefix = "<OctaneJBehaveGherkinReporter Error> ";
    public static final String XML_VERSION = "1";
    public static final String TAG_ID1 = "TID";
    public static final String TAG_ID2 = "BSPID";

    public static final String LINE_SEPARATOR = System.lineSeparator();

    //Step statuses
    public static final String PASSED = "passed";
    public static final String FAILED = "failed";
    public static final String PENDING = "pending";
    public static final String SKIPPED = "skipped";

    public static final String UNIMPLEMENTED_STEP = "step not implemented";

    public static final String JB_LEFT_PARAM_CHAR = "\uFF5F";
    public static final String JB_RIGHT_PARAM_CHAR = "\uFF60";

    public static final String GHERKIN_LEFT_PARAM_CHAR = "<";
    public static final String GHERKIN_RIGHT_PARAM_CHAR = ">";


}
