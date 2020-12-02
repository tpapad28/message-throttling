
package com.tpapad.throttling;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A simplistic implementation of a {@link ThrottlingBuffer} using a
 * {@link LinkedBlockingQueue}
 *
 * @author tpapad
 * @param <T>
 */
public class SimpleThrottlingBuffer<T extends Serializable> implements ThrottlingBuffer<T> {

    private final BlockingQueue<ThrottledEntity<T>> queue = new LinkedBlockingQueue<>();

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public ThrottledEntity<T> take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void add(T entity) {
        queue.add(new ThrottledEntity<>(entity));
    }

    @Override
    public void saveState() {
        // NOOP
        // TODO: Save queue contents on disk
    }

    @Override
    public void restoreState() {
        // NOOP
        // TODO: Load state from disk into queue
    }

}
