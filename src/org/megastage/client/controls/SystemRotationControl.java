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
        if(ClientGlobals.fixedEntity == null) {
            Log.warn("No fixed entity");
            spatial.setLocalRotation(Quaternion.IDENTITY);            
            return;
        }

        Rotation rotation = ClientGlobals.fixedEntity.getComponent(Rotation.class);
        if(rotation == null) {
            spatial.setLocalRotation(Quaternion.IDENTITY);            
        } else {
            Quaternion q = new Quaternion((float) rotation.x, (float) rotation.y, (float) rotation.z, (float) rotation.w);
            if(q == null) {
                Log.info("Warning: setting non invertable rotation");
            } else {
                spatial.setLocalRotation(q.inverse());
                ClientGlobals.sceneNode.setLocalRotation(q);
//                Vector3f[] axis = new Vector3f[3];
//                axis[0] = new Vector3f(0f,0f,0f);
//                axis[1] = new Vector3f(0f,0f,0f);
//                axis[2] = new Vector3f(0f,0f,0f);
//                q.toAxes(axis);
//                Log.info("x " + axis[0].toString());
//                Log.info("y " + axis[1].toString());
//                Log.info("z " + axis[2].toString());
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}

