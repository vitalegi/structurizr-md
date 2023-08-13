package it.vitalegi.structurizr.gen.model;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.SystemLandscapeView;
import com.structurizr.view.View;
import it.vitalegi.structurizr.gen.util.FileUtil;
import it.vitalegi.structurizr.gen.util.StructurizrUtil;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
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

    public Stream<ComponentView> getComponentViewsSorted() {
        return sortViews(workspace.getViews().getComponentViews().stream());
    }

    public Stream<Component> getComponentsSorted(Container c) {
        return sortModels(c.getComponents().stream());
    }

    public Stream<ContainerView> getContainerViewsSorted() {
        return sortViews(workspace.getViews().getContainerViews().stream());
    }

    public Stream<Container> getContainersSorted(SoftwareSystem ss) {
        return sortModels(ss.getContainers().stream());
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

    public Path getSoftwareSystemDir(SoftwareSystem softwareSystem) {
        return SOFTWARE_SYSTEMS_ROOT.resolve(StructurizrUtil.sanitizeName(softwareSystem.getName()));
    }

    public Path getSoftwareSystemPath(SoftwareSystem softwareSystem) {
        var dir = mainDir.resolve(getSoftwareSystemDir(softwareSystem));
        FileUtil.createDirs(dir);
        return dir.resolve("README.md");
    }

    public Path getSoftwareSystemPathToRoot() {
        return Path.of("..", "..");
    }

    public Path getSoftwareSystemRelativePath(SoftwareSystem softwareSystem) {
        var dir = getSoftwareSystemDir(softwareSystem);
        return dir.resolve("README.md");
    }

    public Stream<SoftwareSystem> getSoftwareSystemsSorted() {
        return sortModels(workspace.getModel().getSoftwareSystems().stream());
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

    protected <E extends Element> Stream<E> sortModels(Stream<E> stream) {
        return stream.sorted(Comparator.comparing(E::getName).thenComparing(E::getId));
    }

    protected <E extends View> Stream<E> sortViews(Stream<E> views) {
        return views.sorted(Comparator.comparing(View::getName).thenComparing(View::getKey));
    }
}
