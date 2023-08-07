package it.vitalegi.structurizr.gen.cli;

import it.vitalegi.structurizr.gen.model.Configuration;

import java.util.Iterator;

public abstract class AbstractParamExtractorSingleValue implements ParamExtractor {

    String key;

    String sampleValue;

    public AbstractParamExtractorSingleValue(String key, String sampleValue) {
        this.key = key;
        this.sampleValue = sampleValue;
    }

    @Override
    public boolean accept(String name) {
        return key.equalsIgnoreCase(name);
    }

    @Override
    public void process(Iterator<String> it, Configuration config) {
        if (!it.hasNext()) {
            throw new IllegalArgumentException("key " + key + " expects a value");
        }
        var value = it.next();
        doProcess(config, value);
    }

    public String sample() {
        return key + ": " + sampleValue;
    }

    protected abstract void doProcess(Configuration config, String value);
}
