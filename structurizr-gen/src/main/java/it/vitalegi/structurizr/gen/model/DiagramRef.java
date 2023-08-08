package it.vitalegi.structurizr.gen.model;

import java.nio.file.Path;

public class DiagramRef {
    String key;
    Path path;

    String type;

    public DiagramRef() {
    }

    public DiagramRef(String key, Path path, String type) {
        this.key = key;
        this.path = path;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
