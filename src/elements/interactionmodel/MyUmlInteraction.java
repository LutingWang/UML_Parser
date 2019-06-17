package elements.interactionmodel;

import com.oocourse.uml2.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml2.models.elements.UmlInteraction;
import com.oocourse.uml2.models.elements.UmlLifeline;
import com.oocourse.uml2.models.elements.UmlMessage;
import datastructure.MyMap;
import datastructure.exceptions.ElementDuplicatedException;
import datastructure.exceptions.ElementNotFoundException;

import java.util.ArrayList;

public class MyUmlInteraction {
    private final UmlInteraction interaction;
    private final MyMap<UmlLifeline> lifelines = new MyMap<>();
    private final ArrayList<UmlMessage> messages = new ArrayList<>();
    
    MyUmlInteraction(UmlInteraction interaction) {
        this.interaction = interaction;
    }
    
    String getName() {
        return interaction.getName();
    }
    
    public int getLifelineCount() {
        return lifelines.size();
    }
    
    public UmlLifeline getLifeline(String lifelineName)
            throws LifelineNotFoundException, LifelineDuplicatedException {
        try {
            return lifelines.get(lifelineName);
        } catch (ElementNotFoundException e) {
            throw new LifelineNotFoundException(
                    interaction.getName(), lifelineName);
        } catch (ElementDuplicatedException e) {
            throw new LifelineDuplicatedException(
                    interaction.getName(), lifelineName);
        }
    }
    
    public ArrayList<UmlMessage> getMessages() {
        return messages;
    }
    
    void addLifeline(UmlLifeline lifeline) {
        this.lifelines.put(lifeline.getName(), lifeline);
    }
    
    void addMessage(UmlMessage message) {
        this.messages.add(message);
    }
}
