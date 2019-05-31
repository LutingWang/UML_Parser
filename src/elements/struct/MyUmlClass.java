package elements.struct;

import com.oocourse.uml1.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml1.models.elements.UmlAttribute;
import com.oocourse.uml1.models.elements.UmlClass;
import datastructure.MyMap;
import datastructure.exceptions.ElementDuplicatedException;
import datastructure.exceptions.ElementNotFoundException;

import java.util.Set;

public class MyUmlClass extends MyUmlStruct {
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
        return interfaces.values();
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
