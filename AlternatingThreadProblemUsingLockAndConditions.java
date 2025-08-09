import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AlternatingThreadProblemUsingLockAndConditions {
    static class PrintFoo implements Runnable {

        @Override
        public void run() {
            System.out.print("foo");
        }
    }

    static class PrintBar implements Runnable {

        @Override
        public void run() {
            System.out.print("bar");
        }
    }

    static class FooBar {
        private final int n;
        final Lock lock = new ReentrantLock();
        final Condition fooTurn = lock.newCondition();
        final Condition barTurn = lock.newCondition();
        boolean isFooTurn = true;

        public FooBar(int n) {
            this.n = n;
        }

        public void foo(Runnable printFoo) throws InterruptedException {
            for (int i = 0; i < n; i++) {
                lock.lock();
                try {
                    while(!isFooTurn) {
                        fooTurn.await();
                    }
                    printFoo.run();
                    isFooTurn = false;
                    barTurn.signal();
                } finally {
                    lock.unlock();
                }
            }
        }

        public void bar(Runnable printBar) throws InterruptedException {
            for (int i = 0; i < n; i++) {
                lock.lock();
                try {
                    while(isFooTurn)
                        barTurn.await();
                    printBar.run();
                    isFooTurn = true;
                    fooTurn.signal();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        FooBar foobar = new FooBar(10);
        Runnable printFoo = new PrintFoo();
        Runnable printBar = new PrintBar();

        Thread t1 = new Thread(() -> {
            try {
                foobar.foo(printFoo);
            } catch (InterruptedException ignored) {}
        });

        Thread t2 = new Thread(() -> {
            try {
                foobar.bar(printBar);
            } catch (InterruptedException ignored) {}
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}