package it.vitalegi.structurizr.gen.util;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;

import java.io.File;
import java.nio.file.Path;

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
}
