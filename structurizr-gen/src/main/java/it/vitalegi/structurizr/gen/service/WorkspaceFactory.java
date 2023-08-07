package it.vitalegi.structurizr.gen.service;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;

import java.io.File;

public class WorkspaceFactory {
    public Workspace create(String dsl) {
        StructurizrDslParser parser = createParser(dsl);
        return parser.getWorkspace();
    }

    protected StructurizrDslParser createParser(String dsl) {
        StructurizrDslParser parser = new StructurizrDslParser();
        try {
            parser.parse(new File(dsl));
        } catch (StructurizrDslParserException e) {
            throw new RuntimeException(e);
        }
        return parser;
    }
}
