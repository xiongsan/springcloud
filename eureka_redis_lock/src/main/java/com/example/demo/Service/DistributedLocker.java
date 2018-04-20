package com.example.demo.Service;

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
 * Time :10:36
 * </p>
 * <p>
 * Department :
 * </p>
 * <p> Copyright : 江苏飞博软件股份有限公司 </p>
 */
/**
 * Created by fangzhipeng on 2017/4/5.
 * 获取锁管理类
 */
public interface DistributedLocker {

    /**
     * 获取锁
     * @param resourceName  锁的名称
     * @param worker 获取锁后的处理类
     * @param <T>
     * @return 处理完具体的业务逻辑要返回的数据
     * @throws Exception
     */
    <T> T lock(String resourceName, AcquiredLockWorker<T> worker) throws  Exception;

    <T> T lock(String resourceName, AcquiredLockWorker<T> worker, int lockTime) throws  Exception;

}