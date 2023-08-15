package it.vitalegi.structurizr.md;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.ContainerInstance;
import com.structurizr.model.DeploymentNode;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.DeploymentView;

public class WorkspaceBuilder {

    Workspace ws;

    private WorkspaceBuilder(String name, String descrition) {
        ws = new Workspace(name, descrition);
    }

    public static WorkspaceBuilder init(String name, String description) {
        return new WorkspaceBuilder(name, description);
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
            var container = ss.addContainer(getContainerName(name, c1));
            for (var c2 = 0; c2 < componentsPerContainer; c2++) {
                container.addComponent(getComponentName(name, c1, c2));
            }
        }
        return ss;
    }

    public String getContainerName(String softwareSystemName, int containerIndex) {
        return softwareSystemName + "_" + containerIndex;
    }

    public String getComponentName(String softwareSystemName, int containerIndex, int componentIndex) {
        return softwareSystemName + "_" + containerIndex + "_" + componentIndex;
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

    public Container findContainer(String softwareSystemName, int containerIndex) {
        return findContainer(getContainerName(softwareSystemName, containerIndex));
    }

    public Container findContainer(String name) {
        return ws.getModel().getSoftwareSystems().stream().flatMap(s -> s.getContainers().stream())
                 .filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }
}
