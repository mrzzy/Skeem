package sstinc.prevoir;

class Deadline {
    private long taskId = -1;
    Datetime deadline;
    boolean hasDueTime;

    Deadline(Datetime deadline) {
        this.deadline = deadline;
        this.hasDueTime = deadline.hasTime();
    }

    void setId(long newId) {
        this.taskId = newId;
    }

    long getId() {
        return this.taskId;
    }
}
