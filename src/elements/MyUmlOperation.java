package elements;

import com.oocourse.uml1.interact.common.OperationQueryType;
import com.oocourse.uml1.models.common.Direction;
import com.oocourse.uml1.models.elements.UmlOperation;
import com.oocourse.uml1.models.elements.UmlParameter;

import java.util.HashSet;

public class MyUmlOperation {
    private final UmlOperation umlOperation;
    private final HashSet<UmlParameter> parameters = new HashSet<>();
    
    MyUmlOperation(UmlOperation umlOperation) {
        this.umlOperation = umlOperation;
    }
    
    public UmlOperation getUmlOperation() {
        return umlOperation;
    }
    
    void addParameter(UmlParameter parameter) {
        this.parameters.add(parameter);
    }
    
    private boolean hasReturn() {
        return parameters.stream()
                .anyMatch(parameter ->
                        parameter.getDirection() == Direction.RETURN);
    }
    
    private boolean hasParameter() {
        return parameters.stream()
                .anyMatch(parameter ->
                        parameter.getDirection() != Direction.RETURN);
    }
    
    public boolean isType(OperationQueryType queryType) {
        switch (queryType) {
            case NON_RETURN:
                return !hasReturn();
            case RETURN:
                return hasReturn();
            case NON_PARAM:
                return !hasParameter();
            case PARAM:
                return hasParameter();
            case ALL:
            default:
                return true;
        }
    }
}
