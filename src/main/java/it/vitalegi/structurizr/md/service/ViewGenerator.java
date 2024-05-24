package it.vitalegi.structurizr.md.service;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.SystemContextView;
import it.vitalegi.structurizr.md.util.StructurizrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static it.vitalegi.structurizr.md.util.StringUtil.getFirstNotNullOrEmpty;

public class ViewGenerator {

    Logger log = LoggerFactory.getLogger(getClass());

    Workspace ws;

    public ViewGenerator(Workspace workspace) {
        ws = workspace;
    }

    public void initDefaultViews() {
        initViewLandscape();
        initViewsSystemContext();
        initViewsContainers();
        initViewsComponents();
    }

    protected void initViewLandscape() {
        if (ws.getViews().getSystemLandscapeViews().isEmpty()) {
            log.info("Create landscape view");
            var view = ws.getViews().createSystemLandscapeView("landscape", "Landscape");
            view.addDefaultElements();
            view.enableAutomaticLayout();
        }
    }

    protected void initViewsSystemContext() {
        ws.getModel().getSoftwareSystems().forEach(ss -> {
            if (ws.getViews().getSystemContextViews().stream().noneMatch(s -> match(s, ss))) {
                log.info("Create system context view for {} ({})", ss.getName(), ss.getId());
                var view = ws.getViews()
                             .createSystemContextView(ss, createViewId("system_context", ss),
                                     "System Context of " + ss.getName());
                view.addDefaultElements();
                view.enableAutomaticLayout();
            }
        });
    }

    protected void initViewsContainers() {
        ws.getModel().getSoftwareSystems().forEach(ss -> {
            if (ws.getViews().getContainerViews().stream().noneMatch(v -> match(v, ss))) {
                log.info("Create container view for {} ({})", ss.getName(), ss.getId());
                var view = ws.getViews()
                             .createContainerView(ss, createViewId("container", ss),
                                     "Container view of " + ss.getName());
                view.addDefaultElements();
                view.enableAutomaticLayout();
            }
        });
    }

    protected void initViewsComponents() {
        ws.getModel().getSoftwareSystems().stream().flatMap(s -> s.getContainers().stream())
          .forEach(this::initViewsComponents);
    }

    protected boolean match(SystemContextView view, SoftwareSystem softwareSystem) {
        return view.getSoftwareSystemId().equals(softwareSystem.getId());
    }

    protected String createViewId(String prefix, SoftwareSystem softwareSystem) {
        var name = getFirstNotNullOrEmpty(softwareSystem.getName(), softwareSystem.getId());
        return prefix + " " + StructurizrUtil.sanitizeName(name);
    }

    protected boolean match(ContainerView view, SoftwareSystem softwareSystem) {
        return view.getSoftwareSystemId().equals(softwareSystem.getId());
    }

    protected void initViewsComponents(Container container) {
        if (ws.getViews().getComponentViews().stream().noneMatch(v -> match(v, container))) {
            if (container.getComponents().isEmpty()) {
                log.info("Skip components view for container {} ({}), it doesn't contain any component.",
                        container.getName(), container.getId());
                return;
            }
            log.info("Create component view for {} ({})", container.getName(), container.getId());
            var view = ws.getViews()
                         .createComponentView(container, createViewId("component", container),
                                 "Component view of " + container.getName());
            view.addDefaultElements();
            view.enableAutomaticLayout();
        }
    }

    protected boolean match(ComponentView view, Container container) {
        return view.getContainerId().equals(container.getId());
    }

    protected String createViewId(String prefix, Container c) {
        var ss = c.getSoftwareSystem();
        var name = getFirstNotNullOrEmpty(ss.getName(), ss.getId()) + " " + getFirstNotNullOrEmpty(c.getName(),
                c.getId());
        return prefix + " " + StructurizrUtil.sanitizeName(name);
    }
}
