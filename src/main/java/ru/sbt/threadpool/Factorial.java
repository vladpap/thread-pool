package ru.sbt.threadpool;

public class Factorial implements Runnable {
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
                Thread.sleep(500);
            } catch (InterruptedException ignore) {
            }
        }
        System.out.println("Result " + fact + " = " + result);
    }
}

