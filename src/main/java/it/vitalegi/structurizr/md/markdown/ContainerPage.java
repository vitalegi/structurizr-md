package it.vitalegi.structurizr.md.markdown;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Person;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ComponentView;
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

public class ContainerPage {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected MdContext ctx;

    public ContainerPage(MdContext ctx) {
        this.ctx = ctx;
    }

    public void createPage(Container container) {
        var filePath = ctx.getContainerPath(container);
        log.info("Container: id={}, name={}. File={}", container.getId(), container.getName(), filePath);
        createPage(filePath, container);
    }

    protected boolean accept(ComponentView view, Container container) {
        if (view.getContainerId() == null) {
            return false;
        }
        return view.getContainerId().equals(container.getId());
    }

    protected void addDescription(MarkdownUtil md, View view) {
        if (StringUtil.isNotNullOrEmpty(view.getDescription())) {
            md.println("Description: " + view.getDescription());
            md.println();
        }
    }

    protected void createPage(Path filePath, Container container) {
        try (var md = MarkdownUtil.init(filePath)) {
            md.h1(container.getName());

            md.h2("Components");
            createSectionComponents(md, container);
            md.h2("Diagrams");
            createSectionComponentsDiagrams(md, container);
            md.h2("Relationships");
            createSectionUsedBy(md, container);
            createSectionUses(md, container);
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
        md.addViewImages(view, ctx, ctx.getContainerPathToRoot());
    }

    protected void createSectionComponents(MarkdownUtil md, Container container) {
        if (ctx.getComponentsSorted(container).count() == 0) {
            return;
        }
        var table = md.table("Component", "Description");
        ctx.getComponentsSorted(container).forEach(component -> {
            table.addRow(component.getName(), component.getDescription());
        });
        table.build();
        md.println();
    }

    protected void createSectionComponentsDiagrams(MarkdownUtil md, Container container) {
        createSection(md, ctx.getComponentViewsSorted().filter(v -> accept(v, container)));
        md.println();
    }

    protected void createSectionUsedBy(MarkdownUtil md, Container container) {
        var calledBy = ctx.getRelationships(r -> ctx.isDestination(r, container)).collect(Collectors.toList());
        if (calledBy.isEmpty()) {
            return;
        }
        md.h3("Used By");
        relationshipTable(md, calledBy, Relationship::getSource);
    }

    protected void createSectionUses(MarkdownUtil md, Container container) {
        var calledBy = ctx.getRelationships(r -> ctx.isSource(r, container)).collect(Collectors.toList());
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