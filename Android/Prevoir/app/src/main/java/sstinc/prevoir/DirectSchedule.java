package sstinc.prevoir;

import android.content.Context;

import java.util.ArrayList;

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

class DirectSchedule extends Scheduler {
    DirectSchedule(Context ctx) {
        super(ctx, "direct");
    }

    @Override
    public ArrayList<Schedulable> schedule() {
        return new ArrayList<>();
    }
}
