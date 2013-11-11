package org.megastage.server;

import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.Globals;
import org.megastage.systems.*;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Teppo
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

        world.setSystem(new ServerNetworkSystem(1000));

        world.setSystem(new OrbitalMovementSystem());

        //world.setSystem(new EngineAccelerationSystem());
        //world.setSystem(new GravityAccelerationSystem());

        //world.setSystem(new ShipMovementSystem());

        world.setSystem(new DCPUSystem());
        world.setSystem(new VirtualMonitorSenderSystem());

        world.initialize();

        for(Element element: root.getChildren("entity")) {
            EntityFactory.create(world, element, null);
        }
    }

    public void loopForever() throws InterruptedException {
        while (true) {
            long ctime = System.currentTimeMillis();
            world.setDelta((ctime - Globals.time) / 1000.0f);
            Globals.time = ctime;

            world.process();
            
            Thread.sleep(100);
        }
    }
}
