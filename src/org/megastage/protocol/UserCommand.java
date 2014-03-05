/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

import com.artemis.Entity;
import com.cubes.Vector3Int;
import com.esotericsoftware.minlog.Log;
import org.megastage.client.ClientGlobals;
import org.megastage.components.Rotation;
import org.megastage.util.ID;

public class UserCommand {
    public double dx, dy, dz;
    public double qx, qy, qz, qw;
    public transient int count;

    public Keyboard keyboard = new Keyboard();

    public MoveShip ship;
    public Teleport teleport;
    public Pick pick;
    public Unpick unpick;
    public Build build;
    public Unbuild unbuild;

    public UserCommand() {}

    public void reset() {
        dx = dy = dz = 0.0;
        keyboard.keyEventPtr = count = 0;

        ship = null;
        pick = null;
        unpick = null;
        build = null;
        unbuild = null;
        teleport = null;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("UserCommand(");
        sb.append("dx=").append(dx);
        sb.append(", dy=").append(dy);
        sb.append(", dz=").append(dz);
        sb.append(", rotation(").append(qx).append(", ").append(qy).append(", ").append(qz).append(", ").append(qw).append(")");
        sb.append(", count=").append(count);
        sb.append(", keyboard=").append(keyboard);
        sb.append(", ship=").append(ship);
        sb.append(", pick=").append(pick);
        sb.append(", unpick=").append(unpick);
        sb.append(", build=").append(build);
        sb.append(", unbuild=").append(unbuild);
        sb.append(")");
        return sb.toString();
    }

    public void move(double dx, double dy, double dz) {
        this.dx += dx;
        this.dy += dy;
        this.dz += dz;
        count++;
    }
    
    public void look(Rotation rot) {
        qx = rot.x;
        qy = rot.y;
        qz = rot.z;
        qw = rot.w;
        count++;
    }

    public void shipMove(double dx, double dy, double dz) {
        if(ship == null) ship = new MoveShip();

        ship.forward += dz;
        ship.left += dx;
        ship.up += dy;
        count++;
    }
    
    public void shipPitch(double up) {
        if(ship == null) ship = new MoveShip();
        ship.pitch += up;
        count++;
    }
    
    public void shipRoll(double cw) {
        if(ship == null) ship = new MoveShip();
        ship.roll += cw;
        count++;
    }
    
    public void shipYaw(double left) {
        if(ship == null) ship = new MoveShip();
        ship.yaw += left;
        count++;
    }
    
    public void pickItem(Entity entity) {
        Log.info("Pick " + ID.get(entity));
        pick = new Pick();
        pick.eid = ClientGlobals.artemis.toServerID(entity.id);
        count++;
    }

    public void unpickItem() {
        Log.info("Unpick");
        unpick = new Unpick();
        count++;
    }

    public void keyPressed(char keyChar) {
        keyboard.keyPressed(keyChar);
        count++;
    }

    public void keyTyped(char keyChar) {
        keyboard.keyTyped(keyChar);
        count++;
    }

    public void keyReleased(Character keyChar) {
        keyboard.keyReleased(keyChar);
        count++;
    }

    public void build(Vector3Int loc) {
        build = new Build();
        build.x = loc.getX();
        build.y = loc.getY();
        build.z = loc.getZ();
        count++;
    }

    public void unbuild(Vector3Int loc) {
        unbuild = new Unbuild();
        unbuild.x = loc.getX();
        unbuild.y = loc.getY();
        unbuild.z = loc.getZ();
        count++;
    }

    public void teleport(Entity entity) {
        teleport = new Teleport();
        teleport.eid = ClientGlobals.artemis.toServerID(entity.id);
        count++;
    }

    public static interface ExtendedCommand {}
    
    public static class MoveShip implements ExtendedCommand {
        public double forward, left, up, pitch, roll, yaw;
    }

    public static class Pick implements ExtendedCommand {
        public int eid;
    }

    public static class Teleport implements ExtendedCommand {
        public int eid;
    }

    public static class Unpick implements ExtendedCommand {
    }

    public static class Build implements ExtendedCommand {
        public int x, y, z;
        
        public String toString() {
            return "[" + x + ", " + y + ", " + z + "]";
        }
    }

    public static class Unbuild implements ExtendedCommand {
        public int x, y, z;
        
        public String toString() {
            return "[" + x + ", " + y + ", " + z + "]";
        }
    }

    public static class Keyboard implements ExtendedCommand {
        public char[] keyEvents = new char[24];
        public int keyEventPtr = 0;

        private void keyPressed(char keyChar) {
            if(keyEventPtr > keyEvents.length) {
                Log.info("Keybuffer overflow");
                return;
            }

            keyEvents[keyEventPtr++] = 'P';
            keyEvents[keyEventPtr++] = keyChar;
        }

        private void keyTyped(char keyChar) {
            if(keyEventPtr > keyEvents.length) {
                Log.info("Keybuffer overflow");
                return;
            }

            keyEvents[keyEventPtr++] = 'T';
            keyEvents[keyEventPtr++] = keyChar;
        }

        private void keyReleased(Character keyChar) {
            if(keyEventPtr > keyEvents.length) {
                Log.info("Keybuffer overflow");
                return;
            }

            keyEvents[keyEventPtr++] = 'R';
            keyEvents[keyEventPtr++] = keyChar;
        }
    }
}

