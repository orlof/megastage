package org.megastage.util;

public class MegastageException extends RuntimeException {
    public MegastageException(String format, Object...args) {
        super(String.format(format, args));
    }

    public MegastageException(Exception e) {
        this(e.getMessage());
    }
}
