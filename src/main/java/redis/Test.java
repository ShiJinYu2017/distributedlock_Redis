package redis;


import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Test {
    public static void main(String[] args) {

        int currency = 120;//120个并发进程
        final CyclicBarrier cb = new CyclicBarrier(currency);

        for (int i = 1; i <= currency; i++) {
            new Thread(new Runnable() {
                public void run() {
                    SellTicket_Redis str = new SellTicket_Redis(Thread.currentThread().toString());
                    System.out.println(Thread.currentThread().getName() + "--------准备好了-----------");
                    try {
                        cb.await();
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    str.sellTicket();
                }
            }).start();
        }


        //SellTicket st = new SellTicket();
        //SellTicket_sync st = new SellTicket_sync();
        /*SellTicket_Lock st = new SellTicket_Lock();
        for (int i = 0; i < 3; i++) {
            new Thread(st,"窗口" + i).start();
        }*/


    }
}
