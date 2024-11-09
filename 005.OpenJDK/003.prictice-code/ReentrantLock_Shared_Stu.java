package com.wei.wang;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @ClassName: ReentrantLock_Shared_Stu
 * @Description: '读写锁'
 *     <p>
 *     三个线程操作 the_inc_val  ， 读取时，需要加读锁,写的时候，需要加写锁
 *     </p>
 * @Author: 'Wei.Wang'
 * @Date: 2024/11/7 09:52
 **/
public class ReentrantLock_Shared_Stu {
    public static final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public static final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    public static final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

    public static volatile int the_inc_val = 0;

    public static void main(String[] args) {
        boolean[] exit_state = new boolean[3];
        for (int idx = 0; idx < exit_state.length; idx++) {
            exit_state[idx] = false;
            int cur_idx = idx;
            (new Thread(() -> {
                try {
                    the_sync_method();
                } catch (Exception ex) {
                    System.out.println(Thread.currentThread().getName() + "执行异常" + ex.getMessage());
                    ex.printStackTrace();
                }
                exit_state[cur_idx] = true;
            }, idx + "号线程")).start();
        }
        for (; ; ) {
            if ((exit_state[0] && exit_state[1] && exit_state[2])) {
                System.out.println("所有线程均已退出,the_inc_val = " + the_inc_val);
                break;
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void the_sync_method() throws InterruptedException {
        int exec_times = 0;
        for (; ; ) {
            if (exec_times++ > 3) {
                System.out.println(Thread.currentThread().getName() + "退出");
                break;
            }
            try {
                readLock.lock();
                System.out.println(Thread.currentThread().getName() + ":获取到读锁,当前值:" + the_inc_val);
                readLock.unlock();
                System.out.println(Thread.currentThread().getName() + "读锁已释放");
            } finally {
            }

            TimeUnit.MILLISECONDS.sleep(100);

            try {
                writeLock.lock();
                System.out.println(Thread.currentThread().getName() + ":获取到写锁");
                for (int add_times = 0; add_times < 10; add_times++) {
                    the_inc_val = the_inc_val + 1;
                    System.out.println(Thread.currentThread().getName() + "当前值:" + the_inc_val);
                }
            } finally {
                // 执行完成了,需要释放
                if (writeLock.isHeldByCurrentThread()) {
                    System.out.println(Thread.currentThread().getName() + "释放锁");
                    // 解锁后就直接唤起下一个线程: 如果是写锁，那就唤醒一个；若是读锁，那根据读锁加锁方法，那就是唤醒所有的读锁
                    writeLock.unlock();
                }
            }
        }
    }
}
