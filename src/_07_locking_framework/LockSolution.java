package _07_locking_framework;

/**
 * created by toan.tx.vn
 */

/**
 * Lock interface cung cấp nhiều operations hơn locks của monitors. Ví dụ, mình có thể ngay lập tức hủy bỏ yêu cầu lock,
 * khi mà lock không có sẵn. Các methods của Lock interface:
 *      - void lock(): Yêu cầu lấy lock. Nếu lock không có sẵn, calling thread sẽ bị bắt phải đợi cho tới khi có lock.
 *
 *      - void lockInterruptibly(): Yêu cầu lấy lock trừ khi calling thread bị interrupted. Khi lock không có sẵn, calling
 *                  thread sẽ bị buộc phải đợi cho tới khi lock có sẵn, hoặc thread bị interrupted, nó sẽ dẫn tới InterruptedException.
 *
 *      - Condition newCondition(): Tạo và trả về một Condition instance mới mà nó đính vào vưới Lock instance hiện tại.
 *                  Nếu Lock hiện tại không hỗ trợ newCondition(), thì UnsupportedOperationException sẽ được ném ra.
 *
 *      - boolean tryLock(): Yêu cầu lấy lock khi lock có sẵn tại thời điểm method này được gọi.
 *                  Trả về true nếu lấy được lock, còn false khi không lấy được lock.
 *
 *      - boolean tryLock(long time, TimeUnit unit): Yêu cầu lấy log, và có thể đợi để lấy lock trong timeout. Hết timeout
 *                  thì trả về false.
 *
 *      - void unlock(): Release the lock
 */

/**
 * Chúng ta nên sử dụng cú pháp sau khi cần dùng tới lock
 *
 *      Lock l = ...; // ... là instance Lock được khởi tạo
 *      l.lock();
 *      try {
 *          // Truy cập vào resource cần được bảo vệ bởi lock
 *      } catch (Exception ex) {
 *          // khôi phục lại giá trị
 *      } finally {
 *          l.unlock(); // nhả lock ra
 *      }
 *
 *  Tất cả các Lock implementations được yêu cầu phải tuân theo cùng ngữ nghĩa về memory synchronization giống như
 *  built-in monitor lock. --> Cái này đảm bảo được yêu cầu Linkov trong SOLID principles.
 */

public class LockSolution {
}
