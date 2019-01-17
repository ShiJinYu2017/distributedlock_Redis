package redis;


import org.omg.PortableServer.THREAD_POLICY_ID;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.locks.Lock;

public class SellTicket_Redis {
    private String str = null;
    private static JedisPool pool = null;
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        // 设置最大连接数
        config.setMaxTotal(200);
        // 设置最大空闲数
        config.setMaxIdle(8);
        // 设置最大等待时间
        config.setMaxWaitMillis(1000 * 100);
        // 在borrow一个jedis实例时，是否需要验证，若为true，则所有jedis实例均是可用的
        config.setTestOnBorrow(true);
        pool = new JedisPool(config, "127.0.0.1", 6379, 3000);
    }

    public SellTicket_Redis(String string) {
        str = string;
    }

    //private static Ticketout to = new Ticketout();//多个窗口共用一个卖票库存系统
    Lock lock = new RedisDistributeLock(pool,str);

    public void sellTicket() {
        int num;
        try {
            lock.lock();
            //System.out.println(Thread.currentThread().getName()+"真的得到了锁");
            //num = to.sellTicket();
        }finally {
            System.out.println(Thread.currentThread().getName()+"指定释放锁");
            lock.unlock();
        }
        /*if (num != -1) {
            System.out.println(Thread.currentThread().getName() + "正在卖出第" + num + "张票!剩余"+to.getTicketnum()+"张票");
        } else {
            System.out.println(Thread.currentThread().getName() + "当前已经无票！");
        }*/
    }

}
