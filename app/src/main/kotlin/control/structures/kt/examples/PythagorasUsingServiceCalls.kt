package control.structures.kt.examples

import control.structures.examples.Pythagoras
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.sqrt
import io.reactivex.rxjava3.core.*


class PythagorasUsingServiceCalls {
    fun interface ISyncRequestResponseService {
        fun request(someParameter: String): Double
    }

    fun interface IAsyncRequestResponseService {
        fun request(someParameter: String, callback: Observer<Double>)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
        val pythagoras: Pythagoras = Pythagoras()

        fun calculateDistanceFromOriginFromServiceInput(service: ISyncRequestResponseService): Double {
            logger.info("-----------------------------------------------------------------------------")
            logger.info("PythagorasUsingServiceCalls.Companion.calculateDistanceFromOriginFromServiceInput()")
            val result = sqrtFromServiceInput(service)
            logger.debug("result is: $result")
            return result
        }

        fun sqrtFromServiceInput(service: ISyncRequestResponseService): Double {
            val result = sqrt(sumFromServiceInput(service))
            return result
        }

        fun sumFromServiceInput(service: ISyncRequestResponseService): Double {
            val xsquare = xsquareFromServiceInput(service)
            val ysquare = ysquareFromServiceInput(service)
            return xsquare + ysquare
        }

        fun xsquareFromServiceInput(service: ISyncRequestResponseService): Double {
            logger.debug("Starting to query service for 'x'.")
            val x = service.request("x")
            logger.debug("Finished to query service for 'x' and got result: $x")
            return x * x
        }

        fun ysquareFromServiceInput(service: ISyncRequestResponseService): Double {
            logger.debug("Starting to query service for 'y'.")
            val y = service.request("y")
            logger.debug("Finished to query service for 'y' and got result: $y")
            return y * y
        }
    }
}