package sstinc.prevoir;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

    private Order orderBy = Order.NAME;
    private boolean isAscending;

    enum Order {NAME, SUBJECT, TASKTYPE, DEADLINE, DURATION, MIN_TIME_PERIOD};

    // Ascending is A-Z 0-9
    public void setSortBy(Order order, boolean isAscending) {
        this.orderBy = order;
        this.isAscending = isAscending;
    }

    @Override
    public int compare(Task o1, Task o2) {
        int mul_val = isAscending ? 1 : -1;
        switch (this.orderBy) {
            case NAME:
                return mul_val*o1.name.compareTo(o2.name);
            case SUBJECT:
                return mul_val*o1.subject.compareTo(o2.subject);
            case TASKTYPE:
                return mul_val*o1.type.compareTo(o2.type);
            case DEADLINE:
                return mul_val*o1.deadline.toString().compareTo(o2.deadline.toString());
            case DURATION:
                return mul_val*o1.duration.toString().compareTo(o2.duration.toString());
            case MIN_TIME_PERIOD:
                return mul_val*o1.min_time_period.toString().compareTo(
                        o2.min_time_period.toString());
            default:
                // Default to name
                return mul_val*o1.name.compareTo(o2.name);
        }
    }
}
