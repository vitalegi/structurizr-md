package it.vitalegi.structurizr.md;

import com.structurizr.Workspace;
import com.structurizr.view.View;
import it.vitalegi.structurizr.md.markdown.ContainerPage;
import it.vitalegi.structurizr.md.markdown.LandscapePageService;
import it.vitalegi.structurizr.md.markdown.SoftwareSystemPage;
import it.vitalegi.structurizr.md.model.MdContext;
import it.vitalegi.structurizr.md.service.C4PlantUmlExporter;
import it.vitalegi.structurizr.md.service.ViewGenerator;
import it.vitalegi.structurizr.md.util.FileUtil;
import it.vitalegi.structurizr.md.util.StructurizrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class GenerateMarkdownApp {

    private static final Logger log = LoggerFactory.getLogger(GenerateMarkdownApp.class);

    MdContext ctx;

    public GenerateMarkdownApp(Path dsl, Path mainDir, boolean generateViews) {
        var ws = StructurizrUtil.getWorkspace(dsl);
        if (generateViews) {
            new ViewGenerator(ws).initDefaultViews();
        }
        ctx = new MdContext(ws, mainDir);
    }

    public GenerateMarkdownApp(MdContext ctx, boolean generateViews) {
        this.ctx = ctx;
        if (generateViews) {
            new ViewGenerator(ctx.getWorkspace()).initDefaultViews();
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Mandatory arguments: dsl, outputDir");
        }
        var dsl = Path.of(args[0]);
        var mainDir = Path.of(args[1]);
        var generateViews = true;
        if (args.length > 2) {
            generateViews = Boolean.parseBoolean(args[2]);
        }
        log.info("DSL:            {}", dsl);
        log.info("Output dir:     {}", mainDir);
        log.info("Generate views: {}", generateViews);
        FileUtil.createDirs(mainDir);
        var app = new GenerateMarkdownApp(dsl, mainDir, generateViews);
        app.createImages();
        app.createMd();
    }

    public void createImages() {
        new C4PlantUmlExporter().exportDiagramsC4Plant(ctx.getWorkspace(), ctx.getImagesRoot());
    }

    public void createMd() {

        loadViews(ctx.getWorkspace());
        new LandscapePageService(ctx).createPage();

        var ssp = new SoftwareSystemPage(ctx);
        ctx.getSoftwareSystemsSorted().forEach(ssp::createPage);

        var containerPage = new ContainerPage(ctx);
        ctx.getContainersSorted().forEach(containerPage::createPage);
    }

    protected void loadViews(Workspace workspace) {
        workspace.getViews().getViews().stream().forEach(this::loadView);
    }

    protected void loadView(View view) {
        ctx.addImage(view.getKey(), "png");
        ctx.addImage(view.getKey(), "svg");
    }

}