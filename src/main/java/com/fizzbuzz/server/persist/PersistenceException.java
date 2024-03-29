package com.fizzbuzz.server.persist;

public class PersistenceException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenceException(final String message) {
        super(message);
    }

    public PersistenceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(final Throwable cause) {
        super(cause);
    }

}
