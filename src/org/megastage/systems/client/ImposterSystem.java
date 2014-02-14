package org.megastage.systems.client;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.client.ClientGlobals;
import org.megastage.components.Position;
import org.megastage.components.gfx.ImposterGeometry;
import org.megastage.util.ID;
import org.megastage.util.Time;
import org.megastage.util.Vector3d;

public class ImposterSystem extends EntityProcessingSystem {
    ComponentMapper<Position> POSITION;
    ComponentMapper<ImposterGeometry> IMPOSTER_GEOMETRY;

    public ImposterSystem(long interval) {
        super(Aspect.getAspectForAll(ImposterGeometry.class, Position.class));
        this.interval = interval;
    }

    @Override
    public void initialize() {
        
        IMPOSTER_GEOMETRY = world.getMapper(ImposterGeometry.class);
        POSITION = world.getMapper(Position.class);
    }

private long interval;
    private long acc;
    
    @Override
    protected boolean checkProcessing() {
        if(ClientGlobals.shipEntity != null && POSITION.has(ClientGlobals.shipEntity) && Time.value >= acc) {
            acc = Time.value + interval;
            return true;
        }
        return false;
    }

    private Vector3d origo;
    
    @Override
    protected void begin() {
        origo = POSITION.get(ClientGlobals.shipEntity).getVector3d();
    }

    @Override
    protected void process(Entity e) {
        Vector3d coord = POSITION.get(e).getVector3d();
        double cutoff = IMPOSTER_GEOMETRY.get(e).cutoff;

        double d = origo.distance(coord);
        boolean visible = d < cutoff;
        
        ClientGlobals.spatialManager.imposter(e, visible);
    }
	
}
