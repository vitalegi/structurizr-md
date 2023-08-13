package it.vitalegi.structurizr.gen.model;

import com.structurizr.model.Element;
import com.structurizr.model.Person;
import com.structurizr.model.Relationship;
import it.vitalegi.structurizr.gen.util.StructurizrUtil;

import java.util.Comparator;

public class RelationshipComparator implements Comparator<Relationship> {
    @Override
    public int compare(Relationship r1, Relationship r2) {
        return getKey(r1).compareTo(getKey(r2));
    }

    private String getKey(Relationship r) {
        return getKey(r.getSource()) + "_" + getKey(r.getDestination());
    }

    private String getKey(Element element) {
        if (element instanceof Person) {
            return element.getName();
        }
        var ss = getName(StructurizrUtil.getParentSoftwareSystem(element));
        var ct = getName(StructurizrUtil.getParentContainer(element));
        var cp = getName(StructurizrUtil.getParentComponent(element));
        return ss + "_" + ct + "_" + cp;
    }

    private String getName(Element e) {
        if (e != null) {
            return e.getName();
        }
        return "";
    }
}
