package _05_concurrency_utilities_and_executors;

/**
 * created by toan.tx.vn
 */

import java.util.concurrent.*;

/**
 * Future interface dùng cho trường hợp lấy kết quả trả về của quá trình tính toán BẤT ĐỒNG BỘ.
 * Kết quả được coi là ở "future" bởi vì nó sẽ chỉ có trong một lúc nào đó ở tương lai.
 */
public class FutureSolution {
    static void test_method_of_Future(ExecutorService executorService) {
        System.out.println(Thread.currentThread().getName() + " Start test");
        Future<String> future = executorService.submit(() -> {
            System.out.println(Thread.currentThread().getName() + " begin");
            Thread.sleep(5000);
            System.out.println(Thread.currentThread().getName() + " after slepp");
            return "10";
        });
        System.out.println(Thread.currentThread().getName() + " after future is got");

//        new Thread(() -> {
//            try {
//                Thread.sleep(1000L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            /** thử cancel task này, true nếu thành công, false nếu không có gì cancel cả*/
//            System.out.println(Thread.currentThread().getName() + " Cancel task: " + future.cancel(true));
//        }).start();

        try {
            /** Đợi cho tới khi nào task thực hiện thành công và trả về kết quả*/
            System.out.println(Thread.currentThread().getName() + " future's answer: " + future.get());
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            e.printStackTrace();
        }

        /** Trả về true nếu task bị cancel trước khi hoàn thành công việc của nó một cách bình thường; ngược lại là false */
        System.out.println(Thread.currentThread().getName() + " isCancelled(): " + future.isCancelled());

        /** Trả về true khi task đã được hoàn thành, ngược lại là false*/
        System.out.println(Thread.currentThread().getName() + " isDone(): " + future.isDone());
        System.out.println(Thread.currentThread().getName() + " End test");
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        test_method_of_Future(executorService);

        executorService.shutdown(); /** Việc gọi executorService.shutdown() là rất quan trọng, nếu không thì chương trình sẽ không dừng lại*/
    }
}

/**
 * Các class khác:
 *      java.util.concurrent.ScheduledExecutorService là interface mở rộng của ExecutorService, nó cho phép chúng ta
 *      lên lịch cho các tasks chạy một lần hoặc định kỳ sau một thời gian nhất định.
 *
 *      java.util.concurrent.Executors giống như một helper giúp chúng ta có thể tạo ra các instance của class ta muốn như:
 *          - ExecutorService
 *          - ScheduledExecutorService
 *          - ThreadFactory: Dùng để tạo ra các Thread objects.
 *          - Callable:
 *
 *      Ví dụ: Executors.newFixedThreadPool(number_Threads) sẽ tạo ra một thread pool mà nó sử dụng lại số lượng threads
 *          hoạt động trên shared unbounded queue. Tại một thời điểm, có tối đa number_Threads hoạt động để xử lý các tasks.
 *          Nếu có một tasks nào mới được submit trong lúc đó, thì nó sẽ phải chờ trong queue cho tới khi có thread rảnh.
 *          Nếu có bất kỳ thread nào bị terminated bởi vì bất kỳ lỗi gì trong quá trình hoạt động trước khi executor shutdown
 *          thì một thread mới sẽ được thay thế vào vì trí đó khi cần thiết để thực hiện các task tiếp theo.
 *          Các threads trong pool sẽ được giải phóng cho tới khi executor được shutdown.
 *
 *          Bởi vì việc tạo ra một thread mới rất là tốn kém, nên việc sử dụng thread pool sẽ loại bỏ được việc phải tạo
 *          mới mỗi thread cho mỗi task.
 *
 *      Chúng ta sẽ thường sử dụng: executors, runnables, callables, futures khi xử lý file, network input/output contexts,
 *      và các xử lý dài hơi.
 */
