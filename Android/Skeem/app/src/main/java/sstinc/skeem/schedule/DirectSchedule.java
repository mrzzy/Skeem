package sstinc.skeem.schedule;

import android.content.Context;

import java.util.ArrayList;

import sstinc.skeem.models.Schedulable;
import sstinc.skeem.models.Task;
import sstinc.skeem.models.Timeblock;

/**
 * <p><i>This class implements {@link Scheduler}, see the documentation there
 * for context.</i></p>
 * Direct schedule schedules the tasks directly to the timeblocks available.
 * Minimum time period, is ignored.
 *
 * @see Scheduler
 * @see Schedulable
 * @see Task
 * @see Timeblock
 */

public class DirectSchedule extends Scheduler {
    public DirectSchedule(Context ctx) {
        super(ctx, "direct");
    }

    @Override
    public ArrayList<Schedulable> schedule() {
        return new ArrayList<>();
    }
}
