package control.structures.kt.examples

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.sqrt


class Pythagoras(var withStacktraces : Boolean = false) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(Pythagoras::class.java.name)
    }

    fun pythagoras_communicating_sequential_processes_style(x: Double, y: Double): Double {
        logger.info("-----------------------------------------------------------------------------");
        logger.info("pythagoras_communicating_sequential_processes_style");

        // https://kotlinlang.org/docs/channels.html
        val xc = Channel<Double>()
        val xc_square = Channel<Double>()
        val yc = Channel<Double>()
        val yc_square = Channel<Double>()
        val square_sum = Channel<Double>()
        val result  = Channel<Double>()
        val r = AtomicReference<Double>()

        runBlocking {
            val f1 = launch {
                try {
                    while(true) {
                        val receive = xc.receive()
                        logger.debug("CSP-style: xc.receive(): " + receive)
                        xc_square.send(receive * receive)
                    }
                } catch (_: ClosedReceiveChannelException){ }

                xc_square.close()
            }

            val f2 = launch {
                try {
                    while(true) {
                        val receive = yc.receive()
                        logger.debug("CSP-style: yc.receive(): " + receive)
                        yc_square.send(receive * receive)
                    }
                } catch (_: ClosedReceiveChannelException){ }

                yc_square.close()
            }

            val f3 = launch {
                try {
                    while(true) {
                        val p1 = xc_square.receive()
                        val p2 = yc_square.receive()
                        logger.debug("CSP-style: p1, p2: $p1, $p2")
                        square_sum.send(p1 + p2)
                    }
                } catch (_: ClosedReceiveChannelException){ }

                square_sum.close();
            }

            val f4 = launch {
                try {
                    while (true) {
                        val p = square_sum.receive()
                        logger.debug("CSP-style: square_sum.receive(): $p")
                        result.send(sqrt(p))
                    }
                } catch (_: ClosedReceiveChannelException){ }

                result.close()
            }

            val f5 = launch {
                logger.debug("CSP-style: yc.send(): $y")
                yc.send(y)
                logger.debug("CSP-style: xc.send(): $x")
                xc.send(x)
                logger.debug("CSP-style: result.receive(): start")
                var receive = result.receive()
                logger.debug("CSP-style: result.receive(): $receive")
                r.set(receive)
                xc.close()
                yc.close()
                /*
                logger.debug("CSP-style: yc.send(): $y")
                yc.send(y)
                logger.debug("CSP-style: xc.send(): $x")
                xc.send(x)
                logger.debug("CSP-style: result.receive(): start")
                receive = result.receive()
                logger.debug("CSP-style: result.receive(): $receive")
                r.set(receive)

                 */
            }

            logger.debug("CSP-style: runBlocking: about to exit 1")
            f5.join()
            /*
            f1.cancel()
            f2.cancel()
            f3.cancel()
            f4.cancel()
             */

            logger.debug("CSP-style: runBlocking: about to exit 2")
        }
        logger.debug("CSP-style: runBlocking: exited")

        return r.get()
    }

    fun pythagoras_kotlin_flow(x: Double, y: Double): Double = runBlocking<Double> {
        logger.info("-----------------------------------------------------------------------------");
        logger.info("pythagoras_kotlin_flow");

        val fx = listOf(x).asFlow()
        val fx_squared = fx.map { it * it }
        val fy = listOf(y).asFlow()
        val fy_squared = fy.map { it * it }

        val pairs = fx_squared.zip(fy_squared) { a, b -> Pair(a, b) }

        pairs.map { p -> p.first + p.second }
            .map { logger.debug("Square-Sum: $it"); it}
            .map { sqrt(it) }.last()
    }

}