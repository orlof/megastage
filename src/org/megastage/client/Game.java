package org.megastage.client;

import com.artemis.World;
import org.megastage.systems.ClientKeyboardSystem;
import org.megastage.systems.ClientMonitorRenderSystem;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.systems.ClientRenderSystem;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Teppo
 * Date: 25.1.2012
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
public class Game {
    World world;

    enum State { RUNNING, STOPPING, STOPPED }
    State state = State.RUNNING;

    public Game() {
        world = new World();

        world.setSystem(new ClientRenderSystem());
        world.setSystem(new ClientMonitorRenderSystem());
        world.setSystem(new ClientNetworkSystem());
        world.setSystem(new ClientKeyboardSystem());

        world.initialize();

        world.getSystem(ClientNetworkSystem.class).sendLogin();

        world.getSystem(ClientRenderSystem.class).addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowevent) {
                exit();
            }
        });
    }

    private void exit() {
        world.getSystem(ClientNetworkSystem.class).sendLogout();
        world.getSystem(ClientRenderSystem.class).close();

        state = State.STOPPING;

    }

    public void loopForever() throws InterruptedException {
        while (state != State.STOPPED) {
            if(state == State.STOPPING) {
                state = State.STOPPED;
            }
            Thread.sleep(100);
            world.process();
        }
    }
}
