package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.dcpu.*;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class DCPUSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<DCPU> dcpuMapper;

    public DCPUSystem() {
        super(Aspect.getAspectForAll(DCPU.class));
    }

    @Override
    protected void process(Entity entity) {
        DCPU dcpu = dcpuMapper.get(entity);
        dcpu.run_ticks();
    }


}