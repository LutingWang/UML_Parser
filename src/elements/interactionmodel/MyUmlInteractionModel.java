package elements.interactionmodel;

import com.oocourse.uml2.interact.exceptions.user.InteractionDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.InteractionNotFoundException;
import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlInteraction;
import com.oocourse.uml2.models.elements.UmlLifeline;
import com.oocourse.uml2.models.elements.UmlMessage;
import datastructure.MyMap;
import datastructure.exceptions.ElementDuplicatedException;
import datastructure.exceptions.ElementNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

public class MyUmlInteractionModel {
    private final MyMap<MyUmlInteraction> interactions = new MyMap<>();
    
    public MyUmlInteractionModel(ArrayList<UmlElement> elements) {
        // id -> value
        HashMap<String, MyUmlInteraction> interactions = new HashMap<>();
        
        UmlElement element;
        for (ListIterator<UmlElement> li = elements.listIterator();
             li.hasNext(); ) {
            element = li.next();
            if (element.getElementType() == ElementType.UML_INTERACTION) {
                interactions.put(element.getId(),
                        new MyUmlInteraction((UmlInteraction) element));
                li.remove();
            }
        }
    
        for (ListIterator<UmlElement> li = elements.listIterator();
             li.hasNext(); ) {
            element = li.next();
            switch (element.getElementType()) {
                case UML_LIFELINE:
                    interactions.get(element.getParentId())
                            .addLifeline((UmlLifeline) element);
                    break;
                case UML_MESSAGE:
                    interactions.get(element.getParentId())
                            .addMessage((UmlMessage) element);
                    break;
                default:
                    continue;
            }
            li.remove();
        }
        
        interactions.values()
                .forEach(it -> this.interactions.put(it.getName(), it));
    }
    
    public MyUmlInteraction getInteraction(String interactionName)
            throws InteractionNotFoundException,
            InteractionDuplicatedException {
        try {
            return this.interactions.get(interactionName);
        } catch (ElementNotFoundException e) {
            throw new InteractionNotFoundException(interactionName);
        } catch (ElementDuplicatedException e) {
            throw new InteractionDuplicatedException(interactionName);
        }
    }
}
