package elements.smmodel;

import com.oocourse.uml2.interact.exceptions.user.StateMachineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineNotFoundException;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlFinalState;
import com.oocourse.uml2.models.elements.UmlPseudostate;
import com.oocourse.uml2.models.elements.UmlState;
import com.oocourse.uml2.models.elements.UmlStateMachine;
import com.oocourse.uml2.models.elements.UmlTransition;
import datastructure.MyMap;
import datastructure.exceptions.ElementDuplicatedException;
import datastructure.exceptions.ElementNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

public class MyUmlStateMachineModel {
    private final MyMap<MyUmlStateMachine> stateMachines = new MyMap<>();
    
    public MyUmlStateMachineModel(ArrayList<UmlElement> elements) {
        HashMap<String, MyUmlStateMachine> stateMachines
                = new HashMap<>(); // id -> value
        HashMap<String, String> regions = new HashMap<>(); // id -> parentId
        
        UmlElement element;
        for (ListIterator<UmlElement> it = elements.listIterator();
             it.hasNext(); ) {
            element = it.next();
            switch (element.getElementType()) {
                case UML_STATE_MACHINE:
                    stateMachines.put(element.getId(),
                            new MyUmlStateMachine((UmlStateMachine) element));
                    break;
                case UML_REGION:
                    regions.put(element.getId(), element.getParentId());
                    break;
                default:
                    continue;
            }
            it.remove();
        }
        
        for (ListIterator<UmlElement> it = elements.listIterator();
             it.hasNext(); ) {
            element = it.next();
            switch (element.getElementType()) {
                case UML_PSEUDOSTATE:
                    stateMachines.get(regions.get(element.getParentId()))
                            .setPseudostate((UmlPseudostate) element);
                    break;
                case UML_FINAL_STATE:
                    stateMachines.get(regions.get(element.getParentId()))
                            .setFinalState((UmlFinalState) element);
                    break;
                case UML_STATE:
                    stateMachines.get(regions.get(element.getParentId()))
                            .addState((UmlState) element);
                    break;
                case UML_TRANSITION:
                    stateMachines.get(regions.get(element.getParentId()))
                            .addTransition((UmlTransition) element);
                    break;
                default:
                    continue;
            }
            it.remove();
        }
        
        stateMachines.values().forEach(sm -> this.stateMachines
                .put(sm.getName(), sm));
    }
    
    public MyUmlStateMachine getStateMachine(String stateMachineName)
            throws StateMachineNotFoundException,
            StateMachineDuplicatedException {
        try {
            return this.stateMachines.get(stateMachineName);
        } catch (ElementNotFoundException e) {
            throw new StateMachineNotFoundException(stateMachineName);
        } catch (ElementDuplicatedException e) {
            throw new StateMachineDuplicatedException(stateMachineName);
        }
    }
}
