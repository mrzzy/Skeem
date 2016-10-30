package sstinc.prevoir;

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
    protected long id = -1;
    protected Datetime scheduled_start;
    protected Datetime scheduled_stop;

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
     * {@link #getId()}
     * @param id schedulable's id
     */
    void setId(long id) {
        this.id = id;
    }
    /**
     * {@link #getScheduledStart()}
     * @param scheduled_start schedulable's start time
     */
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
}
