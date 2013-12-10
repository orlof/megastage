/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.esotericsoftware.minlog.Log;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Rotation;
import org.megastage.util.ClientGlobals;

public class SystemRotationControl extends AbstractControl {
    
    public SystemRotationControl() {
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(ClientGlobals.shipEntity == null) {
            spatial.setLocalRotation(Quaternion.IDENTITY);            
            return;
        }

        Rotation rotation = ClientGlobals.shipEntity.getComponent(Rotation.class);
        if(rotation == null) {
            spatial.setLocalRotation(Quaternion.IDENTITY);            
            ClientGlobals.sceneNode.setLocalRotation(Quaternion.IDENTITY);
        } else {
            Quaternion q = new Quaternion((float) rotation.x, (float) rotation.y, (float) rotation.z, (float) rotation.w);
            spatial.setLocalRotation(q.inverse());
            ClientGlobals.sceneNode.setLocalRotation(q);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}

