package ru.sbt.threadpool;

public interface ThreadPool {
    void start();

    void execute(Runnable runnable);

    void terminate();
}
