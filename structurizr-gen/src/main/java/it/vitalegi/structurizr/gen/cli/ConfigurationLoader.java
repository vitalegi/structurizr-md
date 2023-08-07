package it.vitalegi.structurizr.gen.cli;

import it.vitalegi.structurizr.gen.model.Configuration;
import it.vitalegi.structurizr.gen.model.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ConfigurationLoader {
    Logger log = LoggerFactory.getLogger(getClass());
    String[] args;
    List<ParamExtractor> extractors;

    protected ConfigurationLoader(String[] args, List<ParamExtractor> extractors) {
        this.args = args;
        this.extractors = extractors;
    }

    public static ConfigurationLoader create(String[] args) {
        var extractors = extractors();
        return new ConfigurationLoader(args, extractors);
    }

    protected static List<ParamExtractor> extractors() {
        return Arrays.asList(new StringParamExtractorSingleValue("--dsl", "workspace.dsl", Configuration::setDsl),
                new StringParamExtractorSingleValue("--out-dir-png", "export/png/", Configuration::setOutputDirPng),
                new StringParamExtractorSingleValue("--out-dir-svg", "export/svg/", Configuration::setOutputDirSvg),
                new OperationParamExtractor("--o", "RAW"));
    }

    public Configuration process() {
        try {
            Configuration config = initConfiguration();
            var it = Arrays.stream(args).iterator();
            while (it.hasNext()) {
                var key = it.next();
                var extractor = extractors.stream().filter(e -> e.accept(key)).findFirst()
                                          .orElseThrow(() -> new IllegalArgumentException("Key " + key + " is " +
                                                  "unknown"));
                extractor.process(it, config);
            }
            validate(config);
            return config;
        } catch (Exception e) {
            log.info("Acceptable values:");
            extractors.stream().map(ParamExtractor::sample).forEach(sample -> log.info(" {}", sample));
            throw e;
        }
    }

    protected void failIfEmpty(String name, String str) {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException("Field " + name + " is mandatory");
        }
    }

    protected Configuration initConfiguration() {
        var config = new Configuration();
        config.setOperation(Operation.RAW);
        config.setOutputDirPng("export/png/");
        config.setOutputDirSvg("export/svg/");
        return config;
    }

    protected void validate(Configuration config) {
        if (config.getOperation() == null) {
            throw new IllegalArgumentException("Operation is mandatory");
        }
        failIfEmpty("--dsl", config.getDsl());
        failIfEmpty("--out-dir-png", config.getOutputDirPng());
        failIfEmpty("--out-dir-svg", config.getOutputDirSvg());
    }
}
