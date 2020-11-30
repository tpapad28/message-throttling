
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

    private Runnable refiller(final int msgsPerInterval) {
        return () -> {
            semaphore.drainPermits(); // remove permits from previous intervall
            semaphore.release(msgsPerInterval); // set permits for the next intervall
        };
    }

    public static Throttler build(final int msgsPerInterval, final int intervalUnits, final TimeUnit intervalUnit) {
        return new Throttler(msgsPerInterval, intervalUnits, intervalUnit);
    }

    private Throttler(final int msgsPerInterval, final int intervalUnits, final TimeUnit intervalUnit) {
        semaphore = new Semaphore(msgsPerInterval);
        ses = Executors.newScheduledThreadPool(msgsPerInterval + 1);
        ses.scheduleAtFixedRate(refiller(msgsPerInterval), intervalUnits, intervalUnits, intervalUnit);
    }

    @Override
    public void close() {
        ses.shutdown();
    }

    public void submit(String msg) throws InterruptedException {
        semaphore.acquire();
        ses.execute(
                () -> {
                    System.out.println("Processing msg: " + msg + " at " + LocalTime.now().toString());
                });
    }
}
