package org.megastage.components.gfx;

import com.cubes.Block;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.jme3.effect.ParticleEmitter;
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
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;
import org.megastage.util.Ship;


public class ShipGeometry extends BaseComponent {

    public static Ship getShip(int eid) throws ECSException {
        ShipGeometry sg = (ShipGeometry) World.INSTANCE.getComponentOrError(eid, CompType.ShipGeometry);
        return sg.ship;
    }

    public Ship ship = new Ship(32);
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {

        createMapFromXML(element);
        
        BaseComponent[] extraComponents = new BaseComponent[] {
            Mass.create(ship.getMass()),
            CollisionType.create(CollisionType.SHIP, ship.getCollisionRadius()),
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

        initGeometry(node.offset);
        node.setOffset(ship.getCenterOfMass());

        initExplosion(node);
        
        ClientGlobals.globalRotationNode.attachChild(node);

        if(ClientGlobals.gfxSettings.ENABLE_SHIP_SHADOWS) {
            node.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        }
    }

    public void initGeometry(Node node) {
        int size = ship.getSize();

        node.removeControl(BlockTerrainControl.class);
        BlockTerrainControl ctrl = CubesManager.getControl(size);
        node.addControl(ctrl);

        // convert block componentName to Cubes control
        for(int x = 0; x <= size; x++) {
            for(int y = 0; y <= size; y++) {
                for(int z = 0; z <= size; z++) {
                    char c = ship.getBlock(x, y, z);
                    Class<? extends Block> block = CubesManager.getBlock(c);
                    if(block != null) {
                        ctrl.setBlock(x, y, z, block);
                    }
                }
            }
        }
    }

    private void initExplosion(EntityNode node) {
        ParticleEmitter pe = ExplosionNode.blockSparks(ClientGlobals.app.getAssetManager());
        node.attachChild(pe);
    }

    @Override
    public void delete(int eid) {
        EntityNode shipNode = SpatialManager.getOrCreateNode(eid);
        shipNode.removeFromParent();
    }

    public float getXAxisInertia() {
        return ship.getXAxisInertia();
    }

    public float getYAxisInertia() {
        return ship.getYAxisInertia();
    }

    public float getZAxisInertia() {
        return ship.getZAxisInertia();
    }

    public void createMapFromXML(Element element) throws DataConversionException {
        Vector3Int vec = new Vector3Int();
        List<Element> mapElements = element.getChildren("componentName");
        for(Element elem: mapElements) {
            Attribute attr = elem.getAttribute("y");
            if(attr != null) {
                vec.setY(attr.getIntValue());
            }

            attr = elem.getAttribute("z");
            if(attr != null) {
                vec.setZ(attr.getIntValue());
            }
            
            String blocks = elem.getText();
            for(int x=0; x < blocks.length(); x++) {
                vec.setX(x);
                char c = blocks.charAt(x);
                if(c != ' ') {
                    ship.setBlock(vec, c);
                }
            }
        }
    }
}
