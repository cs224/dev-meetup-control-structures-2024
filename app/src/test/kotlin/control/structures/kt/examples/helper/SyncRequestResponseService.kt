package control.structures.kt.examples.helper

import control.structures.kt.examples.PythagorasUsingServiceCalls.ISyncRequestResponseService;


class SyncRequestResponseService: ISyncRequestResponseService {
    override fun request(someParameter: String): Double {
        if("x" == someParameter) {
            return 3.0;
        } else if("y" == someParameter) {
            return 4.0;
        } else {
            throw RuntimeException("Don't know parameter: '" + someParameter + "'");
        }
    }
}