/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

public interface Action extends Carrier {
    void receive(PlayerConnection conn);
}
