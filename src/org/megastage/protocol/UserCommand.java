/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

public class UserCommand {
    public double xMove, zMove;
    public double shipForward, shipLeft, shipUp, shipPitch, shipRoll, shipYaw;
    public int count;

    public UserCommand() {}

    public void move(double dx, double dz) {
        xMove += dx;
        zMove += dz;
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
        xMove = zMove = shipForward = shipLeft = shipUp = shipPitch = shipRoll = shipYaw = 0.0;
        count = 0;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("UserCommand(");
        sb.append("xMove=").append(xMove);
        sb.append("zMove=").append(zMove);
        sb.append("shipForward=").append(shipForward);
        sb.append("shipLeft=").append(shipLeft);
        sb.append("shipUp=").append(shipUp);
        sb.append("shipPitch=").append(shipPitch);
        sb.append("shipRoll=").append(shipRoll);
        sb.append("shipYaw=").append(shipYaw);
        sb.append("count=").append(count);
        sb.append(")");
        return sb.toString();
    }
}

