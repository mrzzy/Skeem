package sstinc.skeem;

import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;

import sstinc.skeem.models.Datetime;
import sstinc.skeem.models.Schedulable;

/**
 * Unit Tests for the Schedulable Class
 * Defines Unit Tests for Schedulable Class
 * Unit Tests Run on the local host
 */

public class SchedulableTest {
    private Schedulable[] testCases;

    /**
     * Generates Test Cases for Unit Test
     * Generates Tests Cases and outputs to this.testCases
     * Only generates on the first call.
     */
    private void generateTestCases() {
        if (this.testCases == null) {
            this.testCases = new Schedulable[2];
            int index = 0;

            //Empty Case
            this.testCases[index] = new Schedulable();
            index++;

            //Large Case
            org.joda.time.DateTime beginDate = org.joda.time.DateTime.now();
            org.joda.time.DateTime endDate = beginDate.plusYears(50);
            Schedulable largeCase = new Schedulable();
            largeCase.setScheduledStart(new Datetime(beginDate));
            largeCase.setScheduledStop(new Datetime(endDate));
            largeCase.setId(1);
            this.testCases[index] = largeCase;
            index++;
        }
    }

    private void setup() {
        generateTestCases();
    }

    private void clean() {

    }

    //Unit Tests
    @Test
    public void defaultConstructor() {
        setup();

        Schedulable testObject = new Schedulable();
        Datetime expectedScheduledStart = new Datetime();
        Datetime expectedScheduledStop  = new Datetime();
        Period expectedPeriod = Period.ZERO;

        assertTrue(testObject != null);
        assertTrue(testObject.getId() == -1);
        assertTrue(testObject.getScheduledStart().equals(expectedScheduledStart));
        assertTrue(testObject.getScheduledStop().equals(expectedScheduledStop));
        assertTrue(testObject.getScheduledPeriod().equals(expectedPeriod));

        clean();
    }

    @Test
    public void copyConstructor() {
        setup();

        for (Schedulable testCase : this.testCases) {
            Schedulable copyObject = new Schedulable(testCase);
            Schedulable indirectCopyObject = new Schedulable(copyObject);

            assertTrue(testCase.equals(copyObject));
            assertTrue(copyObject.equals(testCase));
            assertTrue(testCase.equals(indirectCopyObject));
        }

        clean();
    }

    @Test
    public void equals()
    {
        setup();
        Schedulable differentObject = new Schedulable();
        differentObject.setId(10);

        for(Schedulable testCase : this.testCases)
        {
            Schedulable copyObject = new Schedulable(testCase);

            assertTrue(testCase.equals(copyObject));
            assertTrue(copyObject.equals(testCase));
            assertTrue(testCase.equals(differentObject) == false);
            assertTrue(differentObject.equals(testCase) == false);
        }

        clean();
    }
}
