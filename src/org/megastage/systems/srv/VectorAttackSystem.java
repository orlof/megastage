package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.util.Log;
import java.util.Random;
import org.megastage.components.dcpu.VirtualForceField;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.components.srv.VectorAttack;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.Processor;
import org.megastage.server.ForceFieldHit;
import org.megastage.server.Hit;
import org.megastage.server.NoHit;
import org.megastage.server.ShipStructureHit;

public class VectorAttackSystem extends Processor {
    Random random = new Random();

    public VectorAttackSystem(World world, long interval) {
        super(world, interval, CompType.VectorAttack);
    }

    @Override
    protected void process(int eid) throws ECSException {
        VectorAttack vecAttComp = (VectorAttack) world.getComponent(eid, CompType.VectorAttack);
        
        if(vecAttComp.isEnabled()) {
            Hit hit = TargetManager.vectorAttackHit(eid);

            if(hit instanceof NoHit) {
                //vtlComponent.setHit(0f);
            } else if(hit instanceof ForceFieldHit) {
                ForceFieldHit ffhit = (ForceFieldHit) hit;

                VirtualForceField forceField = (VirtualForceField) world.getComponent(ffhit.eid, CompType.VirtualForceField);
                forceField.damage(world.delta * vecAttComp.damageRate);

                // vtlComponent.setHit((float) hit.distance);

            } else if(hit instanceof ShipStructureHit) {
                ShipStructureHit shit = (ShipStructureHit) hit;
                ShipGeometry geom = (ShipGeometry) world.getComponent(shit.eid, CompType.ShipGeometry);

                // vtlComponent.setHit((float) hit.distance);

                double shotPower = world.delta * vecAttComp.damageRate;
                if(shotPower > 50.0 * random.nextDouble()) {
                    geom.ship.setBlock(shit.block.getX(), shit.block.getY(), shit.block.getZ(), (char) 0);
                }

            } else {
                Log.error("Unknown hit target: " + hit.toString());
            }
        }
    }
}
