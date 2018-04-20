package com.example.demo;

import com.example.demo.Service.AcquiredLockWorker;
import com.example.demo.Service.DistributedLocker;
import com.example.demo.Service.RedissonConnector;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Title :
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Author :Hairui
 * Date :2018/4/18
 * Time :10:38
 * </p>
 * <p>
 * Department :
 * </p>
 * <p> Copyright : 江苏飞博软件股份有限公司 </p>
 */
@Component
public class RedisLocker  implements DistributedLocker {

    private final static String LOCKER_PREFIX = "lock:";

    @Autowired
    RedissonConnector redissonConnector;
    @Override
    public <T> T lock(String resourceName, AcquiredLockWorker<T> worker) throws Exception {

        return lock(resourceName, worker, 100);
    }

    @Override
    public <T> T lock(String resourceName, AcquiredLockWorker<T> worker, int lockTime) throws  Exception {
        RedissonClient redisson= redissonConnector.getClient();
        RLock lock = redisson.getLock(LOCKER_PREFIX + resourceName);
        // Wait for 100 seconds and automatically unlock it after lockTime seconds
        boolean success = lock.tryLock(100, lockTime, TimeUnit.SECONDS);
        if (success) {
            try {
                return worker.invokeAfterLockAcquire();
            } finally {
                lock.unlock();
            }
        }
        throw new RuntimeException();
    }
}
