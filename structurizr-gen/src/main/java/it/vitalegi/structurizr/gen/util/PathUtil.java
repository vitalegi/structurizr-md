package it.vitalegi.structurizr.gen.util;

import java.nio.file.Path;

public class PathUtil {

    public static String toRelativeUrl(Path path) {
        return path.toString().replace("\\", "/");
    }
}
