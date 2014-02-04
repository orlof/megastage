/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.util;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;



public class ExplosionTestApp extends SimpleApplication {

    private float time = 0;
    private int state = 0;
    private Geometry crate;
    private Node explosionEffect = new Node("explosionFX");
    private ParticleEmitter sparks, burst,
            shockwave, debris,
            fire, smoke, embers;

    public static void main(String[] args) {
        ExplosionTestApp app = new ExplosionTestApp();
        app.start();
    }

    private void createFire() {
        fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 200);
        Material fire_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fire_mat.setTexture("Texture", assetManager.loadTexture("Effects/flame.png"));
        fire.setMaterial(fire_mat);
        fire.setImagesX(2);
        fire.setImagesY(2);
        fire.setRandomAngle(true);
        rootNode.attachChild(fire);

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

    private void createBurst() {
        burst = new ParticleEmitter("Flash", Type.Triangle, 5);
        Material burst_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        burst_mat.setTexture("Texture", assetManager.loadTexture("Effects/flash.png"));
        burst.setMaterial(burst_mat);
        burst.setImagesX(2);
        burst.setImagesY(2);
        burst.setSelectRandomImage(true);
        rootNode.attachChild(burst);

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

    private void createEmbers() {
        embers = new ParticleEmitter("embers", Type.Triangle, 50);
        Material embers_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        embers_mat.setTexture("Texture", assetManager.loadTexture("Effects/roundspark.png"));
        embers.setMaterial(embers_mat);
        embers.setImagesX(1);
        embers.setImagesY(1);
        rootNode.attachChild(embers);

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

    private void createSparks() {
        sparks = new ParticleEmitter("Spark", Type.Triangle, 20);
        Material spark_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        spark_mat.setTexture("Texture", assetManager.loadTexture("Effects/spark.png"));
        sparks.setMaterial(spark_mat);
        sparks.setImagesX(1);
        sparks.setImagesY(1);
        rootNode.attachChild(sparks);

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

    private void createSmoke() {
        smoke = new ParticleEmitter("Smoke emitter", Type.Triangle, 40);
        Material smoke_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        smoke_mat.setTexture("Texture", assetManager.loadTexture("Effects/smoketrail.png"));
        smoke.setMaterial(smoke_mat);
        smoke.setImagesX(2);
        smoke.setImagesY(2);
        smoke.setSelectRandomImage(true);
        rootNode.attachChild(smoke);

        debris.setStartColor(new ColorRGBA(0.5f, 0.5f, 0.5f, .3f));
        debris.setEndColor(new ColorRGBA(.1f, 0.1f, 0.1f, 0f));
        debris.setLowLife(4f);
        debris.setHighLife(5f);
        smoke.setRandomAngle(true);
        smoke.getParticleInfluencer().setInitialVelocity(new Vector3f(0, .5f, 0));
        smoke.getParticleInfluencer().setVelocityVariation(1);

        smoke.setParticlesPerSec(0);
    }

    private void createDebris() {
        debris = new ParticleEmitter("Debris", Type.Triangle, 15);
        Material debris_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        debris_mat.setTexture("Texture", assetManager.loadTexture("Effects/debris.png"));
        debris.setMaterial(debris_mat);
        debris.setImagesX(3);
        debris.setImagesY(3);
        debris.setSelectRandomImage(false);
        rootNode.attachChild(debris);

        debris.setRandomAngle(true);
        debris.setRotateSpeed(FastMath.TWO_PI * 4);
        debris.setStartColor(new ColorRGBA(0.4f, 0.4f, 0.0f, 1.0f));
        debris.setEndColor(new ColorRGBA(0.4f, 0.4f, 0.0f, 1.0f));
        debris.setStartSize(.2f);
        debris.setEndSize(2f);
        debris.setGravity(0, 15f, 0);
        debris.setLowLife(2f);
        debris.setHighLife(3f);
        debris.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 7, 0));
        debris.getParticleInfluencer().setVelocityVariation(.60f);
        debris.setParticlesPerSec(0);

    }

    private void createShockwave() {
        shockwave = new ParticleEmitter("Shockwave", Type.Triangle, 2);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/shockwave.png"));
        shockwave.setImagesX(1);
        shockwave.setImagesY(1);
        shockwave.setMaterial(mat);
        explosionEffect.attachChild(shockwave);

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

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(0f, 10f, 10f)); // get a better view
        cam.lookAt(Vector3f.UNIT_Y, Vector3f.ZERO);
        createSparks();
        createBurst();
        createDebris();
        createSmoke();
        createFire();
        createEmbers();
        createShockwave();
        explosionEffect.setLocalScale(0.5f);
        rootNode.attachChild(explosionEffect);
    }

    @Override
    public void simpleUpdate(float tpf) {
        time += tpf / speed;
        if (time > 1f && state == 0) {
            sparks.emitAllParticles();
            state++;
        }
        if (time > 2f && state == 1) {
            burst.emitAllParticles();
            debris.emitAllParticles();
            state++;
        }
        if (time > 2f + .05f / speed && state == 2) {
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
        if (time > 10 / speed && state == 4) {
            // restart the effect
            state = 0;
            time = 0;
        }
    }
}
