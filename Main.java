import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public class Main {

    private static final int NUM_WORKERS = 4;
    private static final Queue<String> taskQueue = new LinkedList<>();
    private static final List<String> results = Collections.synchronizedList(new ArrayList<>());
    private static final String fileName = "results.txt";

    public static void main(String[] args) {
        // Populate tasks
        for (int i = 1; i <= 10; i++) {
            taskQueue.add("Task " + i);
        }

        ExecutorService executor = Executors.newFixedThreadPool(NUM_WORKERS);

        for (int i = 0; i < NUM_WORKERS; i++) {
            // create executor task
            executor.submit(new Worker(i));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.err.println("Execution interrupted: " + e.getMessage());
        }


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String result : results) {
                writer.write(result);
                writer.newLine();
            }
            System.out.println("Results saved in file successfully -> File Name: " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    static class Worker implements Runnable {
        private final int workerId;

        public Worker(int id) {
            this.workerId = id;
        }

        @Override
        public void run() {
            while (true) {
                String task;
                synchronized (taskQueue) {
                    task = taskQueue.poll();
                    if (task == null) break;
                }

                try {
                    System.out.println("Worker " + workerId + " started: " + task);
                    Thread.sleep((int)(Math.random() * 1000)); // Simulate work
                    String result = task + " -> processed by Worker " + workerId;
                    results.add(result);
                    System.out.println("Worker " + workerId + " completed: " + task);
                } catch (InterruptedException e) {
                    System.err.println("Worker " + workerId + " interrupted: " + e.getMessage());
                }
            }
        }

    }
}
