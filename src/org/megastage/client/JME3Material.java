package org.megastage.client;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

public class JME3Material {
    public static Material getBasicMaterial(String filename) {
        return new Material(ClientGlobals.app.getAssetManager(), filename);
    }

    public static void setBasicMaterial(Spatial spatial, String filename) {
        spatial.setMaterial(getBasicMaterial(filename));
    }

    public static Material getUnshadedMaterial(ColorRGBA color) {
        Material mat = getBasicMaterial("Common/MatDefs/Misc/Unshaded.j3md");

        // color = new ColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * .3f);
        mat.setColor("Color", color);
        
        return mat;
    }
    
    public static void setUnshadedMaterial(Spatial spatial, ColorRGBA color) {
        spatial.setMaterial(getUnshadedMaterial(color));
    }
    
    public static Material getGlowingMaterial(ColorRGBA color) {
        Material mat = getUnshadedMaterial(color);
        mat.setColor("GlowColor", ColorRGBA.Yellow);
        return mat;
    }
    
    public static void setGlowingMaterial(Spatial spatial, ColorRGBA color) {
        spatial.setMaterial(getGlowingMaterial(color));
    }
    
    public static Material getLightingMaterial(ColorRGBA color) {
        Material mat = getBasicMaterial("Common/MatDefs/Light/Lighting.j3md");

        // color = new ColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * .3f);
        mat.setColor("Ambient", color);
        mat.setColor("Diffuse", color);
        
        mat.setBoolean("UseMaterialColors", true);
        
        return mat;
    }
    
    public static void setLightingMaterial(Spatial spatial, ColorRGBA color) {
        spatial.setMaterial(getLightingMaterial(color));
    }
    
    public static Material getTexturedMaterial(ColorRGBA color, String filename) {
        Material mat = getLightingMaterial(color);
        mat.setTexture("DiffuseMap", getTexture(filename));
        return mat;
    }
    
    public static void setTexturedMaterial(Geometry geom, ColorRGBA color, String filename) {
        geom.setMaterial(getTexturedMaterial(color, filename));
    }

    public static Texture getTexture(String name) {
        return ClientGlobals.app.getAssetManager().loadTexture("Textures/" + name);
    }
}
