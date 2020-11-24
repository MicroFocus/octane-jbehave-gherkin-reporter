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

import org.jbehave.core.model.Story;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.time.Instant;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

class JbFeatureElement extends FeatureElement {
    private final ClassLoader embedder;
    private String fileName;

    public JbFeatureElement(ClassLoader embedder, Story story) {
        super();
        this.embedder = embedder;
        setStarted(Instant.now().toEpochMilli());
        setFileName(story.getName());
        setName(story.getDescription().asString());
        setPath(story.getPath());
        setTag(story.getMeta().getPropertyNames());
        setFileContent(extractFileContent(getPath()));
    }

    public void setTag(Set<String> tagsSet) {
        for (String tag : tagsSet) {
            if (tag.startsWith(Constants.TAG_ID1) || tag.startsWith(Constants.TAG_ID2)) {
                super.setTag("@" + tag);
                break;
            }
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String extractFileContent(String relativeFilePath) {
        Scanner scanner = null;
        StringBuilder fileContent = new StringBuilder();
        try {
            //Assuming files are under test/resources
            URI path = embedder.getResource(relativeFilePath).toURI();
            scanner = new Scanner(new BufferedReader(new FileReader(new File(path))));
            //Line terminators to include
            Pattern pat = Pattern.compile(".*\\R|.+\\z");
            String line;
            while ((line = scanner.findWithinHorizon(pat, 0)) != null) {
                fileContent.append(line);
            }
        } catch (Exception e) {
            ErrorHandler.error("Cannot read feature file content", e);
        } finally {
            if (scanner != null) scanner.close();
        }
        return fileContent.toString();
    }
}




