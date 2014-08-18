package org.megastage.components;

import com.cubes.Block;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import org.megastage.util.Log;
import com.jme3.effect.ParticleEmitter;
import org.megastage.client.CubesManager;
import org.megastage.client.EntityNode;
import org.megastage.client.SoundManager;
import org.megastage.client.SpatialManager;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
import org.megastage.util.ID;
import org.megastage.util.Ship;

public class BlockChange extends ReplicatedComponent {
    public enum Event { BUILD, UNBUILD, BREAK }

    public int x, y, z;
    public Event event;
    public char value;

    public BlockChange() {}

    public BlockChange(int x, int y, int z, char value, Event event) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.value = value;
        this.event = event;
    }

    @Override
    public void receive(int eid) {
        Log.info(ID.get(eid) + toString());

        ShipGeometry sg = (ShipGeometry) World.INSTANCE.getComponent(eid, CompType.ShipGeometry);
        Ship ship = sg.ship;
        
        int majorVersion = ship.majorVersion;
        ship.setBlock(new Vector3Int(x, y, z), value);

        EntityNode node = SpatialManager.getOrCreateNode(eid);
        BlockTerrainControl ctrl = node.offset.getControl(BlockTerrainControl.class);

        if(majorVersion == ship.majorVersion) {
            if(value == 0) {
                ctrl.removeBlock(x, y, z);
            } else {
                Class<? extends Block> block = CubesManager.getBlock(value);
                ctrl.setBlock(x, y, z, block);
            }
        } else {
            sg.initGeometry(node.offset);
        }

        if(event == Event.BREAK) {
            ParticleEmitter pe = (ParticleEmitter) node.getChild("BlockSparks");

            SoundManager.get(SoundManager.EXPLOSION_3).playInstance();

            pe.killAllParticles();
            pe.setLocalTranslation(x, y, z);
            pe.emitAllParticles();
            //pe.addControl(new DeleteControl(3000));
        }
    }
}
