package it.vitalegi.structurizr.gen;

import com.structurizr.Workspace;
import com.structurizr.view.View;
import it.vitalegi.structurizr.gen.markdown.LandscapePageService;
import it.vitalegi.structurizr.gen.markdown.SoftwareSystemPages;
import it.vitalegi.structurizr.gen.model.MdContext;
import it.vitalegi.structurizr.gen.util.FileUtil;
import it.vitalegi.structurizr.gen.util.StructurizrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class GenerateMarkdownApp {

    private static final Logger log = LoggerFactory.getLogger(GenerateMarkdownApp.class);

    MdContext ctx;

    public GenerateMarkdownApp(Path dsl, Path mainDir) {
        var ws = StructurizrUtil.getWorkspace(dsl);
        ctx = new MdContext(ws, mainDir);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Mandatory arguments: dsl, outputDir");
        }
        var dsl = Path.of(args[0]);
        var mainDir = Path.of(args[1]);

        log.info("DSL:        {}", dsl);
        log.info("Output dir: {}", mainDir);

        FileUtil.createDirs(mainDir);
        new GenerateMarkdownApp(dsl, mainDir).createMd();
    }

    public void createMd() {

        loadViews(ctx.getWorkspace());
        new LandscapePageService(ctx).createLandscapePage();
        new SoftwareSystemPages(ctx).softwareSystemPages();
    }

    protected void loadView(View view) {
        ctx.addImage(view.getKey(), "png");
        ctx.addImage(view.getKey(), "svg");
    }

    protected void loadViews(Workspace workspace) {
        workspace.getViews().getViews().stream().forEach(this::loadView);
    }

}