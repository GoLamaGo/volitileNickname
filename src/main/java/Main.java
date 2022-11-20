import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class Main {
    public static final LongAdder beautyNumber3 = new LongAdder();
    public static final LongAdder beautyNumber4 = new LongAdder();
    public static final LongAdder beautyNumber5 = new LongAdder();

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void main(String[] args) {

        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        try (ExecutorService executor = Executors.newFixedThreadPool(3)) {

            executor.submit(new Thread(() -> {
                Thread.currentThread().setName("Все буквы одинаковы");
                for (String text : texts) {
                    if (String.valueOf(text.charAt(0))
                            .repeat(text.length())
                            .equals(text)) {
                        switch (text.length()) {
                            case 3 -> beautyNumber3.increment();
                            case 4 -> beautyNumber4.increment();
                            case 5 -> beautyNumber5.increment();
                        }
                    }
                }
            }));


            executor.submit(new Thread(() -> {
                Thread.currentThread().setName("Палиндром");

                for (String text : texts) {
                    var lettersAmount = text.length() / 2;
                    var firstPart = text.substring(0, lettersAmount);
                    var lastPart = new StringBuilder(text.substring(text.length() - lettersAmount)).reverse().toString();
                    if (firstPart.equals(lastPart) &&
                            !String.valueOf(text.charAt(0))
                                    .repeat(text.length())
                                    .equals(text)) {
                        switch (text.length()) {
                            case 3 -> beautyNumber3.increment();
                            case 4 -> beautyNumber4.increment();
                            case 5 -> beautyNumber5.increment();
                        }

                    }
                }
            }));

            executor.submit(new Thread(() -> {
                Thread.currentThread().setName("По возрастанию");

                for (String text : texts) {
                    var charsArray = text.toCharArray();
                    Arrays.sort(charsArray);
                    if (new String(charsArray).equals(text) &&
                            !String.valueOf(text.charAt(0))
                                    .repeat(text.length())
                                    .equals(text)) {
                        switch (text.length()) {
                            case 3 -> beautyNumber3.increment();
                            case 4 -> beautyNumber4.increment();
                            case 5 -> beautyNumber5.increment();
                        }
                    }
                }
            }));

            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("Ожидание прервано" + e.getMessage());
            }
            executor.shutdown();
        }

        beautyNumber3.sum();
        beautyNumber4.sum();
        beautyNumber5.sum();

        System.out.println("Красивых слов с длиной 3: " + beautyNumber3.intValue());
        System.out.println("Красивых слов с длиной 4: " + beautyNumber4.intValue());
        System.out.println("Красивых слов с длиной 5: " + beautyNumber5.intValue());

    }
}
