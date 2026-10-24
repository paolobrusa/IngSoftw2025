package it.polimi.ingsw.is25am31.model.components;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The Tiles class represents a collection of components used within the game.
 * It manages components classified as face-down, face-up, used, and an overall list of all components.
 * The class provides functionality for drawing components, managing their state, and ensuring rules are followed for their usage.
 */
public class Tiles {
    private final List<Component> allComponents;
    private final List<Component> faceDownComponents;
    private final List<Component> usedComponents;
    private final List<Component> faceUpComponents;


    /**
     * Reads the components from the JSON file and puts them in {@code faceDownComponents}.<br>
     * If the specified level is 1 (test flight), replaces all the alien life support components
     * with structural components as per game rules
     *
     * @param level level of the game
     */
    public Tiles(int level){
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("components.json");
            try {
                allComponents = objectMapper.readValue(inputStream, new TypeReference<>() {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            usedComponents = new ArrayList<>();
            faceUpComponents = new ArrayList<>();

            if(level == 1) {
                faceDownComponents = new ArrayList<>();
                for (Component component : allComponents)
                    if(component.getName().equals("AlienLifeSupport"))
                        faceDownComponents.add(new Component(component.id, "Component", component.connector, component.orientation));
                    else
                        faceDownComponents.add(component);
            }
            else faceDownComponents = new ArrayList<>(allComponents);
    }

    /**
     *
     * @param id id of the required component
     * @return component with the specified id
     */
    public synchronized Component getComponent(int id){
        for(Component component : allComponents){
            if(component.getId() == id)
                return component;
        }
        return null;
    }

    /**+
     * Drawn component is moved from face down components to used components (unavailable to be drawn)
     *
     * @return a random face down component
     */
    public synchronized Component getFaceDownComponent(){
        if(faceDownComponents.isEmpty())
            throw new RuntimeException("No face down components left");
        int randomIndex = (int) (Math.random() * faceDownComponents.size());
        usedComponents.add(faceDownComponents.get(randomIndex));
        return faceDownComponents.remove(randomIndex);
    }
    

    /**
     * Drawn component is moved from face up components to used components (unavailable to be drawn)
     *
     * @param component component to be drawn form face up components
     */
    public synchronized void getFaceUpComponent(Component component){
        for(Component c : faceUpComponents){
            if(c.getId() != component.getId())
                continue;
            usedComponents.add(c);
            faceUpComponents.remove(c);
            return;
        }
        throw new RuntimeException("Selected component has already been taken by another player");
    }

    /**
     * Put down component is moved from used components (unavailable to be drawn) to face up components
     * 
     * @param component component to be put down
     */
    public synchronized void putDownComponent(Component component){
        for(Component c : usedComponents){
            if(c.getId() != component.getId())
                continue;
            usedComponents.remove(c);
            faceUpComponents.add(c);
            return;
        }
    }

    /**
     * 
     * @return list of face up components 
     */
    public List<Component> getFaceUpComponentsList(){
        return new ArrayList<>(faceUpComponents);
    }
}
