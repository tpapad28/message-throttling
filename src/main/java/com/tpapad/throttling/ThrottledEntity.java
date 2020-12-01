
package com.tpapad.throttling;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

/**
 *
 * @author tpapad
 * @param <T> Entity type
 */
public class ThrottledEntity<T extends Serializable> {

    private final T entity;
    private final long enqueuedTS;
    private long processedTS;

    public ThrottledEntity(final T entity) {
        this.entity = entity;
        enqueuedTS = System.currentTimeMillis();
    }

    public T getEntity() {
        return entity;
    }

    public void processed() {
        processedTS = System.currentTimeMillis();
    }

    public Duration getQueueTime() {
        return Duration.between(Instant.ofEpochMilli(enqueuedTS), Instant.ofEpochMilli(processedTS));
    }

}
