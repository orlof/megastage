package org.megastage.client.controls;

import com.artemis.Entity;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.LightNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class ExplosionControl extends AbstractControl {
    private float time = -3;
    private int state = 0;
    private final Entity entity;

    private static ParticleEmitter sparks, burst, shockwave, fire, smoke, embers;
    private PointLight light;
    
    public static void initialize(AssetManager assetManager) {
        createSparks(assetManager);
        createBurst(assetManager);
        createSmoke(assetManager);
        createFire(assetManager);
        createEmbers(assetManager);
        createShockwave(assetManager);

    }
    
    public ExplosionControl(Entity entity) {
        this.entity = entity;
    }
    
    private void attachEffects(Node explosionEffect) {
        explosionEffect.attachChild(fire.clone());
        explosionEffect.attachChild(burst.clone());
        explosionEffect.attachChild(sparks.clone());
        explosionEffect.attachChild(embers.clone());
        explosionEffect.attachChild(smoke.clone());
        explosionEffect.attachChild(shockwave.clone());

        light = new PointLight();
        light.setColor(ColorRGBA.Yellow);
        light.setRadius(0);

        LightNode lightNode = new LightNode("Light", light);
        explosionEffect.attachChild(lightNode);
        explosionEffect.addLight(light);
    }
    
    private static final float[] delay = new float[] {
        0f, 0f, 1.5f, 3.5f, 3.7f, 8f, 12f, 12f
    };

    @Override
    protected void controlUpdate(float tpf) {
        // this is a timer that triggers the effects in the right order
        time += tpf;

        while(state < delay.length && time > delay[state]) {
            switch(state) {
                case 0:
                    attachEffects((Node) spatial);
                    break;
                case 1:
                    getEmitter("SparksEmitter").emitAllParticles();
                    break;
                case 2:
                    getEmitter("BurstEmitter").emitAllParticles();
                    light.setColor(ColorRGBA.Red);
                    light.setRadius(5000);
                    break;
                case 3:
                    getEmitter("ShockwaveEmitter").emitAllParticles();
                    getEmitter("FireEmitter").emitAllParticles();
                    getEmitter("EmberEmitter").emitAllParticles();
                    getEmitter("SmokeEmitter").emitAllParticles();
                    light.setColor(ColorRGBA.Yellow);
                    light.setRadius(10000);
                    break;
                case 4:
                    for(Spatial s: spatial.getParent().getChildren()) {
                        if(s != spatial) {
                            s.removeFromParent();
                        }
                    }
                    //entity.getComponent(ShipGeometry.class).delete(null, entity);
                    break;
                case 5:
                    light.setColor(ColorRGBA.Red);
                    light.setRadius(5000);
                    getEmitter("BurstEmitter").killAllParticles();
                    getEmitter("SparksEmitter").killAllParticles();
                    break;
                case 6:
                    // rewind the effect
                    light.setColor(ColorRGBA.Red);
                    light.setRadius(2000);
                    getEmitter("FireEmitter").killAllParticles();
                    getEmitter("SmokeEmitter").killAllParticles();
                    getEmitter("EmberEmitter").killAllParticles();
                    getEmitter("ShockwaveEmitter").killAllParticles();
                    break;
                case 7:
                    ((Node) spatial).removeLight(light);
                    spatial.getParent().removeFromParent();
                    // restart the effect
                    /*
                    state = 1;
                    time = 0;
                    */
                    break;
            }
            state++;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

    private static void createFire(AssetManager assetManager) {
        fire = new ParticleEmitter("FireEmitter", ParticleMesh.Type.Triangle, 120);
        Material fire_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fire_mat.setTexture("Texture", assetManager.loadTexture("Effects/flame.png"));
        fire.setMaterial(fire_mat);
        fire.setImagesX(2);
        fire.setImagesY(2);
        fire.setRandomAngle(true);

        fire.setStartColor(new ColorRGBA(1f, 1f, .5f, 1f));
        fire.setEndColor(new ColorRGBA(1f, 0f, 0f, 0f));
        fire.setGravity(0, 0, 0);
        fire.setStartSize(10f);
        fire.setEndSize(0.5f);
        fire.setLowLife(2f);
        fire.setHighLife(15f);
        fire.getParticleInfluencer().setVelocityVariation(3f);
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(8f, 8f, 8f));
        fire.setParticlesPerSec(0);
    }

    private static void createBurst(AssetManager assetManager) {
        burst = new ParticleEmitter("BurstEmitter", Type.Triangle, 10);
        Material burst_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        burst_mat.setTexture("Texture", assetManager.loadTexture("Effects/flash.png"));
        burst.setMaterial(burst_mat);
        burst.setImagesX(2);
        burst.setImagesY(2);
        burst.setSelectRandomImage(true);

        burst.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1f));
        burst.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        burst.setStartSize(1f);
        burst.setEndSize(20.0f);
        burst.setGravity(0, 0, 0);
        burst.setLowLife(1.5f);
        burst.setHighLife(1.5f);
        burst.getParticleInfluencer().setInitialVelocity(new Vector3f(4, 4f, 4));
        burst.getParticleInfluencer().setVelocityVariation(1);
        burst.setShape(new EmitterSphereShape(Vector3f.ZERO, .5f));
        burst.setParticlesPerSec(0);
    }

    private static void createEmbers(AssetManager assetManager) {
        embers = new ParticleEmitter("EmberEmitter", Type.Triangle, 40);
        Material embers_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        embers_mat.setTexture("Texture", assetManager.loadTexture("Effects/roundspark.png"));
        embers.setMaterial(embers_mat);
        embers.setImagesX(1);
        embers.setImagesY(1);

        embers.setStartColor(new ColorRGBA(1f, 0.29f, 0.34f, 1.0f));
        embers.setEndColor(new ColorRGBA(0, 0, 0, 0.5f));
        embers.setStartSize(12f);
        embers.setEndSize(18f);
        embers.setGravity(0, 0, 0);
        embers.setLowLife(8f);
        embers.setHighLife(20f);
        embers.getParticleInfluencer().setInitialVelocity(new Vector3f(3, 3, 3));
        embers.getParticleInfluencer().setVelocityVariation(1f);
        embers.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f));
        embers.setParticlesPerSec(0);

    }

    private static void createSparks(AssetManager assetManager) {
        sparks = new ParticleEmitter("SparksEmitter", Type.Triangle, 40);
        Material spark_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        spark_mat.setTexture("Texture", assetManager.loadTexture("Effects/spark.png"));
        sparks.setMaterial(spark_mat);
        sparks.setImagesX(1);
        sparks.setImagesY(1);

        sparks.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1.0f)); // orange
        sparks.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        sparks.getParticleInfluencer().setInitialVelocity(new Vector3f(10, 10, 10));
        sparks.getParticleInfluencer().setVelocityVariation(3);
        sparks.setFacingVelocity(true);
        // sparks.setGravity(0, 10, 0);
        sparks.setStartSize(3f);
        sparks.setEndSize(3f);
        sparks.setLowLife(3f);
        sparks.setHighLife(5f);
        sparks.setParticlesPerSec(0);

    }

    private static void createSmoke(AssetManager assetManager) {
        smoke = new ParticleEmitter("SmokeEmitter", Type.Triangle, 50);
        Material smoke_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        smoke_mat.setTexture("Texture", assetManager.loadTexture("Effects/smoketrail.png"));
        smoke.setMaterial(smoke_mat);
        smoke.setImagesX(1);
        smoke.setImagesY(3);
        smoke.setSelectRandomImage(true);

        smoke.setStartColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
        smoke.setEndColor(new ColorRGBA(.1f, 0.1f, 0.1f, 0f));
        smoke.setLowLife(4f);
        smoke.setHighLife(20f);
        //smoke.setGravity(0, 1, 0);
        smoke.setFacingVelocity(true);
        smoke.getParticleInfluencer().setInitialVelocity(new Vector3f(10f, 10f, 10f));
        smoke.getParticleInfluencer().setVelocityVariation(5);
        smoke.setStartSize(5f);
        smoke.setEndSize(1f);
        smoke.setParticlesPerSec(0);
    }

    private static void createShockwave(AssetManager assetManager) {
        shockwave = new ParticleEmitter("ShockwaveEmitter", Type.Triangle, 2);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/shockwave.png"));
        shockwave.setImagesX(1);
        shockwave.setImagesY(1);
        shockwave.setMaterial(mat);

        /* The shockwave faces upward (along the Y axis) to make it appear as
         * a horizontally expanding circle. */
        //shockwave.setFaceNormal(Vector3f.UNIT_Y);
        shockwave.setStartColor(new ColorRGBA(.68f, 0.77f, 0.61f, 1f));
        shockwave.setEndColor(new ColorRGBA(.68f, 0.77f, 0.61f, 0f));
        shockwave.setStartSize(1f);
        shockwave.setEndSize(40f);
        shockwave.setGravity(0, 0, 0);
        shockwave.setLowLife(2f);
        shockwave.setHighLife(2f);
        shockwave.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0));
        shockwave.getParticleInfluencer().setVelocityVariation(0f);
        shockwave.setParticlesPerSec(0);
    }

    private ParticleEmitter getEmitter(String name) {
        return (ParticleEmitter) ((Node) spatial).getChild(name);
    }

}
