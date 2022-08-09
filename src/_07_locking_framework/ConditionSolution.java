package _07_locking_framework;

/**
 * created by toan.tx.vn
 */

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition interface chia các vấn đề Object's (wait(), notify(), notifyAll()) thành các condition object phân biệt, để
 * mục đích "có nhiều wait-set trên một object", để có thể kết hợp nó với một implementation của Lock bất kỳ.
 *
 * Khi mà Lock được tạo ra với mục đích thay thế synchronized của method và block, thì Condition thay
 * thế Object's (wait()/notify()/notifyAll()).
 *
 *
 *  Một Condition instance thực chất bị ràng buộc vào một Lock. Để có được một Condition instance cho chính xác cái Lock instance
 *  mà mình mong muốn, thì chúng ta dùng method sau của Lock: newCondition().
 *
 *  Các hàm của Condition:
 *      - void await(): Buộc calling thread phải đợi cho tới khi nó được thông báo, hoặc bị interrupted.
 *      - boolean await(long time, TimeUnit unit): Buộc calling thread phải đợi cho tới khi nó nhận được thông báo, hoặc
 *                  bị interrupted, hoặc hết timeout
 *      - long awaitNanos(long nanosTimeout): Buộc curent thread đợi cho tới khi nó nhận đc signal hoặc interrupt, hoặc
 *                  tới cái timeout
 *      - void awaitUninterruptibly(): Buộc thread hiện tại phải đợi cho tới khi nhận được signal
 *      - boolean awaitUntil(Date deadline): Buộc current thread phải đợi tới khi nhận được signal hoặc interrupt, hoặc
 *                  cho tới khi deadline.
 *      - void signal(): đánh thức một waiting thread
 *      - void signalAll(): đánh thức tất cả các waiting threads.
 */

public class ConditionSolution {

    public static void main(String[] args) {
        Shared s = new Shared();
        new Producer(s).start();
        new Consumer(s).start();
    }
}

class Shared {
    private char c;
    private volatile boolean available;
    private final Lock lock;
    private final Condition condition;

    Shared() {
        available = false;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    Lock getLock() {
        return lock;
    }

    char getSharedChar() {
        lock.lock();
        char answer;
        try {
            while (!available) {
                try {
                    condition.await();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
            answer = c;
            available = false;
            condition.signal();
        } finally {
            lock.unlock();
        }
        return answer;
    }

    void setSharedChar(char c) {
        lock.lock();
        try {
            while (available)
                try {
                    condition.await();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            this.c = c;
            available = true;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}

class Producer extends Thread {
    private final Lock l;
    private final Shared s;

    Producer(Shared s) {
        this.s = s;
        l = s.getLock();
    }

    @Override
    public void run() {
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            l.lock(); // Thêm vào để sync cái in ra màn hình
            s.setSharedChar(ch);
            System.out.println(ch + " produced by producer.");
            l.unlock();
        }
    }
}

class Consumer extends Thread {
    private final Lock l;
    private final Shared s;

    Consumer(Shared s) {
        this.s = s;
        l = s.getLock();
    }

    @Override
    public void run() {
        char ch;
        do {
            l.lock(); // Thêm vào để sync cái in ra màn hình
            try {
                ch = s.getSharedChar();
                System.out.println(ch + " consumed by consumer.");
            } finally {
                l.unlock();
            }
        }
        while (ch != 'Z');
    }
}
/**
 * Ví dụ này tạo ra cho trường hợp Produce và Consumer thao tác trên một ký tự char chung.
 */