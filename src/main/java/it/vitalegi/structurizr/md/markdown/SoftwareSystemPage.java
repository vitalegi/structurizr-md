package it.vitalegi.structurizr.md.markdown;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Person;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ModelView;
import com.structurizr.view.View;
import it.vitalegi.structurizr.md.model.MdContext;
import it.vitalegi.structurizr.md.util.MarkdownTable;
import it.vitalegi.structurizr.md.util.MarkdownUtil;
import it.vitalegi.structurizr.md.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoftwareSystemPage {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected MdContext ctx;

    public SoftwareSystemPage(MdContext ctx) {
        this.ctx = ctx;
    }

    public void createPage(SoftwareSystem ss) {
        var filePath = ctx.getSoftwareSystemPath(ss);
        log.info("Found SoftwareSystem: id={}, name={}. File={}", ss.getId(), ss.getName(), filePath);
        createPage(filePath, ss);
    }

    protected boolean accept(ModelView view, SoftwareSystem system) {
        if (view.getSoftwareSystemId() == null) {
            return false;
        }
        return view.getSoftwareSystemId().equals(system.getId());
    }

    protected void addDescription(MarkdownUtil md, View view) {
        if (StringUtil.isNotNullOrEmpty(view.getDescription())) {
            md.println("Description: " + view.getDescription());
            md.println();
        }
    }

    protected void createPage(Path filePath, SoftwareSystem ss) {
        try (var md = MarkdownUtil.init(filePath)) {
            md.h1(ss.getName());

            md.h2("System Contexts");
            createSectionSystemContextsDiagrams(md, ss);
            md.h2("Containers");
            createSectionContainers(md, ss);
            createSectionContainersDiagrams(md, ss);
            md.h2("Relationships");
            createSectionUsedBy(md, ss);
            createSectionUses(md, ss);

            createSectionDeployments(md, ss);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    protected void createSectionSystemContextsDiagrams(MarkdownUtil md, SoftwareSystem ss) {
        createSection(md, ctx.getSystemContextViewsSorted().filter(v -> accept(v, ss)));
        md.println();
    }

    protected void createSectionUsedBy(MarkdownUtil md, SoftwareSystem softwareSystem) {
        var calledBy = ctx.getRelationships(r -> ctx.isDestination(r, softwareSystem)).collect(Collectors.toList());
        if (calledBy.isEmpty()) {
            return;
        }
        md.h3("Used By");
        relationshipTable(md, calledBy, Relationship::getSource);
    }

    protected void createSectionUses(MarkdownUtil md, SoftwareSystem softwareSystem) {
        var calledBy = ctx.getRelationships(r -> ctx.isSource(r, softwareSystem)).collect(Collectors.toList());
        if (calledBy.isEmpty()) {
            return;
        }
        md.h3("Uses");
        relationshipTable(md, calledBy, Relationship::getDestination);
    }

    protected String getName(View view) {
        if (StringUtil.isNotNullOrEmpty(view.getTitle())) {
            return view.getTitle();
        }
        return view.getName();
    }

    protected void relationshipTable(MarkdownUtil md, List<Relationship> relationships, Function<Relationship,
            Element> other) {
        var table = md.table("Element", "Description", "Tags");
        relationships.forEach(r -> relationshipTableRow(md, table, other.apply(r)));
        table.build();
        md.println();
    }

    protected void relationshipTableRow(MarkdownUtil md, MarkdownTable table, Element element) {
        if (element instanceof Person) {
            relationshipTableRowPerson(md, table, (Person) element);
        } else if (element instanceof Component) {
            relationshipTableRowComponent(md, table, (Component) element);
        } else if (element instanceof Container) {
            relationshipTableRowContainer(md, table, (Container) element);
        } else if (element instanceof SoftwareSystem) {
            relationshipTableRowSoftwareSystem(md, table, (SoftwareSystem) element);
        } else {
            table.addRow(element.getName(), element.getDescription(), element.getTags());
        }
    }

    protected void relationshipTableRowComponent(MarkdownUtil md, MarkdownTable table, Component e) {
        var path = ctx.getContainerPathToRoot().resolve(ctx.getContainerRelativePath(e.getContainer()));
        table.addRow(md.link(e.getName(), path), e.getDescription(), e.getTags());
    }

    protected void relationshipTableRowContainer(MarkdownUtil md, MarkdownTable table, Container e) {
        var path = ctx.getContainerPathToRoot().resolve(ctx.getContainerRelativePath(e));
        table.addRow(md.link(e.getName(), path), e.getDescription(), e.getTags());
    }

    protected void relationshipTableRowPerson(MarkdownUtil md, MarkdownTable table, Person e) {
        table.addRow(e.getName(), e.getDescription(), e.getTags());
    }

    protected void relationshipTableRowSoftwareSystem(MarkdownUtil md, MarkdownTable table, SoftwareSystem e) {
        var path = ctx.getContainerPathToRoot().resolve(ctx.getSoftwareSystemRelativePath(e));
        table.addRow(md.link(e.getName(), path), e.getDescription(), e.getTags());
    }


}