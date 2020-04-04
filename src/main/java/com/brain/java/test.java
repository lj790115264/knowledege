package com.brain.java;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class test {
    public static ThreadPoolExecutor threadPoolExecutor =
            new ThreadPoolExecutor(10, 20, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(Integer.MAX_VALUE));

    public static Semaphore semp = new Semaphore(50);

    public static Queue queue = new LinkedBlockingQueue(Integer.MAX_VALUE);

    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();


    public static void add(Object o) {
        queue.add(o);
    }

    public static void poll() {
        queue.poll();
    }

    public static void signalAll() {
        if (queue.size() == 0) {
            lock.lock();
            condition.signalAll();
            lock.unlock();
        }
    }

    public static void await() throws InterruptedException {

        if (queue.size() != 0) {
            lock.lock();
            condition.await();
            lock.unlock();
        }
    }
}
