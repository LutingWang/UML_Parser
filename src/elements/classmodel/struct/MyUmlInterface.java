package elements.classmodel.struct;

import com.oocourse.uml2.models.elements.UmlInterface;
import datastructure.MyMap;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class MyUmlInterface extends MyUmlClassOrInterface {
    private final UmlInterface umlInterface;
    private final MyMap<MyUmlInterface> superInterfaces = new MyMap<>();
    
    public MyUmlInterface(UmlInterface umlInterface) {
        this.umlInterface = umlInterface;
    }
    
    @Override
    public String getName() {
        return umlInterface.getName();
    }
    
    Set<MyUmlInterface> getSuperInterfaces() {
        Set<MyUmlInterface> result = superInterfaces.values();
        result.addAll(result.stream().map(MyUmlInterface::getSuperInterfaces)
                .flatMap(Set::stream).collect(Collectors.toSet()));
        return result;
    }
    
    public void addSuperInterface(MyUmlInterface umlInterface) {
        superInterfaces.put(umlInterface.getName(), umlInterface);
    }
    
    @Override
    public UmlInterface checkForUml008() {
        ArrayList<MyUmlInterface> list = new ArrayList<>();
        int pos = 0;
        for (list.add(this); pos < list.size(); pos++) {
            for (MyUmlInterface i : list.get(pos).superInterfaces.values()) {
                if (list.contains(i)) {
                    return this.umlInterface;
                }
                list.add(i);
            }
        }
        return null;
    }
    
    public UmlInterface checkForUml009() {
        ArrayList<MyUmlInterface> queue = new ArrayList<>();
        queue.add(this);
        for (int pos = 0; pos != queue.size(); pos++) {
            for (MyUmlInterface i : queue.get(pos).superInterfaces.values()) {
                if (queue.contains(i)) {
                    return this.umlInterface;
                }
                queue.add(i);
            }
        }
        return null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MyUmlInterface)) {
            return false;
        }
        return umlInterface.equals(((MyUmlInterface) obj).umlInterface);
    }
    
    @Override
    public int hashCode() {
        return umlInterface.hashCode();
    }
    
    @Override
    public String toString() {
        return "interface " + getName();
    }
}
