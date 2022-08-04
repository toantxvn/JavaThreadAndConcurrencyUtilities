package _06_synchronizers;

/**
 * created by toan.tx.vn
 */

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  Thường được dùng cho thuật toàn Genetic_algorithm <a href="https://en.wikipedia.org/wiki/Genetic_algorithm"/>, hoặc
 *  pipeline designs.
 *
 *  Exchanger cung cấp một synchronization point - nơi mà các threads có thể trao đổi các objects với nhau.
 *  Thông qua hàm exchange().
 *
 *              ------------                                 ------------
 *              |          |  ------- Object 1 ------------> |          |
 *              | Thread 1 |                                 | Thread 2 |
 *              |          |  <--------------Object 2 ------ |          |
 *              -----------                                  ------------
 *
 *      Các hàm cơ bản:
 *          - V exchange(V x): Đợi cho thread khác tới điểm exchange (trừ khi thread hiện tại bị interrupted), sau đó
 *                          truyền cái object cần truyền qua cho nó, đồng thời nhận object của thread kia trong dữ liệu
 *                          trả về. Nếu thằng thread kia đang đợi sẵn rồi, thì thread này sẽ nhận luộn object của thread
 *                          kia, và truyền object của mình cho nó.
 *
 *          - V exchange(V x, long timeout, TimeUnit unit): Tương tự như trên, nhưng kèm timeout cho waiting.
 */

public class ExchangerSolution {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        ExchangerRunnable exchangerRunnable1 = new ExchangerRunnable(exchanger, "A");
        ExchangerRunnable exchangerRunnable2 = new ExchangerRunnable(exchanger, "B");

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(exchangerRunnable1);
        executorService.execute(exchangerRunnable2);
        executorService.shutdown();
    }
}

class ExchangerRunnable implements Runnable {

    Exchanger<String> exchanger;
    String object;

    public ExchangerRunnable(Exchanger<String> exchanger, String object) {
        this.exchanger = exchanger;
        this.object = object;
    }

    public void run() {
        try {
            Object previous = this.object;

            object = exchanger.exchange(object);

            System.out.println(Thread.currentThread().getName() + " exchanged " + previous + " for " + object);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}