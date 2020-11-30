
package com.tpapad.throttling;

import java.io.Closeable;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tpapad
 */
public class Throttler implements Closeable {

    private final Semaphore semaphore;

    private final ScheduledExecutorService ses;

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
     * @return
     */
    public static Throttler build(final int msgsPerInterval, final int intervalUnits, final TimeUnit intervalUnit) {
        return new Throttler(msgsPerInterval, intervalUnits, intervalUnit);
    }

    private Throttler(final int msgsPerInterval, final int intervalUnits, final TimeUnit intervalUnit) {
        semaphore = new Semaphore(msgsPerInterval);
        // We always need a free executor thread for the refiller, hence the +1
        ses = Executors.newScheduledThreadPool(msgsPerInterval + 1);
        // Schedule the refill for the leaky bucket
        ses.scheduleAtFixedRate(refill(msgsPerInterval), intervalUnits, intervalUnits, intervalUnit);
    }

    @Override
    public void close() {
        ses.shutdown();
    }

    /**
     * Submit a message for processing
     *
     * @param msg
     * @throws InterruptedException
     */
    public void submit(String msg) throws InterruptedException {
        semaphore.acquire();
        ses.execute(
                () -> {
                    System.out.println("Processing msg: " + msg + " at " + LocalTime.now().toString());
                });
    }
}
