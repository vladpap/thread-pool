package ru.sbt.threadpool;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ThreadPool pool = new FixedThreadPool(3);
        pool.start();

        for (int i = 1; i < 10; i++) {
            final int finalI = i;
            pool.execute(new Factorial(finalI));
            pool.execute(() -> System.out.println("run " + finalI));
        }


        Thread.sleep(5_000);
        System.out.println("Done");
        pool.terminate();
    }
}
