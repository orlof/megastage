package org.megastage.components.dcpu;

public interface PowerConsumer {
    public double consumePower(double delta);
    public void shortage();
}
