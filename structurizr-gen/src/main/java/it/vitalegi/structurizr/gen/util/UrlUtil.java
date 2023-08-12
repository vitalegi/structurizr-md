package it.vitalegi.structurizr.gen.util;

import java.nio.file.Path;

public class UrlUtil {

    public static String toUrl(Path path) {
        return path.toString().replace("\\", "/").replace(" ", "%20");
    }
}
