package it.vitalegi.structurizr.gen;

import it.vitalegi.structurizr.gen.service.C4PlantUmlExporter;
import it.vitalegi.structurizr.gen.util.StructurizrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class GenerateImagesApp {

    private static final Logger log = LoggerFactory.getLogger(GenerateImagesApp.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Expected 2 arguments: path/to/file.dsl path/to/out/dir/");
        }
        var dsl = Path.of(args[0]);
        var mainDir = Path.of(args[1]);

        log.info("DSL:        {}", dsl);
        log.info("Output dir: {}", mainDir);

        var ws = StructurizrUtil.getWorkspace(dsl);
        new C4PlantUmlExporter().exportDiagramsC4Plant(ws, mainDir);
    }
}