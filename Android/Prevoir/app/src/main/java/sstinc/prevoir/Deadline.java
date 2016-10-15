package sstinc.prevoir;

import java.util.Calendar;

public class Deadline {
    private long taskId;
    Calendar deadline;
    boolean hasDueTime;

    public Deadline(Calendar deadline, Boolean hasDueTime) {
        this.deadline = deadline;
        this.hasDueTime = hasDueTime;
    }

    public void setId(long newId) {
        this.taskId = newId;
    }

    public long getId() {
        return this.taskId;
    }
}
