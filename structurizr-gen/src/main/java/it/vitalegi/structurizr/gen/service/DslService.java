package it.vitalegi.structurizr.gen.service;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;

import java.io.File;

public class DslService {

    public Workspace getWorkspace(String dsl) {
        StructurizrDslParser parser = new StructurizrDslParser();
        try {
            parser.parse(new File(dsl));
        } catch (StructurizrDslParserException e) {
            throw new RuntimeException(e);
        }
        return parser.getWorkspace();
    }
}
