package org.megastage.server;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.megastage.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PrefabManager {
    public static HashMap<String, Element> prefabs = new HashMap<>();

    public static void initialize() {
        File path = new File(ServerGlobals.prefabPath);
        readDirectory(path);
    }

    public static void readDirectory(File path) {
        for (File file : path.listFiles()) {
            if (file.isFile()) {
                readFile(file);
            } else if (file.isDirectory()) {
                readDirectory(file);
            }
        }
    }

    public static void readFile(File file) {
        try {
            Element root = new SAXBuilder().build(file).getRootElement();
            prefabs.put(root.getAttributeValue("name"), root);
        } catch (JDOMException | IOException e) {
            Log.error(e);
        }
    }
}
