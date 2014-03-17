package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import java.util.Arrays;
import java.util.Comparator;
import org.megastage.components.dcpu.DCPUHardware;
import org.megastage.components.dcpu.PowerConsumer;
import org.megastage.components.dcpu.PowerSupply;
import org.megastage.components.dcpu.VirtualPowerController;
import org.megastage.util.Mapper;
import org.megastage.util.Time;

public class PowerControllerSystem extends EntitySystem {
    private long interval;

    private long wakeup;
    private double delta;

    public PowerControllerSystem() {
        super(Aspect.getAspectForAll(VirtualPowerController.class));
    }

    public PowerControllerSystem(long interval) {
        this();
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        if(Time.value >= wakeup) {
            delta = (Time.value + interval - wakeup) / 1000.0;
            wakeup = Time.value + interval;
            return true;
        }
        return false;
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        for (int i = 0, s = entities.size; s > i; i++) {
            process(entities.get(i));
        }
    }

    protected void process(Entity entity) {
        VirtualPowerController ctrl = Mapper.VIRTUAL_POWER_CONTROLLER.get(entity);
        DCPUHardware[] hw = ctrl.dcpu.hardware.toArray(new DCPUHardware[ctrl.dcpu.hardware.size()]);
        Arrays.sort(hw, new Comparator<DCPUHardware>() {
            @Override
            public int compare(DCPUHardware o1, DCPUHardware o2) {
                if(o1.priority < o2.priority) return -1;
                if(o1.priority > o2.priority) return 1;
                return 0;
            }
        });
        
        double energy = 0;
        for(DCPUHardware comp: hw) {
            if(comp instanceof PowerSupply) {
                PowerSupply supply = (PowerSupply) comp;
                energy += supply.generatePower(delta);
            } 
        }
        
        for(DCPUHardware comp: hw) {
            if(comp instanceof PowerConsumer) {
                PowerConsumer consumer = (PowerConsumer) comp;
                
                double load = consumer.consumePower(delta);
                if(load > energy) {
                    consumer.shortage();
                } else {
                    energy -= load;
                }
            } 
        }
        
    }
}
