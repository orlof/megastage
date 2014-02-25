package org.megastage.client.controls;

import com.artemis.Entity;
import com.jme3.audio.AudioNode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.shaderblow.forceshield.ForceShieldControl;
import org.megastage.client.SoundManager;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.util.Mapper;

/**
 *
 * @author Orlof
 */
public class ForceFieldControlControl extends AbstractControl {
    private final AudioNode an;
    private final ForceShieldControl ctrl;
    private final Entity entity;

    public ForceFieldControlControl(Entity entity, ForceShieldControl ctrl) {
        this.an = SoundManager.get(SoundManager.FORCE_FIELD).clone();
        an.setLooping(true);
        this.ctrl = ctrl;
        this.entity = entity;
     }

    @Override
    protected void controlUpdate(float tpf) {
        ForceFieldData data = Mapper.FORCE_FIELD_DATA.get(entity);
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
