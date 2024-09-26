package control.structures.continuations;

public interface ICoroutine {
    void yield();
    void run();
    boolean isDone();
}
