/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.util.application;

import java.util.List;
import org.jdom2.Element;

/**
 *
 * @author lubuntu
 */
public class XmlUtil {
    public static final List<Element> getChildElements(Element root) {
        return cast(root.getChildren());
    }
    
    public static final List<Element> getChildElements(Element root, String name) {
        return cast(root.getChildren(name));
    }
    
    private static final List<Element> cast(List list) {
        @SuppressWarnings( "unchecked" )
        List<Element> retValue = (List<Element>)  list;
        return retValue;
    }
}
