package it.vitalegi.structurizr.gen;

import com.structurizr.Workspace;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.StaticView;
import com.structurizr.view.View;
import it.vitalegi.structurizr.gen.model.DiagramRef;
import it.vitalegi.structurizr.gen.model.DslContext;
import it.vitalegi.structurizr.gen.model.SoftwareSystemPage;
import it.vitalegi.structurizr.gen.service.WorkspaceFactory;
import it.vitalegi.structurizr.gen.util.FileUtil;
import it.vitalegi.structurizr.gen.util.MarkdownUtil;
import it.vitalegi.structurizr.gen.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateMarkdownApp {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Mandatory arguments: dsl, outputDir");
        }
        String dsl = args[0];
        var mainDir = Path.of(args[1]);

        log.info("DSL:        {}", dsl);
        log.info("Output dir: {}", mainDir);

        var workspace = new WorkspaceFactory().create(dsl);

        FileUtil.createDirs(mainDir);
        var app = new GenerateMarkdownApp();
        var ctx = new DslContext();
        ctx.setImages(app.createImages(mainDir, workspace));

        app.softwareSystemPages(mainDir, workspace, ctx);
    }

    public List<DiagramRef> createImages(Path mainDir, Workspace workspace) {
        return workspace.getViews().getViews().stream().flatMap(v -> createImages(mainDir, workspace, v))
                        .collect(Collectors.toList());
    }

    public List<SoftwareSystemPage> softwareSystemPages(Path mainDir, Workspace workspace, DslContext ctx) {
        return workspace.getModel().getSoftwareSystems().stream()
                        .map(ss -> softwareSystemPage(mainDir, workspace, ss, ctx)).collect(Collectors.toList());
    }

    protected boolean accept(StaticView view, SoftwareSystem system) {
        return view.getSoftwareSystemId().equals(system.getId());
    }

    protected boolean accept(DeploymentView view, SoftwareSystem system) {
        return view.getSoftwareSystemId().equals(system.getId());
    }


    protected boolean accept(DiagramRef diagramRef, View view) {
        if (diagramRef == null || diagramRef.getKey() == null) {
            return false;
        }
        if (view == null) {
            return false;
        }
        return diagramRef.getKey().equals(view.getKey());
    }

    protected void addDescription(MarkdownUtil md, View view) {
        if (StringUtil.isNotNullOrEmpty(view.getDescription())) {
            md.println("description: " + view.getDescription());
        }
    }

    protected void addImages(MarkdownUtil md, View view, DslContext ctx) {
        var diagramRefs = getViews(ctx, view);
        diagramRefs.stream().forEach(dr -> {
            md.println("image " + dr.getKey() + " -> " + dr.getPath());
            md.image(dr.getKey(), dr.getPath().toString());
            md.println();
        });
    }

    protected Stream<DiagramRef> createImages(Path mainDir, Workspace workspace, View view) {
        var relativePathImages = Path.of("..", "..", "images");
        var tmp1 = new DiagramRef(view.getKey(), relativePathImages.resolve(view.getKey() + ".png"), "png");
        var tmp2 = new DiagramRef(view.getKey(), relativePathImages.resolve(view.getKey() + ".svg"), "svg");
        return Stream.of(tmp1, tmp2);
    }

    protected void createSection(MarkdownUtil md, Stream<? extends View> views, DslContext ctx) {
        views.forEach(v -> createSection(md, v, ctx));
    }

    protected void createSection(MarkdownUtil md, View view, DslContext ctx) {
        md.h3(getName(view));
        addDescription(md, view);
        addImages(md, view, ctx);
    }

    protected void createSectionContainers(MarkdownUtil md, Workspace workspace, SoftwareSystem ss, DslContext ctx) {
        md.h2("Containers");
        createSection(md, workspace.getViews().getContainerViews().stream().filter(v -> accept(v, ss)), ctx);
        md.println();
    }

    protected void createSectionDeployments(MarkdownUtil md, Workspace workspace, SoftwareSystem ss, DslContext ctx) {
        md.h2("Deployments");
        createSection(md, workspace.getViews().getDeploymentViews().stream().filter(v -> accept(v, ss)), ctx);
        md.println();
    }

    protected void createSectionSystemComponents(MarkdownUtil md, Workspace workspace, SoftwareSystem ss,
                                                 DslContext ctx) {
        md.h2("Component Diagrams");
        createSection(md, workspace.getViews().getComponentViews().stream().filter(v -> accept(v, ss)), ctx);
        md.println();
    }

    protected void createSectionSystemContexts(MarkdownUtil md, Workspace workspace, SoftwareSystem ss,
                                               DslContext ctx) {
        md.h2("System Contexts");
        createSection(md, workspace.getViews().getSystemContextViews().stream().filter(v -> accept(v, ss)), ctx);
        md.println();
    }

    protected void createSoftwareSystemPage(Path filePath, Workspace workspace, SoftwareSystem ss, DslContext ctx) {
        try (var md = MarkdownUtil.init(filePath)) {
            md.h1(ss.getName());
            //md.h2("TOC");

            createSectionSystemContexts(md, workspace, ss, ctx);
            createSectionContainers(md, workspace, ss, ctx);
            createSectionSystemComponents(md, workspace, ss, ctx);
            createSectionDeployments(md, workspace, ss, ctx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getName(View view) {
        if (StringUtil.isNotNullOrEmpty(view.getTitle())) {
            return view.getTitle();
        }
        return view.getName();
    }

    protected List<DiagramRef> getViews(DslContext ctx, View view) {
        return ctx.getImages().stream().filter(dr -> accept(dr, view)).collect(Collectors.toList());
    }

    protected SoftwareSystemPage softwareSystemPage(Path mainDir, Workspace workspace, SoftwareSystem ss,
                                                    DslContext ctx) {
        var page = new SoftwareSystemPage();

        var softwareSystemsDir = mainDir.resolve("software-systems");
        FileUtil.createDirs(softwareSystemsDir);
        var filePath = softwareSystemsDir.resolve(ss.getName() + ".md");
        page.setKey(ss.getId());
        page.setFilePath(filePath);
        log.info("Found SoftwareSystem: id={}, name={}. File={}", ss.getId(), ss.getName(), filePath);
        createSoftwareSystemPage(filePath, workspace, ss, ctx);
        return page;
    }
}
