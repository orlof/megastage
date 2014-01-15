package org.megastage.server;

import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.systems.*;

import java.io.IOException;
import org.megastage.util.ServerGlobals;

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
        world = new World();

        world.setManager(new GroupManager());
        world.setManager(new TagManager());
        world.setManager(new TemplateManager());

        //world.setSystem(new ServerEngineTestSystem(5000));
        //world.setSystem(new ServerGyroTestSystem(5000));
        world.setSystem(new ServerCleanupSystem(500));
        world.setSystem(new ServerUpdateSystem(20));
        world.setSystem(new ServerNetworkSystem());

        world.setSystem(new OrbitalMovementSystem());

        world.setSystem(new EngineAccelerationSystem());
        world.setSystem(new AttitudeControlSystem());
        world.setSystem(new GravityFieldSystem());
        world.setSystem(new GravityAccelerationSystem());

        world.setSystem(new ShipMovementSystem());

        world.setSystem(new DCPUSystem());
        //world.setSystem(new VirtualMonitorSenderSystem());

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
            world.setDelta((ctime - ServerGlobals.time) / 1000.0f);
            ServerGlobals.time = ctime;

            world.process();
            
            long tpt = System.currentTimeMillis() - ctime;
            if(tpt < 20) {
                Thread.sleep(20-tpt);
            }
        }
    }
}
