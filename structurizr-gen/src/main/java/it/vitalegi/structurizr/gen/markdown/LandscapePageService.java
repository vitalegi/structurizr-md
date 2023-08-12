package it.vitalegi.structurizr.gen.markdown;

import it.vitalegi.structurizr.gen.model.MdContext;
import it.vitalegi.structurizr.gen.util.MarkdownUtil;
import it.vitalegi.structurizr.gen.util.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class LandscapePageService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected MdContext ctx;

    public LandscapePageService(MdContext ctx) {
        this.ctx = ctx;
    }

    public void createLandscapePage() {
        var filePath = ctx.getSystemLandscape();
        log.info("Create landscape page {}", filePath);
        try (var md = MarkdownUtil.init(filePath)) {
            addSystemLandscapeViews(md);
            softwareSystemsToc(md);
            landscapeSystemConnectivity(md);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    protected void addSystemLandscapeViews(MarkdownUtil md) {
        md.h1("Landscape");
        ctx.getSystemLandscapeViewsSorted().forEach(v -> {
            md.addViewImages(v, ctx, Path.of("."));
            md.println();
        });
    }

    protected void landscapeSystemConnectivity(MarkdownUtil md) {
        md.h2("Stats");
        md.h3("Software Systems");
        md.th("Software System", "# Containers", "# Components");
        ctx.getSoftwareSystemsSorted()
           .forEach(ss -> md.td(ss.getName(), ss.getContainers().size(), ss.getContainers().stream()
                                                                           .flatMap(c -> c.getComponents().stream())
                                                                           .count()));
        md.println();
        md.h3("Containers");
        md.th("Software System", "Container", "# Components");
        ctx.getSoftwareSystemsSorted().forEach(ss -> ctx.getContainersSorted(ss)
                                                        .forEach(c -> md.td(ss.getName(), c.getName(),
                                                                (long) c.getComponents()
                                                                                                               .size())));
        md.println();
        md.h2("Relations");
        md.th("Software System", "#");
        ctx.getSoftwareSystemsSorted().forEach(ss -> md.td(ss.getName(), ss.getRelationships().size()));
        md.println();
        md.th("Software System", "Container", "#");
        ctx.getSoftwareSystemsSorted().forEach(ss -> //
                ctx.getContainersSorted(ss) //
                   .forEach(container -> //
                           md.td(ss.getName(), container.getName(), container.getRelationships().size())));
        md.println();
        md.th("Software System", "Container", "Component", "#");
        ctx.getSoftwareSystemsSorted().forEach(ss -> //
                ctx.getContainersSorted(ss).forEach(container -> //
                        ctx.getComponentsSorted(container).forEach(component -> //
                                md.td(ss.getName(), container.getName(), component.getName(),
                                        component.getRelationships()
                                                                                                       .size()))));
        md.println();
    }

    protected void softwareSystemsToc(MarkdownUtil md) {
        md.h2("Software Systems");
        ctx.getSoftwareSystemsSorted().forEach(ss -> {
            md.print(" - ");
            md.mdLink(ss.getName(), UrlUtil.toUrl(ctx.getSoftwareSystemRelativePath(ss)));
            md.println();
        });
        md.println();
    }
}
