package redis;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SellTicket_Lock  implements Runnable{
    private int tickets = 100;
    Lock lock = new ReentrantLock();
    public void run() {
        while (tickets > 0) {
            lock.lock();
            try {
                if (tickets > 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "正在出售第" + tickets-- + "张票");
                }
            }finally {
                lock.unlock();
            }
        }
    }
}
