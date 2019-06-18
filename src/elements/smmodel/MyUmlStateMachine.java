package elements.smmodel;

import com.oocourse.uml2.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml2.models.elements.UmlFinalState;
import com.oocourse.uml2.models.elements.UmlPseudostate;
import com.oocourse.uml2.models.elements.UmlState;
import com.oocourse.uml2.models.elements.UmlStateMachine;
import com.oocourse.uml2.models.elements.UmlTransition;
import datastructure.MyMap;
import datastructure.exceptions.ElementDuplicatedException;
import datastructure.exceptions.ElementNotFoundException;

import java.util.ArrayList;

public class MyUmlStateMachine {
    private final UmlStateMachine stateMachine;
    private UmlPseudostate pseudostate = null;
    private UmlFinalState finalState = null;
    private final MyMap<UmlState> states = new MyMap<>();
    private final ArrayList<UmlTransition> transitions = new ArrayList<>();
    
    MyUmlStateMachine(UmlStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }
    
    String getName() {
        return stateMachine.getName();
    }
    
    public int getStateCount() {
        int result = this.states.size();
        if (this.pseudostate != null) {
            result += 1;
        }
        if (this.finalState != null) {
            result += 1;
        }
        return result;
    }
    
    public int getSubsequentStateCount(String stateName)
            throws StateNotFoundException,
            StateDuplicatedException {
        ArrayList<String> fromId = new ArrayList<>();
        int pos = -1;
        String stateId;
        try {
            stateId = states.get(stateName).getId();
        } catch (ElementNotFoundException e) {
            throw new StateNotFoundException(stateMachine.getName(), stateName);
        } catch (ElementDuplicatedException e) {
            throw new StateDuplicatedException(
                    stateMachine.getName(), stateName);
        }
        for (; fromId.size() != pos; pos++) {
            final String fid;
            if (pos >= 0) {
                fid = fromId.get(pos);
            } else {
                fid = stateId;
            }
            this.transitions.stream()
                    .filter(t -> fid.equals(t.getSource()))
                    .map(UmlTransition::getTarget)
                    .filter(targetId -> !fromId.contains(targetId))
                    .forEach(fromId::add);
        }
        return (int) fromId.stream().distinct().count();
    }
    
    public ArrayList<UmlTransition> getTransitions() {
        return transitions;
    }
    
    void setPseudostate(UmlPseudostate pseudostate) {
        this.pseudostate = pseudostate;
    }
    
    void setFinalState(UmlFinalState finalState) {
        this.finalState = finalState;
    }
    
    void addState(UmlState state) {
        this.states.put(state.getName(), state);
    }
    
    void addTransition(UmlTransition transition) {
        this.transitions.add(transition);
    }
}
