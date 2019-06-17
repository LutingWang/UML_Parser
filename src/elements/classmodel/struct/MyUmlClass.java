package elements.classmodel.struct;

import com.oocourse.uml2.interact.common.AttributeClassInformation;
import com.oocourse.uml2.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml2.models.elements.UmlAttribute;
import com.oocourse.uml2.models.elements.UmlClass;
import datastructure.MyMap;
import datastructure.exceptions.ElementDuplicatedException;
import datastructure.exceptions.ElementNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MyUmlClass extends MyUmlClassOrInterface {
    private final UmlClass umlClass;
    private MyUmlClass superClass = null;
    private final MyMap<MyUmlInterface> interfaces = new MyMap<>();
    private final MyMap<UmlAttribute> attributes = new MyMap<>();
    
    public MyUmlClass(UmlClass umlClass) {
        this.umlClass = umlClass;
    }
    
    @Override
    public String getName() {
        return umlClass.getName();
    }
    
    public MyUmlClass getSuperClass() {
        return superClass;
    }
    
    public Set<MyUmlInterface> getInterfaces() {
        Set<MyUmlInterface> interfaces = this.interfaces.values();
        for (MyUmlClass c = getSuperClass(); c != null; c = c.getSuperClass()) {
            interfaces.addAll(c.interfaces.values());
        }
        interfaces.addAll(interfaces.stream()
                .map(MyUmlInterface::getSuperInterfaces)
                .flatMap(Set::stream).collect(Collectors.toSet()));
        return interfaces;
    }
    
    public Set<UmlAttribute> getAttributes() {
        return attributes.values();
    }
    
    public UmlAttribute getAttribute(String name)
            throws AttributeDuplicatedException {
        try {
            return attributes.get(name);
        } catch (ElementNotFoundException e) {
            return null;
        } catch (ElementDuplicatedException e) {
            throw new AttributeDuplicatedException(umlClass.getName(), name);
        }
    }
    
    public void setSuperClass(MyUmlClass superClass) {
        this.superClass = superClass;
    }
    
    public void addInterface(MyUmlInterface umlInterface) {
        interfaces.put(umlInterface.getName(), umlInterface);
    }
    
    public void addAttribute(UmlAttribute attribute) {
        attributes.put(attribute.getName(), attribute);
    }
    
    public HashSet<AttributeClassInformation> checkForUml002(String endName) {
        HashSet<AttributeClassInformation> result = new HashSet<>();
        Consumer<String> addInfo = attrname ->
                result.add(new AttributeClassInformation(
                        attrname, umlClass.getName()));
        try {
            if (this.getAttribute(endName) != null) {
                addInfo.accept(endName);
            }
        } catch (Exception e) {
            addInfo.accept(endName);
        }
        ArrayList<String> attrnames = new ArrayList<>(this.attributes.keys());
        for (int i = 0; i < attrnames.size() - 1; i++) {
            for (int j = i + 1; j < attrnames.size(); j++) {
                if (attrnames.get(i).equals(attrnames.get(j))) {
                    addInfo.accept(attrnames.get(i));
                }
            }
        }
        return result;
    }
    
    @Override
    public UmlClass checkForUml008() {
        HashSet<MyUmlClass> list = new HashSet<>();
        MyUmlClass c;
        for (c = this; c != null && !list.contains(c); c = c.getSuperClass()) {
            list.add(c);
        }
        if (c != null) {
            return c.umlClass;
        } else {
            return null;
        }
    }
    
    public UmlClass checkForUml009(Set<MyUmlInterface> markedInterfaces) {
        ArrayList<MyUmlInterface> interfaces = new ArrayList<>();
        for (MyUmlClass c = this; c != null; c = c.getSuperClass()) {
            for (MyUmlInterface i : c.interfaces.values()) {
                if (markedInterfaces.contains(i) || interfaces.contains(i)) {
                    return this.umlClass;
                }
                interfaces.add(i);
            }
        }
        for (MyUmlInterface i : interfaces) {
            for (MyUmlInterface si : i.getSuperInterfaces()) {
                if (interfaces.contains(i)) {
                    return this.umlClass;
                }
                interfaces.add(si);
            }
        }
        return null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MyUmlClass)) {
            return false;
        }
        return umlClass.equals(((MyUmlClass) obj).umlClass);
    }
    
    @Override
    public int hashCode() {
        return umlClass.hashCode();
    }
    
    @Override
    public String toString() {
        return "class " + getName();
    }
}
