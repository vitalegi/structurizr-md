package it.vitalegi.structurizr.gen.markdown;

import it.vitalegi.structurizr.gen.model.MdContext;
import it.vitalegi.structurizr.gen.util.MarkdownUtil;
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

    protected void softwareSystemsToc(MarkdownUtil md) {
        md.h2("Software Systems");
        ctx.getSoftwareSystemsSorted().forEach(ss -> {
            md.print(" - ");
            md.mdLink(ss.getName(), ctx.getSoftwareSystemRelativePath(ss));
            md.println();
        });
        md.println();
    }

    protected void landscapeSystemConnectivity(MarkdownUtil md) {
        md.h2("Stats");
        md.h3("Software Systems");
        var table1 = md.table("Software System", "# Containers", "# Components");
        ctx.getSoftwareSystemsSorted()
           .forEach(ss -> table1.addRow(ss.getName(), ss.getContainers().size(), ctx.countComponents(ss)));
        table1.build();
        md.println();

        md.h3("Containers");
        var table2 = md.table("Software System", "Container", "# Components");
        ctx.forEachContainerSorted((ss, c) -> table2.addRow(ss.getName(), c.getName(), c.getComponents().size()));
        table2.build();
        md.println();

        md.h2("Relations");
        var table3 = md.table("Software System", "#");
        ctx.getSoftwareSystemsSorted().forEach(ss -> table3.addRow(ss.getName(), ss.getRelationships().size()));
        table3.build();
        md.println();

        var table4 = md.table("Software System", "Container", "#");
        ctx.forEachContainerSorted((ss, c) -> table4.addRow(ss.getName(), c.getName(), c.getRelationships().size()));

        table4.build();
        md.println();

        var table5 = md.table("Software System", "Container", "Component", "#");
        ctx.forEachContainerSorted((ss, c) -> ctx.getComponentsSorted(c).forEach(component -> //
                table5.addRow(ss.getName(), c.getName(), component.getName(), component.getRelationships().size())));

        table5.build();
        md.println();
    }
}
