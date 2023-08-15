package it.vitalegi.structurizr.md.model;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.SystemLandscapeView;
import com.structurizr.view.View;
import it.vitalegi.structurizr.md.util.FileUtil;
import it.vitalegi.structurizr.md.util.StructurizrUtil;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MdContext {

    private static final Path IMAGES_ROOT = Path.of("images");
    private static final Path SOFTWARE_SYSTEMS_ROOT = Path.of("software-systems");

    Workspace workspace;
    AssetContext assetContext;
    Path mainDir;

    public MdContext(Workspace workspace, Path mainDir) {
        assetContext = new AssetContext();
        this.mainDir = mainDir;
        this.workspace = workspace;
    }

    public Path addImage(String key, String format) {
        var path = IMAGES_ROOT.resolve(key + "." + format);
        assetContext.addImage(key, format, path);
        return path;
    }

    public long countComponents(SoftwareSystem ss) {
        return ss.getContainers().stream().flatMap(c -> c.getComponents().stream()).count();
    }

    public void forEachContainerSorted(BiConsumer<SoftwareSystem, Container> consumer) {
        getSoftwareSystemsSorted().forEach(ss -> getContainersSorted(ss).forEach(c -> consumer.accept(ss, c)));
    }

    public Stream<SoftwareSystem> getSoftwareSystemsSorted() {
        return sortModels(workspace.getModel().getSoftwareSystems().stream());
    }

    public Stream<Container> getContainersSorted(SoftwareSystem ss) {
        return sortModels(ss.getContainers().stream());
    }

    protected <E extends Element> Stream<E> sortModels(Stream<E> stream) {
        return stream.sorted(Comparator.comparing(E::getName).thenComparing(E::getId));
    }

    public Stream<ComponentView> getComponentViewsSorted() {
        return sortViews(workspace.getViews().getComponentViews().stream());
    }

    protected <E extends View> Stream<E> sortViews(Stream<E> views) {
        return views.sorted(Comparator.comparing(View::getName).thenComparing(View::getKey));
    }

    public Stream<Component> getComponentsSorted(SoftwareSystem ss) {
        return ss.getContainers().stream().flatMap(this::getComponentsSorted);
    }

    public Stream<Component> getComponentsSorted(Container c) {
        return sortModels(c.getComponents().stream());
    }

    public Path getContainerPath(Container container) {
        var dir = mainDir.resolve(getContainerDir(container));
        FileUtil.createDirs(dir);
        return dir.resolve("README.md");
    }

    public Path getContainerDir(Container container) {
        return getSoftwareSystemDir(container.getSoftwareSystem()).resolve(StructurizrUtil.sanitizeName(container.getName()));
    }

    public Path getSoftwareSystemDir(SoftwareSystem softwareSystem) {
        return SOFTWARE_SYSTEMS_ROOT.resolve(StructurizrUtil.sanitizeName(softwareSystem.getName()));
    }

    public Stream<ContainerView> getContainerViewsSorted() {
        return sortViews(workspace.getViews().getContainerViews().stream());
    }

    public Stream<Container> getContainersSorted() {
        return sortModels(workspace.getModel().getSoftwareSystems().stream().flatMap(s -> s.getContainers().stream()));
    }

    public Stream<DeploymentView> getDeploymentViewsSorted() {
        return sortViews(workspace.getViews().getDeploymentViews().stream());
    }

    public List<String> getImageFormats(String key) {
        return assetContext.getFormats(key);

    }

    public Path getImagePath(String key, String format) {
        return assetContext.getPath(key, format);
    }

    public Path getImagesRoot() {
        return mainDir.resolve(IMAGES_ROOT);
    }

    public Path getMainDir() {
        return mainDir;
    }

    public Stream<Relationship> getRelationsFromTree(SoftwareSystem ss) {
        var tree = getTree(ss).collect(Collectors.toList());
        return sortRelationship(workspace.getModel().getRelationships().stream().filter(r -> isSource(r, tree)));
    }

    public Stream<Element> getTree(SoftwareSystem ss) {
        return Stream.concat(Stream.of(ss), ss.getContainers().stream().flatMap(this::getTree));
    }

    protected Stream<Relationship> sortRelationship(Stream<Relationship> stream) {
        return stream.sorted(new RelationshipComparator());
    }

    public boolean isSource(Relationship relationship, List<Element> sources) {
        return sources.stream().anyMatch(s -> relationship.getSource().equals(s));
    }

    public Stream<Element> getTree(Container c) {
        return Stream.concat(Stream.of(c), c.getComponents().stream());
    }

    public Stream<Relationship> getRelationsToTree(SoftwareSystem ss) {
        var tree = getTree(ss).collect(Collectors.toList());
        return sortRelationship(workspace.getModel().getRelationships().stream().filter(r -> isDestination(r, tree)));
    }

    public boolean isDestination(Relationship relationship, List<Element> destinations) {
        return destinations.stream().anyMatch(d -> relationship.getDestination().equals(d));
    }

    public Stream<Relationship> getRelationships(Function<Relationship, Boolean> accept) {
        return sortRelationship(workspace.getModel().getRelationships().stream().filter(accept::apply));
    }

    public Path getSoftwareSystemPath(SoftwareSystem softwareSystem) {
        var dir = mainDir.resolve(getSoftwareSystemDir(softwareSystem));
        FileUtil.createDirs(dir);
        return dir.resolve("README.md");
    }

    public Path getSoftwareSystemPathToRoot() {
        return Path.of("..", "..");
    }

    public Path getContainerPathToRoot() {
        return Path.of(".." ).resolve(getSoftwareSystemPathToRoot());
    }

    public Path getSoftwareSystemRelativePath(SoftwareSystem softwareSystem) {
        var dir = getSoftwareSystemDir(softwareSystem);
        return dir.resolve("README.md");
    }

    public Stream<SystemContextView> getSystemContextViewsSorted() {
        return sortViews(workspace.getViews().getSystemContextViews().stream());
    }

    public Path getSystemLandscape() {
        return mainDir.resolve("README.md");
    }

    public Stream<SystemLandscapeView> getSystemLandscapeViewsSorted() {
        return workspace.getViews().getSystemLandscapeViews().stream()
                        .sorted(Comparator.comparing(SystemLandscapeView::getName));
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public boolean isDestination(Relationship relationship, Element element) {
        return relationship.getDestination().equals(element);
    }

    public boolean isSource(Relationship relationship, Element element) {
        return relationship.getSource().equals(element);
    }

}
