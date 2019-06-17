package elements.classmodel;

import com.oocourse.uml2.models.elements.UmlAssociation;
import com.oocourse.uml2.models.elements.UmlAssociationEnd;
import elements.classmodel.struct.MyUmlClassOrInterface;

class MyUmlAssociation {
    private final UmlAssociation umlAssociation;
    private final UmlAssociationEnd[] umlAssociationEnds
            = new UmlAssociationEnd[2];
    private MyUmlClassOrInterface[] umlStructs = null;
    
    MyUmlAssociation(UmlAssociation association) {
        umlAssociation = association;
        umlAssociationEnds[0] = null;
        umlAssociationEnds[1] = null;
    }
    
    UmlAssociation getUmlAssociation() {
        return umlAssociation;
    }
    
    UmlAssociationEnd[] getUmlAssociationEnds() {
        return umlAssociationEnds;
    }
    
    MyUmlClassOrInterface[] getAssociatedStructs() {
        return umlStructs;
    }
    
    void addEnd(UmlAssociationEnd end) {
        if (umlAssociation.getEnd1().equals(end.getId())) {
            umlAssociationEnds[0] = end;
        } else if (umlAssociation.getEnd2().equals(end.getId())) {
            umlAssociationEnds[1] = end;
        } else {
            throw new RuntimeException();
        }
    }
    
    void setUmlStructs(MyUmlClassOrInterface source,
                       MyUmlClassOrInterface target) {
        umlStructs = new MyUmlClassOrInterface[]{source, target};
    }
}
