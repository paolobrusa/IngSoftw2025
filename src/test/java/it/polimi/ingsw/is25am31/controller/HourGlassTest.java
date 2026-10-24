package it.polimi.ingsw.is25am31.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class HourGlassTest {

    private HourGlass hourGlass;
    private Field secondsLeftField;
    private Field schedulerField;
    private Field isRunningField;

    @BeforeEach
    void setUp() throws Exception {
        hourGlass = new HourGlass();
        secondsLeftField = HourGlass.class.getDeclaredField("secondsLeft");
        secondsLeftField.setAccessible(true);
        schedulerField = HourGlass.class.getDeclaredField("scheduler");
        schedulerField.setAccessible(true);
        isRunningField = HourGlass.class.getDeclaredField("isRunning");
        isRunningField.setAccessible(true);
    }

    @AfterEach
    void tearDown() {
        hourGlass.stop();
    }

    @Test
    void testInitialState() {
        assertFalse(hourGlass.isRunning());
        assertEquals(90, hourGlass.getSecondsLeft());
    }

    @Test
    void testStartSetsRunning() throws InterruptedException {
        hourGlass.start();
        assertTrue(hourGlass.isRunning());
        Thread.sleep(50);
        assertTrue(hourGlass.getSecondsLeft() < 90);
    }

    @Test
    void testStopCancelsTimer() throws InterruptedException {
        hourGlass.start();
        assertTrue(hourGlass.isRunning());
        hourGlass.stop();
        assertFalse(hourGlass.isRunning());
        int leftAfterStop = hourGlass.getSecondsLeft();
        Thread.sleep(50);
        assertEquals(leftAfterStop, hourGlass.getSecondsLeft());
    }

    @Test
    void testCountdownAndReset() throws Exception {
        secondsLeftField.setInt(hourGlass, 2);
        hourGlass.start();
        Thread.sleep(2500);
        assertFalse(hourGlass.isRunning());
        assertEquals(90, hourGlass.getSecondsLeft());
    }

    @Test
    void testStartWhenAlreadyRunning() throws Exception {
        secondsLeftField.setInt(hourGlass, 1);
        hourGlass.start();
        hourGlass.start();
        Thread.sleep(1500);
        assertFalse(hourGlass.isRunning());
        assertEquals(90, hourGlass.getSecondsLeft());
    }

    @Test
    void testDeserializationWhenNotRunning() throws Exception {
        secondsLeftField.setInt(hourGlass, 50);
        HourGlass deserialized = serializeAndDeserialize(hourGlass);
        assertEquals(50, deserialized.getSecondsLeft());
        assertFalse(deserialized.isRunning());
        assertNotNull(schedulerField.get(deserialized));
        assertFalse(((ScheduledExecutorService) schedulerField.get(deserialized)).isShutdown());
    }

    @Test
    void testStartWhenSchedulerNull() throws Exception {
        schedulerField.set(hourGlass, null);
        hourGlass.start();
        assertTrue(hourGlass.isRunning());
        assertNotNull(schedulerField.get(hourGlass));
        assertFalse(((ScheduledExecutorService) schedulerField.get(hourGlass)).isShutdown());
    }

    @Test
    void testStartWhenSchedulerShutdown() throws Exception {
        hourGlass.start();
        hourGlass.stop();
        hourGlass.start();
        assertTrue(hourGlass.isRunning());
        assertNotNull(schedulerField.get(hourGlass));
        assertFalse(((ScheduledExecutorService) schedulerField.get(hourGlass)).isShutdown());
    }

    @Test
    void testStopWhenNotRunning() throws Exception {
        ScheduledExecutorService initialScheduler = (ScheduledExecutorService) schedulerField.get(hourGlass);
        hourGlass.stop();
        assertTrue(initialScheduler.isShutdown());
    }

    @Test
    void testCountdownWhenAtZero() throws Exception {
        secondsLeftField.setInt(hourGlass, 0);
        hourGlass.start();
        Thread.sleep(100);
        assertFalse(hourGlass.isRunning());
        assertEquals(90, hourGlass.getSecondsLeft());
    }

    @Test
    void testStopAfterReset() throws Exception {
        secondsLeftField.setInt(hourGlass, 1);
        hourGlass.start();
        Thread.sleep(1500);
        hourGlass.stop();
        assertFalse(hourGlass.isRunning());
        assertEquals(90, hourGlass.getSecondsLeft());
    }

    @Test
    void testSchedulerRecreatedAfterShutdown() throws Exception {
        hourGlass.start();
        hourGlass.stop();
        assertTrue(((ScheduledExecutorService) schedulerField.get(hourGlass)).isShutdown());
        hourGlass.start();
        assertNotNull(schedulerField.get(hourGlass));
        assertFalse(((ScheduledExecutorService) schedulerField.get(hourGlass)).isShutdown());
        assertTrue(hourGlass.isRunning());
    }

    private HourGlass serializeAndDeserialize(HourGlass original) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(original);
        out.flush();
        byte[] bytes = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bis);
        return (HourGlass) in.readObject();
    }
}