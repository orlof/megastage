package org.megastage.server;

import org.jdom2.Element;
import org.megastage.util.XmlUtil;

public class ServerConfig {
    public Element root;

    public ServerConfig(String filename) {
        root = XmlUtil.read(filename);
    }
}
