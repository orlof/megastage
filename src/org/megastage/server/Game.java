package org.megastage.server;

import com.esotericsoftware.minlog.Log;
import org.megastage.systems.srv.AttitudeControlSystem;
import org.megastage.systems.srv.DCPUSystem;
import org.megastage.systems.srv.EngineAccelerationSystem;
import org.megastage.systems.srv.GravityManagerSystem;
import org.megastage.systems.srv.CleanupSystem;
import org.megastage.systems.srv.NetworkSystem;
import org.megastage.systems.srv.EntitySynchronizeSystem;
import org.jdom2.Element;
import org.megastage.ecs.World;
import org.megastage.systems.*;

import org.megastage.systems.srv.CollisionSystem;
import org.megastage.systems.srv.EntityDeleteSystem;
import org.megastage.systems.srv.EntityReplicateToAllSystem;
import org.megastage.systems.srv.ExplosionSystem;
import org.megastage.systems.srv.EntityInitializeSystem;
import org.megastage.systems.srv.ForceFieldSystem;
import org.megastage.systems.srv.PowerControllerSystem;
import org.megastage.systems.srv.RadarManagerSystem;
import org.megastage.systems.srv.TargetManagerSystem;
import org.megastage.systems.srv.ShipMovementSystem;
import org.megastage.systems.srv.SoiManagerSystem;
import org.megastage.systems.srv.ThermalLaserSystem;

public class Game {
    World world;

    public Game(Element root) throws Exception {
        world = new World();

        world.addProcessor(new EntityInitializeSystem(world, 500));
        world.addProcessor(new CleanupSystem(world, 500));
        world.addProcessor(new EntityDeleteSystem(world, 50));
        world.addProcessor(new EntityReplicateToAllSystem(world, 50));
        world.addProcessor(new EntitySynchronizeSystem(world, 50));
        world.addProcessor(new NetworkSystem(world, 0));

        world.addProcessor(new PowerControllerSystem(world, 1000));
        
        world.addProcessor(new OrbitalMovementSystem(world, 0));

        world.addProcessor(new EngineAccelerationSystem(world, 0));
        world.addProcessor(new AttitudeControlSystem(world, 0));
        //world.addProcessor(new GravityAccelerationSystem(world, ));

        world.addProcessor(new ShipMovementSystem(world, 0));
        world.addProcessor(new CollisionSystem(world, 200));
        world.addProcessor(new ExplosionSystem(world, 201));
        
        world.addProcessor(new GravityManagerSystem(world, 5000));
        world.addProcessor(new RadarManagerSystem(world, 1000));
        world.addProcessor(new SoiManagerSystem(world, 10000));
        world.addProcessor(new TargetManagerSystem(world, 0));
        
        world.addProcessor(new ForceFieldSystem(world, 1000));
        world.addProcessor(new ThermalLaserSystem(world, 0));

        world.addProcessor(new DCPUSystem(world, 0));

        world.initialize();
        
        for(Element element: root.getChildren("entity")) {
            EntityFactory.create(world, element, 0);
        }

        for(Element element: root.getChildren("entity-template")) {
            TemplateManager.addTemplate(element);
        }
    }

    public void loopForever() throws InterruptedException {
        while (true) {
            long ctime = System.currentTimeMillis();
            world.setGametime(ctime);

            world.tick();
            
            long tpt = System.currentTimeMillis() - ctime;
            if(tpt < 20) {
                Log.trace("sleep for " + (20-tpt));
                Thread.sleep(20-tpt);
            }
        }
    }
}
