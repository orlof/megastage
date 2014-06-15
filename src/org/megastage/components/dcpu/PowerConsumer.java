package org.megastage.components.dcpu;

public interface PowerConsumer {
    public double consume(int ship, double available, double delta);
}
