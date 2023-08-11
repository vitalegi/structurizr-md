package it.vitalegi.structurizr.gen.markdown;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.SystemLandscapeView;
import com.structurizr.view.View;
import it.vitalegi.structurizr.gen.model.DiagramRef;
import it.vitalegi.structurizr.gen.model.DslContext;
import it.vitalegi.structurizr.gen.util.FileUtil;
import it.vitalegi.structurizr.gen.util.MarkdownUtil;
import it.vitalegi.structurizr.gen.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LandscapePageService {

    private final Logger log = LoggerFactory.getLogger(getClass());


    public void createLandscapePage(Path mainDir, Workspace ws, DslContext ctx) {
        FileUtil.createDirs(mainDir);
        var filePath = mainDir.resolve("README.md");
        log.info("Create landscape page {}", filePath);
        try (var md = MarkdownUtil.init(filePath)) {
            md.h1("Landscape");
            ws.getViews().getSystemLandscapeViews().stream().sorted(Comparator.comparing(SystemLandscapeView::getName))
              .forEach(v -> {
                  addImages(md, v, ctx);
                  md.println();
              });
            landscapeSystemTOC(md, ws, ctx);
            landscapeSystemConnectivity(md, ws, ctx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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


    protected void addImage(MarkdownUtil md, DiagramRef dr) {
        md.image(dr.getKey(), PathUtil.toRelativeUrl(dr.getPath()));
        md.println();
        md.println();
    }

    protected void addImages(MarkdownUtil md, View view, DslContext ctx) {
        var diagramRefs = getViews(ctx, view);
        if (diagramRefs.isEmpty()) {
            return;
        }
        var png = diagramRefs.stream().filter(dr -> dr.getType().equals("png")).findFirst().orElse(null);

        if (png != null) {
            addImage(md, png);
            for (var i = 0; i < diagramRefs.size(); i++) {
                if (i > 0) {
                    md.print(" | ");
                }
                addLink(md, diagramRefs.get(i));
            }
            ;
        } else {
            diagramRefs.forEach(dr -> addImage(md, dr));
        }
        md.println();
    }

    protected void addLink(MarkdownUtil md, DiagramRef dr) {
        md.mdLink(dr.getType(), PathUtil.toRelativeUrl(dr.getPath()));
    }

    protected Stream<Component> getSortedComponents(Container c) {
        return sort(c.getComponents().stream());
    }

    protected Stream<Container> getSortedContainers(SoftwareSystem ss) {
        return sort(ss.getContainers().stream());
    }

    protected Stream<SoftwareSystem> getSortedSoftwareSystems(Workspace ws) {
        return sort(ws.getModel().getSoftwareSystems().stream());
    }

    protected List<DiagramRef> getViews(DslContext ctx, View view) {
        return ctx.getImages().stream().filter(dr -> accept(dr, view)).collect(Collectors.toList());
    }

    protected void landscapeSystemConnectivity(MarkdownUtil md, Workspace ws, DslContext ctx) {
        md.h2("Stats");
        md.h3("Software Systems");
        md.th("Software System", "# Containers", "# Components");
        getSortedSoftwareSystems(ws).forEachOrdered(ss -> md.td(ss.getName(), ss.getContainers()
                                                                                .size(), ss.getContainers().stream()
                                                                                           .flatMap(c -> c.getComponents()
                                                                                                          .stream())
                                                                                           .count()));
        md.println();
        md.h3("Containers");
        md.th("Software System", "Container", "# Components");
        getSortedSoftwareSystems(ws).forEachOrdered(ss -> getSortedContainers(ss).forEachOrdered(c -> md.td(ss.getName(), c.getName(), (long) c.getComponents()
                                                                                                                                               .size())));
        md.println();
        md.h2("Relations");
        md.th("Software System", "#");
        getSortedSoftwareSystems(ws).forEachOrdered(ss -> md.td(ss.getName(), "", ss.getRelationships().size()));
        md.println();
        md.th("Software System", "Container", "#");
        getSortedSoftwareSystems(ws).forEachOrdered(ss -> //
                getSortedContainers(ss) //
                                        .forEachOrdered(container -> //
                                                md.td(ss.getName(), container.getName(), container.getRelationships()
                                                                                                  .size())));
        md.println();
        md.th("Software System", "Container", "Component", "#");
        getSortedSoftwareSystems(ws).forEachOrdered(ss -> //
                getSortedContainers(ss).forEachOrdered(container -> //
                        getSortedComponents(container).forEachOrdered(component -> //
                                md.td(ss.getName(), container.getName(), component.getName(),
                                        component.getRelationships()
                                                                                                       .size()))));
        md.println();
    }

    protected void landscapeSystemTOC(MarkdownUtil md, Workspace ws, DslContext ctx) {
        md.h2("Software Systems");
        sort(ws.getModel().getSoftwareSystems().stream()).forEach(ss -> {
            md.print(" - ");
            var url = ss.getName() + ".md";
            md.mdLink(ss.getName(), url.replace(" ", "%20"));
            md.println();
        });
        md.println();
    }

    protected <E extends Element> Stream<E> sort(Stream<E> stream) {
        return stream.sorted(Comparator.comparing(E::getName).thenComparing(E::getId));
    }
}
