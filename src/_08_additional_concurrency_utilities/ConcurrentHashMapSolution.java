package _08_additional_concurrency_utilities;

/**
 * created by toan.tx.vn
 *
 *  ConcurrentHashMap có hành động giống như HashMap, nhưng được thiết kế để làm việc trong môi trường multithread.
 *
 *  Chú ý nhỏ:
 *      if (!map.containsKey("some string-based key"))
 *          map.put("some string-based key", "some string-based value");
 *      --> đoạn code này không phải là thread-safe, vì một thread khác có thể chen vào giữa quá trình check and put.
 *      để khắc phục đoạn này, chúng ta có 2 cách sau:
 *      1. synchronized (map) {
 *             if (!map.containsKey("some string-based key"))
 *                 map.put("some string-based key", "some string-based value");
 *         }
 *      2. map.putIfAbsent(key, value); --> Cách này có performance tốt hơn cách 1.
 */
public class ConcurrentHashMapSolution {
    public static void main(String[] args) {
    }
}
