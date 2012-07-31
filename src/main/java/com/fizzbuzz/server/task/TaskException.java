package com.fizzbuzz.server.task;

public class TaskException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TaskException() {
        super(generateMessage());
    }

    public TaskException(final Throwable cause) {
        super(generateMessage(), cause);
    }

    private static String generateMessage() {
        return "";
    }
}
