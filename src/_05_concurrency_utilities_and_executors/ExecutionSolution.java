package _05_concurrency_utilities_and_executors;

/**
 * created by toan.tx.vn
*/
/**
 *  Ở chapter trước, chúng ta đã biết về wait()/notify() và synchronized
 *  Nhưng nếu như developer có một sự hiểu biết, handle không tốt về chúng, thì có thể gặp các vấn đề sau:
 *      - wait()/notify() và synchronized khó để implement cho đúng. Nếu implement sai, có thể gây ra liveness
 *      - Quá phụ thuộc vào từ khóa synchronized có thể dẫn tới performance issues, ảnh hưởng tới khả năng scalability của App.
 *  Đây là vấn đề quan trọng cho các App cần nhiều thread, ví dụ như Web servers.
 *      - Tốn thời gian code và code không an toàn.
 *
 *  Để cải thiện các vấn đề trên, Concurrency Utilities được thêm vào từ Java 5. Nó bao gồm:
 *      - java.util.concurrent: Cung cấp các class thiết thực cho lập trình concurrent programming, ví dụ: Executors
 *      - java.util.concurrent.atomic: Cung cấp các class thiết thực cho lock-free thread-safe programming trên các biến đơn.
 *      - java.util.concurrent.locks: Cung cấp các loại để lock và wait trên conditions. Locking và waiting từ những cái
 *          này thì hiệu quả hơn và flexible hơn so với việc Java's monitor-based synchronization và cơ chế wait/notification
 *
 *      java.util.concurrent.Executor
 *      Executor execute Tasks (Runnable or Callable)
 *      Executor executor = ...; // ... represents some executor creation
 *      executor.execute(new RunnableTask());
 *
 *  Tuy dễ sử dụng, nhưng Executor cũng có những hạn chế sau:
 *      - Executor chỉ tập trung vào Runnable. Bởi vì Runnable's run() không trả về giá trị, nên không dễ để trả về
 *        kết quả của runnable task cho caller.
 *      - Executor không cung cấp một cách để theo dõi tiến trình của runnable task đang chạy, cancel một runnable task
 *        đang chạy, hay biết khi nào runnable task đó kết thúc công việc.
 *      - Executor không thể thực hiện một chuỗi các runnable tasks
 *      - Executor không cung cấp cách thức để Application có thể shutdown một executor
 *
 *  Từ đó nó sinh ra thêm java.util.concurrent.ExecutorService để khắc phục những nhược điểm đó.
 *      Các method chính của ExecutorService:
 *      - boolean awaitTermination(long timeout, TimeUnit unit): Blocks until all tasks have completed execution after
 *          a shutdown request, or the timeout occurs, or the current thread is interrupted, whichever happens first
 *
 *      - <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks): Thực hiện từng callable task trong tasks
 *          và trả về List<Future<T>>
 *
 *      - <T> T invokeAny(Collection<? extends Callable<T>> tasks): Thực hiện một task bất kỳ trong tasks, trả về kết quả
 *
 *      - boolean isShutdown(): true: khi executor này đã bị shutdown, false: trong trường hợp ngược lại
 *
 *      - boolean isTerminated(): true: khi tất cả các tasks hoàn toàn shutdown, người lại thì false
 *          method này sẽ không bao giờ trả về true trước khi shutdown() shutdownNow() được gọi.
 *
 *      - void shutdown(): Initiate an orderly shutdown in which previously submitted tasks are executed, but no new tasks
 *          will be accepted. Calling this method has no effect after the executor has shut down. This method
 *          doesn’t wait for previously submitted tasks to complete execution. Use awaitTermination() when waiting is necessary
 *
 *      - List<Runnable> shutdownNow(): Cố gắng stop tất cả các tasks đang được thực hiên, tạm hoãn các tasks đang chờ, và
 *          trả về list các tasks đang chờ đó. Method này không đợi cho tới khi các tasks đang được thực hiên kết thúc, để
 *          đạt được mục đích này, thì dùng awaitTermination().
 *          Không có đảm bảo nào cho việc stop các task đang được thực hiện. Ví dụ điển hình là cách implementations cancel task
 *          bằng Thread.interrupt, nếu task đó có lỗi trong lúc interrupt, thì nó sẽ không bao giờ bị terminated nữa.
 *
 *      - <T> Future<T> submit(Callable<T> task): Submit a callable task for execution and return a Future instance representing
 *          task's pending results. The Future instance's get() method return task's result on successful completion.
 *          Nếu mình muốn ngay lập tức block trong khi đợi kết quả của task hoàn thành, thì mình có thể dùng như sau:
 *              result = exec.submit(callable).get();
 */

