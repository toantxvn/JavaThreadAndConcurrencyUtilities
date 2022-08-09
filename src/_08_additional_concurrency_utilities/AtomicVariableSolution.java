package _08_additional_concurrency_utilities;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * created by toan.tx.vn
 *
 *      Understanding the Atomic Magic
 *          Java's low-level synchronization mechanism, bó bắt buộc phải tuân thủ cơ chế "mutual exclusion" (một thread
 *          đang giữ lock, mà nó bảo vệ một set các biến sẽ có quyền truy cập độc quyền vào chúng) và "visibility" (các
 *          thay đổi vào biến được bảo vệ, sẽ được hiển thị cho các thread khác khi chúng chiếm được lock), tác động tới
 *          việc sử dụng phần cứng và khả năng mở rộng theo những cách sau:
 *              - Tranh đấu về đồng bộ hóa (Contended Synchronization -Các thread liên tục tranh giành lock): là đắt đỏ
 *              và throughput bị ảnh hưởng. Cái đắt đỏ này bị gây ra chủ yếu bởi chuyển đổi context thường xuyên (swithching
 *              the central processing unit from one thread to another) xảy ra. Mỗi thao tác chuyển (switching) có thể mất
 *              nhiều chu kỳ xử lý để hoàn thành.
 *              - Khi một thread đang giữ một khóa bị delay, không có thread nào khác đang yêu cầu lock đó có thể tiếp tục
 *              hoạt động; hardware cũng không được sử dụng như chúng có thể.
 *
 *          Compare and Swap (CAS):
 *              1. Read value x from address A
 *              2. Perform a multistep computation on x to derive a new value called y
 *              3. Use CAS to change the value of A from x to y. CAS succeeds when A's value hasn't changed while performing
 *              these steps.
 *
 */
public class AtomicVariableSolution {

    public static void main(String[] args) {
        AtomicInteger num = new AtomicInteger(1);
        int value = num.incrementAndGet();
        System.out.println(value);
    }
}