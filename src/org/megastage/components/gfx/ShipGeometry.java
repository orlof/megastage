package org.megastage.components.gfx;

import com.cubes.Block;
import com.cubes.BlockTerrainControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.client.CubesManager;
import org.megastage.client.EntityNode;
import org.megastage.client.ExplosionNode;
import org.megastage.client.SpatialManager;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.components.Mass;
import org.megastage.components.srv.BlockChanges;
import org.megastage.components.srv.CollisionType;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
import org.megastage.util.Cube3dMap;
import org.megastage.util.Log;


public class ShipGeometry extends ReplicatedComponent {
    public Cube3dMap map = new Cube3dMap();
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {

        createMapFromXML(element);
        
        BaseComponent[] extraComponents = new BaseComponent[] {
            Mass.create(map.getMass()),
            CollisionType.create(CollisionType.SHIP, map.getCollisionRadius()),
            new BlockChanges(),
        };
        
        return extraComponents;
    }

    @Override
    public void receive(int eid) {
        super.receive(eid);

        EntityNode node = SpatialManager.getOrCreateNode(eid);
        node.addControl(new PositionControl(eid));
        node.addControl(new RotationControl(eid));

        initGeometry(node.offset, eid);
        node.setOffset(map.getCenterOfMass());
        Log.info(map.getCenterOfMass().toString());

        ClientGlobals.globalRotationNode.attachChild(node);
    }

    protected void initGeometry(Node node, int eid) {
        Vector3f centerOfMass = map.getCenterOfMass();
        node.setLocalTranslation(centerOfMass.negateLocal());

        // convert block map to Cubes control
        BlockTerrainControl blockControl = CubesManager.getControl(map);
        for(int x = 0; x <= map.xsize; x++) {
            for(int y = 0; y <= map.ysize; y++) {
                for(int z = 0; z <= map.zsize; z++) {
                    char c = map.get(x, y, z);
                    Class<? extends Block> block = CubesManager.getBlock(c);
                    if(block != null) {
                        blockControl.setBlock(x, y, z, block);
                    }
                }
            }
        }
        node.addControl(blockControl);
        
        if(ClientGlobals.gfxSettings.ENABLE_SHIP_SHADOWS) {
            node.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        }
        
        ParticleEmitter pe = ExplosionNode.blockSparks(ClientGlobals.app.getAssetManager());
        node.attachChild(pe);
        pe.setLocalTranslation(centerOfMass);
    }

    @Override
    public void delete(int eid) {
        EntityNode shipNode = SpatialManager.getOrCreateNode(eid);
        shipNode.removeFromParent();
    }

    public float getInertia(Vector3f axis) {
        return map.getInertia(axis);
    }

    public void createMapFromXML(Element element) throws DataConversionException {
        int y = 0, z = 0;
        
        List<Element> mapElements = element.getChildren("map");
        for(Element elem: mapElements) {
            Attribute attr = elem.getAttribute("y");
            if(attr != null) {
                y = attr.getIntValue();
            }

            attr = elem.getAttribute("z");
            if(attr != null) {
                z = attr.getIntValue();
            }
            
            String blocks = elem.getText();
            for(int x=0; x < blocks.length(); x++) {
                char c = blocks.charAt(x);
                if(c != ' ') {
                    map.set(x, y, z, c);
                }
            }
        }
    }
}
