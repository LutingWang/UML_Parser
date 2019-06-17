package elements.classmodel;

import com.oocourse.uml2.interact.common.AttributeClassInformation;
import com.oocourse.uml2.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.UmlRule002Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule008Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule009Exception;
import com.oocourse.uml2.models.elements.UmlAssociation;
import com.oocourse.uml2.models.elements.UmlAssociationEnd;
import com.oocourse.uml2.models.elements.UmlAttribute;
import com.oocourse.uml2.models.elements.UmlClass;
import com.oocourse.uml2.models.elements.UmlClassOrInterface;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlGeneralization;
import com.oocourse.uml2.models.elements.UmlInterface;
import com.oocourse.uml2.models.elements.UmlInterfaceRealization;
import com.oocourse.uml2.models.elements.UmlOperation;
import com.oocourse.uml2.models.elements.UmlParameter;
import datastructure.MyMap;
import datastructure.exceptions.ElementDuplicatedException;
import datastructure.exceptions.ElementNotFoundException;
import elements.classmodel.struct.MyUmlClass;
import elements.classmodel.struct.MyUmlInterface;
import elements.classmodel.struct.MyUmlClassOrInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

public class MyUmlClassModel {
    private final MyMap<MyUmlClass> classes = new MyMap<>();
    private final MyMap<MyUmlInterface> interfaces = new MyMap<>();
    private final MyMap<MyUmlAssociation> associations = new MyMap<>();
    
    public MyUmlClassModel(ArrayList<UmlElement> elements) {
        // id -> value
        HashMap<String, MyUmlClassOrInterface> structs = new HashMap<>();
        HashMap<String, MyUmlOperation> operations = new HashMap<>();
        HashMap<String, MyUmlAssociation> associations = new HashMap<>();
        
        // filter out classes and operations
        UmlElement element;
        for (ListIterator<UmlElement> it = elements.listIterator();
             it.hasNext(); ) {
            element = it.next();
            switch (element.getElementType()) {
                case UML_ASSOCIATION:
                    associations.put(element.getId(),
                            new MyUmlAssociation((UmlAssociation) element));
                    break;
                case UML_CLASS:
                    structs.put(element.getId(),
                            new MyUmlClass((UmlClass) element));
                    break;
                case UML_INTERFACE:
                    structs.put(element.getId(),
                            new MyUmlInterface((UmlInterface) element));
                    break;
                case UML_OPERATION:
                    operations.put(element.getId(),
                            new MyUmlOperation((UmlOperation) element));
                    break;
                default:
                    continue;
            }
            it.remove();
        }
        
        // add properties to classes and operations
        for (ListIterator<UmlElement> it = elements.listIterator();
             it.hasNext(); ) {
            element = it.next();
            switch (element.getElementType()) {
                case UML_ASSOCIATION_END:
                    associations.get(element.getParentId())
                            .addEnd((UmlAssociationEnd) element);
                    break;
                case UML_ATTRIBUTE:
                    ((MyUmlClass) structs.get(element.getParentId()))
                            .addAttribute((UmlAttribute) element);
                    break;
                case UML_GENERALIZATION:
                    UmlGeneralization generalization
                            = (UmlGeneralization) element;
                    MyUmlClassOrInterface source
                            = structs.get(generalization.getSource());
                    MyUmlClassOrInterface target
                            = structs.get(generalization.getTarget());
                    if (source instanceof MyUmlClass) {
                        ((MyUmlClass) source)
                                .setSuperClass((MyUmlClass) target);
                    } else if (source instanceof MyUmlInterface) {
                        ((MyUmlInterface) source)
                                .addSuperInterface((MyUmlInterface) target);
                    }
                    break;
                case UML_INTERFACE_REALIZATION:
                    UmlInterfaceRealization realization
                            = (UmlInterfaceRealization) element;
                    ((MyUmlClass) structs.get(realization.getSource()))
                            .addInterface((MyUmlInterface) structs
                                    .get(realization.getTarget()));
                    break;
                case UML_PARAMETER:
                    operations.get(element.getParentId())
                            .addParameter((UmlParameter) element);
                    break;
                default:
                    continue;
            }
            it.remove();
        }
        
        operations.values().forEach(op -> structs.get(op.getUmlOperation()
                .getParentId()).addOperation(op));
        structs.values().forEach(c -> {
            if (c instanceof MyUmlClass) {
                this.classes.put(c.getName(), (MyUmlClass) c);
            } else if (c instanceof MyUmlInterface) {
                this.interfaces.put(c.getName(), (MyUmlInterface) c);
            } else {
                throw new RuntimeException();
            }
        });
        associations.values().forEach(a -> {
            UmlAssociationEnd[] ends = a.getUmlAssociationEnds();
            MyUmlClassOrInterface struct1 = structs.get(ends[0].getReference());
            MyUmlClassOrInterface struct2 = structs.get(ends[1].getReference());
            struct1.addAssociation(struct2);
            struct2.addAssociation(struct1);
            a.setUmlStructs(struct1, struct2);
            this.associations.put(a.getUmlAssociation().getName(), a);
        });
    }
    
    public Set<MyUmlClass> getClasses() {
        return classes.values();
    }
    
    public MyUmlClass getClass(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        try {
            return classes.get(className);
        } catch (ElementNotFoundException e) {
            throw new ClassNotFoundException(className);
        } catch (ElementDuplicatedException e) {
            throw new ClassDuplicatedException(className);
        }
    }
    
    public void checkForUml002() throws UmlRule002Exception {
        HashSet<AttributeClassInformation> result = new HashSet<>();
        associations.values().forEach(association -> {
            MyUmlClassOrInterface[] structs =
                    association.getAssociatedStructs();
            UmlAssociationEnd[] ends = association.getUmlAssociationEnds();
            if (structs[0] instanceof MyUmlClass) {
                result.addAll(((MyUmlClass) structs[0])
                        .checkForUml002(ends[1].getName()));
            }
            if (structs[1] instanceof MyUmlClass) {
                result.addAll(((MyUmlClass) structs[1])
                        .checkForUml002(ends[0].getName()));
            }
        });
        if (result.size() != 0) {
            throw new UmlRule002Exception(result);
        }
    }
    
    public void checkForUml008() throws UmlRule008Exception {
        HashSet<UmlClassOrInterface> result = new HashSet<>();
        for (MyUmlClass c : this.classes.values()) {
            result.add(c.checkForUml008());
        }
        for (MyUmlInterface i : this.interfaces.values()) {
            result.add(i.checkForUml008());
        }
        result.remove(null);
        if (result.size() != 0) {
            throw new UmlRule008Exception(result);
        }
    }
    
    public void checkForUml009() throws UmlRule009Exception {
        HashSet<UmlClassOrInterface> result = new HashSet<>();
        Set<MyUmlInterface> markedInterfaces = this.interfaces.values().stream()
                .filter(i -> {
                    UmlInterface ui = i.checkForUml009();
                    if (ui != null) {
                        result.add(ui);
                    }
                    return ui != null;
                }).collect(Collectors.toSet());
        for (MyUmlClass c : this.classes.values()) {
            UmlClass uc = c.checkForUml009(markedInterfaces);
            if (uc != null) {
                result.add(uc);
            }
        }
        if (result.size() != 0) {
            throw new UmlRule009Exception(result);
        }
    }
}
