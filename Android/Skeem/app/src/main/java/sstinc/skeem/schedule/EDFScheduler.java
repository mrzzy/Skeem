package sstinc.skeem.schedule;

import android.content.Context;

import org.joda.time.Period;

import java.util.ArrayList;

import sstinc.skeem.models.Datetime;
import sstinc.skeem.models.Schedulable;
import sstinc.skeem.models.Task;
import sstinc.skeem.models.Timeblock;

public class EDFScheduler extends Scheduler {
    public EDFScheduler(Context ctx) {
        super(ctx, "earliest deadline first");
    }

    @Override
    public ArrayList<Schedulable> schedule() {
        if (this.isEmpty()) {
            return new ArrayList<>();
        }

        //TODO: Check that changing this does not affect the original
        ArrayList<Task> unscheduledTasks = this.getNonRepeatedTasks();

        ArrayList<Schedulable> schedule = new ArrayList<>();
        ArrayList<Task> unscheduledRepeatedTasks = this.getRepeatedTasksExpanded();

        // Sort the repeated tasks first
        for (Schedulable schedulable : this.getEmptySchedule()) {
            if (schedulable instanceof Timeblock) {
                Timeblock timeblock = (Timeblock) schedulable;
                while (!timeblock.getPeriodLeft().equals(new Period()) &&
                        unscheduledRepeatedTasks.size() != 0) {
                    Task taskToSchedule = unscheduledRepeatedTasks.get(0);
                    Datetime deadlineToday = taskToSchedule.getDeadlinePerDay();
                    // Set deadline per day in current context
                    deadlineToday.setDay(timeblock.getScheduledStart().getDay());
                    deadlineToday.setMonth(timeblock.getScheduledStart().getMonth());
                    deadlineToday.setYear(timeblock.getScheduledStart().getYear());

                    if (timeblock.getScheduledStart().compareDates(
                            taskToSchedule.getScheduledStart()) == 0 &&
                            timeblock.getScheduledStart().getMillis() < deadlineToday.getMillis()) {
                        // Timeblock starts on the same day as task to schedule
                        // Timeblock starts before the time to end on the day
                        if (timeblock.getPeriodLeft(deadlineToday).toStandardDuration().getMillis() >=
                                taskToSchedule.getPeriodNeeded().toStandardDuration().getMillis()) {
                            timeblock.addTask(taskToSchedule);
                            unscheduledRepeatedTasks.remove(0);
                        } else {
                            Task taskLeftover = new Task(unscheduledRepeatedTasks.get(0));

                            taskToSchedule.setPeriodNeeded(timeblock.getPeriodLeft());
                            taskLeftover.setPeriodNeeded(unscheduledRepeatedTasks.get(0)
                                    .getPeriodNeeded().minus(timeblock.getPeriodLeft()));

                            timeblock.addTask(taskToSchedule);
                            unscheduledRepeatedTasks.set(0, taskLeftover);
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        for (Schedulable schedulable : this.getEmptySchedule()) {
            if (schedulable instanceof Timeblock) {
                Timeblock timeblock = (Timeblock) schedulable;
                while (!timeblock.getPeriodLeft().equals(new Period()) &&
                        unscheduledTasks.size() != 0) {

                    if (timeblock.getPeriodLeft().toStandardDuration().getMillis() >=
                            unscheduledTasks.get(0).getPeriodNeeded().toStandardDuration()
                                    .getMillis()) {
                        timeblock.addTask(unscheduledTasks.get(0));
                        unscheduledTasks.remove(0);
                    } else {
                        Task taskToSchedule = new Task(unscheduledTasks.get(0));
                        Task taskLeftover = new Task(unscheduledTasks.get(0));

                        taskToSchedule.setPeriodNeeded(timeblock.getPeriodLeft());
                        taskLeftover.setPeriodNeeded(unscheduledTasks.get(0).getPeriodNeeded()
                                .minus(timeblock.getPeriodLeft()));

                        timeblock.addTask(taskToSchedule);
                        unscheduledTasks.set(0, taskLeftover);
                    }
                }

                schedule.add(timeblock);
            } else {
                schedule.add(schedulable);
            }
        }

        return schedule;
    }
}
