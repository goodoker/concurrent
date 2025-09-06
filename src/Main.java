import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    // Три потокобезопасные блокирующие очереди
    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    // Переменные для хранения результатов
    private static String maxAString = "";
    private static int maxACount = -1;

    private static String maxBString = "";
    private static int maxBCount = -1;

    private static String maxCString = "";
    private static int maxCCount = -1;

    public static void main(String[] args) throws InterruptedException {
        // Запуск генератора текстов
        Thread generatorThread = new Thread(() -> {
            try {
                for (int i = 0; i < 10000; i++) {
                    String text = generateText("abc", 100000);

                    // Помещаем текст во все три очереди
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                }

                // Сигналы о завершении для всех очередей
                queueA.put("END");
                queueB.put("END");
                queueC.put("END");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Поток для анализа символа 'a'
        Thread analyzerA = new Thread(() -> {
            try {
                while (true) {
                    String text = queueA.take();
                    if (text.equals("END")) break;

                    int count = countChar(text, 'a');
                    if (count > maxACount) {
                        maxACount = count;
                        maxAString = text;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Поток для анализа символа 'b'
        Thread analyzerB = new Thread(() -> {
            try {
                while (true) {
                    String text = queueB.take();
                    if (text.equals("END")) break;

                    int count = countChar(text, 'b');
                    if (count > maxBCount) {
                        maxBCount = count;
                        maxBString = text;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Поток для анализа символа 'c'
        Thread analyzerC = new Thread(() -> {
            try {
                while (true) {
                    String text = queueC.take();
                    if (text.equals("END")) break;

                    int count = countChar(text, 'c');
                    if (count > maxCCount) {
                        maxCCount = count;
                        maxCString = text;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Запуск всех потоков
        generatorThread.start();
        analyzerA.start();
        analyzerB.start();
        analyzerC.start();

        // Ожидание завершения всех потоков
        generatorThread.join();
        analyzerA.join();
        analyzerB.join();
        analyzerC.join();

        // Вывод результатов
        System.out.println("Текст с максимальным количеством 'a': " + maxACount + " символов");
        System.out.println("Текст с максимальным количеством 'b': " + maxBCount + " символов");
        System.out.println("Текст с максимальным количеством 'c': " + maxCCount + " символов");
    }

    // Генератор текстов (предоставлен в задании)
    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    // Метод для подсчета конкретного символа в строке
    private static int countChar(String text, char target) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }
}