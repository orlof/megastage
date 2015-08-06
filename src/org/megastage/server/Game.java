package org.megastage.server;

import org.megastage.util.Log;
import org.megastage.systems.srv.DCPUSystem;
import org.megastage.systems.srv.CleanupSystem;
import org.megastage.systems.srv.NetworkSystem;
import org.jdom2.Element;
import org.megastage.components.geometry.CharacterGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.KryoWorld;
import org.megastage.ecs.World;
import org.megastage.systems.srv.AttitudeControlSystem;
import org.megastage.systems.srv.CollisionSystem;
import org.megastage.systems.srv.EngineAccelerationSystem;
import org.megastage.systems.srv.EntityInitializeSystem;
import org.megastage.systems.srv.ExplosionSystem;
import org.megastage.systems.srv.PersistenceSystem;
import org.megastage.systems.srv.ShipMovementSystem;
import org.megastage.systems.srv.TargetManager;

public class Game {
    World world;
    private static long TICK_SPEED = 50;

    public Game() throws Exception {
        world = new KryoWorld();

        world.addProcessor(new EntityInitializeSystem(world, 0));
        world.addProcessor(new CleanupSystem(world, 0));
        world.addProcessor(new NetworkSystem(world, 0));
        world.addProcessor(new PersistenceSystem(world, 60000));

//        world.addProcessor(new PowerControllerSystem(world, 1000));
//        world.addProcessor(new OrbitalMovementSystem(world, 0));

          world.addProcessor(new EngineAccelerationSystem(world, 0));
          world.addProcessor(new AttitudeControlSystem(world, 0));
        //world.addProcessor(new GravityAccelerationSystem(world, ));

          world.addProcessor(new ShipMovementSystem(world, 0));
          world.addProcessor(new CollisionSystem(world, 0));
          world.addProcessor(new ExplosionSystem(world, 0));
        
//        world.addProcessor(new GravityManagerSystem(world, 5000));
        
//        world.addProcessor(new ForceFieldSystem(world, 1000));
//        world.addProcessor(new ThermalLaserSystem(world, 0));

        world.addProcessor(new DCPUSystem(world, 0));

        world.initialize();
        
        TargetManager.initialize();
        RadarManager.initialize();
        SoiManager.initialize();

        PrefabManager.initialize();
    }
    
    public void initializeNewWorld(Element root) {
        for(Element element: root.getChildren("entity")) {
            EntityFactory.create(world, element, 0);
        }
    }
    
    public void loopForever() throws InterruptedException {
        while (true) {
            long time = System.currentTimeMillis();
            world.tick(time);
            time = System.currentTimeMillis() - time;

            if(time < TICK_SPEED) {
                Log.trace("sleep for " + (TICK_SPEED-time));
                Thread.sleep(TICK_SPEED-time);
            }
        }
    }

    public boolean loadSavedWorld() {
        if(((KryoWorld) world).load()) {
            for(int eid=world.eidIter(); eid > 0; eid = world.eidNext()) {
                if(world.hasComponent(eid, CompType.CharacterGeometry)) {
                    CharacterGeometry cg = (CharacterGeometry) world.getComponent(eid, CompType.CharacterGeometry);
                    cg.isFree = true;
                }
            }

            return true;
        }
        return false;
    }
}
