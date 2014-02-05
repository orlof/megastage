package org.megastage.server;

import org.megastage.systems.srv.AttitudeControlSystem;
import org.megastage.systems.srv.DCPUSystem;
import org.megastage.systems.srv.EngineAccelerationSystem;
import org.megastage.systems.srv.GravityAccelerationSystem;
import org.megastage.systems.srv.GravityFieldSystem;
import org.megastage.systems.srv.CleanupSystem;
import org.megastage.systems.srv.NetworkSystem;
import org.megastage.systems.srv.SynchronizeSystem;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.systems.*;

import java.io.IOException;
import org.megastage.systems.srv.CollisionSystem;
import org.megastage.systems.srv.ExplosionSystem;
import org.megastage.systems.srv.InitializeSystem;
import org.megastage.systems.srv.PrevPositionSystem;
import org.megastage.systems.srv.RadarEchoSystem;
import org.megastage.systems.srv.ShipMovementSystem;
import org.megastage.systems.srv.SphereOfInfluenceSystem;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Time;

/**
 * Created by IntelliJ IDEA.
 * User: Orlof
 * Date: 17.8.2013
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */
public class Game {
    World world;

    public Game(Element root) throws ClassNotFoundException, InstantiationException, IllegalAccessException, DataConversionException, IOException {
        ServerGlobals.world = world = new World();

        world.setManager(new GroupManager());
        world.setManager(new TagManager());
        world.setManager(new TemplateManager());

        //world.setSystem(new ServerEngineTestSystem(5000));
        //world.setSystem(new ServerGyroTestSystem(5000));
        world.setSystem(new InitializeSystem());
        world.setSystem(new CleanupSystem(500));
        world.setSystem(new SynchronizeSystem(50));
        world.setSystem(new NetworkSystem());

        world.setSystem(new PrevPositionSystem());
        world.setSystem(new OrbitalMovementSystem());

        world.setSystem(new EngineAccelerationSystem());
        world.setSystem(new AttitudeControlSystem());
        world.setSystem(new GravityFieldSystem());
        world.setSystem(new GravityAccelerationSystem());

        world.setSystem(new ShipMovementSystem());
        world.setSystem(new CollisionSystem(200));
        world.setSystem(new ExplosionSystem(201));
        
        world.setSystem(new RadarEchoSystem(1000));
        world.setSystem(new SphereOfInfluenceSystem(10000));

        world.setSystem(new DCPUSystem());

        world.initialize();

        for(Element element: root.getChildren("entity")) {
            EntityFactory.create(world, element, null);
        }

        for(Element element: root.getChildren("entity-template")) {
            world.getManager(TemplateManager.class).addTemplate(element);
        }
    }

    public void loopForever() throws InterruptedException {
        while (true) {
            long ctime = System.currentTimeMillis();
            world.setDelta((ctime - Time.value) / 1000.0f);
            Time.value = ctime;

            world.process();
            
            long tpt = System.currentTimeMillis() - ctime;
            if(tpt < 20) {
                Thread.sleep(20-tpt);
            }
        }
    }
}
