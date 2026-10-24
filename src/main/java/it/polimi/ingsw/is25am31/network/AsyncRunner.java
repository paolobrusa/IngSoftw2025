package it.polimi.ingsw.is25am31.network;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The {@code AsyncRunner} class provides a utility for executing tasks asynchronously
 * using a single-threaded {@link ExecutorService}. This ensures that tasks are processed
 * sequentially, preventing potential concurrency issues when updating shared resources
 * or dispatching events that require a specific order.
 */
public class AsyncRunner {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Submits a {@link SafeRunnable} task for asynchronous execution.
     * The task will be executed by the internal single-threaded executor.
     *
     * @param task The {@link SafeRunnable} task to be executed. This is a functional interface
     * that allows for throwing checked exceptions.
     * @return A {@link CompletableFuture<Void>} representing the completion of the task.
     */
    public CompletableFuture<Void> run(SafeRunnable task) {
        return CompletableFuture.runAsync(() -> {
            try {
                task.run();
            } catch (Exception e) {
                System.out.println(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            }
        }, executor);
    }

    /**
     * Initiates an orderly shutdown of the executor service.
     * Previously submitted tasks are executed, but no new tasks will be accepted.
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * A functional interface similar to {@link Runnable}, but allows for methods
     * that can throw {@link Exception}.
     */
    @FunctionalInterface
    public interface SafeRunnable {
        /**
         * The task to be performed, which may throw an {@link Exception}.
         *
         * @throws Exception If an error occurs during the execution of the task.
         */
        void run() throws Exception;
    }
}