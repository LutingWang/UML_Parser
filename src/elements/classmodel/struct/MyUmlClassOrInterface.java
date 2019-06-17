package elements.classmodel.struct;

import com.oocourse.uml2.interact.common.OperationQueryType;
import com.oocourse.uml2.models.elements.UmlClassOrInterface;
import datastructure.MyMap;
import elements.classmodel.MyUmlOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MyUmlClassOrInterface {
    private final MyMap<MyUmlOperation> operations = new MyMap<>();
    private final ArrayList<MyUmlClassOrInterface> associations
            = new ArrayList<>();
    
    public abstract String getName();
    
    public Set<MyUmlOperation> getOperations(OperationQueryType queryType) {
        return operations.values().stream()
                .filter(myUmlOperation -> myUmlOperation.isType(queryType))
                .collect(Collectors.toSet());
    }
    
    public Set<MyUmlOperation> getOperations(String name) {
        return operations.getAll(name);
    }
    
    public List<MyUmlClassOrInterface> getAssociations() {
        return associations;
    }
    
    public void addOperation(MyUmlOperation operation) {
        operations.put(operation.getUmlOperation().getName(), operation);
    }
    
    public void addAssociation(MyUmlClassOrInterface associatedStruct) {
        associations.add(associatedStruct);
    }
    
    public abstract UmlClassOrInterface checkForUml008();
    
    @Override
    public String toString() {
        return getName();
    }
}
