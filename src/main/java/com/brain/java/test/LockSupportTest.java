package com.brain.java.test;

import lombok.SneakyThrows;

import java.util.concurrent.locks.LockSupport;

public class LockSupportTest {


    public static void main(String ...args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println(1111);
                Thread.sleep(1000l);
                LockSupport.park(this);
                System.out.println(2222);
            }
        });

        thread.start();

//        Thread.sleep(2000l);

        LockSupport.unpark(thread);
    }
}
