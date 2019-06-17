package model;

import com.oocourse.uml2.interact.common.AttributeClassInformation;
import com.oocourse.uml2.interact.common.AttributeQueryType;
import com.oocourse.uml2.interact.common.OperationQueryType;
import com.oocourse.uml2.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.AttributeNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.InteractionDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.InteractionNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.UmlRule002Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule008Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule009Exception;
import com.oocourse.uml2.interact.format.UmlGeneralInteraction;
import com.oocourse.uml2.models.common.Visibility;
import com.oocourse.uml2.models.elements.UmlAttribute;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlMessage;
import elements.classmodel.struct.MyUmlClass;
import elements.classmodel.MyUmlClassModel;
import elements.classmodel.struct.MyUmlInterface;
import elements.classmodel.struct.MyUmlClassOrInterface;
import elements.interactionmodel.MyUmlInteraction;
import elements.interactionmodel.MyUmlInteractionModel;
import elements.smmodel.MyUmlStateMachine;
import elements.smmodel.MyUmlStateMachineModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyUmlGeneralInteraction implements UmlGeneralInteraction {
    private final MyUmlClassModel diagram;
    private final MyUmlStateMachineModel stateMachine;
    private final MyUmlInteractionModel interaction;
    
    public MyUmlGeneralInteraction(UmlElement... elements) {
        ArrayList<UmlElement> umlElements = new ArrayList<>();
        Collections.addAll(umlElements, elements);
        diagram = new MyUmlClassModel(umlElements);
        stateMachine = new MyUmlStateMachineModel(umlElements);
        interaction = new MyUmlInteractionModel(umlElements);
    }
    
    /* UmlClassModelInteraction */
    @Override
    public int getClassCount() {
        return diagram.getClasses().size();
    }
    
    @Override
    public int getClassOperationCount(String className,
                                      OperationQueryType queryType)
            throws ClassNotFoundException, ClassDuplicatedException {
        return diagram.getClass(className).getOperations(queryType).size();
    }
    
    @Override
    public int getClassAttributeCount(String className,
                                      AttributeQueryType queryType)
            throws ClassNotFoundException, ClassDuplicatedException {
        MyUmlClass c = diagram.getClass(className);
        switch (queryType) {
            case SELF_ONLY:
                return c.getAttributes().size();
            case ALL:
                int size = 0;
                while (c != null) {
                    size += c.getAttributes().size();
                    c = c.getSuperClass();
                }
                return size;
            default:
                throw new RuntimeException();
        }
    }
    
    @Override
    public int getClassAssociationCount(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        int result = 0;
        for (MyUmlClass c = diagram.getClass(className);
             c != null; c = c.getSuperClass()) {
            result += c.getAssociations().size();
        }
        return result;
    }
    
    @Override
    public List<String> getClassAssociatedClassList(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        ArrayList<MyUmlClassOrInterface> tmp = new ArrayList<>();
        for (MyUmlClass c = diagram.getClass(className);
             c != null; c = c.getSuperClass()) {
            tmp.addAll(c.getAssociations());
        }
        return tmp.stream().map(MyUmlClassOrInterface::getName).distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<Visibility, Integer> getClassOperationVisibility(
            String className, String operationName)
            throws ClassNotFoundException, ClassDuplicatedException {
        Map<Visibility, Integer> result = new HashMap<>();
        diagram.getClass(className).getOperations(operationName)
                .forEach(op -> {
                    Visibility visibility = op.getUmlOperation()
                            .getVisibility();
                    int cur = result.getOrDefault(visibility, 0);
                    result.put(visibility, cur + 1);
                });
        return result;
    }
    
    @Override
    public Visibility getClassAttributeVisibility(
            String className, String attributeName)
            throws ClassNotFoundException, ClassDuplicatedException,
            AttributeNotFoundException, AttributeDuplicatedException {
        UmlAttribute attribute = null;
        MyUmlClass c = diagram.getClass(className);
        for (; c != null; c = c.getSuperClass()) {
            if ((attribute = c.getAttribute(attributeName)) != null) {
                c = c.getSuperClass();
                break;
            }
        }
        for (; c != null; c = c.getSuperClass()) {
            if (c.getAttribute(attributeName) != null) {
                throw new AttributeDuplicatedException(className,
                        attributeName);
            }
        }
        if (attribute == null) {
            throw new AttributeNotFoundException(className, attributeName);
        }
        return attribute.getVisibility();
    }
    
    @Override
    public String getTopParentClass(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        MyUmlClass c = diagram.getClass(className);
        while (c.getSuperClass() != null) {
            c = c.getSuperClass();
        }
        return c.getName();
    }
    
    @Override
    public List<String> getImplementInterfaceList(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        return this.diagram.getClass(className).getInterfaces()
                .stream().map(MyUmlInterface::getName)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AttributeClassInformation> getInformationNotHidden(
            String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        ArrayList<AttributeClassInformation> result = new ArrayList<>();
        for (MyUmlClass c = diagram.getClass(className);
             c != null; c = c.getSuperClass()) {
            for (UmlAttribute attr : c.getAttributes()) {
                if (attr.getVisibility() != Visibility.PRIVATE) {
                    result.add(new AttributeClassInformation(attr.getName(),
                            c.getName()));
                }
            }
        }
        return result;
    }
    
    /* UmlStandardPreCheck */
    @Override
    public void checkForUml002() throws UmlRule002Exception {
        this.diagram.checkForUml002();
    }
    
    @Override
    public void checkForUml008() throws UmlRule008Exception {
        this.diagram.checkForUml008();
    }
    
    @Override
    public void checkForUml009() throws UmlRule009Exception {
        this.diagram.checkForUml009();
    }
    
    
    /* UmlStateChartInteraction */
    @Override
    public int getStateCount(String stateMachineName)
            throws StateMachineNotFoundException,
            StateMachineDuplicatedException {
        return this.stateMachine.getStateMachine(stateMachineName)
                .getStateCount();
    }
    
    @Override
    public int getTransitionCount(String stateMachineName)
            throws StateMachineNotFoundException,
            StateMachineDuplicatedException {
        return this.stateMachine.getStateMachine(stateMachineName)
                .getTransitions().size();
    }
    
    @Override
    public int getSubsequentStateCount(String stateMachineName,
                                       String stateName)
            throws StateMachineNotFoundException,
            StateMachineDuplicatedException,
            StateNotFoundException, StateDuplicatedException {
        MyUmlStateMachine stateMachine = this.stateMachine
                .getStateMachine(stateMachineName);
        return stateMachine.getSubsequentStateCount(stateName);
    }
    
    /* UmlCollaborationInteraction */
    @Override
    public int getParticipantCount(String interactionName)
            throws InteractionNotFoundException,
            InteractionDuplicatedException {
        return this.interaction.getInteraction(interactionName)
                .getLifelineCount();
    }
    
    @Override
    public int getMessageCount(String interactionName)
            throws InteractionNotFoundException,
            InteractionDuplicatedException {
        return this.interaction.getInteraction(interactionName)
                .getMessages().size();
    }
    
    @Override
    public int getIncomingMessageCount(String interactionName,
                                       String lifelineName)
            throws InteractionNotFoundException,
            InteractionDuplicatedException,
            LifelineNotFoundException, LifelineDuplicatedException {
        MyUmlInteraction interaction = this.interaction
                .getInteraction(interactionName);
        String targetId = interaction.getLifeline(lifelineName).getId();
        return (int) interaction.getMessages().stream()
                .map(UmlMessage::getTarget).filter(targetId::equals).count();
    }
}
