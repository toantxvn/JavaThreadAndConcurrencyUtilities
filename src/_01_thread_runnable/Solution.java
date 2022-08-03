package _01_thread_runnable;

/**
 * Thread và Runnable
 *      Thread là đơn vị nhỏ nhất để thực hiện tác vụ
 *      Runnable là một interface dùng để đóng gói tác vụ cần làm và truyền vào trong Thread
 *
 *      Mỗi thread sẽ có một tên, nếu mình không đặt tên cho nó, thì tên đó sẽ tự động generate.
 *      Mỗi thread sẽ có một priority. Khi một thread được tạo ra, thì nó sẽ có priority, isDaemon giống như của cha nó.
 *      Daemon thread giống như một helper cho nonDaemon thread, và nó tự động được hủy khi nonDaemon cuối cùng chết.
 *      Mặc định thì thread là nonDaemon.
 *
 *          Note: Một application sẽ không terminate khi mà nondaemon main thread terminate, cho tới khi tất cả các
 *          background nonDaemon threads khác terminate. Nếu các tất cả background thread là daemon, thì application sẽ
 *          terminate ngay khi mà default main thread terminate.
 *
 *      States của thread gồm có những loại sau:
 *          - NEW: Một thread được tạo, nhưng chưa được start()
 *          - RUNNABLE: Một thread đang thực thi trong JVM
 *          - BLOCKED: Một thread bị chặn và đang đợi monitor lock
 *          - WAITING: Một thread đang chờ một thread khác thực hiện một hành động cụ thể
 *          - TIMED_WAITING: Một thread đang chờ một thread khác thực hiện hành động cụ thể trong thời gian xác đinh
 *          - TERMINATED: A thread has exited.
 *
 *      Di chuyển giữa các state của Thread xem tại đây: https://www.baeldung.com/wp-content/uploads/2018/02/Life_cycle_of_a_Thread_in_Java.jpg
 *
 *      Khi một thread running hoặc died, mà mình gọi start() của thread đó, sẽ xảy ra ngoại lệ: IllegalThreadStateException
 */

/** created by toan.tx.vn@gmail.com */

public class Solution {

    // Cách tạo thread
    private void createThread() {
        Thread thread = new Thread("name_of_thread") {
            @Override
            public void run() {
                super.run();
                // thực hiện task của bạn tại đây
            }
        };
        thread.start();
    }

    // Truyền Runnable vào thread
    private void createRunnableToThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // thực hiện task của bạn
            }
        };

        runnable = () -> {
            // Thực hiện task của bạn
        };

        // thread sẽ gọi tới run() của runnable, và run() sẽ được chạy trên thread này.
        Thread thread = new Thread(runnable);
        thread.start();

        // Hàm run() sẽ được chạy trên thread hiện tại
        runnable.run();
        thread.run();
    }

    // Cách lấy số lượng processor của hệ thống
    private int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    // Kiểm tra thread còn sống hay chết
    private static boolean isAlive() {
        Thread thread = new Thread();
        return thread.isAlive();
    }
}
