package control.structures.kt.examples

import control.structures.examples.Pythagoras
import control.structures.kt.examples.PythagorasUsingServiceCalls.IAsyncRequestResponseService
import control.structures.kt.examples.helper.AsyncRequestResponseService
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.number.IsCloseTo.closeTo
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.sqrt

class GivenAnEuclideanPointAndAsyncRequestResponseService {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
        val service: IAsyncRequestResponseService = AsyncRequestResponseService()
        val serviceXSleep: IAsyncRequestResponseService = AsyncRequestResponseService(100)
        val pythagoras: Pythagoras = Pythagoras()
    }

    fun flowFrom(parameter: String, service: IAsyncRequestResponseService): Flow<Double> = callbackFlow {
        val callback = object : Observer<Double> {
            override fun onNext(value: Double) {
                trySendBlocking(value)
                    .onFailure { throwable ->
                        logger.error("Downstream has been cancelled or failed!", throwable)
                    }
            }
            override fun onComplete() {
                channel.close()
            }
            override fun onError(e: Throwable) {
                cancel(CancellationException("API Error", e))
            }
            override fun onSubscribe(d: Disposable) {}
        }
        service.request(parameter, callback)
        awaitClose {  }
    }
    @Test
    fun whenUsingTheAsyncServiceAsKotlinFlowTheEuclideanDistanceIsReturned() {
        logger.info("-----------------------------------------------------------------------------")
        runBlocking<Unit> {
            val fx = flowFrom("x", service)
            val fx_squared = fx.map { it * it }
            val fy = flowFrom("y", service)
            val fy_squared = fy.map { it * it }

            val pairs = fx_squared.zip(fy_squared) { a, b -> Pair(a, b) }

            val result = pairs.map { p -> p.first + p.second }
                .map { logger.debug("Square-Sum: $it"); it}
                .map { sqrt(it) }.last()
            assertThat("The distance of the point (3.0, 4.0) from the origin should be 5.0.", result, closeTo(5.0, 0.01));
        }
    }

    @Test
    fun whenUsingTheAsyncServiceAsKotlinFlowWithXSleepTheEuclideanDistanceIsReturned() {
        logger.info("-----------------------------------------------------------------------------")
        runBlocking<Unit> {
            val fx = flowFrom("x", serviceXSleep)
            val fx_squared = fx.map { it * it }
            val fy = flowFrom("y", serviceXSleep)
            val fy_squared = fy.map { it * it }

            val pairs = fx_squared.zip(fy_squared) { a, b -> Pair(a, b) }

            val result = pairs.map { p -> p.first + p.second }
                .map { logger.debug("Square-Sum: $it"); it}
                .map { sqrt(it) }.last()
            assertThat("The distance of the point (3.0, 4.0) from the origin should be 5.0.", result, closeTo(5.0, 0.01));
        }
    }

    suspend fun dataflowRequest(parameter: String, service: IAsyncRequestResponseService, promise: CompletableFuture<Double>) {
        val callback = object : Observer<Double> {
            override fun onNext(value: Double) {
                promise.complete(value)
            }
            override fun onComplete() {}
            override fun onError(e: Throwable) {
                logger.error("dataflowReuqest($parameter, ...) onError: ", e)
            }
            override fun onSubscribe(d: Disposable) {}
        }
        service.request(parameter, callback)
    }

    @Test
    fun whenUsingTheAsyncServiceViaDataflowVariableTheEuclideanDistanceIsReturned() {
        logger.info("-----------------------------------------------------------------------------")
        runBlocking<Unit> {
            val x: CompletableFuture<Double> = CompletableFuture()
            val y: CompletableFuture<Double> = CompletableFuture()
            dataflowRequest("x", service, x)
            dataflowRequest("y", service, y)
            val result = pythagoras.pythagoras_function_call_with_local_variables(x.get(), y.get())
            assertThat("The distance of the point (3.0, 4.0) from the origin should be 5.0.", result, closeTo(5.0, 0.01));
        }
    }

}