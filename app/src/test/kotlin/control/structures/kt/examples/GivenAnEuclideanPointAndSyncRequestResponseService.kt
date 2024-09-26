package control.structures.kt.examples

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.hamcrest.MatcherAssert.*
import org.hamcrest.number.IsCloseTo.*

import control.structures.kt.examples.PythagorasUsingServiceCalls.ISyncRequestResponseService
import control.structures.kt.examples.PythagorasUsingServiceCalls.Companion.calculateDistanceFromOriginFromServiceInput
import control.structures.kt.examples.helper.SyncRequestResponseService
import org.junit.jupiter.api.Test

class GivenAnEuclideanPointAndSyncRequestResponseService {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
        val service: ISyncRequestResponseService = SyncRequestResponseService()
    }

    @Test
    fun whenUsingTheServiceTheEuclideanDistanceIsReturned() {
        val result = calculateDistanceFromOriginFromServiceInput(service)
        assertThat("The distance of the point (3.0, 4.0) from the origin should be 5.0.", result, closeTo(5.0, 0.01));
    }
}