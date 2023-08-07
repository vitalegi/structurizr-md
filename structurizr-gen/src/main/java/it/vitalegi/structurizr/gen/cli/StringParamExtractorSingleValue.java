package it.vitalegi.structurizr.gen.cli;

import it.vitalegi.structurizr.gen.model.Configuration;

import java.util.function.BiConsumer;

public class StringParamExtractorSingleValue extends AbstractParamExtractorSingleValue {

    BiConsumer<Configuration, String> consumer;

    public StringParamExtractorSingleValue(String key, String sampleValue, BiConsumer<Configuration, String> consumer) {
        super(key, sampleValue);
        this.consumer = consumer;
    }

    @Override
    protected void doProcess(Configuration config, String value) {
        consumer.accept(config, value);
    }
}
