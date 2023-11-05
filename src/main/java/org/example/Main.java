package org.example;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {

    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<String> list1 = new ArrayBlockingQueue<>(100);
        BlockingQueue<String> list2 = new ArrayBlockingQueue<>(100);
        BlockingQueue<String> list3 = new ArrayBlockingQueue<>(100);

        AtomicInteger counterA = new AtomicInteger(0);
        AtomicInteger counterB = new AtomicInteger(0);
        AtomicInteger counterC = new AtomicInteger(0);

        String[] texts = new String[10_000];

        var threadPut = new Thread(() -> {
            for (int i = 0; i < texts.length; i++) {
                try {
                    texts[i] = generateText("abc", 100_000);
                    list1.put(texts[i]);
                    list2.put(texts[i]);
                    list3.put(texts[i]);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        threadPut.start();


        var threadA = new Thread(() -> {
            String buf;
            int bufA;
            for (int i = 0; i < texts.length; i++) {
                try {
                    buf = list1.take();
                    bufA = buf.length() - buf.replace("a", "").length();
                    if (counterA.get() < bufA) {
                        counterA.set(bufA);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        threadA.start();

        var threadB = new Thread(() -> {
            String buf;
            int bufB;
            for (int i = 0; i < texts.length; i++) {
                try {
                    buf = list2.take();
                    bufB = buf.length() - buf.replace("b", "").length();
                    if (counterB.get() < bufB) {
                        counterB.set(bufB);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        threadB.start();

        var threadC = new Thread(() -> {
            String buf;
            int bufC;
            for (int i = 0; i < texts.length; i++) {
                try {
                    buf = list3.take();
                    bufC = buf.length() - buf.replace("c", "").length();
                    if (counterC.get() < bufC) {
                        counterC.set(bufC);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        threadC.start();

        threadPut.join();
        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println(counterA + " символов а \n" +
                counterB + " символов b \n" +
                counterC + " символов c \n");
    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}