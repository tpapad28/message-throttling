
package com.tpapad.poc;

import com.tpapad.throttling.SimpleThrottlingBuffer;
import com.tpapad.throttling.Throttler;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author tpapad
 */
@ApplicationScoped
public class MessageService {

    private Throttler<Serializable> throttler;

    @PostConstruct
    void setup() {

        throttler = new Throttler.ThrottlerBuilder<>()
                .withMsgsPerInterval(8)
                .withIntervalUnits(1)
                .withIntervalUnit(TimeUnit.SECONDS)
                .withBuffer(new SimpleThrottlingBuffer<>())
                .build();
        throttler.start();
    }

    @PreDestroy
    void stop() {
        throttler.stop();
    }

    public void submitMessage(final String message) {
        throttler.submit(message);
    }

}
