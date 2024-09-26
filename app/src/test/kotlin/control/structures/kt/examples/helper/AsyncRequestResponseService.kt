package control.structures.kt.examples.helper

import control.structures.kt.examples.PythagorasUsingServiceCalls.IAsyncRequestResponseService
import io.reactivex.rxjava3.core.Observer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AsyncRequestResponseService(val sleep_millis: Long = -1):  IAsyncRequestResponseService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(AsyncRequestResponseService::class.java)
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
    }


    override fun request(someParameter: String, callback: Observer<Double>) {
        logger.debug("AsyncRequestResponseService.request($someParameter): enter")
        if ("x" == someParameter) {
            executor.execute {
                if (sleep_millis > 0) {
                    try {
                        logger.debug("AsyncRequestResponseService.request($someParameter): calling sleep()")
                        Thread.sleep(sleep_millis)
                    } catch (e: Exception) {
                        logger.error("Thread.sleep() threw an exception!", e)
                    }
                }
                logger.debug("AsyncRequestResponseService.request($someParameter): calling onNext()")
                callback.onNext(3.0)
                callback.onComplete()
            }
        } else if ("y" == someParameter) {
            logger.debug("AsyncRequestResponseService.request($someParameter): calling onNext()")
            callback.onNext(4.0)
            callback.onComplete()
        } else {
            logger.debug("AsyncRequestResponseService.request($someParameter): calling onError()")
            callback.onError(RuntimeException(("Don't know parameter: '$someParameter").toString() + "'"))
        }
    }
}