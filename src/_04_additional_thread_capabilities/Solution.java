package _04_additional_thread_capabilities;

// created by toan.tx.vn

/**
 *  Giới thiệu về ThreadGroup:
 *      ThreadGroup chứa một tập hợp các Thread, và có thể chứa cả các ThreadGroup khác.
 *      Các ThreadGroup con đó tổ chức theo dạng tree, có cha là cái ThreadGroup ban đầu đó. Cái cha thì ko có cha :))
 *      Một thread chỉ có thể truy xuất thông tin của ThreadGroup trực tiếp chứa nó.
 *
 *      Mặc dù ThreadGroup này có vẻ như rất tiện lợi, nhưng chúng ta được khuyến cáo không nên sử dụng nó vì lý do sau:
 *      -   Các hàm hiệu quả nhất của ThreadGroup là: suspend(), resume(), stop() - đều được gắn @Deprecated, vì có thể
 *          gây ra deadlock và các vấn đề tiềm tàng khác
 *      -   ThreadGroup không phải là thread-safe. (chúng ta không đi vào chi tiết vấn đề này). Bạn đọc có thể đọc ở đây:
 *          https://en.wikipedia.org/wiki/Time-of-check_to_time-of-use
 *
 */

/**
 *  ThreadLocal variables:
 *      Mỗi một ThreadLocal instance mô tả một "thread-local variable", nó là một variable được cung cấp một vùng nhớ riêng
 *      cho mỗi thread. Tức là, với cùng một biến a, nhưng với mỗi một thread lại có biến a riêng của nó, và thread chỉ
 *      làm việc với biến a riêng đó thôi, chứ ko biết tới các biến a của các thread khác.
 */
public class Solution {
    static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    static void testThreadLocal() {
        threadLocal.set(Thread.currentThread().getName());
        System.out.println(threadLocal.get());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new Thread(Solution::testThreadLocal).start();
        }

        testInheritableThreadLocal();
    }

    /**------------------------------------------------------------------*/

    private static final InheritableThreadLocal<Integer> intVal = new InheritableThreadLocal<>();

    public static void testInheritableThreadLocal() {
        Runnable rP = () -> {
            intVal.set(10);
            Thread thdChild = new Thread(() -> {
                Thread thd = Thread.currentThread();
                String name = thd.getName();
                System.out.printf("%s %d%n", name, intVal.get());
            });
            thdChild.setName("Child");
            thdChild.start();
        };
        new Thread(rP).start();
    }
}

/**
 *  Thực chất ThreadLocal lưu value của hàm .set(value) vào trong một cái Map có key là current Thread.
 */