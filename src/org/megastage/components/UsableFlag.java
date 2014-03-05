/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Entity;
import org.megastage.protocol.Message;

/**
 *
 * @author Orlof
 */
public class UsableFlag extends BaseComponent {
    @Override
    public Message replicate(Entity entity) {
        return always(entity);
    }
}
