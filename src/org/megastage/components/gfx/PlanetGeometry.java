package org.megastage.components.gfx;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import jmeplanet.FractalDataSource;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.client.JME3Material;
import static org.megastage.client.SpatialManager.getOrCreateNode;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class PlanetGeometry extends CelestialGeometryComponent {
    public int center;
    public float radius;
    public String generator;
    public String color;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        center = parentEid;

        radius = getFloatValue(element, "radius", 10.0f);
        generator = getStringValue(element, "generator", "Earth");
        color = getStringValue(element, "color", "red");
        
        return null;
    }
    
    @Override
    protected void initGeometry(Node node, int eid) {
        if(ClientGlobals.gfxSettings.ENABLE_PLANETS) {
            node.attachChild(createJMEPlanet());
        } else {
            node.attachChild(createSimplePlanet());
        }
    }

    public Planet createJMEPlanet() {
        Planet planet = null;
        switch(generator) {
            case "Earth":
                planet = Utility.createEarthLikePlanet(radius);
                break;
            case "Moon":
                planet = Utility.createMoonLikePlanet(radius);
                break;
            case "Water":
                planet = Utility.createWaterPlanet(radius);
                break;
            default:
                throw new RuntimeException("Unknwon generator: " + generator);
        }

        PlanetAppState appState = ClientGlobals.app.getStateManager().getState(PlanetAppState.class);
        if(appState != null) appState.addPlanet(planet);
        
        return planet;
    }

    private Spatial createSimplePlanet() {
        try {
            Geometry geom = createSphere(radius);

            ColorRGBA col = (ColorRGBA) ColorRGBA.class.getDeclaredField(this.color).get(null);
            JME3Material.setTexturedMaterial(geom, col, "rock.jpg");
            
            return geom;
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

}
