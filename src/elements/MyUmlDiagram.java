package elements;

import com.oocourse.uml1.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml1.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml1.models.elements.UmlAssociation;
import com.oocourse.uml1.models.elements.UmlAssociationEnd;
import com.oocourse.uml1.models.elements.UmlAttribute;
import com.oocourse.uml1.models.elements.UmlClass;
import com.oocourse.uml1.models.elements.UmlElement;
import com.oocourse.uml1.models.elements.UmlGeneralization;
import com.oocourse.uml1.models.elements.UmlInterface;
import com.oocourse.uml1.models.elements.UmlInterfaceRealization;
import com.oocourse.uml1.models.elements.UmlOperation;
import com.oocourse.uml1.models.elements.UmlParameter;
import datastructure.MyMap;
import datastructure.exceptions.ElementDuplicatedException;
import datastructure.exceptions.ElementNotFoundException;
import elements.struct.MyUmlClass;
import elements.struct.MyUmlInterface;
import elements.struct.MyUmlStruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Set;

public class MyUmlDiagram {
    private final MyMap<MyUmlClass> classes = new MyMap<>();
    private final MyMap<MyUmlInterface> interfaces = new MyMap<>();
    private final MyMap<MyUmlAssociation> associations = new MyMap<>();
    
    public MyUmlDiagram(ArrayList<UmlElement> elements) {
        // id -> value
        HashMap<String, MyUmlStruct> structs = new HashMap<>();
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
                    MyUmlStruct source
                            = structs.get(generalization.getSource());
                    MyUmlStruct target
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
            this.associations.put(a.getUmlAssociation().getName(), a);
            String[] ends = a.getReferences();
            MyUmlStruct struct1 = structs.get(ends[0]);
            MyUmlStruct struct2 = structs.get(ends[1]);
            struct1.addAssociation(struct2);
            struct2.addAssociation(struct1);
        });
    }
    
    public Set<MyUmlClass> getClasses() {
        return classes.values();
    }
    
    public MyUmlClass getClass(String name)
            throws ClassNotFoundException, ClassDuplicatedException {
        try {
            return classes.get(name);
        } catch (ElementNotFoundException e) {
            throw new ClassNotFoundException(name);
        } catch (ElementDuplicatedException e) {
            throw new ClassDuplicatedException(name);
        }
    }
}
