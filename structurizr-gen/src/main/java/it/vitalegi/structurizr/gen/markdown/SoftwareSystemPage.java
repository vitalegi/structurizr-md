package it.vitalegi.structurizr.gen.markdown;

import com.structurizr.model.Element;
import com.structurizr.model.Person;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.StaticView;
import com.structurizr.view.View;
import it.vitalegi.structurizr.gen.model.MdContext;
import it.vitalegi.structurizr.gen.util.MarkdownTable;
import it.vitalegi.structurizr.gen.util.MarkdownUtil;
import it.vitalegi.structurizr.gen.util.StringUtil;
import it.vitalegi.structurizr.gen.util.StructurizrUtil;
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

    protected void createSectionCalledBy(MarkdownUtil md, SoftwareSystem ss) {
        if (ctx.getRelationsToTree(ss).count() == 0) {
            return;
        }
        md.h3("Called by");
        relationshipTable(md, ctx.getRelationsToTree(ss));
    }


    protected void createSectionCalls(MarkdownUtil md, SoftwareSystem ss) {
        if (ctx.getRelationsFromTree(ss).count() == 0) {
            return;
        }
        md.h3("Calls");
        relationshipTable(md, ctx.getRelationsFromTree(ss));
    }

    protected void createSectionContainers(MarkdownUtil md, SoftwareSystem ss) {
        if (ctx.getContainersSorted(ss).count() == 0) {
            return;
        }
        var table = md.table("Container", "Description");
        ctx.getContainersSorted(ss).forEach(c -> table.addRow(c.getName(), c.getDescription()));
        table.build();
        md.println();
    }

    protected void createSectionContainersDiagrams(MarkdownUtil md, SoftwareSystem ss) {
        createSection(md, ctx.getContainerViewsSorted().filter(v -> accept(v, ss)));
        md.println();
    }

    protected void createSectionDeployments(MarkdownUtil md, SoftwareSystem ss) {
        md.h2("Deployments");
        createSection(md, ctx.getDeploymentViewsSorted().filter(v -> accept(v, ss)));
        md.println();
    }

    protected void createSectionSystemComponents(MarkdownUtil md, SoftwareSystem ss) {
        if (ctx.getComponentsSorted(ss).count() == 0) {
            return;
        }
        var table = md.table("Container", "Component", "Description");
        ctx.getComponentsSorted(ss).forEach(component -> {
            table.addRow(component.getContainer().getName(), component.getName(), component.getDescription());
        });
        table.build();
        md.println();
    }

    protected void createSectionSystemComponentsDiagrams(MarkdownUtil md, SoftwareSystem ss) {
        createSection(md, ctx.getComponentViewsSorted().filter(v -> accept(v, ss)));
        md.println();
    }

    protected void createSectionSystemContextsDiagrams(MarkdownUtil md, SoftwareSystem ss) {
        createSection(md, ctx.getSystemContextViewsSorted().filter(v -> accept(v, ss)));
        md.println();
    }

    protected void createSoftwareSystemPage(Path filePath, SoftwareSystem ss) {
        try (var md = MarkdownUtil.init(filePath)) {
            md.h1(ss.getName());

            md.h2("System Contexts");
            createSectionSystemContextsDiagrams(md, ss);
            md.h2("Containers");
            createSectionContainers(md, ss);
            createSectionContainersDiagrams(md, ss);
            md.h2("Components");
            createSectionSystemComponents(md, ss);
            createSectionSystemComponentsDiagrams(md, ss);
            md.h2("Relationships");
            createSectionCalledBy(md, ss);
            createSectionCalls(md, ss);

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

    protected void relationshipTable(MarkdownUtil md, Stream<Relationship> relationships) {
        var table = md.table("Source Software System", "Source Container", "Source Component", "Target Software " +
                "System", "Target Container", "Target Component", "Description");
        relationships.forEach(r -> relationshipTableRow(table, r));
        table.build();
        md.println();
    }

    protected void relationshipTableRow(MarkdownTable table, Relationship r) {
        var src = r.getSource();
        Element srcSS = StructurizrUtil.getParentSoftwareSystem(src);
        Element srcCT = StructurizrUtil.getParentContainer(src);
        Element srcCP = StructurizrUtil.getParentComponent(src);
        if (src instanceof Person) {
            srcSS = src;
            srcCT = null;
            srcCP = null;
        }
        var dest = r.getDestination();
        Element destSS = StructurizrUtil.getParentSoftwareSystem(dest);
        Element destCT = StructurizrUtil.getParentContainer(dest);
        Element destCP = StructurizrUtil.getParentComponent(dest);
        if (dest instanceof Person) {
            destSS = dest;
            destCT = null;
            destCP = null;
        }
        table.addRow(getNameOrDefault(srcSS), getNameOrDefault(srcCT), getNameOrDefault(srcCP),
                getNameOrDefault(destSS), getNameOrDefault(destCT), getNameOrDefault(destCP), r.getDescription());
    }

    private String getNameOrDefault(Element element) {
        if (element != null) {
            return element.getName();
        }
        return "";
    }

}