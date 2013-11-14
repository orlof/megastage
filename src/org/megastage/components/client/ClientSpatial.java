/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.client;

import com.artemis.Component;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;

/**
 *
 * @author Teppo
 */
public class ClientSpatial extends Component {
    private Node node;
    private ArrayList<AbstractControl> controls = new ArrayList<>(5); 
    
    public void setNode(Node node) {
        if(this.node != null) throw new RuntimeException("Node already set");
        
        this.node = node;

        for(AbstractControl control: controls) {
            node.addControl(control);
        }
    }
    
    public Node getNode() {
        return node;
    }
    
    public void addControl(AbstractControl control) {
        controls.add(control);
        if(node != null) {
            node.addControl(control);
        }
    }
}
