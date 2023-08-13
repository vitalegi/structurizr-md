package it.vitalegi.structurizr.gen.markdown;

import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.StaticView;
import com.structurizr.view.View;
import it.vitalegi.structurizr.gen.model.MdContext;
import it.vitalegi.structurizr.gen.util.MarkdownUtil;
import it.vitalegi.structurizr.gen.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class SoftwareSystemPage {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected MdContext ctx;

    public SoftwareSystemPage(MdContext ctx) {
        this.ctx = ctx;
    }

    public void softwareSystemPage(SoftwareSystem ss) {
        var filePath = ctx.getSoftwareSystemPath(ss);
        log.info("Found SoftwareSystem: id={}, name={}. File={}", ss.getId(), ss.getName(), filePath);
        createSoftwareSystemPage(filePath, ss);
    }

    protected boolean accept(StaticView view, SoftwareSystem system) {
        return view.getSoftwareSystemId().equals(system.getId());
    }

    protected boolean accept(DeploymentView view, SoftwareSystem system) {
        return view.getSoftwareSystemId().equals(system.getId());
    }

    protected void addDescription(MarkdownUtil md, View view) {
        if (StringUtil.isNotNullOrEmpty(view.getDescription())) {
            md.println("description: " + view.getDescription());
        }
    }

    protected void createSection(MarkdownUtil md, Stream<? extends View> views) {
        views.forEach(v -> createSection(md, v));
    }

    protected void createSection(MarkdownUtil md, View view) {
        md.h3(getName(view));
        addDescription(md, view);
        md.addViewImages(view, ctx, ctx.getSoftwareSystemPathToRoot());
    }

    protected void createSectionContainers(MarkdownUtil md, SoftwareSystem ss) {
        md.h2("Containers");
        createSection(md, ctx.getContainerViewsSorted().filter(v -> accept(v, ss)));
        md.println();
    }

    protected void createSectionDeployments(MarkdownUtil md, SoftwareSystem ss) {
        md.h2("Deployments");
        createSection(md, ctx.getDeploymentViewsSorted().filter(v -> accept(v, ss)));
        md.println();
    }

    protected void createSectionSystemComponents(MarkdownUtil md, SoftwareSystem ss) {
        md.h2("Component Diagrams");
        createSection(md, ctx.getComponentViewsSorted().filter(v -> accept(v, ss)));
        md.println();
    }

    protected void createSectionSystemContexts(MarkdownUtil md, SoftwareSystem ss) {
        md.h2("System Contexts");
        createSection(md, ctx.getSystemContextViewsSorted().filter(v -> accept(v, ss)));
        md.println();
    }

    protected void createSoftwareSystemPage(Path filePath, SoftwareSystem ss) {
        try (var md = MarkdownUtil.init(filePath)) {
            md.h1(ss.getName());

            createSectionSystemContexts(md, ss);
            createSectionContainers(md, ss);
            createSectionSystemComponents(md, ss);
            createSectionDeployments(md, ss);
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

}