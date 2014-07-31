package org.megastage.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.megastage.util.Log;
import org.megastage.systems.srv.DCPUSystem;
import org.megastage.systems.srv.CleanupSystem;
import org.megastage.systems.srv.NetworkSystem;
import org.jdom2.Element;
import org.megastage.components.gfx.CharacterGeometry;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.protocol.Network;
import org.megastage.systems.srv.AttitudeControlSystem;
import org.megastage.systems.srv.CollisionSystem;
import org.megastage.systems.srv.EngineAccelerationSystem;
import org.megastage.systems.srv.EntityInitializeSystem;
import org.megastage.systems.srv.ExplosionSystem;
import org.megastage.systems.srv.PersistenceSystem;
import org.megastage.systems.srv.ShipMovementSystem;

public class Game {
    World world;
    private static long TICK_SPEED = 50;

    public Game(Element root) throws Exception {
        world = new World();

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
//        world.addProcessor(new RadarManagerSystem(world, 1000));
//        world.addProcessor(new SoiManagerSystem(world, 10000));
//        world.addProcessor(new TargetManagerSystem(world, 0));
        
//        world.addProcessor(new ForceFieldSystem(world, 1000));
//        world.addProcessor(new ThermalLaserSystem(world, 0));

        world.addProcessor(new DCPUSystem(world, 0));

        world.initialize();
        
        for(Element element: root.getChildren("entity-template")) {
            TemplateManager.addTemplate(element);
        }
    }
    
    public void initializeNewWorld(Element root) {
        for(Element element: root.getChildren("entity")) {
            EntityFactory.create(world, element, 0);
        }
    }
    
    public boolean loadSavedWorld() {
        File latest = null;
        for(File file: PersistenceSystem.files) {
            if(file.exists() && (latest == null || file.lastModified() > latest.lastModified())) {
                latest = file;
            }
        }
        
        if(latest != null) {
            try {
                Kryo kryo = new Kryo();
                Network.register(kryo);
                
                try (Input input = new Input(new FileInputStream(latest))) {
                    World tmp = (World) kryo.readClassAndObject(input);
                    world.size = tmp.size;
                    world.population = tmp.population;
                    world.next = tmp.next;
                    world.prev = tmp.prev;
                    world.free = tmp.free;
                }
                world.updateAll();
                
                for(int i=0; i < world.population.length; i++) {
                    if(world.population[i][CompType.CharacterGeometry] != null) {
                        ((CharacterGeometry) (world.population[i][CompType.CharacterGeometry])).isFree = true;
                    }
                }
                
                return true;
            } catch (FileNotFoundException ex) {
                Log.error(ex);
                return false;
            }
        }
        
        return false;
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
}
