package it.vitalegi.structurizr.gen;

import com.structurizr.Workspace;
import it.vitalegi.structurizr.gen.service.WorkspaceFactory;
import it.vitalegi.structurizr.gen.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class GenerateBitbucketApp {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Mandatory arguments: dsl, outputDir");
        }
        String dsl = args[0];
        String outputDir = args[1];
        log.info("DSL:        {}", dsl);
        log.info("Output dir: {}", outputDir);

        var workspace = new WorkspaceFactory().create(dsl);

        var mainDir = Path.of(outputDir);
        FileUtil.createDirs(mainDir);

    }

    protected static void createSoftwareSystems(Path mainDir, Workspace workspace) {
        //workspace.getModel().getSoftwareSystems().stream().peek()
    }

}
