package com.helper.OpenJDK;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName: ReentrantLock_Stu
 * @Description: 'ReentrantLock'
 *               <p>
 *               描述: 三个线程,每个线程均操作 the_inc_val ， 分三次加锁，每次获取锁后，对the_inc_val值加10
 *               </p>
 * @Author: 'Wei.Wang'
 * @Date: 2024/11/4 09:39
 **/
public class ReentrantLock_Stu {
    public static final ReentrantLock the_lock = new ReentrantLock(false);
    public static final Condition the_condition = the_lock.newCondition();
    public static volatile long the_inc_val = 0;

    public static void main(String[] args) {
        boolean[] exit_state = new boolean[3];
        for (int idx = 0; idx < exit_state.length; idx++) {
            exit_state[idx] = false;
            int cur_idx = idx;
            (new Thread(() -> {
                try {
                    ReentrantLock_Stu.the_sync_method();
                } catch (Exception ex) {
                    System.out.println(Thread.currentThread().getName() + "执行异常" + ex.getMessage());
                    ex.printStackTrace();
                }
                exit_state[cur_idx] = true;
            }, idx + "号线程")).start();
        }
        for (;;) {
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
        long lock_times = 0;
        for (;;) {
            int temp_val = 0;
            the_lock.lock();
            try {
                lock_times += 1;
                if (lock_times > 3) {
                    System.out.println(Thread.currentThread().getName() + "已获取过3次锁,释放锁并退出...");

                    // 唤起其他线程
                    the_condition.signalAll(); // signalAll()-break
                    System.out.println(Thread.currentThread().getName() + "已唤醒其他线程......");
                    break;
                }
                System.out.println(Thread.currentThread().getName() + "获取锁成功......");
                for (; temp_val < 10; temp_val++) {
                    the_inc_val += 1;
                    System.out.println(Thread.currentThread().getName() + " 加一了,当前值: " + the_inc_val);
                    TimeUnit.MILLISECONDS.sleep(300);
                }
                // 唤起其他线程
                the_condition.signalAll();
                System.out.println(Thread.currentThread().getName() + "已唤醒其他线程......");
                // 将当前线程挂起
                System.out.println(Thread.currentThread().getName() + "将当前线程挂起......");
                // 必须使用等待一定时间,否则，其他线程先signalAll,再await,就会导致线程一直在等待被唤醒
                /*
                 * 测试实际情况:
                 * 如果使用.await()，则曾经获取到锁的线程在 signalAll()-break
                 * 后直接break了，另一个获取锁的线程此时执行到此处(修改前为.await())
                 * 那么就会导致调用.await方法的线程一直在等待被唤醒，从而导致代码无法达到预期
                 *
                 * await await(..,..)方法均要在获取锁的情况下调用,否则异常
                 */
                the_condition.await(100, TimeUnit.MILLISECONDS); // 符合预期
                // the_condition.await(); // 其中一个线程在一直等待被唤醒,程序无法退出
            } finally {
                the_lock.unlock();
            }
        }
        System.out.println(Thread.currentThread().getName() + "退出... " + the_lock.isHeldByCurrentThread());
    }
}
