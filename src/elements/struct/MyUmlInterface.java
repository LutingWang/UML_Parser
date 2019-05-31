package elements.struct;

import com.oocourse.uml1.models.elements.UmlInterface;
import datastructure.MyMap;

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
    
    public void addSuperInterface(MyUmlInterface umlInterface) {
        superInterfaces.put(umlInterface.getName(), umlInterface);
    }
    
    @Override
    public String toString() {
        return "interface " + getName();
    }
}
