
package com.tpapad.throttling;

import java.io.Closeable;
import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tpapad
 * @param <T> Entity type
 */
public class Throttler<T extends Serializable> implements Closeable {

    private final Semaphore semaphore;
    private final ThrottlingBuffer<T> buffer;
    private Thread workerThread;
    private final ScheduledExecutorService ses;
    private static final Logger LOG = Logger.getLogger(Throttler.class.getName());

    /**
     * Refill method for the leaky bucket algorithm
     *
     * @param msgsPerInterval
     * @return
     */
    private Runnable refill(final int msgsPerInterval) {
        return () -> {
            // Remove any left-over permits from previous interval
            semaphore.drainPermits();
            // Fill the leaky bucket with new permits to be used during the next interval
            semaphore.release(msgsPerInterval);
        };
    }

    /**
     * Factory method, allows creation of a Throttler which will process
     * {@code msgsPerInterval} messages every {@code intervalUnits}
     * {@code intervalUnit}, for example 5 messages per 3 seconds
     *
     * @param msgsPerInterval
     * @param intervalUnits
     * @param intervalUnit
     * @param buffer
     * @return
     */
    public static Throttler build(final int msgsPerInterval, final int intervalUnits, final TimeUnit intervalUnit, final ThrottlingBuffer buffer) {
        return new Throttler(msgsPerInterval, intervalUnits, intervalUnit, buffer);
    }

    private Throttler(final int msgsPerInterval, final int intervalUnits, final TimeUnit intervalUnit, final ThrottlingBuffer<T> buffer) {
        // TODO: Replace with builder ^^
        this.buffer = buffer;
        semaphore = new Semaphore(msgsPerInterval);
        // We always need a free executor thread for the refiller, hence the +1
        ses = Executors.newScheduledThreadPool(msgsPerInterval + 1);
        // Schedule the refill for the leaky bucket
        ses.scheduleAtFixedRate(refill(msgsPerInterval), intervalUnits, intervalUnits, intervalUnit);
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    @Override
    public void close() {
        ses.shutdown();
    }

    public void start() {
        buffer.restoreState();
        final Runnable worker = () -> {
            System.out.println("Starting worker thread...");
            while (true) {
                final ThrottledEntity<T> next;
                try {
                    next = buffer.take();
                } catch (InterruptedException ex) {
                    LOG.log(Level.FINE, "Interrupted!", ex);
                    System.out.println("Interrupted, bailing");
                    return;
                }
                if (next != null) {
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException("Failed to acquire semaphore", ex);
                    }
                    ses.execute(
                            () -> {
                                next.processed();
                                System.out.println("Processed msg: " + next.getEntity() + " after " + next.getQueueTime());
                            });
                } else {
                    System.out.println("Queue is empty...");
                }

            }
        };

        workerThread = new Thread(worker, "Worker Thread");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    public void stop() {
        workerThread.interrupt();
        buffer.saveState();
    }

    /**
     * Submit a message for processing
     *
     * @param msg
     *
     */
    public void submit(T msg) {
        buffer.add(msg);
    }
}
