package org.example;
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
            threadProcedure(texts, list1, counterA, "a");
        });
        threadA.start();

        var threadB = new Thread(() -> {
            threadProcedure(texts, list2, counterB, "b");
        });
        threadB.start();

        var threadC = new Thread(() -> {
            threadProcedure(texts, list3, counterC, "c");
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


    public static void threadProcedure (String[] texts, BlockingQueue<String> list, AtomicInteger counter, String target) {
        String buf;
        int bufLength;
        for (int i = 0; i < texts.length; i++) {
            try {
                buf = list.take();
                bufLength = buf.length() - buf.replace(target, "").length();
                if (counter.get() < bufLength) {
                    counter.set(bufLength);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}