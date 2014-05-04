package org.megastage.components.dcpu;

import org.megastage.ecs.World;

public interface PowerConsumer {
    public double consume(World world, int ship, double available, double delta);
}
