package _02_synchronization;

/**
 * Created by toan.tx.vn
 */


/**
 *  Race Condition xảy ra khi tính đúng đắn của phép tính dựa vào thời gian tương đối hoặc sự đan xen của nhiều threads với bộ lập lịch.
 *  Các trường hợp xảy ra Race condition:
 *      - check-then-act            Ví dụ: static int a; if (a == 10) b = a / 2;
 *      - read-modify-write         Ví dụ: public int getId() {return counter++;}
 *
 *  Data Race là trường hợp như này:
 *      private static Parser parser;
 *      public static Parser getInstance() {
 *          if (parser == null)
 *              parser = new Parser();
 *          return parser;
 *      }
 *      Giả sử thread 1 gọi getInstance(), nó thấy parser == null, nên nó khởi tạo parser.
 *      Nhưng cùng lúc đó thread 2 cũng gọi getInstance() và nó cũng thấy parser == null, nên nó cũng khởi tạo parser.
 *
 *  Cached Variables: Để tăng tốc độ, JVM và hệ thống xử lý có thể hợp tác với nhau để cache một biến trong Register hoặc
 *  processor-local cache, thay vì trên "main memory". Mỗi thread có một bản copy của biến đó. Khi một thread write vào biến
 *  đó, nó sẽ write vào cái bản copy đó; các threads khác sẽ gần như không thể thấy update đó trong copies của chúng.
 *  -----------------------------------------------------------------------------------------------------------------
 *
 *  Synchronized là một tính năng của JVM, nó dùng để ngăn việc các threads có thể thao tác cùng một lúc vào một critical section.
 *  Cái Critical section đó, phải được truy cập một cách lần lượt từng thread một. --> Tính chất này được gọi là: Mutual Exclusion.
 *  Và cái lock mà thread này chiếm được thường được gọi là "mutex lock"
 *
 *  Khi Thread chạy vào synchronized section sẽ đọc dữ liệu từ "main memory", và ghi dữ liệu vào "main memory" khi thoát ra.
 *  Cái này được gọi là: visibility.
 *
 *  Synchronized được implement trên nguyên tắc của "monitor". Mỗi object trong Java đều có một "monitor", cái mà một thread
 *  có thể lock hoặc unlock bởi lấy hoặc nhả monitor's lock.
 *  Chú ý: Một thread đang giữ một lock sẽ không release lock đó khi nó gọi sleep().
 *
 *  Tại một thời điểm, chỉ một thread có thể chiếm giữ lock, các thread khác phải đợi cho tới khi lock đó được nhả ra.
 *  Lock được thiết kế là "Reentrant" nghĩa là: một thread đang thử chiếm lấy lock mà nó đang giữ, thì sẽ thành công.
 */
public class Solution {
    static int counter = 0;

    /**
     *  synchronized giữ khóa của object this.
     */
    public synchronized int getId() {
        return counter++;
    }

    /**
     *  synchronized giữ khóa của Person.class
     */
    public static synchronized int getId(int type) {
        return counter++;
    }
    /**----------------------------------------------------*/

    public void syncBasedOnObject() {
        final Object key = new Object();
        synchronized (key) { /** synchronized dựa trên monitor của object key */
            // TODO: do somthing.
        }
    }
}

/**
 * Liveness problems: Có 3 loại
 *      - Deadlock: Thread 1 đang đợi resource được nắm giữ bởi Thread 2 để tiếp tục process, đồng thời,
 *                  Thread 2 cũng đang đợi resource được giữ bởi Thread 1.
 *      - Livelock: Thread x tiếp tục cố thử một operation mà nó luôn không thực hiện được (một cách chủ động)
 *      - Starvation: Thread x liên tục bị từ chối việc truy cập vào resource mà nó cần để thực hiện công việc.
 */

/**
 * Java cung cấp từ khóa "volatile".
 * "volatile" được dùng để bảo các thread đọc và ghi giá trị vào "main memory".
 * Note: Chỉ dùng "volatile" khi sự visibility phải được cân nhắc.
 *      Chúng ta có thể dùng "volatile" cho các biến double/long, nhưng tránh dùng khi ở JVM loại 32-bit, vì nó sẽ cần 2
 *      operations. Trong trường hợp này, ta có thể dùng synchronized để thay thế.
 *
 *  volatile và final không đi với nhau. Nhưng final không cần tới volatile, vì bản thân nó là immutable và được khởi tạo
 *  lúc ban đầu, và không thay đổi giá trị.
 */

/**
 *  Không nên làm như này, vì constructor của ThisEscapeDemo chưa được thực hiện xong, instance đó chưa được khởi tạo hoàn toàn.
 *  nên việc gán "lastCreatedInstance = this;" rất nguy hiểm.
 *
 *      public class ThisEscapeDemo {
 *          private static ThisEscapeDemo lastCreatedInstance;
 *          public ThisEscapeDemo() {
 *              lastCreatedInstance = this;
 *          }
 *      }
 */
