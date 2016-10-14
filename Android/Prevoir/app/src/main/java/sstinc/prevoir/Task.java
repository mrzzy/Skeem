package sstinc.prevoir;

import java.util.ArrayList;

public class Task {
    private int id;
    String name;
    String subject;
    enum TaskType {ONETIME, REPETITIVE}
    enum WeekDay {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY}
    TaskType type;
    ArrayList<WeekDay> weekDays;
    Deadline deadline;
    String description;

    public Task(String name, String subject, ArrayList<WeekDay> weekdays,
                Deadline deadline, String description) {
        this.name = name;
        this.subject = subject;
        this.weekDays = weekdays;
        this.deadline = deadline;
        this.description = description;
    }

    public Task(String name, String subject,
                Deadline deadline, String description) {
        this.name = name;
        this.subject = subject;
        this.weekDays = new ArrayList<>();
        this.deadline = deadline;
        this.description = description;
    }

    public void setId(int newId) {
        this.id = newId;
        this.deadline.setId(newId);
    }
}
