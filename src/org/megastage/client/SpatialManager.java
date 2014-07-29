package org.megastage.client;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.megastage.client.controls.ImposterPositionControl;
import org.megastage.client.controls.PositionControl;
import org.megastage.components.client.NodeComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class SpatialManager {
    public static EntityNode getOrCreateNode(int eid) {
        NodeComponent comp = (NodeComponent) World.INSTANCE.getOrCreateComponent(eid, CompType.NodeComponent, NodeComponent.class);
 
        if(comp.node == null) {
            comp.node = new EntityNode(eid);
        }
        
        return comp.node;
    }
    
    public static EntityNode getOrCreateCleanNode(int eid) {
        NodeComponent comp = (NodeComponent) World.INSTANCE.getOrCreateComponent(eid, CompType.NodeComponent, NodeComponent.class);
 
        if(comp.node == null) {
            comp.node = new EntityNode(eid);
        } else {
            comp.node.reset();
        }
        
        return comp.node;
    }
    
    
    
    // NOT VERIFIED

    public static void imposter(int eid, boolean gfxVisible) {
        Node node = getOrCreateNode(eid);

        for(Spatial s: node.getChildren()) {
            if(s.getName().equals("imposter")) {
                boolean imposterVisible = !gfxVisible;
                boolean imposterDraw = draw(s);
                if(!imposterVisible && imposterDraw) {
                    s.setCullHint(Spatial.CullHint.Always);
                    s.getParent().getControl(ImposterPositionControl.class).setEnabled(false);
                    s.getParent().getControl(PositionControl.class).setEnabled(true);
                } else if(imposterVisible && !imposterDraw) {
                    s.setCullHint(Spatial.CullHint.Inherit);
                    s.getParent().getControl(ImposterPositionControl.class).setEnabled(true);
                    s.getParent().getControl(PositionControl.class).setEnabled(false);
                }
            } else {
                boolean gfxDraw = draw(s);
                if(gfxVisible && !gfxDraw) {
                    s.setCullHint(Spatial.CullHint.Inherit);
                } else if(!gfxVisible && gfxDraw) {
                    s.setCullHint(Spatial.CullHint.Always);
                }
            }
        }
    }
    
    private static boolean draw(Spatial s) {
        return s.getCullHint() != Spatial.CullHint.Always;
    }
}
