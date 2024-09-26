package control.structures.continuations;

import java.io.Serial;
import java.io.Serializable;

import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

public class Coroutine implements ICoroutine, Serializable {

    private static final ContinuationScope continuationScope = new ContinuationScope("Coroutine");

    @Serial
    private static final long serialVersionUID = -198528706444387242L;

    final private Continuation continuation;

    public Coroutine(Runnable proto) {
        continuation = new Continuation(continuationScope, proto);
    }

    public void yield() {
        Continuation.yield(continuationScope);
    }

    public void run() {
        continuation.run();
    }

    public boolean isDone() {
        return continuation.isDone();
    }
}
