package _06_synchronizers;

/**
 * created by toan.tx.vn
 */

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Một CyclicBarrier cho phép một tập hợp các threads đợi nhau để cùng đạt được một barrier point chung.
 * Tại sao lại gọi là CyclicBarrier, vì barrier có thể được sử dụng lại sau khi tất cả các waiting threads được nhả ra.
 * Chiến thuật này rất hữu dụng cho những application có liên quan tới số lượng threads đã được giới hạn, mà thỉnh thoảng
 * phải đợi nhau.
 *
 *  Các method của CyclicBarrier:
 *      - int await(): Buộc current thread phải đợi cho tới khi tất cả các thread gọi await(). Calling thread sẽ kết thúc
 *                      đợi khi nó hoặc thread khác bị interrupted, hoặc thread khác hết timeout cho waiting, hoặc thread
 *                      khác gọi reset().
 *                      Nếu calling thread là thread cuối cùng, và non-null barrierAction được cung cấp trong constructor,
 *                      thì thread này sẽ thực hiện cái runnable này trước khi cho phép các threads khác được tiếp tục.
 *                      Return index getParties() - 1 cho thread đầu tiên; và 0 cho thread cuối cùng tới.
 *
 *      - int await(long timeout, TimeUnit unit): tương tự nhưng có thêm timeout.
 *
 *      - int getNumberWaiting(): Trả về số lượng các parties đang đợi barrier. Rất hữu dụng cho debugging
 *
 *      - int getParties(): Trả về số lượng các parties được yêu cầu vượt qua barrier.
 *
 *      - boolean isBroken(): Trả về true khi một hoặc nhiều parties thoát ra khỏi barrier vì interruption hoặc timeout,
 *                     hoặc barrier barrier gặp exception, hoặc barrier được khởi tạo, hoặc bị reset.
 *                     Trả về false cho các trường hợp còn lại.
 *
 *      - void reset(): Reset barrier này về trạng thái khởi tạo ban đầu của nó. Nếu có bất cứ parties nào đang đợi barrier,
 *                      thì chúng sẽ gặp BrokenBarrierException.
 *                      "Khuyến cáo, nên tạo barrier mới cho những lần dùng sau."
 *
 *   CyclicBarrier rất hữu dụng trong các trường hợp "parallel decomposition", nơi mà các task dài được chia thành nhiều
 *   task nhỏ, mà kết quả (không liên quan tới nhau) của từng task nhỏ được gộp lại thành kết quả cuối cùng của task lớn.
 */

public class CyclicBarrierSolution {

    public static void main(String[] args) {
        float[][] matrix = new float[3][3];
        int counter = 0;
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                matrix[row][col] = counter++;
            }
        }
        dump(matrix);
        System.out.println();
        Solver solver = new Solver(matrix);
        System.out.println();
        dump(matrix);
    }

    static void dump(float[][] matrix) {
        for (float[] floats : matrix) {
            for (int col = 0; col < matrix[0].length; col++) {
                System.out.print(floats[col] + " ");
            }
            System.out.println();
        }
    }
}

class Solver {
    final int N;
    final float[][] data;
    final CyclicBarrier barrier;

    final Object lock = new Object();

    public Solver(float[][] matrix) {
        data = matrix;
        N = matrix.length;
        System.out.println("main thread init barrier");
        barrier = new CyclicBarrier(N, this::mergeRows);
        System.out.println("main thread create threads to do work");
        for (int i = 0; i < N; ++i)
            new Thread(new Worker(i)).start();
        waitUntilDone();
    }

    class Worker implements Runnable {
        int myRow;
        boolean done = false;

        Worker(int row) {
            myRow = row;
        }

        @Override
        public void run() {
            while (!done()) {
                processRow(myRow);
                try {
                    System.out.println(Thread.currentThread().getName() + " before waiting!");
                    Thread.sleep(2000);
                    barrier.await();  // Đợi cho tới khi tất cả các thread cần thiết gọi await(), thì barrier sẽ tripped,
                                      // và mergeRows() sẽ được gọi
                    System.out.println(Thread.currentThread().getName() + " after waiting!");
                } catch (InterruptedException | BrokenBarrierException ie) {
                    return;
                }
            }
        }

        boolean done() {
            return done;
        }

        void processRow(int myRow) {
            System.out.println(Thread.currentThread().getName() + " - Processing row: " + myRow);
            for (int i = 0; i < N; i++)
                data[myRow][i] *= 10;
            done = true;
            System.out.println(Thread.currentThread().getName() + " - Proceed row: " + myRow);
        }
    }

    void mergeRows() {
        System.out.println("merging");
        synchronized (lock) {
            lock.notify();
        }
    }

    void waitUntilDone() {
        synchronized (lock) {
            try {
                System.out.println("main thread waiting");
                lock.wait();
                System.out.println("main thread notified");
            } catch (InterruptedException ie) {
                System.out.println("main thread interrupted");
            }
        }
    }
}
