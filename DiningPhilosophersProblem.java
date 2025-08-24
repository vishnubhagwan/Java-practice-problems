import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class DiningPhilosophersProblem {
    static class DiningPhilosophers {
        private final Semaphore[] lock = new Semaphore[5];

        public DiningPhilosophers() {
            for(int i = 0 ; i < 5 ; i++)
                lock[i] = new Semaphore(1);
        }

        public void wantsToEat(int philosopher,
                               Runnable pickLeftFork,
                               Runnable pickRightFork,
                               Runnable eat,
                               Runnable putLeftFork,
                               Runnable putRightFork) throws InterruptedException {
            int left = philosopher;
            int right = (philosopher + 1) % 5;

            if(left < right) {
                lock[left].acquire();
                lock[right].acquire();
                pickLeftFork.run();
                pickRightFork.run();
            } else {
                lock[right].acquire();
                lock[left].acquire();
                pickRightFork.run();
                pickLeftFork.run();
            }
            eat.run();
            if(left < right) {
                putRightFork.run();
                putLeftFork.run();
                lock[right].release();
                lock[left].release();
            } else {
                putLeftFork.run();
                putRightFork.run();
                lock[left].release();
                lock[right].release();
            }
        }
    }

    public static void main(String... args) {
        Runnable pickL = () -> System.out.print(" Picked Left ");
        Runnable pickR = () -> System.out.print(" Picked Right ");
        Runnable eat   = () -> System.out.print(" Eating ");
        Runnable putL  = () -> System.out.print(" Put Left ");
        Runnable putR  = () -> System.out.print(" Put Right ");
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        DiningPhilosophers d = new DiningPhilosophers();
        for(int i = 0 ; i < 5 ; i++) {
            int id = i;
            new Thread(() -> {
                try {
                    for(int j = 0 ; j < n ; j++) {
                        d.wantsToEat(id, pickL, pickR, eat, putL, putR);
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
