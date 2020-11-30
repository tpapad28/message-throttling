
package com.tpapad.throttling;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tpapad
 */
public class MyMain {

    public static void main(String[] args) throws InterruptedException {
        try ( Throttler throttler = Throttler.build(5, 1, TimeUnit.SECONDS)) {
            for (int i = 0; i < 50; i++) {
                final String msg = "MSG#" + i;
                System.out.println("Submitted " + msg + " at " + LocalTime.now().toString());
                throttler.submit(msg);
            }
        }
    }
}
