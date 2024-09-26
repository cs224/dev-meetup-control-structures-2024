package control.structures.continuations;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.Iterator;

public class CoIteratorTest {

    private static final Logger logger = LoggerFactory.getLogger(CoIteratorTest.class);

    @Test
    public void testCoIterator() {
        Iterator<String> iter = new TestIterator();
        while(iter.hasNext()) {
            logger.info(iter.next());
        }
    }

    @Test
    public void testCoIterator2() {
        Iterator<String> iter = new TestIterator2();
        while(iter.hasNext()) {
            logger.info(iter.next());
        }
    }

    public static class TestIterator extends CoIterator<String> {
        @Serial
        private static final long serialVersionUID = 7636131888074378106L;

        @Override
        public void run() {
            logger.info("In the run() method.");
            produce("A");
            logger.info("In the run() method.");
            produce("B");
            logger.info("In the run() method.");
            for(int i = 0; i < 4; i++) {
                produce("C" + i);
            }
            logger.info("In the run() method.");
            produce("D");
            logger.info("In the run() method.");
            produce("E");
            logger.info("In the run() method.");
        }
    }

    public static class TestIterator2 extends CoIterator2<String> {
        @Serial
        private static final long serialVersionUID = 4165375504500599382L;

        @Override
        public void run() {
            logger.info("In the run() method.");
            produce("A");
            logger.info("In the run() method.");
            produce("B");
            logger.info("In the run() method.");
            for(int i = 0; i < 4; i++) {
                produce("C" + i);
            }
            logger.info("In the run() method.");
            produce("D");
            logger.info("In the run() method.");
            produce("E");
            logger.info("In the run() method.");
        }
    }
}
