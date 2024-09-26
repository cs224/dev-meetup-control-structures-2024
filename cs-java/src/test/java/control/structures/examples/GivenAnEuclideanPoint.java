package control.structures.examples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.number.IsCloseTo.*;
import static org.hamcrest.MatcherAssert.*;


public class GivenAnEuclideanPoint {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(GivenAnEuclideanPoint.class);

    public interface IPythagoras {
        double pythagoras(double x, double y);
    }

    static Pythagoras pythagoras = new Pythagoras();
    static double x = 3.0;
    static double y = 4.0;
    static double expected_result = 5.0;

    static IPythagoras[] whenCalculatingThePythagorasThenTheResultIsTheDistanceFromTheOrigin = {
            (IPythagoras) (double x, double y) -> pythagoras.pythagoras_function_call_with_local_variables(x,y),
            (IPythagoras) (double x, double y) -> pythagoras.pythagoras_function_calls_per_level(x,y),
            (IPythagoras) (double x, double y) -> pythagoras.pythagoras_function_calls_per_level_by_need(x,y),
            (IPythagoras) (double x, double y) -> pythagoras.pythagoras_continuation_passing_style(x,y),
    };

    @ParameterizedTest
    @FieldSource
    public void whenCalculatingThePythagorasThenTheResultIsTheDistanceFromTheOrigin(IPythagoras ipythagoras) {
        // IPythagoras ipythagoras = (IPythagoras) (double x, double y) -> pythagoras.pythagoras_function_call_with_local_variables(x,y);
        assertThat("The distance of the point (3.0, 4.0) from the origin should be 5.0.", ipythagoras.pythagoras(x, y), closeTo(expected_result, 0.01));
    }

}
