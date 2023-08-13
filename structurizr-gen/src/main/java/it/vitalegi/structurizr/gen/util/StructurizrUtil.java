package it.vitalegi.structurizr.gen.util;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;

import java.nio.file.Path;
import java.util.Arrays;

public class StructurizrUtil {

    public static Workspace getWorkspace(Path dsl) {
        StructurizrDslParser parser = new StructurizrDslParser();
        try {
            parser.parse(dsl.toFile());
            return parser.getWorkspace();
        } catch (StructurizrDslParserException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sanitizeName(String name) {
        var forbidden = Arrays.asList("*", "?", "|", ":", "/", "\\", "%", "<", ">", "|", "?", "\"", "'", "(", ")",
                "[", "]", "$", "&");
        for (var i = 0; i < forbidden.size(); i++) {
            name = name.replace(forbidden.get(i), "_");
        }
        return name;
    }

}
