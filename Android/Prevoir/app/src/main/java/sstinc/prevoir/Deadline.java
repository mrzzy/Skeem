package sstinc.prevoir;

import java.util.Calendar;

public class Deadline {
    private int taskId;
    Calendar deadline;
    boolean hasDueTime;

    public Deadline(Calendar deadline, Boolean hasDueTime) {
        this.deadline = deadline;
        this.hasDueTime = hasDueTime;
    }

    public void setId(int newId) {
        this.taskId = newId;
    }
}
