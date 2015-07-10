package org.megastage.server;

import org.megastage.util.MegastageException;

import java.util.HashMap;

public class EntityResolver {
    private static final HashMap<String, Integer> entities = new HashMap<>();

    public static int resolve(String name) {
        if(entities.containsKey(name)) {
            return entities.get(name);
        }

        throw new MegastageException("Unknown entity name: " + name);
    }

    public static void add(String name, int eid) {
        entities.put(name, eid);
    }
}
