package it.vitalegi.structurizr.gen.operation;

import com.structurizr.Workspace;
import it.vitalegi.structurizr.gen.model.Configuration;
import it.vitalegi.structurizr.gen.service.DiagramService;

public abstract class AbstractOperation {
    DiagramService diagramService;

    public AbstractOperation(DiagramService diagramService) {
        this.diagramService = diagramService;
    }

    public abstract boolean accept(Configuration config);

    public abstract void process(Workspace workspace, Configuration config);

    public void setDiagramService(DiagramService diagramService) {
        this.diagramService = diagramService;
    }
}
