package sstinc.prevoir;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

    private Order orderBy = Order.NAME;
    private boolean isAscending = false;

    enum Order {NAME, SUBJECT, TASKTYPE, DEADLINE, DURATION, MIN_TIME_PERIOD};

    // Ascending is A-Z 0-9
    public void setSortBy(Order order, boolean isAscending) {
        this.orderBy = order;
        this.isAscending = isAscending;
    }

    @Override
    public int compare(Task o1, Task o2) {
        int mul_val = isAscending ? 1 : -1;
        int c;
        switch (this.orderBy) {
            case NAME:
                c = mul_val*o1.name.compareTo(o2.name);
                if (c == 0) {
                    c = mul_val*o1.subject.compareTo(o2.subject);
                }
            case SUBJECT:
                c = mul_val*o1.subject.compareTo(o2.subject);
                if (c == 0) {
                    c = mul_val*o1.name.compareTo(o2.name);
                }
            case DEADLINE:
                c = mul_val*o1.deadline.toString().compareTo(o2.deadline.toString());
                if (c == 0) {
                    c = mul_val*o1.name.compareTo(o2.name);
                }
                if (c == 0) {
                    c = mul_val*o1.subject.compareTo(o2.subject);
                }
            case DURATION:
                c = mul_val*o1.duration.toString().compareTo(o2.duration.toString());
                if (c == 0) {
                    c = mul_val*o1.name.compareTo(o2.name);
                }
                if (c == 0) {
                    c = mul_val*o1.subject.compareTo(o2.subject);
                }
            case MIN_TIME_PERIOD:
                c = mul_val*o1.min_time_period.toString().compareTo(
                        o2.min_time_period.toString());
                if (c == 0) {
                    c = mul_val*o1.name.compareTo(o2.name);
                }
                if (c == 0) {
                    c = mul_val*o1.subject.compareTo(o2.subject);
                }
            default:
                // Default to name
                c = mul_val*o1.name.compareTo(o2.name);
                if (c == 0) {
                    c = mul_val*o1.subject.compareTo(o2.subject);
                }
        }
        return c;
    }
}
