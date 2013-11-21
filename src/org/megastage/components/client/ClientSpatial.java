/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.client;

import com.artemis.Component;
import com.jme3.scene.Node;

/**
 *
 * @author Teppo
 */
public class ClientSpatial extends Component {
    public Node node;
    
    public String toString() {
        return "ClientSpatial(" + node.getName() + ")";
    }
}
