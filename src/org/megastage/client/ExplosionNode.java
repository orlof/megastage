package org.megastage.client;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.LightNode;
import com.jme3.scene.Node;

public class ExplosionNode extends Node {
    public ParticleEmitter sparks, burst, shockwave, fire, smoke, embers;
    public PointLight light;

    public ExplosionNode(String explosionFX) {
        super(explosionFX);
        
        sparks = _sparks.clone();
        burst = _burst.clone();
        shockwave = _shockwave.clone();
        fire = _fire.clone();
        smoke = _smoke.clone();
        embers = _embers.clone();

        attachChild(fire);
        attachChild(burst);
        attachChild(sparks);
        attachChild(embers);
        attachChild(smoke);
        attachChild(shockwave);

        light = new PointLight();
        light.setColor(ColorRGBA.Yellow);
        light.setRadius(0);

        LightNode lightNode = new LightNode("Light", light);
        attachChild(lightNode);
        addLight(light);

        setLocalScale(1.0f);
    }

    private static ParticleEmitter _sparks, _burst, _shockwave, _fire, _smoke, _embers;
    
    public static void initialize(AssetManager assetManager) {
        createSparks(assetManager);
        createBurst(assetManager);
        createSmoke(assetManager);
        createFire(assetManager);
        createEmbers(assetManager);
        createShockwave(assetManager);
    }
    
    private static void createFire(AssetManager assetManager) {
        _fire = new ParticleEmitter("FireEmitter", ParticleMesh.Type.Triangle, 120);
        Material fire_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fire_mat.setTexture("Texture", assetManager.loadTexture("Effects/flame.png"));
        _fire.setInWorldSpace(false);
        _fire.setMaterial(fire_mat);
        _fire.setImagesX(2);
        _fire.setImagesY(2);
        _fire.setRandomAngle(true);

        _fire.setStartColor(new ColorRGBA(1f, 1f, .5f, 1f));
        _fire.setEndColor(new ColorRGBA(1f, 0f, 0f, 0f));
        _fire.setGravity(0, 0, 0);
        _fire.setStartSize(10f);
        _fire.setEndSize(0.5f);
        _fire.setLowLife(2f);
        _fire.setHighLife(15f);
        _fire.getParticleInfluencer().setVelocityVariation(3f);
        _fire.getParticleInfluencer().setInitialVelocity(new Vector3f(8f, 8f, 8f));
        _fire.setParticlesPerSec(0);
    }

