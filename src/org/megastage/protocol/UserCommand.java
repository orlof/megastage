/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

import org.megastage.components.Rotation;

public class UserCommand {
    public double xMove, yMove, zMove;
    public double qx, qy, qz, qw;
    public double shipForward, shipLeft, shipUp, shipPitch, shipRoll, shipYaw;
    public int count;

    public UserCommand() {}

    public void move(double dx, double dy, double dz) {
        xMove += dx;
        yMove += dy;
        zMove += dz;
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
        shipForward += dz;
        shipLeft += dx;
        shipUp += dy;
        count++;
    }
    
    public void shipPitch(double up) {
        shipPitch += up;
        count++;
    }
    
    public void shipRoll(double cw) {
        shipRoll += cw;
        count++;
    }
    
    public void shipYaw(double left) {
        shipYaw += left;
        count++;
    }
    
    public void reset() {
        xMove = yMove = zMove = shipForward = shipLeft = shipUp = shipPitch = shipRoll = shipYaw = 0.0;
        count = 0;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("UserCommand(");
        sb.append("xMove=").append(xMove);
        sb.append(", yMove=").append(yMove);
        sb.append(", zMove=").append(zMove);
        sb.append(", protation(").append(qx).append(", ").append(qy).append(", ").append(qz).append(", ").append(qw).append(")");
        sb.append(", shipForward=").append(shipForward);
        sb.append(", shipLeft=").append(shipLeft);
        sb.append(", shipUp=").append(shipUp);
        sb.append(", shipPitch=").append(shipPitch);
        sb.append(", shipRoll=").append(shipRoll);
        sb.append(", shipYaw=").append(shipYaw);
        sb.append(", count=").append(count);
        sb.append(")");
        return sb.toString();
    }

}

