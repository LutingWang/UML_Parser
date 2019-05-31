package elements.struct;

import com.oocourse.uml1.models.elements.UmlInterface;
import datastructure.MyMap;

import java.util.Set;

public class MyUmlInterface extends MyUmlStruct {
    private final UmlInterface umlInterface;
    private final MyMap<MyUmlInterface> superInterfaces = new MyMap<>();
    
    public MyUmlInterface(UmlInterface umlInterface) {
        this.umlInterface = umlInterface;
    }
    
    @Override
    public String getName() {
        return umlInterface.getName();
    }
    
    public Set<MyUmlInterface> getSuperInterfaces() {
        Set<MyUmlInterface> result = superInterfaces.values();
        for (MyUmlInterface umlInterface : result) {
            result.addAll(umlInterface.getSuperInterfaces());
        }
        return result;
    }
    
    public void addSuperInterface(MyUmlInterface umlInterface) {
        superInterfaces.put(umlInterface.getName(), umlInterface);
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
