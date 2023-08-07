package it.vitalegi.structurizr.gen.cli;

import it.vitalegi.structurizr.gen.model.Configuration;

import java.util.Iterator;

public interface ParamExtractor {
    boolean accept(String name);

    void process(Iterator<String> it, Configuration config);

    String sample();
}
