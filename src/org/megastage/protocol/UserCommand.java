/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

public class UserCommand {
    public double x, z;

    public UserCommand() {}
    public UserCommand(double x, double z) {
        this();
        this.x = x;
        this.z = z;
    }

    public String toString() {
        return "UserCommand(x="+x+", z="+z+")";
    }
}

