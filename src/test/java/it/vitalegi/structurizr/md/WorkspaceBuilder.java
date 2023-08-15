package it.vitalegi.structurizr.md;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.ContainerInstance;
import com.structurizr.model.DeploymentNode;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ComponentView;
import com.structurizr.view.DeploymentView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkspaceBuilder {

    Logger log = LoggerFactory.getLogger(getClass());

    Workspace ws;

    private WorkspaceBuilder(String name, String descrition) {
        ws = new Workspace(name, descrition);
    }

    public static WorkspaceBuilder init(String name, String description) {
        return new WorkspaceBuilder(name, description);
    }

    public ComponentView addComponentView(String softwareSystemName, int containerIndex, String key, String description) {
        var container = findContainer(softwareSystemName, containerIndex);
        var view = ws.getViews().createComponentView(container, key, description);
        view.addDefaultElements();
        view.enableAutomaticLayout();
        return view;
    }

    public Container findContainer(String softwareSystemName, int containerIndex) {
        return findContainer(getContainerName(softwareSystemName, containerIndex));
    }

    public Container findContainer(String name) {
        return ws.getModel().getSoftwareSystems().stream().flatMap(s -> s.getContainers().stream())
                 .filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    public String getContainerName(String softwareSystemName, int containerIndex) {
        return "Container " + softwareSystemName + "_" + containerIndex;
    }

    public ContainerInstance addContainer(DeploymentNode node, Container container) {
        return node.add(container);
    }

    public DeploymentNode addDeploymentNode(DeploymentNode node, String name) {
        return node.addDeploymentNode(name);
    }

    public DeploymentNode addDeploymentNode(String env, String name) {
        return ws.getModel().addDeploymentNode(env, name, "description " + name, "technology " + name);
    }

    public DeploymentView addDeploymentView(String key) {
        var view = ws.getViews().createDeploymentView(key, "description");
        view.addDefaultElements();
        view.enableAutomaticLayout();
        view.addAllDeploymentNodes();
        return view;
    }

    public DeploymentView addDeploymentView(SoftwareSystem softwareSystem, String key) {
        return ws.getViews().createDeploymentView(softwareSystem, key, "description");
    }

    public SoftwareSystem addSoftwareSystem(String name, int containers, int componentsPerContainer) {
        var ss = ws.getModel().addSoftwareSystem(name);
        for (var c1 = 0; c1 < containers; c1++) {
            var containerName = getContainerName(name, c1);
            var container = ss.addContainer(containerName);
            log.info("Created container '{}'", containerName);
            for (var c2 = 0; c2 < componentsPerContainer; c2++) {
                var componentName = getComponentName(name, c1, c2);
                container.addComponent(componentName, "description " + componentName);
                log.info("Created component '{}'", componentName);
            }
        }
        return ss;
    }

    public String getComponentName(String softwareSystemName, int containerIndex, int componentIndex) {
        return "Component " + softwareSystemName + "_" + containerIndex + "_" + componentIndex;
    }

    public SoftwareSystem addSoftwareSystem(String name) {
        return ws.getModel().addSoftwareSystem(name);
    }

    public Workspace build() {
        return ws;
    }

    public void connectComponents(String sourceSoftwareSystem, int sourceContainerIndex, int sourceComponentIndex,
                                  String destinationSoftwareSystem, int destinationContainerIndex,
                                  int destinationComponentIndex) {
        var source = getComponentName(sourceSoftwareSystem, sourceContainerIndex, sourceComponentIndex);
        var destination = getComponentName(destinationSoftwareSystem, destinationContainerIndex,
                destinationComponentIndex);
        connectComponents(source, destination);
    }

    public void connectComponents(String source, String destination) {
        var c1 = findComponent(source);
        var c2 = findComponent(destination);
        if (c1 == null) {
            throw new IllegalArgumentException(source + " is not a component");
        }
        if (c2 == null) {
            throw new IllegalArgumentException(destination + " is not a component");
        }
    }

    public Component findComponent(String name) {
        return ws.getModel().getSoftwareSystems().stream().flatMap(s -> s.getContainers().stream())
                 .flatMap(c -> c.getComponents().stream()).filter(c -> c.getName().equals(name)).findFirst()
                 .orElse(null);
    }

    public Component findComponent(String softwareSystemName, int containerIndex, int componentIndex) {
        return findComponent(getComponentName(softwareSystemName, containerIndex, componentIndex));
    }
}
