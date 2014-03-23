package org.megastage.components.dcpu;

public interface PowerConsumer {
    public double consume(double available, double delta);
}