/**
 *  Ở chapter trước, chúng ta đã biết về wait()/notify() và synchronized
 *  Nhưng nếu như developer có một sự hiểu biết, handle không tốt về chúng, thì có thể gặp các vấn đề sau:
 *      - wait()/notify() và synchronized khó để implement cho đúng. Nếu implement sai, có thể gây ra liveness
 *      - Quá phụ thuộc vào từ khóa synchronized có thể dẫn tới performance issues, ảnh hưởng tới khả năng scalability của App.
 *        Đây là vấn đề quan trọng cho các App cần nhiều thread, ví dụ như Web servers.
 *      - Tốn thời gian code và code không an toàn.
 *
 *  Để cải thiện các vấn đề trên, Concurrency Utilities được thêm vào từ Java 5. Nó bao gồm:
 *      - java.util.concurrent: Cung cấp các class thiết thực cho lập trình concurrent programming, ví dụ: Executors
 *      - java.util.concurrent.atomic: Cung cấp các class thiết thực cho lock-free thread-safe programming trên các biến đơn.
 *      - java.util.concurrent.locks: Cung cấp các loại để lock và wait trên conditions. Locking và waiting từ những cái
 *        này thì hiệu quả hơn và flexible hơn so với việc Java's monitor-based synchronization và cơ chế wait/notification
 */

/**
 *  java.util.concurrent.Executor
 *  Executor execute Tasks (Runnable or Callable)
 *      Executor executor = ...; // ... represents some executor creation
 *      executor.execute(new RunnableTask());
 *
 *      Tuy dễ sử dụng, nhưng Executor cũng có những hạn chế sau:
 *          - Executor chỉ tập trung vào Runnable. Bởi vì Runnable's run() không trả về giá trị, nên không dễ để trả về
 *              kết quả của runnable task cho caller.
 *          - Executor không cung cấp một cách để theo dõi tiến trình của runnable task đang chạy, cancel một runnable task
 *              đang chạy, hay biết khi nào runnable task đó kết thúc công việc.
 *          - Executor không thể thực hiện một chuỗi các runnable tasks
 *          - Executor không cung cấp cách thức để Application có thể shutdown một executor
 *
 *  Từ đó nó sinh ra thêm java.util.concurrent.ExecutorService để khắc phục những nhược điểm đó.
 *  Các method chính của ExecutorService:
 *      - boolean awaitTermination(long timeout, TimeUnit unit): Blocks until all tasks have completed execution after
 *          a shutdown request, or the timeout occurs, or the current thread is interrupted, whichever happens first
 *
 *      - <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks): Thực hiện từng callable task trong tasks
 *          và trả về List<Future<T>>
 *
 *      - <T> T invokeAny(Collection<? extends Callable<T>> tasks): Thực hiện một task bất kỳ trong tasks, trả về kết quả
 *
 *      - boolean isShutdown(): true: khi executor này đã bị shutdown, false: trong trường hợp ngược lại
 *
 *      - boolean isTerminated(): true: khi tất cả các tasks hoàn toàn shutdown, người lại thì false
 *          method này sẽ không bao giờ trả về true trước khi shutdown() shutdownNow() được gọi.
 *
 *      - void shutdown(): Initiate an orderly shutdown in which previously submitted tasks are executed, but no new tasks
 *          will be accepted. Calling this method has no effect after the executor has shut down. This method
 *          doesn’t wait for previously submitted tasks to complete execution. Use awaitTermination() when waiting is necessary
 *
 *      - List<Runnable> shutdownNow(): Cố gắng stop tất cả các tasks đang được thực hiên, tạm hoãn các tasks đang chờ, và
 *          trả về list các tasks đang chờ đó. Method này không đợi cho tới khi các tasks đang được thực hiên kết thúc, để
 *          đạt được mục đích này, thì dùng awaitTermination().
 *          Không có đảm bảo nào cho việc stop các task đang được thực hiện. Ví dụ điển hình là cách implementations cancel task
 *          bằng Thread.interrupt, nếu task đó có lỗi trong lúc interrupt, thì nó sẽ không bao giờ bị terminated nữa.
 *
 *      - <T> Future<T> submit(Callable<T> task): Submit a callable task for execution and return a Future instance representing
 *          task's pending results. The Future instance's get() method return task's result on successful completion.
 *          Nếu mình muốn ngay lập tức block trong khi đợi kết quả của task hoàn thành, thì mình có thể dùng như sau:
 *                  result = exec.submit(callable).get();
 *
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class ExecutionSolution {

    /**
     * Example Executor interface
     */
    private static void test_Executor(Executor executor) {
        executor.execute(() -> {
            System.out.println("This is runnable Task");
        });
    }

    /**
     * Example ExecutorService interface
     */
    private static void test_ExecutorService(ExecutorService executorService) {
        Collection<Callable<String>> collection = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int a = i;
            collection.add(() -> "collection Callable: " + a);
        }
        try {
            List<Future<String>> listFutures = executorService.invokeAll(collection);
            listFutures.forEach(stringFuture -> {
                try {
                    System.out.println(stringFuture.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        System.out.println("This is first");
        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(3000);
            return 10;
        });
        System.out.println("Hello");
        try {
            Integer num = future.get();
            System.out.println(num);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("This is end");
    }
}
