package it.vitalegi.structurizr.gen;

import com.structurizr.Workspace;
import com.structurizr.view.View;
import it.vitalegi.structurizr.gen.markdown.LandscapePageService;
import it.vitalegi.structurizr.gen.markdown.SoftwareSystemPages;
import it.vitalegi.structurizr.gen.model.DiagramRef;
import it.vitalegi.structurizr.gen.model.DslContext;
import it.vitalegi.structurizr.gen.util.FileUtil;
import it.vitalegi.structurizr.gen.util.StructurizrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateMarkdownApp {

    private static final Logger log = LoggerFactory.getLogger(GenerateMarkdownApp.class);

    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Mandatory arguments: dsl, outputDir");
        }
        var dsl = Path.of(args[0]);
        var mainDir = Path.of(args[1]);

        log.info("DSL:        {}", dsl);
        log.info("Output dir: {}", mainDir);

        var workspace = StructurizrUtil.getWorkspace(dsl);

        FileUtil.createDirs(mainDir);
        var app = new GenerateMarkdownApp();
        var ctx = new DslContext();
        ctx.setImages(app.createImages(mainDir, workspace));

        new LandscapePageService().createLandscapePage(mainDir, workspace, ctx);
        new SoftwareSystemPages().softwareSystemPages(mainDir, workspace, ctx);
    }

    public List<DiagramRef> createImages(Path mainDir, Workspace workspace) {
        return workspace.getViews().getViews().stream().flatMap(v -> createImages(mainDir, workspace, v))
                        .collect(Collectors.toList());
    }


    protected Stream<DiagramRef> createImages(Path mainDir, Workspace workspace, View view) {
        var relativePathImages = Path.of("images");
        var tmp1 = new DiagramRef(view.getKey(), relativePathImages.resolve(view.getKey() + ".png"), "png");
        var tmp2 = new DiagramRef(view.getKey(), relativePathImages.resolve(view.getKey() + ".svg"), "svg");
        return Stream.of(tmp1, tmp2);
    }

}