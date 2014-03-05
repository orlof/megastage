package org.megastage.client;

import com.jme3.app.SimpleApplication;
import com.jme3.light.*;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/** One normal sphere next to two glowing spheres.
 * One has a black-and-white chequered glow map, 
 * the other has a colored random noise glow map. */
public class DomeTestApp extends SimpleApplication {

  public static void main(String[] args) {
    DomeTestApp app = new DomeTestApp();
    app.start();
  }

  @Override
  public void simpleInitApp() {
    
    FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
    BloomFilter bloom= new BloomFilter(BloomFilter.GlowMode.Objects);
    fpp.addFilter(bloom);
    viewPort.addProcessor(fpp);
    
    Sphere sphere = new Sphere(32,32, 1f);
    
    Geometry shiny_sphere1 = new Geometry("normal sphere", sphere);
    Material mat_lit1 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat_lit1.setBoolean("UseMaterialColors",true);
    mat_lit1.setColor("Ambient", ColorRGBA.Cyan );
    mat_lit1.setColor("Diffuse", ColorRGBA.Cyan );
    shiny_sphere1.setMaterial(mat_lit1);
    shiny_sphere1.move(-2.5f, 0, 0);
    rootNode.attachChild(shiny_sphere1); 
    
    Geometry shiny_sphere2 = new Geometry("black and white chequered", sphere);
    Material mat_lit2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat_lit2.setBoolean("UseMaterialColors",true);
    mat_lit2.setColor("Diffuse", ColorRGBA.Cyan );
    mat_lit2.setColor("Ambient", ColorRGBA.Cyan );
//    mat_lit2.setTexture("GlowMap", assetManager.loadTexture("Textures/bloom-glow2.png"));
    mat_lit2.setColor("GlowColor", ColorRGBA.Red );
    shiny_sphere2.setMaterial(mat_lit2);
    rootNode.attachChild(shiny_sphere2); 
    
    Geometry shiny_sphere3 = new Geometry("random color noise", sphere);
    Material mat_lit3 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat_lit3.setBoolean("UseMaterialColors",true);
    mat_lit3.setColor("Diffuse", ColorRGBA.Cyan );
    mat_lit3.setColor("Ambient", ColorRGBA.Cyan );
//    mat_lit3.setTexture("GlowMap", assetManager.loadTexture("Textures/bloom-glow.png"));
    mat_lit3.setColor("GlowColor", ColorRGBA.White );
    shiny_sphere3.setMaterial(mat_lit3);
    shiny_sphere3.move(2.5f, 0, 0);
    rootNode.attachChild(shiny_sphere3); 
    
    /** Must add a light to make the lit object visible! */
    DirectionalLight sun = new DirectionalLight();
    sun.setDirection(new Vector3f(1, 0, -2).normalizeLocal());
    sun.setColor(ColorRGBA.White);
    rootNode.addLight(sun);
    
  }
}
