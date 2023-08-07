package it.vitalegi.structurizr.gen.constant;

public enum DotFormat {
    SVG("svg", "svg"), PNG("png", "png");

    private String cmd;
    private String fileExtension;

    private DotFormat(String cmd, String fileExtension) {
        this.cmd = cmd;
        this.fileExtension = fileExtension;
    }

    public String getCmd() {
        return cmd;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
