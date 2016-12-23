package sstinc.skeem;

import org.junit.Test;

import java.util.Locale;

import sstinc.skeem.models.Datetime;

import static org.junit.Assert.*;

/**
 *  Unit Tests for Datetime Class.
 *  Unit Test for various aspects of the Datetime Class.
 *  Executes on current development host.
 *
 *  @see Test
 */
public class DatetimeTest {
    private org.joda.time.DateTime[] testCases;

    /**
     * Generates Test Cases for testing Datetime.
     * Outputs test cases to this.testCases, only generates if this.testCases is null.
     * Total Cases: 62406
     */
    private void generateTestCases() {
        if (this.testCases == null) {
            this.testCases = new org.joda.time.DateTime[62406];
            org.joda.time.DateTime now = org.joda.time.DateTime.now();
            int index = 0;

            //Current Date/Time
            this.testCases[index] = new org.joda.time.DateTime(now);
            index++;

            //1 minute duration, resolution: 1 millisecond.
            for (int i = 0; i < (60 * 1000); i++) {
                this.testCases[index] = new org.joda.time.DateTime(now.plusMillis(i));
                index++;
            }

            //1 day test case, resolution: 1 min.
            for (int i = 0; i < (24 * 60); i++) {
                this.testCases[index] = new org.joda.time.DateTime(now.plusMinutes(i));
                index++;
            }

            //1 year test case, resolution: 1 day.
            for (int i = 0; i < 365; i++) {
                this.testCases[index] = new org.joda.time.DateTime(now.plusDays(i));
                index++;
            }

            //50 year test case, resolution: 1 month.
            for (int i = 0; i < (50 * 12); i++) {
                this.testCases[index] = new org.joda.time.DateTime(now.plusMonths(i));
                index++;
            }
        }
    }

    /**
     * Converts Test Cases to String.
     * Test cases would be in the format specified by Datetime.
     *
     * @return Converted Test Cases
     * @see Datetime#toString()
     */
    String[] convertTestCasesString() {
        String[] stringTestCases = new String[this.testCases.length];

        for (int i = 0; i < this.testCases.length; i++) {
            org.joda.time.DateTime testCase = this.testCases[i];
            stringTestCases[i] = String.format(Locale.getDefault(), "%d/%d/%d %d:%d",
                    testCase.getYear(),
                    testCase.getMonthOfYear(),
                    testCase.getDayOfMonth(),
                    testCase.getHourOfDay(),
                    testCase.getMinuteOfHour());
        }

        return stringTestCases;
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

        Datetime testObject = new Datetime();
        org.joda.time.DateTime expectedDatetime = new org.joda.time.DateTime(0);

        assertTrue(testObject != null);
        assertTrue(testObject.getYear() == expectedDatetime.getYear());
        assertTrue(testObject.getMonth() == expectedDatetime.getMonthOfYear());
        assertTrue(testObject.getDay() == expectedDatetime.getDayOfMonth());
        assertTrue(testObject.getHour() == expectedDatetime.getHourOfDay());
        assertTrue(testObject.getMinute() == expectedDatetime.getMinuteOfHour());
        assertTrue(testObject.getHasTime() == false);
        assertTrue(testObject.getHasDate() == false);

        clean();
    }

    @Test
    public void copyConstructor() {
        setup();

        for(org.joda.time.DateTime testCase: this.testCases)
        {
            Datetime testObject = new Datetime(testCase);
            Datetime directCopy = new Datetime(testObject);
            Datetime indirectCopy = new Datetime(testObject);

            assertTrue(testObject != null);
            assertTrue(testObject.equals(directCopy));
            assertTrue(directCopy.equals(testObject));
            assertTrue(testObject.equals(indirectCopy));
        }

        clean();
    }

    @Test
    public void org_joda_time_DatetimeConstructor()
    {
        setup();

        for(org.joda.time.DateTime testCase: this.testCases)
        {
            Datetime testObject = new Datetime(testCase);

            assertTrue(testObject != null);
            assertTrue(testObject.getYear() == testCase.getYear());
            assertTrue(testObject.getMonth() == testCase.getMonthOfYear());
            assertTrue(testObject.getDay() == testCase.getDayOfMonth());
            assertTrue(testObject.getHour() == testCase.getHourOfDay());
            assertTrue(testObject.getMinute() == testCase.getMinuteOfHour());
            assertTrue(testObject.getHasTime() == true);
            assertTrue(testObject.getHasDate() == true);
        }

        clean();
    }

