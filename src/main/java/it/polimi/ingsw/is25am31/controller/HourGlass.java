package it.polimi.ingsw.is25am31.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents a countdown timer with a default duration of 90 seconds.
 * The timer can be started, stopped, and queried for its current state.
 */
public class HourGlass implements Serializable {
    @Serial
    private static final long serialVersionUID = 6147075212525668813L;
    private transient ScheduledExecutorService scheduler;
    private int secondsLeft = 90;
    private boolean isRunning;
    private transient ScheduledFuture<?> currentHG;

    /**
     * Constructs a new HourGlass with default settings.
     * Initializes the scheduler with a single thread pool and sets the timer to not running.
     * The initial countdown value is set to 90 seconds.
     */
    public HourGlass() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.isRunning = false;
    }

    /**
     * Custom deserialization method to properly reconstruct transient fields.
     * Recreates the ScheduledExecutorService after deserialization since it's marked as transient.
     *
     * @param in the ObjectInputStream to read from during deserialization
     * @throws IOException if an I/O error occurs during deserialization
     * @throws ClassNotFoundException if the class cannot be found during deserialization
     */
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.scheduler = Executors.newScheduledThreadPool(1);

    }

    /**
     * Starts the countdown timer if it's not already running.
     * The timer decrements every second and automatically stops and resets when reaching zero.
     * If the scheduler is shutdown or null, it will be recreated before starting.
     * <p>
     * If the timer is already running, this method has no effect.
     * <p>
     * The countdown task runs at a fixed rate of 1 second intervals,
     * decrementing the seconds counter until it reaches zero, at which point
     * the timer stops and resets to 90 seconds.
     */
    public void start(){
        if (scheduler == null || scheduler.isShutdown()) {
            this.scheduler = Executors.newScheduledThreadPool(1);
        }
        if (isRunning) {
            return;
        }
        isRunning = true;
        Runnable task = () -> {
            secondsLeft -= 1;
            if (secondsLeft <= 0) {
                isRunning = false;
                currentHG.cancel(false);
                secondsLeft = 90;
            }
        };
        currentHG = scheduler.scheduleAtFixedRate(task, 0 , 1, TimeUnit.SECONDS);
    }

    /**
     * Stops the countdown timer and shuts down the scheduler.
     * If the timer is currently running, it will be stopped and the running state set to false.
     * The scheduler service is forcefully shut down, terminating any ongoing tasks.
     * <p>
     * After calling this method, the HourGlass instance should not be reused
     * unless a new scheduler is created (which happens automatically in the start() method).
     */
    public void stop(){
        if (isRunning) {
            isRunning = false;
            currentHG.cancel(false);
        }
        scheduler.shutdownNow();
    }

    /**
     * Returns the number of seconds remaining in the countdown.
     *
     * @return the current number of seconds left in the timer (0-90)
     */
    public int getSecondsLeft(){
        return secondsLeft;
    }

    /**
     * Checks whether the countdown timer is currently running.
     *
     * @return true if the timer is currently counting down, false otherwise
     */
    public boolean isRunning(){
        return isRunning;
    }
}
