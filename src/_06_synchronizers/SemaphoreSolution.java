package _06_synchronizers;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * created by toan.tx.vn
 */

/**
 * Tư tưởng: Semaphore sẽ duy trì một set các permits.
 * Semaphore dùng để giới hạn số thread có thể access resource tại một thời điểm.
 * <p>
 * Mỗi acquire() bị block cho tới khi một permit sẵn sàng, sau đó thì nó sẽ lấy permit đó.
 * Mỗi release() sẽ trả lại cái permit đó.
 * Note: Nhưng không có một cái permit nào thực sự được tạo ra như kiểu class, mà nó dùng biến count để đếm các permit.
 */
public class SemaphoreSolution {
    public static void main(String args[]) throws InterruptedException {
        // creating a Semaphore object
        // with number of permits 1
        Semaphore sem = new Semaphore(3);

        // creating two threads with name A and B
        // Note that thread A will increment the count
        // and thread B will decrement the count
        MyThread mt1 = new MyThread(sem, "A");
        MyThread mt2 = new MyThread(sem, "B");
        MyThread mt3 = new MyThread(sem, "C");
        MyThread mt4 = new MyThread(sem, "D");

        // stating threads A and B
        mt1.start();
        mt2.start();
        mt3.start();
        mt4.start();

        // waiting for threads A and B
        mt1.join();
        mt2.join();
        mt3.join();
        mt4.join();

        // count will always remain 0 after
        // both threads will complete their execution
        System.out.println("count: " + Shared.count);
    }
}

class Shared {
    static AtomicInteger count = new AtomicInteger(0);
}

class MyThread extends Thread {
    Semaphore sem;
    String threadName;

    public MyThread(Semaphore sem, String threadName) {
        super(threadName);
        this.sem = sem;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        System.out.println("Starting " + threadName);
        try {
            // First, get a permit.
            System.out.println(threadName + " is getting permit.");

            // acquiring the lock
            sem.acquire();

            System.out.println(threadName + " got a permit.");

            // Now, accessing the shared resource.
            // other waiting threads will wait, until this
            // thread release the lock
            for (int i = 0; i < 5; i++) {
                Shared.count.incrementAndGet();
                System.out.println(threadName + ": " + Shared.count);

                // Now, allowing a context switch -- if possible.
                // for thread B to execute
                Thread.sleep(10);
            }
        } catch (InterruptedException exc) {
            System.out.println(exc);
        }

        // Release the permit.
        System.out.println(threadName + " releases the permit.");
        sem.release();
    }
}