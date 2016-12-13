package sstinc.skeem;

import android.content.ContentProviderOperation;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Defines unit test for Duration class
 */
public class DurationTest
{
    private Duration testObject = null;
    public static final int testLimit =  24 * 60 * 60 * 1000;

    private void setup()
    {
        this.testObject = new Duration(1000);
    }

    private void clean()
    {
        this.testObject = null;
    }

    @Test
    public void defaultConstructor()
    {
        this.testObject = new Duration();

        assertTrue(this.testObject != null);
        assertTrue(this.testObject.getMillisecond() == 0 );
        assertTrue(this.testObject.getCount(Duration.TimeUnit.millisecond) == 0);
        assertTrue(this.testObject.getCount(Duration.TimeUnit.second) == 0);
        assertTrue(this.testObject.getCount(Duration.TimeUnit.minute) == 0);
        assertTrue(this.testObject.getCount(Duration.TimeUnit.hour) == 0);

        clean();
    }

    @Test
    public void copyConstructor()
    {
        setup();

        Duration copyObject = new Duration(this.testObject);
        assertTrue(this.testObject.equals(copyObject));

        clean();
    }

    @Test
    public void millisecondConstructor()
    {
        for (long i = 0; i <= DurationTest.testLimit; i++)
        {
            this.testObject = new Duration(i);

            assertTrue(this.testObject != null);
            assertTrue(this.testObject.getMillisecond() == i);
            assertTrue(this.testObject.getCount(Duration.TimeUnit.millisecond) == i);
            assertTrue(this.testObject.getCount(Duration.TimeUnit.second) == i / 1000);
            assertTrue(this.testObject.getCount(Duration.TimeUnit.minute) == i /(60 * 1000));
            assertTrue(this.testObject.getCount(Duration.TimeUnit.hour) == i /(60 * 1000 * 1000));

            clean();
        }
    }

    @Test
    public void timeUnitConstructor()
    {
        //Test Milisecond
        for (long i = 0; i <= DurationTest.testLimit; i++)
        {
            this.testObject = new Duration(i, Duration.TimeUnit.millisecond);

            assertTrue(this.testObject != null);
            assertTrue(this.testObject.getCount(Duration.TimeUnit.millisecond) == i);

            clean();
        }

        //Test Second
        for (long i = 0; i <= DurationTest.testLimit / 1000; i++)
        {
            this.testObject = new Duration(i, Duration.TimeUnit.second);

            assertTrue(this.testObject != null);
            assertTrue(this.testObject.getCount(Duration.TimeUnit.second) == i);

            clean();
        }

        //Test Minute
        for (long i = 0; i <= DurationTest.testLimit / 60 * 1000; i++)
        {
            this.testObject = new Duration(i, Duration.TimeUnit.minute);

            assertTrue(this.testObject != null);
            assertTrue(this.testObject.getCount(Duration.TimeUnit.minute) == i);

            clean();
        }

        //Test Hour
        for (long i = 0; i <= DurationTest.testLimit / 60 * 1000; i++)
        {
            this.testObject = new Duration(i, Duration.TimeUnit.hour);

            assertTrue(this.testObject != null);
            assertTrue(this.testObject.getCount(Duration.TimeUnit.hour) == i);

            clean();
        }
    }

    @Test
    public void equality()
    {
        setup();
        Duration copyObject = new Duration(this.testObject);
        Duration indirectCopyObject = new Duration(copyObject);

        assertTrue(this.testObject.equals(copyObject));
        assertTrue(copyObject.equals(this.testObject));
        assertTrue(this.testObject.equals(indirectCopyObject));

        clean();
    }

    @Test
    public void compare()
    {
        Duration smallObject = new Duration(1000);
        Duration bigObject = new Duration(500000);

        assertTrue(smallObject.compare(smallObject) == 0);
        assertTrue(bigObject.compare(bigObject) == 0);
        assertTrue(smallObject.compare(bigObject) == -1);
        assertTrue(bigObject.compare(smallObject) == 1);
    }

    @Test
    public void addUnit()
    {
        setup();

        this.testObject.add(1, Duration.TimeUnit.millisecond);
        this.testObject.add(1, Duration.TimeUnit.second);
        this.testObject.add(1, Duration.TimeUnit.minute);
        this.testObject.add(1, Duration.TimeUnit.hour);

        assertTrue(this.testObject.getMillisecond() == 3662001);

        clean();
    }

    @Test
    public void addDuration()
    {
        setup();
        Duration addObject = new Duration(4000);

        this.testObject.add(addObject);

        assertTrue(this.testObject.getMillisecond() == 5000);
        clean();
    }

    @Test
    public void minusUnit()
    {
        setup();

        this.testObject.minus(1, Duration.TimeUnit.millisecond);
        this.testObject.minus(1, Duration.TimeUnit.second);
        this.testObject.minus(1, Duration.TimeUnit.minute);
        this.testObject.minus(1, Duration.TimeUnit.hour);

        assertTrue(this.testObject.getMillisecond() == -411001);
        clean();
    }

    @Test
    public void minusDuration()
    {
        setup();
        Duration minusObject = new Duration(4000);

        this.testObject.minus(minusObject);

        assertTrue(this.testObject.getMillisecond() == -3000);
        clean();
    }
}
