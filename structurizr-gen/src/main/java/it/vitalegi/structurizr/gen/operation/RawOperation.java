package it.vitalegi.structurizr.gen.operation;

import com.structurizr.Workspace;
import com.structurizr.export.Diagram;
import it.vitalegi.structurizr.gen.model.Configuration;
import it.vitalegi.structurizr.gen.model.Operation;
import it.vitalegi.structurizr.gen.service.DiagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class RawOperation extends AbstractOperation {

    Logger log = LoggerFactory.getLogger(getClass());

    public RawOperation(DiagramService diagramService) {
        super(diagramService);
    }

    @Override
    public boolean accept(Configuration config) {
        return Operation.RAW == config.getOperation();
    }

    @Override
    public void process(Workspace workspace, Configuration config) {
        Collection<Diagram> diagrams = diagramService.exportDiagrams(workspace);
        diagrams.forEach(d -> process(d, config));
    }

    void process(Diagram diagram, Configuration config) {
        log.info("Export diagram " + diagram.getKey());
        diagramService.saveAsSvg(config.getOutputDirSvg(), diagram);
        diagramService.saveAsPng(config.getOutputDirPng(), diagram);
    }
}
