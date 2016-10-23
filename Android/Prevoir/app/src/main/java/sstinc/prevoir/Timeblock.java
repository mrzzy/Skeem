package sstinc.prevoir;

import java.util.ArrayList;

class Timeblock extends Schedulable {
    ArrayList<Task> tasks_scheduled;
    Datetime from;
    Datetime to;
    Duration duration;
    Duration duration_used;
    Duration duration_left;

    Timeblock(Datetime from, Datetime to) {
        this.from = from;
        this.to = to;

        // Calculate duration
        int duration_year = from.getYear() - to.getYear();
        int duration_month = from.getMonth() - to.getMonth();
        int duration_day = from.getDay() - to.getDay();
        int duration_hour = from.getHour() - to.getHour();
        int duration_minute = from.getMinute() - to.getMinute();

        this.duration = new Duration(duration_year, duration_month, duration_day,
                duration_hour, duration_minute);
        this.duration_used = new Duration();
        this.duration_left = duration;

        this.tasks_scheduled = new ArrayList<>();
    }

    boolean addTask(Task task) {
        if (duration_left.toFullString().compareTo(task.duration.toFullString()) == -1) {
            return false;
        }
        this.tasks_scheduled.add(task);

        // Calculate duration
        duration_left.setYears(duration_left.getYears() - task.duration.getYears());
        duration_left.setMonths(duration_left.getMonths() - task.duration.getMonths());
        duration_left.setDays(duration_left.getDays() - task.duration.getDays());
        duration_left.setHours(duration_left.getHours() - task.duration.getHours());
        duration_left.setMinutes(duration_left.getMinutes() - task.duration.getMinutes());

        duration_used.setYears(duration_used.getYears() + task.duration.getYears());
        duration_used.setMonths(duration_used.getMonths() + task.duration.getMonths());
        duration_used.setDays(duration_used.getDays() + task.duration.getDays());
        duration_used.setHours(duration_used.getHours() + task.duration.getHours());
        duration_used.setMinutes(duration_used.getMinutes() + task.duration.getMinutes());

        return true;
    }
}
