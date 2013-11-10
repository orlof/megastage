/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Component;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import org.megastage.protocol.Network;

/**
 *
 * @author Teppo
 */
public class ClientSpatial extends Component {
    public Node geom;
    
    public ClientSpatial(Node node) {
        this.geom = node;
    }
}
