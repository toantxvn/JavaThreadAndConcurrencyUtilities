package _03_waiting_notification;

/* created by toan.tx.vn */

/**
 *  Để giao tiếp giữa các threads với nhau, Java cung cấp một API như sau:
 *      Wait-and_Notify:
 *          - wait(), wait(timeout), wait(timeout, nano_timeout): Làm cho thread hiện tại phải đợi cho tới khi có thread
 *                      khác gọi notify() hoặc notifyAll() cho object này.
 *                      Hoặc thread khác interrupt thread hiện tại trong khi đang waiting.
 *                      If any thread interrupted the current thread before or while the current thread was waiting.
 *                      The interrupted status of the current thread is cleared when this exception is thrown.
 *
 *                      Một thread sẽ nhả "monitor" của object này khi wait() của object đó được gọi.
 *
 *          - notify(): wakeup một thread đang đợi monitor của object này. Thread được đánh thức sẽ không thể tiếp tục công việc
 *                      cho tới khi monitor được nhả ra.
 *
 *          - notifyAll(): wakeup tất cả các threads đang đợi monitor của object này. Các threads được đánh thức sẽ không
 *                      thể tiếp tục công việc cho tới khi monitor được nhả ra.
 *
 *      Note that: Các hàm trên phải được gọi trong khối synchronized của CÙNG object đó; và wait() phải được đặt trong vòng lặp:
 *                  Việc đặt wait() trong vòng lặp có condition để đảm bảo tránh "liveness".
 *                  Giả sử không thực hiện việc kiểm tra điều kiện như vòng lặp while, và gặp phải điều kiện lúc đó đúng,
 *                  và notify() + wait() cùng đang được gọi, mà notify() được gọi trước, thì mình không thể đảm bảo được
 *                  waiting thread có được thức dậy hay không.
 *
 *         synchronized(obj) {
 *             while (<condition does not hold>) {
 *                 obj.wait();
 *             }
 *             // Perform an action that's appropriate to condition.
 *         }
 *
 *         synchronized(obj) {
 *             // Set the condition.
 *             obj.notify();
 *         }
 *
 */

/**
 *  Áp dụng wait() và notify() cho bài toán Producer-Consumer: (BlockingQueue áp dụng chiến thuật này)
 *      Producer là thằng tạo ra sản phẩm và đặt vào vùng nhớ chung
 *      Consumer là thằng vào vùng nhớ chung, lấy sản phẩm ra xử lý
 */
public class Solution {
    public static void main(String[] args) {
        Shared shared = new Shared();
        Thread producer = new Procedure(shared);
        Thread consumer = new Consumer(shared);

        producer.start();
        consumer.start();
    }
}

class Shared {
    private char sharedVariable;
    private volatile boolean writeable = true;

    synchronized void setSharedVariable(char c) {
        while (!writeable) {
            try {
                this.wait();
            } catch (InterruptedException ignore) {
            }
        }
        this.sharedVariable = c;
        writeable = false;
        this.notify();
    }

    synchronized char getSharedVariable() {
        while (writeable) {
            try {
                this.wait();
            } catch (InterruptedException ignore) {
            }
        }
        writeable = true;
        char returnValue = sharedVariable;
        notify(); // notifyAll();
        return returnValue;
    }
}

class Procedure extends Thread {
    private final Shared shared;

    Procedure(Shared shared) {
        this.shared = shared;
    }

    @Override
    public void run() {
        for (char c = 'A'; c <= 'Z'; c++) {
            synchronized (shared) { // đảm bảo cho việc in log ra màn hình theo đúng thứ tự Producer rồi Consumer
                shared.setSharedVariable(c);
                System.out.printf("%s set --> value: %c\n", Thread.currentThread(), c);
            }
        }
    }
}

class Consumer extends Thread {
    private final Shared shared;

    Consumer(Shared shared) {
        this.shared = shared;
    }

    @Override
    public void run() {
        char ch;
        do {
            synchronized (shared) { // đảm bảo cho việc in log ra màn hình theo đúng thứ tự Producer rồi Consumer
                ch = shared.getSharedVariable();
                System.out.printf("%s get <-- value: %c\n", Thread.currentThread(), ch);
            }
        } while (ch != 'Z');
    }
}

/**
 * Chú thích thêm:
 *      - Một file .java có thể có nhiều class bên trong.
 *      - Một file .java chỉ có một class public.
 *      - class nào là public thì phải trùng tên với tên file java.
 */

/** Tổng kết:
 *      Java cung cấp API để giao tiếp giữa các thread với nhau, bao gồm 3 hàm wait(), 1 hàm notify(), 1 hàm notifyAll()
 *      - wait() chờ cho tới khi điều kiện đó tồn tại.
 *      - notify(), notifyAll() thông báo ngay cho waiting thread khi điều kiện đó tồn tại.
 *      Các methods trên phải được đặt trong khối synchronized block của cùng một object - cái object mà chúng nó cùng gọi.
 *      Để tránh việc đánh thức giả, wait() phải được đặt trong vòng while loop để nó thực hiện lại wait() khi mà điều kiện ko thỏa mãn
 *
 *      Producer phải đợi tới khi nó được thông báo rằng sản phẩm trước đã được tiêu thụ.
 *      Consumer phải đợi tới khi nó được thông báo rằng có sản phẩm mới rồi.
 */