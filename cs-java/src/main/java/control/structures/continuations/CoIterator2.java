/*
 * https://www.linkedin.com/pulse/having-fun-loom-generators-sebastian-kaiser/
 */
package control.structures.continuations;

import control.structures.examples.Pythagoras;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class CoIterator2<E> implements Iterator<E>, Runnable, Serializable {
    @Serial
    private static final long serialVersionUID = 8089836263818156916L;

    private static final Logger logger = LoggerFactory.getLogger(CoIterator2.class);

    private final Coroutine2 co;

    private E element;
    private boolean hasElement;

    protected CoIterator2() {
        co = new Coroutine2(this);
    }

    @Override
    public boolean hasNext() {
        while(!hasElement && !co.isDone()) {
            co.run();
        }
        return hasElement;
    }

    @Override
    public E next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        E result = element;
        hasElement = false;
        element = null;
        return result;
    }

    protected void produce(E element) {
        if(hasElement) {
            throw new IllegalStateException("hasElement = true");
        }
        this.element = element;
        hasElement = true;
        co.yield();
    }

    public abstract void run();

}
