package org.example;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AlternatingThreadProblemMain {
    public static void main(String[] args) throws InterruptedException {
        String foo = "foo";
        String bar = "bar";
        Object lock = new Object();
        AtomicBoolean isFoo = new AtomicBoolean(true);
        AtomicInteger count = new AtomicInteger(0);

        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                while(count.getAndIncrement() < 10) {
                    while (!isFoo.get()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                    System.out.print(foo);
                    isFoo.set(false);
                    lock.notify();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                while(count.getAndIncrement() < 10) {
                    while (isFoo.get()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                    System.out.print(bar);
                    isFoo.set(true);
                    lock.notify();
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}