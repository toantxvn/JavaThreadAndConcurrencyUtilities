package _07_locking_framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * created toan.tx.vn
 */

/**
 * ReadWriteLock gồm có 2 khóa: readLock và writeLock.
 *      Một readLock có thể được chiếm giữ bởi nhiều thread cùng 1 lúc, miễn là không có thread nào đang write
 *      Một writeLock chỉ có thể được giữ bởi một thread tại một thời điểm, và không có thread nào đang đọc lúc đó.
 *
 * ReadWriteLock dùng cho những trường hợp như sau:
 *      Số lượng các thread đọc nhiều, có khi rất nhiều, trong khi đó số lượng thread update dữ liệu ít, có khi rất ít.
 *      Chú ý: Nếu quá trình đọc quá ngắn, tổng lại không bằng chi phí cài đặt ban đầu của ReadWriteLock thì cũng cần cân nhắc.
 *
 *      - Khi cả ReadLock và WriteLock cùng đang chờ đợi, thì chúng ta nên chọn WriteLock hơn, vì giả sử của lib này là
 *      việc write sẽ rất ít so với việc read, dẫn tới việc nếu cấp quyền cho read, thì write sẽ phải đợi toàn bộ read thực
 *      hiện xong, dẫn tới write phải đợi lâu hơn. Ngoài ra thì có Fair, chúng ta cũng có thể cân nhắc
 *      - Khi có 1 thread request khóa Read trong khi đang có một reader active và một write đang đợi, thì mình ưu tiên khóa Read
 *      ... (update sau)
 */
public class ReadWriteLockSolution {
    static final int READER_SIZE = 10;
    static final int WRITER_SIZE = 2;

    public static void main(String[] args) {
        Integer[] initialElements = {33, 28, 86, 99};

        ReadWriteList<Integer> sharedList = new ReadWriteList<>(initialElements);

        for (int i = 0; i < WRITER_SIZE; i++) {
            new Writer(sharedList, "Writer-" + i).start();
        }

        for (int i = 0; i < READER_SIZE; i++) {
            new Reader(sharedList, "Reader-" + i).start();
        }

    }
}

class ReadWriteList<E> {
    private final List<E> list = new ArrayList<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    @SafeVarargs
    public ReadWriteList(E... initialElements) {
        list.addAll(Arrays.asList(initialElements));
    }

    public void add(E element) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();

        try {
            list.add(element);
        } finally {
            writeLock.unlock();
        }
    }

    public E get(int index) {
        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            return list.get(index);
        } finally {
            readLock.unlock();
        }
    }

    public int size() {
        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            return list.size();
        } finally {
            readLock.unlock();
        }
    }

}

class Reader extends Thread {
    private final ReadWriteList<Integer> sharedList;

    public Reader(ReadWriteList<Integer> sharedList, String name) {
        super(name);
        this.sharedList = sharedList;
    }

    public void run() {
        Random random = new Random();
        int index = random.nextInt(sharedList.size());
        Integer number = sharedList.get(index);

        System.out.println(getName() + " <- get: " + number);

        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }
}

class Writer extends Thread {
    private final ReadWriteList<Integer> sharedList;

    public Writer(ReadWriteList<Integer> sharedList, String name) {
        super(name);
        this.sharedList = sharedList;
    }

    public void run() {
        Random random = new Random();
        int number = random.nextInt(100);
        sharedList.add(number);

        try {
            Thread.sleep(100);
            System.out.println(getName() + " -> put: " + number);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}