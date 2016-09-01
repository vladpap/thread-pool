package ru.sbt.threadpool;

public class Main {
    public static void main(String[] args) {
        ThreadPool pool = new FixedThreadPool(3);
        pool.start();

        for (int i = 1; i < 10; i++) {
            final int finalI = i;
            pool.execute(() -> System.out.println("run " + finalI));
            pool.execute(new Factorial(finalI));
        }

        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pool.execute(() -> System.out.println("fgdfgdfg"));
    }
}
