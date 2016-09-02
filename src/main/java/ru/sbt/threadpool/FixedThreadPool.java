package ru.sbt.threadpool;

import java.util.ArrayDeque;
import java.util.Queue;

public class FixedThreadPool implements ThreadPool {
    private final Queue<Runnable> tasks = new ArrayDeque<>();
    private final int threadCount;
    private final Worker[] workers;
    private volatile boolean isRunning = true;

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

    @Override
    public void terminate() {
        isRunning = false;
        for (Worker worker : workers) {
            worker.interrupt();
        }
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            Runnable task;
            while (isRunning) {
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException ignore) {
                            if (!isRunning) return;
                        }
                    }
                    task = tasks.poll();
                }
                try {
                    task.run();
                } catch (RuntimeException e) {
                    throw new ThreadPoolException("Exception for method tasks.run()", e);
                }
            }
        }
    }
}
