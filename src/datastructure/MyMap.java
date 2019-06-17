package datastructure;

import datastructure.exceptions.ElementDuplicatedException;
import datastructure.exceptions.ElementNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MyMap<T> {
    private final HashMap<T, String> map = new HashMap<>();
    
    public int size() {
        return map.size();
    }
    
    public Collection<String> keys() {
        return map.values();
    }
    
    public Set<T> values() {
        return new HashSet<>(map.keySet());
    }
    
    public void put(String name, T element) {
        map.put(element, name);
    }
    
    public T get(String name)
            throws ElementNotFoundException, ElementDuplicatedException {
        T result = null;
        for (Map.Entry<T, String> entry : map.entrySet()) {
            if (entry.getValue().equals(name)) {
                if (result != null) {
                    throw new ElementDuplicatedException(name);
                }
                result = entry.getKey();
            }
        }
        if (result == null) {
            throw new ElementNotFoundException(name);
        } else {
            return result;
        }
    }
    
    public Set<T> getAll(String name) {
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().equals(name))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
    
    @Override
    public String toString() {
        return map.toString();
    }
}
