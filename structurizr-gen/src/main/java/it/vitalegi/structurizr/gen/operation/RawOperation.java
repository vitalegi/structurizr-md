package it.vitalegi.structurizr.gen.operation;

import com.structurizr.Workspace;
import com.structurizr.export.Diagram;
import it.vitalegi.structurizr.gen.model.Configuration;
import it.vitalegi.structurizr.gen.model.Operation;
import it.vitalegi.structurizr.gen.service.DiagramC4PlantUmlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;

public class RawOperation extends AbstractOperation {

    Logger log = LoggerFactory.getLogger(getClass());

    DiagramC4PlantUmlService diagramService;

    public RawOperation(DiagramC4PlantUmlService diagramService) {

        super();
        this.diagramService = diagramService;
    }

    @Override
    public boolean accept(Configuration config) {
        return Operation.RAW == config.getOperation();
    }

    @Override
    public void process(Workspace workspace, Configuration config) {
        Collection<Diagram> diagrams = diagramService.exportDiagramsC4Plant(workspace);
        diagrams.forEach(d -> process(d, config));
    }

    void process(Diagram diagram, Configuration config) {
        log.info("Export diagram " + diagram.getKey());
        diagramService.saveAsSvg(Path.of(config.getOutputDirSvg()), diagram);
        diagramService.saveAsPng(Path.of(config.getOutputDirPng()), diagram);
    }
}
