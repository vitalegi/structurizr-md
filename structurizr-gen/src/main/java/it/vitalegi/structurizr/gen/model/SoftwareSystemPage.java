package it.vitalegi.structurizr.gen.model;

import java.nio.file.Path;

public class SoftwareSystemPage {
    String key;
    Path filePath;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
}
