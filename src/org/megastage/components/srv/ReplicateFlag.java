package org.megastage.components.srv;

import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.World;

public class ReplicateFlag extends BaseComponent {

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        return new BaseComponent[] { 
            new ReplicateToAllConnectionsFlag(),
            new ReplicateToNewConnectionsFlag()
        };
    }
}
