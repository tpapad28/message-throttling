
package com.tpapad.poc;

import com.tpapad.throttling.SimpleThrottlingBuffer;
import com.tpapad.throttling.Throttler;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tpapad
 */
public class MyMain {

    public static void main(String[] args) throws InterruptedException {

        try (final Throttler<Serializable> throttler = new Throttler.ThrottlerBuilder<>()
                .withMsgsPerInterval(8)
                .withIntervalUnits(1)
                .withIntervalUnit(TimeUnit.SECONDS)
                .withBuffer(new SimpleThrottlingBuffer<>())
                .build()) {

            throttler.start();
            for (int i = 0; i < 50; i++) {
                final String msg = "MSG#" + i;
                System.out.println("Submitted " + msg + " at " + LocalTime.now().toString());
                throttler.submit(msg);
            }

            while (!throttler.isEmpty()) {
                // This sleep() is just for demo purposes; nobody sleeps on production code
                Thread.sleep(1_000);
            }
            throttler.stop();
        }
    }
}
