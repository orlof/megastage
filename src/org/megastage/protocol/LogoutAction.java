package org.megastage.protocol;

import org.megastage.components.CmdText;
import org.megastage.components.PlayerCharacter;
import org.megastage.components.generic.Flag;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class LogoutAction implements Action {
    public void receive(PlayerConnection conn) {
        PlayerCharacter pc = (PlayerCharacter) World.INSTANCE.getComponent(conn.player, CompType.PlayerCharacter);

        int eid = World.INSTANCE.createEntity();

        World.INSTANCE.setComponent(eid, CompType.FlagSynchronize, Flag.SYNCHRONIZE);
        World.INSTANCE.setComponent(eid, CompType.CmdText, CmdText.create("left"));

        pc.allocated = false;
        //world.setComponent(connection.player, CompType.DeleteFlag, new DeleteFlag());
        connection.close();
    }


}