    @Test
    public void stringConstructor()
    {
        setup();

        String[] stringTestCases = convertTestCasesString();
        for(int i = 0; i < this.testCases.length; i ++)
        {
            Datetime testObject = new Datetime(stringTestCases[i]);

            assertTrue(testObject != null);
            assertTrue(testObject.getYear() == this.testCases[i].getYear());
            assertTrue(testObject.getMonth() == this.testCases[i].getMonthOfYear());
            assertTrue(testObject.getDay() == this.testCases[i].getDayOfMonth());
            assertTrue(testObject.getHour() == this.testCases[i].getHourOfDay());
            assertTrue(testObject.getMinute() == this.testCases[i].getMinuteOfHour());
            assertTrue(testObject.getHasTime() == true);
            assertTrue(testObject.getHasDate() == true);
        }

        clean();
    }

    @Test
    public void equals()
    {
        setup();

        Datetime differObject = new Datetime();
        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime(testCase);
            Datetime sameObject = new Datetime(testCase);

            assertTrue(testObject.equals(sameObject) == true);
            assertTrue(sameObject.equals(testObject) == true);
            assertTrue(testObject.equals(differObject) == false);
            assertTrue(differObject.equals(testObject) == false);
        }

        clean();
    }

    @Test
    public void to_string()
    {
        setup();

        String[] stringTestCases = convertTestCasesString();
        for(int i = 0; i < this.testCases.length; i ++)
        {
            Datetime testObject = new Datetime(this.testCases[i]);

            assertTrue(testObject.toString() == stringTestCases[i]);
        }

        clean();
    }

    //Get && Set
    @Test
    public void getMillis()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime(testCase);

            assertTrue(testObject.getMillis() == testCase.getMillis());
        }
        clean();
    }

    @Test
    public void getYear()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime(testCase);
            assertTrue(testObject.getYear() == testCase.getYear());
        }
        clean();
    }

    @Test
    public void getMonth()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime(testCase);
            assertTrue(testObject.getMonth() == testCase.getMonthOfYear());
        }
        clean();
    }

    @Test
    public void getDay()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime(testCase);
            assertTrue(testObject.getDay() == testCase.getDayOfMonth());
        }
        clean();
    }

    @Test
    public void getHour()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime(testCase);
            assertTrue(testObject.getHour() == testCase.getHourOfDay());
        }
        clean();
    }


    @Test
    public void getMinute()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime(testCase);
            assertTrue(testObject.getMinute() == testCase.getMinuteOfHour());
        }


        clean();
    }

    @Test
    public void setYear()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime();
            testObject.setYear(testCase.getYear());

            assertTrue(testObject.getHasDate() == true);
            assertTrue(testObject.getYear() == testCase.getYear());
        }
        clean();
    }


    @Test
    public void setMonth()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime();
            testObject.setMonth(testCase.getMonthOfYear());

            assertTrue(testObject.getHasDate() == true);
            assertTrue(testObject.getMonth() == testCase.getMonthOfYear());
        }
        clean();
    }

    @Test
    public void setDay()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime();
            testObject.setDay(testCase.getDayOfMonth());

            assertTrue(testObject.getHasDate() == true);
            assertTrue(testObject.getDay() == testCase.getDayOfMonth());
        }
        clean();
    }

    @Test
    public void setHour()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime();
            testObject.setHour(testCase.getHourOfDay());

            assertTrue(testObject.getHasTime() == true);
            assertTrue(testObject.getHour() == testCase.getHourOfDay());
        }
        clean();
    }

    @Test
    public void setMinute()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime();
            testObject.setMinute(testCase.getMinuteOfHour());

            assertTrue(testObject.getHasTime() == true);
            assertTrue(testObject.getMinute() == testCase.getMinuteOfHour());
        }
        clean();
    }



    @Test
    public void setMillis()
    {
        setup();

        for(org.joda.time.DateTime testCase : this.testCases)
        {
            Datetime testObject = new Datetime();
            testObject.setMillis(testCase.getMillis());

            assertTrue(testObject.getHasDate() == true);
            assertTrue(testObject.getHasTime() == true);
            assertTrue(testObject.getMillis() == testCase.getMillis());
        }
        clean();
    }

    @Test
    public void setHasTime()
    {
        Datetime testObject = new Datetime();

        testObject.setHasTime(true);
        assertTrue(testObject.getHasTime() == true);
        testObject.setHasTime(false);
        assertTrue(testObject.getHasTime() == false);
        clean();
    }

    @Test
    public void setHasDate()
    {
        Datetime testObject = new Datetime();

        testObject.setHasDate(true);
        assertTrue(testObject.getHasDate() == true);
        testObject.setHasDate(false);
        assertTrue(testObject.getHasDate() == false);
        clean();
    }
}
