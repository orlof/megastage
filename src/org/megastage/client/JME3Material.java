package org.megastage.client;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

public class JME3Material {

    public static Material getLighting(ColorRGBA color) {
        Material mat = getMaterial("Common/MatDefs/Light/Lighting.j3md");

        mat.setBoolean("UseMaterialColors", true);
        
        ColorRGBA c = new ColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * .3f);
        mat.setColor("Ambient", c);
        mat.setColor("Diffuse", c);

        return mat;
    }
    
    public static Material getUnshaded(ColorRGBA color) {
        Material mat = getMaterial("Common/MatDefs/Misc/Unshaded.j3md");

        ColorRGBA c = new ColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * .3f);
        mat.setColor("Color", color);
        
        return mat;           
    }
    
    public static Material getWithTexture(String name) {
        Material mat = getLighting(ColorRGBA.Gray);
        mat.setTexture("DiffuseMap", getTexture(name));
        return mat;
    }
    
    public static Material getMaterial(String filename) {
        return new Material(ClientGlobals.app.getAssetManager(), filename);
    }

    public static Texture getTexture(String name) {
        return ClientGlobals.app.getAssetManager().loadTexture("Textures/" + name);
    }
}
