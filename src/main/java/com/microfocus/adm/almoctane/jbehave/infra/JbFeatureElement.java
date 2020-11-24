package com.microfocus.adm.almoctane.jbehave.infra;

import org.jbehave.core.model.Story;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.time.Instant;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class JbFeatureElement extends FeatureElement {
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




