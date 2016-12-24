package sstinc.skeem.schedule;

import android.content.Context;

import org.joda.time.Period;

import java.util.ArrayList;

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
        ArrayList<Task> unscheduledTasks = this.getExpandedTasks();

        ArrayList<Schedulable> schedule = new ArrayList<>();
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
