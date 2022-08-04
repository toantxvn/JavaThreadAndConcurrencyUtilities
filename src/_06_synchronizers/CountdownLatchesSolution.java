package _06_synchronizers;

/**
 * created by toan.tx.vn
 */

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Countdown latch làm cho một hoặc nhiều threads đợi ở một "gate" cho tới khi một thread khác mở "gate" đó ra, lúc đó
 * các threads này có thể tiếp tục hoạt động. Countdown latch bao gồm một biến count và các operation, bắt một thread đợi
 * cho tới khi count đó đạt giá trị bằng 0, và giảm dần giá trị của biến count.
 *
 * CountDownLatch cung cấp các method như sau:
 *  - void await(): Buộc calling thread đợi cho tới khi latch được giảm về giá trị bằng 0. Method này return ngay lập tức
 *                  khi giá trị về 0. Method này ném ra ngoại lệ InterruptedException, khi thread bị interrupt.
 *
 *  - boolean await(long timeout, TimeUnit unit): tương tự như hàm await(), hoặc thread sẽ đợi tới hết timeout.
 *                  Trả về true khi counter về 0, false khi hết timeout.
 *
 *  - void countDown(): Giảm giá trị của count, nhả tất cả các waiting threads khi count về 0. Khi giá trị count = 0, gọi
 *                      hàm này sẽ không thực hiện gì cả.
 *
 *  - long getCount(): Trả về giá trị hiện tại của count. Method này thực sự hữu dụng cho testing và debugging.
 *
 *  - String toString(): Trả về String có thông tin của latch hiện tại, bao gồm cả getCount().
 *
 *  --> Chúng ta thường dùng CountDownLatch cho những trường hợp mà "các Threads cần khởi động gần như cùng một lúc với nhau."
 *
 */
public class CountdownLatchesSolution {

    final static int N_Threads = 3;

    public static void main(String[] args) {
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch doneSignal = new CountDownLatch(N_Threads);

        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    report("Entered run()");
                    startSignal.await(); // Thread này phải đợi tới khi count của startSignal về 0. Khi count về 0.
                    report("Doing work"); // Thread này thực hiện công việc
                    Thread.sleep((int) (Math.random() * 1000)); // Giả sử công việc tốn tưng này thời gian
                    doneSignal.countDown(); // Thread này thực hiện xong việc, thì giảm count của doneSignal đi 1 đơn vị
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }

            void report(String s) {
                System.out.println(System.currentTimeMillis() + ": " + Thread.currentThread() + ": " + s);
            }
        };

        // Tạo ra 3 thread trong thread pool của executor
        ExecutorService executorService = Executors.newFixedThreadPool(N_Threads);
        for (int i = 0; i < N_Threads; i++) {
            // Mỗi thread thực thi một runnable
            executorService.execute(run);
        }

        try {
            System.out.println("Main thread is doing something");
            Thread.sleep(3000); // sleep trong 3 second để đảm bảo các thread trong pool đã được chạy.
            System.out.println("Main thread after 3 seconds");

            startSignal.countDown(); // Giảm count của startSignal về 0, để các thread đang đợi startSignal có thể tiếp tục.

            System.out.println("Main thread doing something else");

            doneSignal.await(); // Đợi cho count của doneSignal về 0 - cái này được các thread khác giảm khi chúng xong việc.

            System.out.println("Main thread is end");
            executorService.shutdown(); // Phải gọi executorService.shutdown() để giải phóng các threads.
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}
