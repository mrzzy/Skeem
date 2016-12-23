package sstinc.skeem.models;


/**
 * @deprecated use datetime to represent deadline instead.
 */
class Deadline {
    private long taskId;
    private Datetime deadline;
    private boolean hasDueTime;

    Deadline() {
        this.deadline = new Datetime();
        this.hasDueTime = false;
        this.taskId = -1;
    }

    Deadline(Datetime deadline) {
        this.deadline = deadline;
        this.taskId = -1;
    }

    Deadline(Deadline deadline) {
        this.deadline = deadline.getDeadline();
        this.hasDueTime = deadline.getHasDueTime();
        this.taskId = deadline.getId();
    }

    void setDeadline(Datetime deadline) {
        this.deadline = deadline;
    }

    void setId(long id) { this.taskId = id; }
    Datetime getDeadline() { return this.deadline; }
    boolean getHasDueTime() { return this.hasDueTime; }
    long getId() { return this.taskId; }
}
