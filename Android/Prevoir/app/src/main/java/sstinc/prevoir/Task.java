package sstinc.prevoir;

import java.util.ArrayList;
import javax.xml.datatype.Duration;

public class Task {
    private long id;
    String name;
    String subject;
    enum TaskType {ONETIME, REPETITIVE}
    enum WeekDay {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY}
    TaskType type;
    ArrayList<WeekDay> weekDays;
    Deadline deadline;
    String description;
    Duration duration;
    Duration min_time_period;

    public Task(String name, String subject, ArrayList<WeekDay> weekdays,
                Deadline deadline, String description, Duration duration) {
        this.name = name;
        this.subject = subject;
        this.weekDays = weekdays;
        this.deadline = deadline;
        this.description = description;
        this.duration = duration;
    }

    public Task(String name, String subject, ArrayList<WeekDay> weekdays,
                Deadline deadline, String description, Duration duration,
                Duration min_time_period) {
        this.name = name;
        this.subject = subject;
        this.weekDays = weekdays;
        this.deadline = deadline;
        this.description = description;
        this.duration = duration;
        this.min_time_period = min_time_period;
    }

    public void setId(long newId) {
        this.id = newId;
        this.deadline.setId(newId);
    }

    public long getId() {
        return this.id;
    }
}
