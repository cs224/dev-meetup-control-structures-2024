package control.structures.kt.examples

import org.hamcrest.MatcherAssert.*
import org.hamcrest.number.IsCloseTo.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.FieldSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun interface IPythagoras {
    fun pythagoras(x: Double, y: Double): Double
}

class GivenAnEuclideanPoint {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(GivenAnEuclideanPoint::class.java.name)

        var pythagoras: Pythagoras = Pythagoras()
        var x: Double = 3.0
        var y: Double = 4.0
        var expected_result: Double = 5.0

        val whenCalculatingThePythagorasThenTheResultIsTheDistanceFromTheOrigin: Array<IPythagoras> = arrayOf(
            IPythagoras {x: Double, y: Double -> pythagoras.pythagoras_communicating_sequential_processes_style(x,y) },
            IPythagoras {x: Double, y: Double -> pythagoras.pythagoras_kotlin_flow(x, y)},
        )
    }


    @ParameterizedTest
    @FieldSource
    fun whenCalculatingThePythagorasThenTheResultIsTheDistanceFromTheOrigin(ipythagoras: IPythagoras) {
        // IPythagoras ipythagoras = (IPythagoras) (double x, double y) -> pythagoras.pythagoras_function_call_with_local_variables(x,y);
        assertThat<Double>("The distance of the point (3.0, 4.0) from the origin should be 5.0.", ipythagoras.pythagoras(x, y),  closeTo(expected_result, 0.01))
    }

}