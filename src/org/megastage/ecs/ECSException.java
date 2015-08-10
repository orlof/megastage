package org.megastage.ecs;

public class ECSException extends RuntimeException {
    public ECSException(String format, Object...args) {
        super(String.format(format, args));
    }
}
