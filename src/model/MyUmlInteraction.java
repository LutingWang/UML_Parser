package model;

import com.oocourse.uml1.interact.common.AttributeClassInformation;
import com.oocourse.uml1.interact.common.AttributeQueryType;
import com.oocourse.uml1.interact.common.OperationQueryType;
import com.oocourse.uml1.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml1.interact.exceptions.user.AttributeNotFoundException;
import com.oocourse.uml1.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml1.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml1.interact.format.UmlInteraction;
import com.oocourse.uml1.models.common.Visibility;
import com.oocourse.uml1.models.elements.UmlAttribute;
import com.oocourse.uml1.models.elements.UmlElement;
import elements.struct.MyUmlClass;
import elements.MyUmlDiagram;
import elements.struct.MyUmlInterface;
import elements.struct.MyUmlStruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyUmlInteraction implements UmlInteraction {
    private final MyUmlDiagram diagram;
    
    public MyUmlInteraction(UmlElement... elements) {
        ArrayList<UmlElement> umlElements = new ArrayList<>();
        Collections.addAll(umlElements, elements);
        diagram = new MyUmlDiagram(umlElements);
    }
    
    @Override
    public int getClassCount() {
        return diagram.getClasses().size();
    }
    
    @Override
    public int getClassOperationCount(String className,
                                      OperationQueryType queryType)
            throws ClassNotFoundException, ClassDuplicatedException {
        return diagram.getClass(className).getOperations(queryType).size();
    }
    
    @Override
    public int getClassAttributeCount(String className,
                                      AttributeQueryType queryType)
            throws ClassNotFoundException, ClassDuplicatedException {
        MyUmlClass c = diagram.getClass(className);
        switch (queryType) {
            case SELF_ONLY:
                return c.getAttributes().size();
            case ALL:
                int size = 0;
                while (c != null) {
                    size += c.getAttributes().size();
                    c = c.getSuperClass();
                }
                return size;
            default:
                throw new RuntimeException();
        }
    }
    
    @Override
    public int getClassAssociationCount(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        int result = 0;
        for (MyUmlClass c = diagram.getClass(className);
             c != null; c = c.getSuperClass()) {
            result += c.getAssociations().size();
        }
        return result;
    }
    
    @Override
    public List<String> getClassAssociatedClassList(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        ArrayList<MyUmlStruct> tmp = new ArrayList<>();
        for (MyUmlClass c = diagram.getClass(className);
             c != null; c = c.getSuperClass()) {
            tmp.addAll(c.getAssociations());
        }
        return tmp.stream().map(MyUmlStruct::getName).distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<Visibility, Integer> getClassOperationVisibility(
            String className, String operationName)
            throws ClassNotFoundException, ClassDuplicatedException {
        Map<Visibility, Integer> result = new HashMap<>();
        diagram.getClass(className).getOperations(operationName)
                .forEach(op -> {
                    Visibility visibility = op.getUmlOperation()
                            .getVisibility();
                    int cur = result.getOrDefault(visibility, 0);
                    result.put(visibility, cur + 1);
                });
        return result;
    }
    
    @Override
    public Visibility getClassAttributeVisibility(
            String className, String attributeName)
            throws ClassNotFoundException, ClassDuplicatedException,
            AttributeNotFoundException, AttributeDuplicatedException {
        UmlAttribute attribute = null;
        MyUmlClass c = diagram.getClass(className);
        for (; c != null; c = c.getSuperClass()) {
            if ((attribute = c.getAttribute(attributeName)) != null) {
                c = c.getSuperClass();
                break;
            }
        }
        for (; c != null; c = c.getSuperClass()) {
            if (c.getAttribute(attributeName) != null) {
                throw new AttributeDuplicatedException(className,
                        attributeName);
            }
        }
        if (attribute == null) {
            throw new AttributeNotFoundException(className, attributeName);
        }
        return attribute.getVisibility();
    }
    
    @Override
    public String getTopParentClass(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        MyUmlClass c = diagram.getClass(className);
        while (c.getSuperClass() != null) {
            c = c.getSuperClass();
        }
        return c.getName();
    }
    
    @Override
    public List<String> getImplementInterfaceList(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        return diagram.getClass(className).getInterfaces().stream()
                .map(MyUmlInterface::getName).distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AttributeClassInformation> getInformationNotHidden(
            String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        ArrayList<AttributeClassInformation> result = new ArrayList<>();
        for (MyUmlClass c = diagram.getClass(className);
             c != null; c = c.getSuperClass()) {
            for (UmlAttribute attr : c.getAttributes()) {
                if (attr.getVisibility() != Visibility.PRIVATE) {
                    result.add(new AttributeClassInformation(attr.getName(),
                            c.getName()));
                }
            }
        }
        return result;
    }
}
