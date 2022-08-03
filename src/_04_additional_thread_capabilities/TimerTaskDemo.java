package _04_additional_thread_capabilities;

import java.util.Timer;
import java.util.TimerTask;

/**
 * created by toan.tx.vn
 */

public class TimerTaskDemo {
    public static void main(String[] args) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Alarm is going off");
//                System.exit(0);
            }
        };
        Timer timer = new Timer(); // Thằng này đóng vai trò chủ yếu trong việc xếp lịch.
//        timer.schedule(timerTask, 2000); // Execute one-shot timerTask after 2-second delay.
        timer.schedule(timerTask, 0, 2000); // Execute shot after delay and repeat it after period.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Second Task");
            }
        }, 1500);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Third Task");
            }
        }, 1500, 3000);

        try {
            Thread.sleep(10_000);
            timerTask.cancel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(5_000);
            timer.cancel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
