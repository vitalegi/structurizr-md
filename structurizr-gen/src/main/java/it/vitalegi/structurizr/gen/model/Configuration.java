package it.vitalegi.structurizr.gen.model;

public class Configuration {
    String dsl;
    Operation operation;
    String outputDirPng;
    String outputDirSvg;

    public String getDsl() {
        return dsl;
    }

    public void setDsl(String dsl) {
        this.dsl = dsl;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getOutputDirPng() {
        return outputDirPng;
    }

    public void setOutputDirPng(String outputDirPng) {
        this.outputDirPng = outputDirPng;
    }

    public String getOutputDirSvg() {
        return outputDirSvg;
    }

    public void setOutputDirSvg(String outputDirSvg) {
        this.outputDirSvg = outputDirSvg;
    }
}
