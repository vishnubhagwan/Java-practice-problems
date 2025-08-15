import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class MultithreadedBankApp {
    static double currentBalance = 0.0;
    static final ReentrantLock lock = new ReentrantLock();
    static final Random rand = new Random();
    private static final int MOD = 500;

    static class Deposit implements Runnable {
        double amount;

        Deposit(Double amount) {
            this.amount = amount;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                currentBalance += amount;
            } finally {
                lock.unlock();
            }
        }
    }

    static class Withdraw implements Runnable {
        double amount;

        Withdraw(Double amount) {
            this.amount = amount;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                currentBalance -= amount;
            } finally {
                lock.unlock();
            }
        }
    }

    static class BalanceCheck implements Runnable {
        @Override
        public void run() {
            lock.lock();
            try {
                System.out.println("Available balance: " + currentBalance);
            } finally {
                lock.unlock();
            }
        }
    }

    public static class Transaction {
        String action;
        Double amount;

        Transaction(String action, Double amount) {
            this.action = action;
            this.amount = amount;
        }

        Transaction(String action) {
            this.action = action;
            this.amount = 0.0;
        }
    }

    public static void main(String[] args) throws InterruptedException{
        double amount = 0.0;

        /**
         * Transaction{action:“deposit”, 1000}
         * Transaction{action:”withdraw”, 2000}
         * Transaction{action:”checkBalance”}
         */

        List<Transaction> transactions = Arrays.asList(
                new Transaction("deposit", 1000.0),
                new Transaction("deposit", 2000.0),
                new Transaction("checkBalance"),
                new Transaction("withdraw", 2000.0),
                new Transaction("checkBalance"));

        for(Transaction transaction : transactions) {
            String txType = transaction.action;
            switch (txType) {
                case "deposit":
                    amount = transaction.amount;
                    Thread t1 = new Thread(new Deposit(amount));
                    t1.start();
                    t1.join();
                    break;
                case "withdraw":
                    amount = transaction.amount;
                    Thread t2 = new Thread(new Withdraw(amount));
                    t2.start();
                    t2.join();
                    break;
                case "checkBalance":
                    Thread t3 = new Thread(new BalanceCheck());
                    t3.start();
                    t3.join();
                    break;
                default:
                    break;
            }
        }
    }
}
