import java.util.Scanner;
import java.util.concurrent.*;

public class BuildingH2O {
    static class H2O {
        private final CyclicBarrier barrier;
        private final Semaphore hydrogen = new Semaphore(2);
        private final Semaphore oxygen = new Semaphore(1);

        public H2O() {
            barrier = new CyclicBarrier(3, () -> {
                hydrogen.release(2);
                oxygen.release(1);
            });
        }

        public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
            if(!hydrogen.tryAcquire(1, 1, TimeUnit.SECONDS))
                return;
            releaseHydrogen.run();
            try {
                barrier.await(1, TimeUnit.SECONDS);
            } catch (BrokenBarrierException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }

        public void oxygen(Runnable releaseOxygen) throws InterruptedException {
            if(!oxygen.tryAcquire(1, 1, TimeUnit.SECONDS))
                return;
            releaseOxygen.run();
            try {
                barrier.await(1, TimeUnit.SECONDS);
            } catch (BrokenBarrierException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String... args) {
        Runnable r1 = () -> {
            System.out.print("H");
        };
        Runnable r2 = () -> {
            System.out.print("O");
        };
        H2O h2o = new H2O();
        Scanner sc = new Scanner(System.in);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        String inp = sc.nextLine();
        try {
            for(char c : inp.toCharArray()) {
                if(c == 'H')
                    executorService.execute(() -> {
                        try {
                            h2o.hydrogen(r1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                else
                    executorService.execute(() -> {
                        try {
                            h2o.oxygen(r2);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
            }
        } finally {
            executorService.shutdown();
        }
    }
}
