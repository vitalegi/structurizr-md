package it.vitalegi.structurizr.gen.operation;

import com.structurizr.Workspace;
import it.vitalegi.structurizr.gen.model.Configuration;

public abstract class AbstractOperation {

    public abstract boolean accept(Configuration config);

    public abstract void process(Workspace workspace, Configuration config);
}
