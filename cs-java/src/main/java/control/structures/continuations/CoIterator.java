/*
 * https://www.linkedin.com/pulse/having-fun-loom-generators-sebastian-kaiser/
 */
package control.structures.continuations;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class CoIterator<E> implements Iterator<E>, Runnable, Serializable {
    @Serial
    private static final long serialVersionUID = 8089836263818156916L;

    private final Coroutine co;

    private E element;
    private boolean hasElement;

    protected CoIterator() {
        co = new Coroutine(this);
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
