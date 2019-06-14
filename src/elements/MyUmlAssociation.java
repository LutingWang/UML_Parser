package elements;

import com.oocourse.uml2.models.elements.UmlAssociation;
import com.oocourse.uml2.models.elements.UmlAssociationEnd;

class MyUmlAssociation {
    private final UmlAssociation umlAssociation;
    private final UmlAssociationEnd[] umlAssociationEnds
            = new UmlAssociationEnd[2];
    
    MyUmlAssociation(UmlAssociation association) {
        umlAssociation = association;
        umlAssociationEnds[0] = null;
        umlAssociationEnds[1] = null;
    }
    
    UmlAssociation getUmlAssociation() {
        return umlAssociation;
    }
    
    String[] getReferences() {
        String[] result = new String[2];
        result[0] = umlAssociationEnds[0].getReference();
        result[1] = umlAssociationEnds[1].getReference();
        return result;
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
}
