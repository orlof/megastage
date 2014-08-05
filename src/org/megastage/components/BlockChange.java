package org.megastage.components;

import com.cubes.Block;
import com.cubes.BlockTerrainControl;
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
import org.megastage.util.Cube3dMap;
import org.megastage.util.ID;

public class BlockChange extends ReplicatedComponent {
    public static final transient char UNBUILD = 0;
    public static final transient char BREAK = 1;
    public static final transient char BUILD = 2;

    public int x, y, z;
    public char type;
    public char event;

    public BlockChange() {}

    public BlockChange(int x, int y, int z, char value, char event) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = value;
        this.event = event;
    }

    @Override
    public void receive(int eid) {
        Log.info(ID.get(eid) + toString());

        ShipGeometry sg = (ShipGeometry) World.INSTANCE.getComponent(eid, CompType.ShipGeometry);
        Cube3dMap theMap = sg.map;
        theMap.set(x, y, z, type);

        EntityNode node = SpatialManager.getOrCreateNode(eid);
        BlockTerrainControl ctrl = node.offset.getControl(BlockTerrainControl.class);

        if(type == 0) {
            ctrl.removeBlock(x, y, z);
            if(event == BlockChange.BREAK) {
                ParticleEmitter pe = (ParticleEmitter) node.getChild("BlockSparks");

                SoundManager.get(SoundManager.EXPLOSION_3).playInstance();

                pe.killAllParticles();
                pe.setLocalTranslation(x, y, z);
                pe.emitAllParticles();
                //pe.addControl(new DeleteControl(3000));
            }
        } else {
            Class<? extends Block> block = CubesManager.getBlock(type);
            ctrl.setBlock(x, y, z, block);
        }
    }
}
