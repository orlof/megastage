package org.megastage.systems.client;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.client.ClientGlobals;
import org.megastage.components.Position;
import org.megastage.components.gfx.ImposterGeometry;
import org.megastage.util.Time;
import org.megastage.util.Vector3d;

public class ImposterSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Position> POSITION;
    @Mapper ComponentMapper<ImposterGeometry> IMPOSTER_GEOMETRY;

    public ImposterSystem(long interval) {
        super(Aspect.getAspectForAll(ImposterGeometry.class, Position.class));
        this.interval = interval;
    }

    private long interval;
    private long acc;
    
    @Override
    protected boolean checkProcessing() {
        if(ClientGlobals.shipEntity != null && POSITION.has(ClientGlobals.shipEntity) && Time.value >= acc) {
            Log.info("" + true);
            acc = Time.value + interval;
            return true;
        }
        return false;
    }

    private Vector3d origo;
    
    @Override
    protected void begin() {
        origo = POSITION.get(ClientGlobals.shipEntity).getVector3d();
        Log.info(origo.toString());
    }

    @Override
    protected void process(Entity e) {
        Log.info("");
        Vector3d coord = POSITION.get(e).getVector3d();
        double cutoff = IMPOSTER_GEOMETRY.get(e).cutoff;

        boolean visible = origo.distance(coord) < cutoff;
        
        ClientGlobals.spatialManager.imposter(e, visible);
    }
	
}
