package org.megastage.systems.client;

import com.jme3.math.Vector3f;
import org.megastage.client.ClientGlobals;
import org.megastage.client.SpatialManager;
import org.megastage.components.Position;
import org.megastage.components.gfx.ImposterGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.util.Vector3d;

public class ImposterSystem extends Processor {
    public ImposterSystem(World world, long interval) {
        super(world, interval, CompType.ImposterGeometry, CompType.Position);
    }

    @Override
    protected boolean checkProcessing() {
        if(super.checkProcessing()) {
            if(ClientGlobals.baseEntity != 0 && world.hasComponent(ClientGlobals.baseEntity, CompType.Position)) {
                return true;
            }
        }
        return false;
    }

    private Vector3f origo;
    
    @Override
    protected void begin() {
        origo = ((Position) world.getComponent(ClientGlobals.baseEntity, CompType.Position)).get();
    }

    @Override
    protected void process(int eid) {
        Position pos = (Position) world.getComponent(eid, CompType.Position);
        Vector3f coord = pos.get();

        ImposterGeometry imp = (ImposterGeometry) world.getComponent(eid, CompType.ImposterGeometry);
        double cutoff = imp.cutoff;

        double d = origo.distance(coord);
        boolean visible = d < cutoff;
        
        SpatialManager.imposter(eid, visible);
    }
}
