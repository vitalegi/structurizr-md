package it.vitalegi.structurizr.gen;

import com.structurizr.Workspace;
import com.structurizr.model.SoftwareSystem;
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

    protected boolean accept(DiagramRef diagramRef, View view) {
        if (diagramRef == null || diagramRef.getKey() == null) {
            return false;
        }
        if (view == null) {
            return false;
        }
        return diagramRef.getKey().equals(view.getKey());
    }

    protected void addImages(MarkdownUtil md, View view, DslContext ctx) {
        var diagramRefs = getViews(ctx, view);
        diagramRefs.stream().forEach(dr -> {
            md.println("image " + dr.getKey() + " -> " + dr.getPath());
        });
    }

    protected Stream<DiagramRef> createImages(Path mainDir, Workspace workspace, View view) {
        var tmp1 = new DiagramRef(view.getKey(), mainDir.resolve(view.getKey() + ".png"), "png");
        var tmp2 = new DiagramRef(view.getKey(), mainDir.resolve(view.getKey() + ".svg"), "svg");
        return Stream.of(tmp1, tmp2);
    }

    protected void createSectionContainers(MarkdownUtil md, Workspace workspace, SoftwareSystem softwareSystem,
                                           DslContext ctx) {
        md.h2("Containers");
        workspace.getViews().getContainerViews().stream().filter(v -> accept(v, softwareSystem)).forEach(view -> {
            md.h3(getName(view));
            safeAddDescription(md, view);
            addImages(md, view, ctx);
        });
        md.println();
    }

    protected void createSectionSystemContexts(MarkdownUtil md, Workspace workspace, SoftwareSystem ss,
                                               DslContext ctx) {
        md.h2("System Contexts");
        workspace.getViews().getSystemContextViews().stream().filter(v -> accept(v, ss)).forEach(view -> {
            md.h3(getName(view));
            safeAddDescription(md, view);
            addImages(md, view, ctx);
        });
        md.println();
    }

    protected void createSoftwareSystemPage(Path filePath, Workspace workspace, SoftwareSystem ss, DslContext ctx) {
        try (var md = MarkdownUtil.init(filePath)) {
            md.h1(ss.getName());
            //md.h2("TOC");

            createSectionSystemContexts(md, workspace, ss, ctx);
            createSectionContainers(md, workspace, ss, ctx);

            md.h2("Component diagrams");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getName(StaticView view) {
        if (StringUtil.isNotNullOrEmpty(view.getTitle())) {
            return view.getTitle();
        }
        return view.getName();
    }

    protected List<DiagramRef> getViews(DslContext ctx, View view) {
        return ctx.getImages().stream().filter(dr -> accept(dr, view)).collect(Collectors.toList());
    }

    protected void safeAddDescription(MarkdownUtil md, StaticView view) {
        if (StringUtil.isNotNullOrEmpty(view.getDescription())) {
            md.println("description: " + view.getDescription());
        }
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
