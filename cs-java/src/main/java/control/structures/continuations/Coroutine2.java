package control.structures.continuations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.concurrent.Executor;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.LockSupport;

public class Coroutine2 implements ICoroutine, Serializable {
    @Serial
    private static final long serialVersionUID = 8005069474223334524L;

    private static final Logger logger = LoggerFactory.getLogger(Coroutine2.class);

    //private static final SuspendingExecutor myFiberScheduler = new SuspendingExecutor("my-scheduler", new MyExecutor());
    private static final ThreadFactory myFiberScheduler = newVirtualBuilder(new MyExecutor()).name("my-scheduler", 0).factory(); //

    private boolean executionDone = false;
    private final Thread thread;

    public Coroutine2(Runnable runnable) {
        //myFiberScheduler.run(runnable);
        System.out.println("Thread ID = " + Thread.currentThread());
        Runnable r = () -> {
            try {
                //logger.debug("Fiber entry, before first park.");
                System.out.println("Thread ID = " + Thread.currentThread());
                LockSupport.park();
                //logger.debug("Fiber entry, after first park.");
                runnable.run();
                //logger.debug("Fiber exiting.");
            } finally {
                executionDone = true;
            }
        };

        ThreadFactory tf = myFiberScheduler;
        thread = tf.newThread(r);
        thread.start();
        /*
         * Thread ID = Thread[#1,main,5,main]
         * Thread ID = VirtualThread[#20,my-scheduler0]/runnable@main
         */
        /*
        Thread.Builder.OfVirtual builder = Thread.ofVirtual().name("MyThread-", 0);
        tf =  builder.factory();
        thread = tf.newThread(r);
        thread.start();
        */
        /*
         * Thread ID = Thread[#1,main,5,main]
         * Thread ID = VirtualThread[#20,MyThread-0]/runnable@ForkJoinPool-1-worker-1
         */
        // thread = builder.start(r);
        /*
         * Thread ID = Thread[#1,main,5,main]
          * Thread ID = VirtualThread[#20,MyThread-0]/runnable@ForkJoinPool-1-worker-1
         */
    }

    @Override
    public void yield() {
        //logger.debug("LockSupport.park(): entry");
        LockSupport.park();
        //logger.debug("LockSupport.park(): exit");
    }

    @Override
    public void run() {
        // logger.debug("LockSupport.unpark(): entry");
        LockSupport.unpark(thread);
        // logger.debug("LockSupport.unpark(): exit");
    }

    @Override
    public boolean isDone() {
        return executionDone;
    }

    /*
     * https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html
     * https://stackoverflow.com/questions/69749459/is-there-a-way-to-tell-what-carrier-thread-a-virtual-thread-is-running-on
     * --add-opens java.base/java.lang=ALL-UNNAMED
     */

    public static Thread.Builder.OfVirtual newVirtualBuilder(Executor executor) {
        Objects.requireNonNull(executor);

        // Construct a specialized virtual thread builder which runs continuations on given executor.
        // We need to use reflection since the API is not public:
        // https://bugs.java.com/bugdatabase/view_bug?bug_id=JDK-8308541
        try {
            final Class<?> vtbclass = Class.forName("java.lang.ThreadBuilders$VirtualThreadBuilder");
            final Constructor<?> c = vtbclass.getDeclaredConstructor(Executor.class);
            c.setAccessible(true);
            final Thread.Builder.OfVirtual vtb = (Thread.Builder.OfVirtual) c.newInstance(executor);
            return vtb;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class MyExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

}
