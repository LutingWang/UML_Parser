package elements.struct;

import com.oocourse.uml1.interact.common.OperationQueryType;
import datastructure.MyMap;
import elements.MyUmlOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MyUmlStruct {
    private final MyMap<MyUmlOperation> operations = new MyMap<>();
    private final ArrayList<MyUmlStruct> associations = new ArrayList<>();
    
    public abstract String getName();
    
    public Set<MyUmlOperation> getOperations(OperationQueryType queryType) {
        return operations.values().stream()
                .filter(myUmlOperation -> myUmlOperation.isType(queryType))
                .collect(Collectors.toSet());
    }
    
    public Set<MyUmlOperation> getOperations(String name) {
        return operations.getAll(name);
    }
    
    public List<MyUmlStruct> getAssociations() {
        return associations;
    }
    
    public void addOperation(MyUmlOperation operation) {
        operations.put(operation.getUmlOperation().getName(), operation);
    }
    
    public void addAssociation(MyUmlStruct associatedStruct) {
        associations.add(associatedStruct);
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
