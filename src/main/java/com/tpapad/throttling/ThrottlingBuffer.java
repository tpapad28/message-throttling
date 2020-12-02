
package com.tpapad.throttling;

import java.io.Serializable;

/**
 *
 * @author tpapad
 * @param <T>
 */
public interface ThrottlingBuffer<T extends Serializable> {

    boolean isEmpty();

    ThrottledEntity<T> take() throws InterruptedException;

    void add(T entity);

    void saveState();

    void restoreState();

}
