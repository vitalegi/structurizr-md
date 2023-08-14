package it.vitalegi.structurizr.md.util;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.SoftwareSystem;

import java.nio.file.Path;
import java.util.Arrays;

public class StructurizrUtil {

    public static Workspace getWorkspace(Path dsl) {
        StructurizrDslParser parser = new StructurizrDslParser();
        try {
            parser.parse(dsl.toFile());
            return parser.getWorkspace();
        } catch (StructurizrDslParserException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sanitizeName(String name) {
        var forbidden = Arrays.asList("*", "?", "|", ":", "/", "\\", "%", "<", ">", "|", "?", "\"", "'", "(", ")",
                "[", "]", "$", "&");
        for (String s : forbidden) {
            name = name.replace(s, "_");
        }
        return name;
    }


    public static Component getParentComponent(Element element) {
        if (element instanceof SoftwareSystem) {
            return null;
        }
        if (element instanceof Container) {
            return null;
        }
        if (element instanceof Component) {
            return (Component) element;
        }
        return null;
    }

    public static Container getParentContainer(Element element) {
        if (element instanceof SoftwareSystem) {
            return null;
        }
        if (element instanceof Container) {
            return (Container) element;
        }
        if (element instanceof Component) {
            return ((Component) element).getContainer();
        }
        return null;
    }

    public static SoftwareSystem getParentSoftwareSystem(Element element) {
        if (element instanceof SoftwareSystem) {
            return (SoftwareSystem) element;
        }
        if (element instanceof Container) {
            return ((Container) element).getSoftwareSystem();
        }
        if (element instanceof Component) {
            return ((Component) element).getContainer().getSoftwareSystem();
        }
        return null;
    }

}