    private static void createBurst(AssetManager assetManager) {
        _burst = new ParticleEmitter("BurstEmitter", Type.Triangle, 10);
        Material burst_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        burst_mat.setTexture("Texture", assetManager.loadTexture("Effects/flash.png"));
        _burst.setInWorldSpace(false);
        _burst.setMaterial(burst_mat);
        _burst.setImagesX(2);
        _burst.setImagesY(2);
        _burst.setSelectRandomImage(true);

        _burst.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1f));
        _burst.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        _burst.setStartSize(1f);
        _burst.setEndSize(20.0f);
        _burst.setGravity(0, 0, 0);
        _burst.setLowLife(1.5f);
        _burst.setHighLife(1.5f);
        _burst.getParticleInfluencer().setInitialVelocity(new Vector3f(4, 4f, 4));
        _burst.getParticleInfluencer().setVelocityVariation(1);
        _burst.setShape(new EmitterSphereShape(Vector3f.ZERO, .5f));
        _burst.setParticlesPerSec(0);
    }

    private static void createEmbers(AssetManager assetManager) {
        _embers = new ParticleEmitter("EmberEmitter", Type.Triangle, 40);
        Material embers_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        embers_mat.setTexture("Texture", assetManager.loadTexture("Effects/roundspark.png"));
        _embers.setInWorldSpace(false);
        _embers.setMaterial(embers_mat);
        _embers.setImagesX(1);
        _embers.setImagesY(1);

        _embers.setStartColor(new ColorRGBA(1f, 0.29f, 0.34f, 1.0f));
        _embers.setEndColor(new ColorRGBA(0, 0, 0, 0.5f));
        _embers.setStartSize(12f);
        _embers.setEndSize(18f);
        _embers.setGravity(0, 0, 0);
        _embers.setLowLife(8f);
        _embers.setHighLife(20f);
        _embers.getParticleInfluencer().setInitialVelocity(new Vector3f(3, 3, 3));
        _embers.getParticleInfluencer().setVelocityVariation(1f);
        _embers.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f));
        _embers.setParticlesPerSec(0);

    }

    public static ParticleEmitter sparks(AssetManager assetManager) {
        ParticleEmitter _sparks = new ParticleEmitter("SparksEmitter", Type.Triangle, 40);
        Material spark_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        spark_mat.setTexture("Texture", assetManager.loadTexture("Effects/spark.png"));
        _sparks.setInWorldSpace(false);
        _sparks.setMaterial(spark_mat);
        _sparks.setImagesX(1);
        _sparks.setImagesY(1);

        _sparks.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1.0f)); // orange
        _sparks.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        _sparks.getParticleInfluencer().setInitialVelocity(new Vector3f(10, 10, 10));
        _sparks.getParticleInfluencer().setVelocityVariation(3);
        _sparks.setFacingVelocity(true);
        // sparks.setGravity(0, 10, 0);
        _sparks.setStartSize(3f);
        _sparks.setEndSize(3f);
        _sparks.setLowLife(3f);
        _sparks.setHighLife(5f);
        _sparks.setParticlesPerSec(0);

        return _sparks;
    }

    public static ParticleEmitter blockSparks(AssetManager assetManager) {
        ParticleEmitter _sparks = new ParticleEmitter("BlockSparks", Type.Triangle, 20);
        Material spark_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        spark_mat.setTexture("Texture", assetManager.loadTexture("Effects/spark.png"));
        _sparks.setInWorldSpace(false);
        _sparks.setMaterial(spark_mat);
        _sparks.setImagesX(1);
        _sparks.setImagesY(1);

        _sparks.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1.0f)); // orange
        _sparks.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        _sparks.getParticleInfluencer().setInitialVelocity(new Vector3f(5, 5, 5));
        _sparks.getParticleInfluencer().setVelocityVariation(2);
        _sparks.setFacingVelocity(true);
        // sparks.setGravity(0, 10, 0);
        _sparks.setStartSize(3f);
        _sparks.setEndSize(3f);
        _sparks.setLowLife(1f);
        _sparks.setHighLife(2f);
        _sparks.setParticlesPerSec(0);

        return _sparks;
    }

    private static void createSparks(AssetManager assetManager) {
        _sparks = sparks(assetManager);
    }

    private static void createSmoke(AssetManager assetManager) {
        _smoke = new ParticleEmitter("SmokeEmitter", Type.Triangle, 50);
        Material smoke_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        smoke_mat.setTexture("Texture", assetManager.loadTexture("Effects/smoketrail.png"));
        _smoke.setInWorldSpace(false);

        _smoke.setMaterial(smoke_mat);
        _smoke.setImagesX(1);
        _smoke.setImagesY(3);
        _smoke.setSelectRandomImage(true);

        _smoke.setStartColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
        _smoke.setEndColor(new ColorRGBA(.1f, 0.1f, 0.1f, 0f));
        _smoke.setLowLife(4f);
        _smoke.setHighLife(20f);
        //smoke.setGravity(0, 1, 0);
        _smoke.setFacingVelocity(true);
        _smoke.getParticleInfluencer().setInitialVelocity(new Vector3f(10f, 10f, 10f));
        _smoke.getParticleInfluencer().setVelocityVariation(5);
        _smoke.setStartSize(5f);
        _smoke.setEndSize(1f);
        _smoke.setParticlesPerSec(0);
    }

    private static void createShockwave(AssetManager assetManager) {
        _shockwave = new ParticleEmitter("ShockwaveEmitter", Type.Triangle, 2);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/shockwave.png"));
        _shockwave.setInWorldSpace(false);
        _shockwave.setImagesX(1);
        _shockwave.setImagesY(1);
        _shockwave.setMaterial(mat);

        /* The shockwave faces upward (along the Y axis) to make it appear as
         * a horizontally expanding circle. */
        //shockwave.setFaceNormal(Vector3f.UNIT_Y);
        _shockwave.setStartColor(new ColorRGBA(.68f, 0.77f, 0.61f, 1f));
        _shockwave.setEndColor(new ColorRGBA(.68f, 0.77f, 0.61f, 0f));
        _shockwave.setStartSize(1f);
        _shockwave.setEndSize(40f);
        _shockwave.setGravity(0, 0, 0);
        _shockwave.setLowLife(2f);
        _shockwave.setHighLife(2f);
        _shockwave.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0));
        _shockwave.getParticleInfluencer().setVelocityVariation(0f);
        _shockwave.setParticlesPerSec(0);
    }
}
