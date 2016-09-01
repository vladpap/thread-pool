package ru.sbt.threadpool;

public class ThreadPoolException extends RuntimeException {
    public ThreadPoolException(String message) {
        super(message);
    }

    public ThreadPoolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThreadPoolException(Throwable cause) {
        super(cause);
    }
}
