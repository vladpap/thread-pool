package ru.sbt.threadpool;

import java.util.ArrayDeque;
import java.util.Queue;

public class FixedThreadPool implements ThreadPool {
    private final Queue<Runnable> tasks = new ArrayDeque<>();
    private final int threadCount;
    private final Worker[] workers;

    public FixedThreadPool(int threadCount) {
        this.threadCount = threadCount;
        this.workers = new Worker[threadCount];
    }

    @Override
    public void start() {
        for (int i = 0; i < threadCount; i++) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (tasks) {
            tasks.add(runnable);
            tasks.notify();
        }
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            Runnable task;
            while (true) {
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException ignore) {
                        }
                    }
                    task = tasks.poll();
                }
                try {
                    task.run(); // handle exception
                } catch (RuntimeException e) {
                    throw new ThreadPoolException("Exception for method tasks.run()", e);
                }
            }
        }
    }
}
