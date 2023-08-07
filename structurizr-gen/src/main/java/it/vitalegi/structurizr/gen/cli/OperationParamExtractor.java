package it.vitalegi.structurizr.gen.cli;

import it.vitalegi.structurizr.gen.model.Configuration;
import it.vitalegi.structurizr.gen.model.Operation;

public class OperationParamExtractor extends AbstractParamExtractorSingleValue {

    public OperationParamExtractor(String key, String sampleValue) {
        super(key, sampleValue);
    }

    @Override
    protected void doProcess(Configuration config, String value) {
        var operation = Operation.valueOf(value);
        config.setOperation(operation);
    }
}
