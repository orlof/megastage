package org.megastage.client.controls;

import org.megastage.util.Log;
import com.jme3.audio.AudioNode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;
import org.megastage.client.ExplosionNode;
import org.megastage.client.SoundManager;
import org.megastage.components.Explosion;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class ExplosionControl extends AbstractControl {
    private int eid;
    
    private int state = -1;

    public ExplosionControl(int eid) {
        this.eid = eid;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        Explosion explosion = (Explosion) World.INSTANCE.getComponent(eid, CompType.Explosion);

        while(state < explosion.state) {
            state++;
            Log.info("Render explosion state: "+ state + "/" + explosion.state);

            ExplosionNode node = (ExplosionNode) spatial;

            switch(state) {
                case 0:
                    break;
                case 1:
                    node.sparks.emitAllParticles();
                    break;
                case 2:
                    AudioNode an = SoundManager.get(SoundManager.EXPLOSION);
                    an.setVolume(1);
                    an.playInstance();
                    node.burst.emitAllParticles();
                    node.light.setColor(ColorRGBA.Red);
                    node.light.setRadius(5000);
                    break;
                case 3:
                    an = SoundManager.get(SoundManager.EXPLOSION);
                    an.setVolume(5);
                    an.playInstance();
                    node.shockwave.emitAllParticles();
                    node.fire.emitAllParticles();
                    node.embers.emitAllParticles();
                    node.smoke.emitAllParticles();
                    node.light.setColor(ColorRGBA.Yellow);
                    node.light.setRadius(10000);
                    break;
                case 4:
                    Node shipNode = node.getParent();
                    List<Spatial> children = new ArrayList<>( shipNode.getChildren() );
                    for(Spatial s: children) {
                        if(s != spatial) {
                            Log.info("remove: " + s.getName() + " from " + shipNode.getName());
                            s.removeFromParent();
                        } else {
                            Log.info("don't remove: " + s.getName() + " from " + shipNode.getName());
                        }
                    }
                    break;
                case 5:
                    node.light.setColor(ColorRGBA.Red);
                    node.light.setRadius(5000);
                    node.burst.killAllParticles();
                    node.sparks.killAllParticles();
                    break;
                case 6:
                    // rewind the effect
                    node.fire.killAllParticles();
                    node.smoke.killAllParticles();
                    node.embers.killAllParticles();
                    node.shockwave.killAllParticles();
                    node.removeLight(node.light);
                    node.getParent().removeFromParent();
                    break;
                case 7:
                    break;
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
