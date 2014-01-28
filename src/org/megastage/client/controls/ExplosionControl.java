package org.megastage.client.controls;

import com.artemis.Entity;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.gfx.ShipGeometry;

public class ExplosionControl extends AbstractControl {
    private float time = 0;
    private int state = -1;
    private final Entity entity;

    private static float speed = 1.0f;
    private static ParticleEmitter sparks, burst,
            shockwave, debris,
            fire, smoke, embers;

    public static void initialize(AssetManager assetManager) {
        createSparks(assetManager);
        createBurst(assetManager);
        createDebris(assetManager);
        createSmoke(assetManager);
        createFire(assetManager);
        createEmbers(assetManager);
        createShockwave(assetManager);
    }
    
    public ExplosionControl(Entity entity) {
        this.entity = entity;
    }
    
    private void attachEffects(Node explosionEffect) {
        explosionEffect.attachChild(fire);
        explosionEffect.attachChild(burst);
        explosionEffect.attachChild(sparks);
        explosionEffect.attachChild(embers);
        explosionEffect.attachChild(smoke);
        explosionEffect.attachChild(debris);
        explosionEffect.attachChild(shockwave);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        // this is a timer that triggers the effects in the right order
        time += tpf / speed;
        if (state == -1) {
            attachEffects((Node) spatial);
            state++;
        }
        if (time > 1.5f && state == 0) {
            sparks.emitAllParticles();
            state++;
        }
        if (time > 2f && state == 1) {
            burst.emitAllParticles();
            debris.emitAllParticles();
            state++;
        }
        if (time > 2f + .05f / speed && state == 2) {
            entity.getComponent(ShipGeometry.class).delete(null, entity);
            shockwave.emitAllParticles();
            fire.emitAllParticles();
            embers.emitAllParticles();
            smoke.emitAllParticles();
            state++;
        }
        if (time > 5 / speed && state == 3) {
            burst.killAllParticles();
            sparks.killAllParticles();
            debris.killAllParticles();
            state++;
        }
        if (time > 7 / speed && state == 4) {
            // rewind the effect
            fire.killAllParticles();
            smoke.killAllParticles();
            embers.killAllParticles();
            shockwave.killAllParticles();
        }
        if (time > 8 / speed && state == 4) {
            // restart the effect
            state = 0;
            time = 0;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

    private static void createFire(AssetManager assetManager) {
        fire = new ParticleEmitter("FireEmitter", ParticleMesh.Type.Triangle, 200);
        Material fire_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fire_mat.setTexture("Texture", assetManager.loadTexture("Effects/flame.png"));
        fire.setMaterial(fire_mat);
        fire.setImagesX(2);
        fire.setImagesY(2);
        fire.setRandomAngle(true);

        fire.setStartColor(new ColorRGBA(1f, 1f, .5f, 1f));
        fire.setEndColor(new ColorRGBA(1f, 0f, 0f, 0f));
        fire.setGravity(0, 0, 0);
        fire.setStartSize(1.5f);
        fire.setEndSize(0.05f);
        fire.setLowLife(0.5f);
        fire.setHighLife(2f);
        fire.getParticleInfluencer().setVelocityVariation(0.3f);
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 3f, 0));
        fire.setParticlesPerSec(0);
    }

