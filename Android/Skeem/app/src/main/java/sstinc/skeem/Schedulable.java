package sstinc.skeem;

import android.os.Parcel;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

/**
 * This class is the superclass for schedulable objects such as tasks,
 * time blocks and void blocks. Each of these schedulable objects have a
 * scheduled start time and stop time and an id.
 *
 * @see Task
 * @see Timeblock
 * @see Voidblock
 */
class Schedulable {
    long id = -1;
    Datetime scheduled_stop;
    Datetime scheduled_start;

    /**
     * Default constructor. Sets the id to -1 to indicate that there is no
     * id. Creates new {@link Datetime} instances for the scheduled start
     * and stop time.
     */
    Schedulable() {
        this.id = -1;
        this.scheduled_start = new Datetime();
        this.scheduled_stop = new Datetime();
    }

    // Copy constructor
    Schedulable(Schedulable schedulable) {
        this.id = schedulable.getId();
        this.scheduled_start = new Datetime(schedulable.getScheduledStart());
        this.scheduled_stop = new Datetime(schedulable.getScheduledStop());
    }

    /**
     * Determines equality between objects.
     *
     * @param otherObject the object to compare to
     * @return Returns true if objects are equal false otherwise
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object otherObject) {
        // Safety Check
        if (otherObject == null) return false;
        if (otherObject.getClass() != this.getClass()) return false;

        // Field Check
        Schedulable otherSchedulable = (Schedulable) otherObject;
        return otherSchedulable.getId() == this.getId() &&
                otherSchedulable.getScheduledStart().equals(this.getScheduledStart()) &&
                otherSchedulable.getScheduledStop().equals(this.getScheduledStop());
    }

    // Getters and Setters
    /**
     * Gets the schedulable's id.
     * @return schedulable's id
     */
    long getId() {
        return this.id;
    }
    /**
     * Gets the scheduled time for the schedulable to start.
     * @return schedulable's scheduled start time
     */
    Datetime getScheduledStart() {
        return this.scheduled_start;
    }
    /**
     * Gets the scheduled time for the schedulable to stop.
     * @return schedulable's scheduled stop time
     */
    Datetime getScheduledStop() {
        return this.scheduled_stop;
    }
    /**
     * Calculates the scheduled period for the schedulable.
     * @return schedulable's scheduled period
     */
    Period getScheduledPeriod() {
        return new Period(this.scheduled_stop.getMillis() - this.getScheduledStart().getMillis());
    }

    /**
     * {@link #getId()}
     * @param id schedulable's id
     */
    void setId(long id) {
        this.id = id;
    }
    /**
     * {@link #getScheduledStart()}
     * @param scheduled_start schedulable's start time */
    void setScheduledStart(Datetime scheduled_start) {
        this.scheduled_start = scheduled_start;
    }
    /**
     * {@link #getScheduledStop()}
     * @param scheduled_stop schedulable's stop time
     */
    void setScheduledStop(Datetime scheduled_stop) {
        this.scheduled_stop = scheduled_stop;
    }

    void writeSchedulableToParcel(Parcel out, int flags) {
        out.writeParcelable(this.scheduled_start, flags);
        out.writeParcelable(this.scheduled_stop, flags);
    }

    static Schedulable readSchedulableFromParcel(Parcel in) {
        Schedulable schedulable = new Schedulable();
        schedulable.setScheduledStart((Datetime) in.readParcelable(
                Datetime.class.getClassLoader()));
        schedulable.setScheduledStop((Datetime) in.readParcelable(
                Datetime.class.getClassLoader()));
        return schedulable;
    }
}
