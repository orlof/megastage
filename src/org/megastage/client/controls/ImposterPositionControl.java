package org.megastage.client.controls;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class ImposterPositionControl extends AbstractControl {
    private final int eid;

    public ImposterPositionControl(int eid) {
        this.eid = eid;
        setEnabled(false);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(true) return;
        Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
        assert pos != null;
        
        Vector3f coord = pos.get();

        Position origoPos = (Position) World.INSTANCE.getComponent(ClientGlobals.baseEntity, CompType.Position);
        assert origoPos != null;

        Vector3f origo = origoPos.get();

        Vector3f line = coord.subtract(origo);

        float distance = line.length();

        if(distance > 1000000.0) {
            float scale = 1000000.0f / distance;
            line.multLocal(scale).addLocal(origo);
            spatial.setLocalTranslation(line);
        } else {
            spatial.setLocalTranslation(coord);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