    private static void createBurst(AssetManager assetManager) {
        burst = new ParticleEmitter("BurstEmitter", Type.Triangle, 5);
        Material burst_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        burst_mat.setTexture("Texture", assetManager.loadTexture("Effects/flash.png"));
        burst.setMaterial(burst_mat);
        burst.setImagesX(2);
        burst.setImagesY(2);
        burst.setSelectRandomImage(true);

        burst.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1f));
        burst.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        burst.setStartSize(.1f);
        burst.setEndSize(6.0f);
        burst.setGravity(0, 0, 0);
        burst.setLowLife(.5f);
        burst.setHighLife(.5f);
        burst.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 5f, 0));
        burst.getParticleInfluencer().setVelocityVariation(1);
        burst.setShape(new EmitterSphereShape(Vector3f.ZERO, .5f));
        burst.setParticlesPerSec(0);
    }

    private static void createEmbers(AssetManager assetManager) {
        embers = new ParticleEmitter("EmberEmitter", Type.Triangle, 50);
        Material embers_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        embers_mat.setTexture("Texture", assetManager.loadTexture("Effects/roundspark.png"));
        embers.setMaterial(embers_mat);
        embers.setImagesX(1);
        embers.setImagesY(1);

        embers.setStartColor(new ColorRGBA(1f, 0.29f, 0.34f, 1.0f));
        embers.setEndColor(new ColorRGBA(0, 0, 0, 0.5f));
        embers.setStartSize(1.2f);
        embers.setEndSize(1.8f);
        embers.setGravity(0, -.5f, 0);
        embers.setLowLife(1.8f);
        embers.setHighLife(5f);
        embers.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 3, 0));
        embers.getParticleInfluencer().setVelocityVariation(.5f);
        embers.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f));
        embers.setParticlesPerSec(0);

    }

    private static void createSparks(AssetManager assetManager) {
        sparks = new ParticleEmitter("SparksEmitter", Type.Triangle, 20);
        Material spark_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        spark_mat.setTexture("Texture", assetManager.loadTexture("Effects/spark.png"));
        sparks.setMaterial(spark_mat);
        sparks.setImagesX(1);
        sparks.setImagesY(1);

        sparks.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1.0f)); // orange
        sparks.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        sparks.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 10, 0));
        sparks.getParticleInfluencer().setVelocityVariation(1);
        sparks.setFacingVelocity(true);
        sparks.setGravity(0, 10, 0);
        sparks.setStartSize(.5f);
        sparks.setEndSize(.5f);
        sparks.setLowLife(.9f);
        sparks.setHighLife(1.1f);
        sparks.setParticlesPerSec(0);

    }

    private static void createSmoke(AssetManager assetManager) {
        smoke = new ParticleEmitter("SmokeEmitter", Type.Triangle, 20);
        Material smoke_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        smoke_mat.setTexture("Texture", assetManager.loadTexture("Effects/smoketrail.png"));
        smoke.setMaterial(smoke_mat);
        smoke.setImagesX(1);
        smoke.setImagesY(3);
        smoke.setSelectRandomImage(true);

        smoke.setStartColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
        smoke.setEndColor(new ColorRGBA(.1f, 0.1f, 0.1f, 0f));
        smoke.setLowLife(4f);
        smoke.setHighLife(6f);
        smoke.setGravity(0, 1, 0);
        smoke.setFacingVelocity(true);
        smoke.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 5f, 0));
        smoke.getParticleInfluencer().setVelocityVariation(1);
        smoke.setStartSize(.5f);
        smoke.setEndSize(1f);
        smoke.setParticlesPerSec(0);
    }

    private static void createDebris(AssetManager assetManager) {
        debris = new ParticleEmitter("DebrisEmitter", Type.Triangle, 15);
        Material debris_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        debris_mat.setTexture("Texture", assetManager.loadTexture("Effects/debris.png"));
        debris.setMaterial(debris_mat);
        debris.setImagesX(3);
        debris.setImagesY(3);
        debris.setSelectRandomImage(false);

        debris.setRandomAngle(true);
        debris.setRotateSpeed(FastMath.TWO_PI * 2);
        debris.setStartColor(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f));
        debris.setEndColor(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f));
        debris.setStartSize(.2f);
        debris.setEndSize(1f);
        debris.setGravity(0, 10f, 0);
        debris.setLowLife(1f);
        debris.setHighLife(1.1f);
        debris.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 15, 0));
        debris.getParticleInfluencer().setVelocityVariation(.60f);
        debris.setParticlesPerSec(0);
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
        shockwave.setFaceNormal(Vector3f.UNIT_Y);
        shockwave.setStartColor(new ColorRGBA(.68f, 0.77f, 0.61f, 1f));
        shockwave.setEndColor(new ColorRGBA(.68f, 0.77f, 0.61f, 0f));
        shockwave.setStartSize(1f);
        shockwave.setEndSize(7f);
        shockwave.setGravity(0, 0, 0);
        shockwave.setLowLife(1f);
        shockwave.setHighLife(1f);
        shockwave.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0));
        shockwave.getParticleInfluencer().setVelocityVariation(0f);
        shockwave.setParticlesPerSec(0);
    }
}
