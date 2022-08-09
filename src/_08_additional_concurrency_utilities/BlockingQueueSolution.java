package _08_additional_concurrency_utilities;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * created by toan.tx.vn
 *      BlockingQueue là subinterface của java.util.Queue
 *      BlockingQueue support blocking operations:
 *          - Đợi cho tới khi queue không empty thì mới lấy được dữ liệu ra
 *          - Đợi cho tới khi queue không bị đầy, thì mới được thêm dữ liệu vào.
 *      BlockingQueue có thể coi là trái tim của một chương trình producer-consumer.
 *
 *      Các lớp có sẵn mà implement BlockingQueue:
 *          - ArrayBlockingQueue:
 *          - DelayQueue:
 *          - LinkedBlockingQueue:
 *          - PriorityBlockingQueue:
 *          - SynchronousQueue:
 *          - LinkedBlockingDequeue:
 *          - LinkedTransferQueue:
 */
public class BlockingQueueSolution {
    static boolean wrote = false;

    public static void main(String[] args) {
        BlockingQueue<Character> blockingQueue = new ArrayBlockingQueue<>(26);
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();

        Runnable producer = () -> {
            for (char ch = 'A'; ch <= 'Z'; ch++) {
                lock.lock();
                try {
                    try {
                        while (wrote) {
                            condition.await();
                        }
                        blockingQueue.put(ch);
                        wrote = true;
                        System.out.println(ch + " produced by producer");
                        condition.signal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    lock.unlock();
                }
            }
        };
        Runnable consumer = () -> {
            char ch = '\0';
            lock.lock();
            try {
                do {
                    try {
                        while (!wrote) {
                            condition.await();
                        }
                        ch = blockingQueue.take();
                        wrote = false;
                        System.out.println(ch + " consumed by consumer.");
                        condition.signal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (ch != 'Z');
            } finally {
                lock.unlock();
            }
        };
        executor.execute(producer);
        executor.execute(consumer);
        executor.shutdown();
    }

    static void concreteClassOfBlockingQueue() {
        BlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(10);

        BlockingQueue<String> linkedListBlockingQueue = new LinkedBlockingQueue<>(20);

        BlockingQueue<Delayed> delayedQueue = new DelayQueue<>();

        BlockingQueue<String> priorityQueue = new PriorityBlockingQueue<>(30);

        BlockingQueue<String> synchronousQueue = new SynchronousQueue<>(true);

        BlockingQueue<String> linkedBlockingDequeue = new LinkedBlockingDeque<>(20);

        BlockingQueue<String> linkedTransferQueue = new LinkedTransferQueue<>();
    }
}

