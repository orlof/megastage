/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

public class UserCommand {
    public double dx, dz;
    public int count;

    public UserCommand() {}
    public UserCommand(double x, double z) {
        this();
        this.dx = x;
        this.dz = z;
    }

    public String toString() {
        return "UserCommand(dx="+dx+", dz="+dz+")";
    }
}

