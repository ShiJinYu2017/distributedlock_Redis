package redis;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class RedisDistributeLock implements Lock {

    //JedisPoolConfig config = new JedisPoolConfig();
    //JedisPool jedisPool= new JedisPool(config, "127.0.0.1", 6379, 3000);
    private JedisPool jedisPool = null;
    private String globlevalue = null;
    private static Ticketout to = new Ticketout();

    public RedisDistributeLock(JedisPool pool,String string) {
        globlevalue = string;
        jedisPool = pool;
    }


    private long SLEEP_PER = 10;
    private final String key = "lock_key10";
    //private String value = "lock_key10";
    private String value;
    public final int exprieTimeInMilliseconds = 10;

    public boolean tryLock() {
        Jedis jedis = jedisPool.getResource();
        String localvalue = UUID.randomUUID().toString() + globlevalue;//这种方式更稳定，防止其他线程解锁当前线程的锁（猜测）
        value = localvalue;
        //String resust = jedis.set(key, value, "NX", "PX", exprieTimeInMilliseconds);
        Long resust = jedis.setnx(key,value);
        //if ("OK".equals(resust)) {
        if (resust.equals(1L)) {
            jedis.expire(key,exprieTimeInMilliseconds);//为key设置一个超时时间，单位为second，超过这个时间锁会自动释放，避免死锁。
            jedis.close();
            System.out.println(Thread.currentThread().getName()+"加锁成功，并释放链接");
            return true;
        }
        return false;
    }
    //获取锁，否则阻塞
    public void lock() {
        while (!tryLock()) {
            try {
                Thread.sleep(SLEEP_PER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName()+"得到锁");
        int num = to.sellTicket();
        if (num != -1) {
            System.out.println(Thread.currentThread().getName() + "正在卖出第" + num + "张票!剩余"+to.getTicketnum()+"张票");
        } else {
            System.out.println(Thread.currentThread().getName() + "当前已经无票！");
        }
    }

    public void unlock( ) {
        Jedis jedis = null;
        Object out = null;
        try {
            jedis = jedisPool.getResource();
            String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
            out = jedis.eval(script,Collections.singletonList(key),Collections.singletonList(value));//使用redis的eval执行lua脚本
        } catch (JedisException e) {
            e.printStackTrace();
        } finally{
            if ((Long)out==1L) {
                jedis.close();
                System.out.println(Thread.currentThread().getName()+"释放锁,并释放链接");
            }else{
                System.out.println("释放锁失败！");
            }
        }

        /*Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            while (true) {
                // 监视lock，准备开始事务
                jedis.watch(key);
                // 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
                if (value.equals(jedis.get(key))) {
                    Transaction transaction = jedis.multi();
                    transaction.del(key);
                    List<Object> results = transaction.exec();
                    if (results == null) {
                        continue;
                    }
                }
                jedis.unwatch();
                break;
            }
        } catch (JedisException e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }*/

    }

    public Condition newCondition() {
        return null;
    }

    public void lockInterruptibly() throws InterruptedException {

    }

    public boolean tryLock(long time,TimeUnit unit) throws InterruptedException {
        return false;
    }
}
