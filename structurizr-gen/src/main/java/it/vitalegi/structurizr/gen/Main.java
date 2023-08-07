package it.vitalegi.structurizr.gen;

import it.vitalegi.structurizr.gen.cli.ConfigurationLoader;
import it.vitalegi.structurizr.gen.operation.AbstractOperation;
import it.vitalegi.structurizr.gen.operation.GraphvizOperation;
import it.vitalegi.structurizr.gen.operation.RawOperation;
import it.vitalegi.structurizr.gen.service.DiagramC4PlantUmlService;
import it.vitalegi.structurizr.gen.service.DiagramDotService;
import it.vitalegi.structurizr.gen.service.WorkspaceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        log.info("Start");
        var config = ConfigurationLoader.create(args).process();
        log.info("Config: DSL={}, operation={}, output-dir-png={}, output-dir-svg={}", config.getDsl(),
                config.getOperation(), config.getOutputDirPng(), config.getOutputDirSvg());
        var workspace = new WorkspaceFactory().create(config.getDsl());
        var operation = operations().stream().filter(o -> o.accept(config)).findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("Can't handle request"));
        operation.process(workspace, config);


    }

    static List<AbstractOperation> operations() {
        return Arrays.asList(new RawOperation(new DiagramC4PlantUmlService()),
                new GraphvizOperation(new DiagramDotService()));
    }
}