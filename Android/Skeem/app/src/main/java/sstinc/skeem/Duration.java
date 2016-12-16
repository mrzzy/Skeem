package sstinc.skeem;

import org.joda.time.DurationFieldType;
import org.joda.time.Period;

import java.sql.Time;

/**
 * Defines Representation of Duration of Time
 * Defines an Object Representation of a Duration of Time, where Duration of time refers to an
 * an amount of time between 2 specified points of time, where the amount of time can be positive or
 * negative.
 * Maximum Time Resolution: 1 milisecond
 *
*/
public class Duration {
    //Constants
    private static final int MILLISECOND_IN_SECOND = 1000;
    private static final int MILLISECOND_IN_MINUTE = 60000;
    private static final int MILLISECOND_IN_HOUR = 3600000;

    //Duration Data
    private long durationMillis; //Duration in Milliseconds

    /**
     * Defines an enum of the units that Duration supports to construct from or retrieve to.
     */
    public enum TimeUnit {
        millisecond,
        second,
        minute,
        hour
    }

    //Constructors

    /**
     * Duration default constructor.
     * Constructs a new Duration object that represent null amount of time.
     */
    public Duration()
    {
        this.durationMillis = 0;
    }

    /**
     * Duration copy constructor.
     * Constructs a new Duration object is equal to the object specifed by <code>duration</code>
     * Eg. <code>this.equals(duration)</code> is true.
     *
     * @param duration
     */
    public Duration(Duration duration)
    {
        this.durationMillis = duration.getDurationMillis();
    }

    /**
     * Duration Microsecond Constructor
     * Constructs a new Duration Object that represents <code>millisecond</code>
     * milliseconds of time.
     *
     * @param millisecond Number of milliseconds
     */
    public Duration(long millisecond)
    {
        this.durationMillis = millisecond;
    }

    /**
     * Duration TimeUnit Constructor
     * Constructs a new Duration Object that represents <code>count</code> number of
     * <code>unit</code> of time.
     *
     * @param count Number of units of time.
     * @param unit  The unit to use.
     * @see TimeUnit
     */
    public Duration(long count, TimeUnit unit)
    {
        if(unit == TimeUnit.millisecond) this.durationMillis = count;
        else if(unit == TimeUnit.second) this.durationMillis = count * Duration.MILLISECOND_IN_SECOND;
        else if(unit == TimeUnit.minute) this.durationMillis = count * Duration.MILLISECOND_IN_MINUTE;
        else if(unit == TimeUnit.hour) this.durationMillis = count * Duration.MILLISECOND_IN_HOUR;
    }

    //Object Methods

    /**
     * Determines equality between objects.
     *
     * @param otherObject
     * @return Returns true if objects are equal false otherwise
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object otherObject)
    {
        if(otherObject == null) return false;
        if(this.getClass() != otherObject.getClass()) return false;

        Duration otherDuration = (Duration)otherObject;
        boolean equal = false;

        if(this.getDurationMillis() == otherDuration.getDurationMillis()) equal = true;

        return equal;
    }

    /**
     * Compares this Duration with Another Duration
     * Compares this object with other object <code>otherObject</code>.
     *
     * @param otherDuration
     * @return Returns 0 if objects are equal, -1 if this object less then otherObject, 1 otherwise.
     */
    public int compare(Duration otherDuration)
    {
        if(this.equals(otherDuration)) return 0;
        return (this.getDurationMillis() < otherDuration.getDurationMillis()) ? -1 : 1;
    }

    //Object Manipulations

    /**
     * Add current duration by specified duration
     * Add the amount of time by: <code>count * unit</code> amount of time
     *
     * @param count The number of units to add
     * @param unit The unit to use
     */
    public void add(long count, TimeUnit unit)
    {
        long addDuration = 0;
        if(unit == TimeUnit.millisecond) addDuration = count;
        else if(unit == TimeUnit.second) addDuration = (count * Duration.MILLISECOND_IN_SECOND);
        else if(unit == TimeUnit.minute) addDuration = (count * Duration.MILLISECOND_IN_MINUTE);
        else if(unit == TimeUnit.hour) addDuration = (count * Duration.MILLISECOND_IN_HOUR);

        this.durationMillis += addDuration;
    }

    /**
     * Add current duration by specified Duration Object
     * Add the amount of time by the amount of time of specified by <code>duration</code> object
     *
     * @param duration The Duration to add
     */
    public void add(Duration duration)
    {
        add(duration.getDurationMillis(), TimeUnit.millisecond);
    }

    /**
     * Minus current duration by specified duration
     * Minus the amount of time by: <code>count * unit</code> amount of time
     *
     * @param count The number of units to minus
     * @param unit The unit to use
     */
    public void minus(long count, TimeUnit unit)
    {
        long minusDuration = 0;
        if(unit == TimeUnit.millisecond) minusDuration = count;
        else if(unit == TimeUnit.second) minusDuration = (count * Duration.MILLISECOND_IN_SECOND);
        else if(unit == TimeUnit.minute) minusDuration = (count * Duration.MILLISECOND_IN_MINUTE);
        else if(unit == TimeUnit.hour) minusDuration = (count * Duration.MILLISECOND_IN_HOUR);

        this.durationMillis -= minusDuration;
    }


    /**
     * Minus current duration by specified Duration Object
     * Minus the amount of time by the amount of time specified by the <code>duration</code> object
     *
     * @param duration The Duration to minus
     */
    public void minus(Duration duration)
    {
        minus(duration.getDurationMillis(), TimeUnit.millisecond);
    }

    //Setters & Getters

    /**
     * Retrieves amount of time represented in milliseconds
     *
     * @return Current amount of time represented in milliseconds
     */
    public long getDurationMillis()
    {
        return this.durationMillis;
    }

    /**
     * Retrieves current duration in units
     * Retrieves the amount of time represented in the current Duration object converted
     * to <code>unit</code> time unit. The conversion is floored: 1 minute 30second 55 microsecond
     * is converted 1 minutes if unit specified is minutes.
     *
     * @param unit The unit to use
     * @return Floored conversion of the current duration in specified units
     */
    public long getDuration(TimeUnit unit)
    {
        long retrieveCount = 0;
        if(unit == TimeUnit.millisecond) retrieveCount = this.getDurationMillis();
        else if(unit == TimeUnit.second) retrieveCount = (this.getDurationMillis() / MILLISECOND_IN_SECOND);
        else if(unit == TimeUnit.minute) retrieveCount = (this.getDurationMillis() / MILLISECOND_IN_MINUTE);
        else if(unit == TimeUnit.hour) retrieveCount = (this.getDurationMillis() / MILLISECOND_IN_HOUR);

        return retrieveCount;
    }
}
