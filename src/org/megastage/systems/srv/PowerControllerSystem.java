package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.util.Log;
import java.util.Arrays;
import java.util.Comparator;
import org.megastage.components.dcpu.DCPU;
import org.megastage.components.dcpu.DCPUHardware;
import org.megastage.components.dcpu.PowerConsumer;
import org.megastage.components.dcpu.PowerSupply;
import org.megastage.components.dcpu.VirtualPowerController;
import org.megastage.ecs.CompType;
import org.megastage.util.ID;

public class PowerControllerSystem extends Processor {

    public PowerControllerSystem(World world, long interval) {
        super(world, interval, CompType.VirtualPowerController);
    }

    @Override
    protected void process(int eid) {
        VirtualPowerController ctrl = (VirtualPowerController) world.getComponent(eid, CompType.VirtualPowerController);
        DCPU dcpu = (DCPU) world.getComponent(ctrl.dcpuEID, CompType.DCPU);

        DCPUHardware[] hw = new DCPUHardware[dcpu.hardwareSize];
        for(int i=0; i < hw.length; i++) {
            hw[i] = (DCPUHardware) world.getComponent(dcpu.hardware[i], CompType.DCPUHardware);
        }
        
        Arrays.sort(hw);
        
        ctrl.supply = 0.0;
        for(DCPUHardware comp: hw) {
            if(comp instanceof PowerSupply) {
                PowerSupply supply = (PowerSupply) comp;
                ctrl.supply += supply.generatePower(delta);
            } 
        }
        
        //Log.info(ID.get(entity) + "total supply: " + ctrl.supply);
        double powerLeft = ctrl.supply;
        
        ctrl.load = 0.0;
        for(DCPUHardware comp: hw) {
            if(comp instanceof PowerConsumer) {
                PowerConsumer consumer = (PowerConsumer) comp;

                double consumption = consumer.consume(ctrl.shipEID, powerLeft, delta);
                
                ctrl.load += consumption;
                powerLeft -= consumption;
            } 
        }
    }
}
