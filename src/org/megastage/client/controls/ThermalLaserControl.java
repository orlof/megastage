package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.audio.AudioNode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Cylinder;
import org.megastage.client.ClientGlobals;
import org.megastage.client.SoundManager;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.components.transfer.ThermalLaserData;
import org.megastage.util.Mapper;

/**
 *
 * @author Orlof
 */
public class ThermalLaserControl extends AbstractControl {
    private final Entity entity;
    private final Cylinder cylinder;
    private final AudioNode an;
    private char status = 0xffff;
    private float length;

    public ThermalLaserControl(Entity entity, Cylinder cylinder) {
        this.entity = entity;
        this.cylinder = cylinder;
        this.an = SoundManager.get(SoundManager.LASER_BEAM).clone();
        an.setLooping(true);
     }

    @Override
    protected void controlUpdate(float tpf) {
        ThermalLaserData data = Mapper.THERMAL_LASER_DATA.get(entity);
        if(data == null) {
            spatial.setCullHint(Spatial.CullHint.Always);
        } else {
            if(status != data.status) {
                status = data.status;
                switch(data.status) {
                    case VirtualThermalLaser.STATUS_FIRING:
                        an.play();
                        spatial.setCullHint(Spatial.CullHint.Inherit);
                        if(data.range != cylinder.getHeight()) {
                            cylinder.updateGeometry(16, 16, 0.2f, 0.2f, data.range, true, false);
                            spatial.setLocalTranslation(0, 0, -data.range/2f - 0.5f);

                        }
                        break;
                    case VirtualThermalLaser.STATUS_COOLDOWN:
                        an.pause();
                        spatial.setCullHint(Spatial.CullHint.Always);
                        break;
                    case VirtualThermalLaser.STATUS_DORMANT:
                        spatial.setCullHint(Spatial.CullHint.Always);
                        break;
                }
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
