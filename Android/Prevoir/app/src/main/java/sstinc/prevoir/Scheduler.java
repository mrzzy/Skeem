package sstinc.prevoir;

import android.content.Context;

import java.util.ArrayList;

/**
 * This class superclasses all schedule sorting algorithms and handles their
 * respective function calls. It also provides a layer of abstraction.
 */
abstract class Scheduler {
    String name;
    protected Context context;

    Scheduler(Context ctx, String name) {
        this.name = name;
        this.context = ctx;
    }

    /**
     * The name of the scheduler, for example, "direct" or "even".
     *
     * @return name of the scheduler method
     */
    String getName() {
        return name;
    }

    abstract ArrayList<Schedulable> schedule();
}
