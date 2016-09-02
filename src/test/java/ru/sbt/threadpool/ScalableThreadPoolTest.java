package ru.sbt.threadpool;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScalableThreadPoolTest {

    @Test
    public void ScalableCountThreadPoolTest() throws InterruptedException {
        int minThread = 3;
        int maxThread = 10;
        ScalableThreadPool scalePool = new ScalableThreadPool(minThread, maxThread);
        scalePool.start();

        assertEquals(minThread, scalePool.getRunThreads());

        for (int i = 0; i < 20; i++) {
            final int finalI = i;
            scalePool.execute(new Factorial(finalI));
        }

        assertEquals(maxThread, scalePool.getRunThreads());

        Thread.sleep(2000);

        assertEquals(minThread, scalePool.getRunThreads());

        for (int i = 0; i < 20; i++) {
            final int finalI = i;
            scalePool.execute(new Factorial(finalI));
        }

        assertEquals(maxThread, scalePool.getRunThreads());

        Thread.sleep(2000);

        assertEquals(minThread, scalePool.getRunThreads());

        Thread.sleep(50);

        scalePool.terminate();
    }

    private class Factorial implements Runnable {
        private final int fact;

        public Factorial(int fact) {
            this.fact = fact;
        }

        @Override
        public void run() {
            int result = 1;
            for (int j = 1; j < fact + 1; j++) {
                result += result * j;
                try {
                    Thread.sleep(25);
                } catch (InterruptedException ignore) {
                }
            }
            System.out.println("Result " + fact + " = " + result);
        }
    }
}