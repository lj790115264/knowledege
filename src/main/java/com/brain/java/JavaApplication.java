package com.brain.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class JavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaApplication.class, args);
    }


    @GetMapping
    public Object object() {
        try {
            test.semp.acquire();
            test.add(1);
            Thread.sleep(4000l);
            System.out.println(111);
            test.semp.release();
            test.poll();
            test.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        test.threadPoolExecutor.submit(() -> {

            try {
                test.await();
                System.out.println(222);
                Thread.sleep(3000l);
                test.semp.release();
                test.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return "ok";
    }
}
