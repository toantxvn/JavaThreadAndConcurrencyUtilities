package _07_locking_framework;

/**
 * created by toan.tx.vn
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  ReentrantLock class implements Lock, nó có thể lấy lại lock mà nó đang sở hữu.
 *
 *  ReentrantLock có 2 constructors:
 *      - ReentrantLock(): nó gọi tới ReentrantLock(false)
 *      - ReentrantLock(boolean fair): tạo một instance cuar ReentrantLock với fairness polity.
 *              Nếu true, có nghĩa là lock nên được cấp cho longest-waiting thread.
 *
 *  ReentrantLock có thêm 2 methods bổ sung so với Lock:
 *      - boolean isFair(): trả về fairness policy.
 *      - boolean isHeldByCurrentThread(): trả về true nếu lock được giữ bởi thread hiện tại.
 */
public class ReentrantLockSolution {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        final ReentrantLock lock = new ReentrantLock();

        class Worker implements Runnable {
            private final String name;

            Worker(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                lock.lock();
                try {
                    if (lock.isHeldByCurrentThread()) {
                        System.out.println("Thread " + name + " entered critical section");
                        System.out.println("Thread " + name + " performing work.");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Thread " + name + " finished work.");
                    }
                } finally {
                    lock.unlock();
                }
            }
        }

        executorService.execute(new Worker("AAA"));
        executorService.execute(new Worker("BBB"));
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}
