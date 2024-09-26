package control.structures.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

import java.util.concurrent.atomic.AtomicReference;

public class Pythagoras {

    private static final Logger logger = LoggerFactory.getLogger(Pythagoras.class);

    private final boolean withStacktraces;

    public Pythagoras() { this(false); }

    public Pythagoras(boolean withStacktraces) {
        this.withStacktraces = withStacktraces;
    }

    public double pythagoras_function_call_with_local_variables(double x, double y) {
        logger.info("-----------------------------------------------------------------------------");
        logger.info("pythagoras_function_call_with_local_variables");

        logger.debug("xsquared");
        double xsquared = times(x, x);
        logger.debug("ysquared");
        double ysquared = times(y, y);
        logger.debug("squaresum");
        double squaresum = plus(xsquared, ysquared);
        logger.debug("distance");
        double distance = sqrt(squaresum);
        return distance;
    }

    public double pythagoras_function_calls_per_level(double x, double y) {
        logger.info("-----------------------------------------------------------------------------");
        logger.info("pythagoras_function_calls_per_level");

        return sqrt(plus(times(x,x),times(y,y)));
    }

    // ------------------------

    public double squaresum(double x, double y) {
        double xsquared = times(x, x);
        double ysquared = times(y, y);
        return plus(xsquared, ysquared);
    }

    public double times(double a, double b) {
        if(withStacktraces) logger.debug("stack", new Exception("stack"));
        return a * b;
    }

    public double plus(double a, double b) {
        return a + b;
    }

    public double sqrt(double a) {
        return Math.sqrt(a);
    }

    // ------------------------

    public double pythagoras_function_calls_per_level_by_need(double x, double y) {
        logger.info("-----------------------------------------------------------------------------");
        logger.info("pythagoras_function_calls_per_level_by_need");

        return sqrt_by_need(plus_by_need(() -> times(x,x), () -> times(y,y))).get();
    }

    public Supplier<Double> plus_by_need(Supplier<Double> a, Supplier<Double> b) {
        return () -> {logger.debug("plus_by_need"); return a.get() + b.get();};
    }

    public Supplier<Double> sqrt_by_need(Supplier<Double> a) {
        return () -> {logger.debug("sqrt_by_need"); return Math.sqrt(a.get());};
    }

    public double pythagoras_continuation_passing_style(double x, double y) {
        logger.info("-----------------------------------------------------------------------------");
        logger.info("pythagoras_continuation_passing_style");

        final AtomicReference<Double> result = new AtomicReference<Double>();
        times_cps(x, x,
                (Double i) -> times_cps(y, y,
                        (Double j) -> plus_cps(i, j,
                                (Double k) -> sqrt_cps(k,
                                        (Double l) -> identity(l, result)
                                )
                        )
                )
        );

        return result.get();
    }

    public void times_cps(double a, double b, Consumer<Double> k) {
        logger.debug("times_cps");
        if(withStacktraces) logger.debug("stack", new Exception("stack"));
        k.accept(a * b);
    }

    public void plus_cps(double a, double b, Consumer<Double> k) {
        logger.debug("plus_cps");
        k.accept(a + b);
    }

    public void sqrt_cps(double a, Consumer<Double> k) {
        logger.debug("sqrt_cps");
        k.accept(Math.sqrt(a));
    }

    public void identity(double a, AtomicReference<Double> result) {
        logger.debug("identity");
        if(withStacktraces) logger.debug("stack", new Exception("stack"));
        result.set(a);
    }

    public double fn(double i1, double i2, double in) {
        return 0.0;
    }

    public void fn_cps(double i1, double i2, double in, Consumer<Double> k) {
        k.accept(0.0);
    }

    // ------------------------

    // I did not find a dataflow equivalent outside of Parallel Universe Quasar: https://docs.paralleluniverse.co/quasar/#dataflow-reactive-programming
    // public double pythagoras_dataflow(double x, double y) { ... }

    // java.util.Observable was deprecated:
    // public double pythagoras_event_driven_observer_pattern_style(double x, double y) { ... }

}
