package sstinc.skeem;

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
    private final int MILLISECOND_IN_SECOND = 1000;
    private final int MILLISECOND_IN_MINUTE = 60000;
    private final int MILLISECOND_IN_HOUR = 3600000;

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
    public Duration();

    /**
     * Duration copy constructor.
     * Constructs a new Duration object is equal to the object specifed by <code>duration</code>
     * Eg. <code>this.equals(duration)</code> is true.
     *
     * @param duration
     */
    public Duration(Duration duration);

    /**
     * Duration Microsecond Constructor
     * Constructs a new Duration Object that represents <code>millisecond</code>
     * milliseconds of time.
     *
     * @param milisecond Number of milliseconds
     */
    public Duration(long milisecond);

    /**
     * Duration TimeUnit Constructor
     * Constructs a new Duration Object that represents <code>count</code> number of
     * <code>unit</code> of time.
     *
     * @param count Number of units of time.
     * @param unit  The unit to use.
     * @see TimeUnit
     */
    public Duration(long count, TimeUnit unit);

    //Object Methods

    /**
     * Determines equality between objects.
     *
     * @param otherObject
     * @return Returns true if objects are equal false otherwise
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object otherObject);

    /**
     * Compares this object with another object
     * Compares this object with other object <code>otherObject</code>.
     *
     * @param otherObject
     * @return Returns 0 if objects are equal, -1 if this object less then otherObject, 1 otherwise.
     */
    public byte compare(Object otherObject);

    //Object Manipulations

    /**
     * Add current duration by specified duration
     * Add the amount of time by: <code>count * unit</code> amount of time
     *
     * @param count The number of units to add
     * @param unit The unit to use
     */
    public void add(long count, TimeUnit unit);

    /**
     * Add current duration by specified Duration Object
     * Add the amount of time by the amount of time of specified by <code>duration</code> object
     *
     * @param duration The Duration to add
     */
    public void add(Duration duration);

    /**
     * Minus current duration by specified duration
     * Minus the amount of time by: <code>count * unit</code> amount of time
     *
     * @param count The number of units to minus
     * @param unit The unit to use
     */
    public void minus(long count, TimeUnit unit);

    /**
     * Minus current duration by specified Duration Object
     * Minus the amount of time by the amount of time specified by the <code>duration</code> object
     *
     * @param duration The Duration to minus
     */
    public void minus(Duration duration);

    //Setters & Getters

    /**
     * Retrieves amount of time represented in milliseconds
     *
     * @return Current amount of time represented in milliseconds
     */
    public long getMillisecond();

    /**
     * Retrieves current duration in units
     * Retrieves the amount of time represented in the current Duration object converted
     * to <code>unit</code> time unit. The conversion is floored: 1 minute 30second 55 microsecond
     * is converted 1 minutes if unit specified is minutes.
     *
     * @param unit The unit to use
     * @return Floored conversion of the current duration in specified units
     */
    public long getCount(TimeUnit unit);
}

