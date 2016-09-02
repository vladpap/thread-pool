package ru.sbt.threadpool;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static java.lang.Thread.State.WAITING;

public class ScalableThreadPool implements ThreadPool {
    private final int minThreads;
    private final int maxThreads;
    private List<Worker> workers;
    private volatile boolean isRunning = true;
    private volatile int runThreads;
    private final Queue<Runnable> tasks = new ArrayDeque<>();

    public ScalableThreadPool(int minThreads, int maxThreads) {
        this.minThreads = minThreads;
        this.maxThreads = maxThreads;
        workers = new ArrayList<>();
    }

    public int getRunThreads() {
//        for TEST
        return runThreads;
    }

    @Override
    public void start() {
        for (int i = 0; i < minThreads; i++) {
            workers.add(new Worker());
        }
        for (Worker worker : workers) {
            worker.start();
        }
        runThreads = minThreads;
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (tasks) {
            tasks.add(runnable);

            if ((tasks.size() > runThreads) && (runThreads < maxThreads)) {
                Worker temp = new Worker();
                workers.add(temp);
                temp.start();
                ++runThreads;
            }
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
                    throw new ThreadPoolException("Exception  for method tasks.run()", e);
                }
                synchronized (tasks) {
                    if ((tasks.size() < runThreads) && (runThreads > minThreads)) {
                        for (Worker worker : workers) {
                            Thread.State state = worker.getState();
                            if (state == WAITING) {
                                worker.interrupt();
                                --runThreads;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}