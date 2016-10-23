package sstinc.prevoir;

import java.util.Comparator;

class TimeblockComparator implements Comparator<Timeblock> {
    private TimeblockComparator.Order orderBy = Order.TOTAL_DURATION;
    private boolean isAscending = true;

    enum Order {TOTAL_DURATION, DURATION_USED, DURATION_LEFT, START_DATETIME, END_DATETIME};

    // Ascending is A-Z 0-9
    void setSortBy(TimeblockComparator.Order order, boolean isAscending) {
        this.orderBy = order;
        this.isAscending = isAscending;
    }

    @Override
    public int compare(Timeblock o1, Timeblock o2) {
        int mul_val = isAscending ? 1 : -1;
        int c;
        switch (this.orderBy) {
            case TOTAL_DURATION:
                c = mul_val*o1.duration.toFullString().compareTo(o2.duration.toFullString());
                // Sort by duration left
                if (c == 0) {
                    c = mul_val*o1.duration_left.toFullString().compareTo(o2.duration_left.toFullString());
                }
                // Sort by start datetime
                if (c == 0) {
                    c = mul_val*o1.from.toString().compareTo(o2.from.toString());
                }
            case DURATION_USED:
                c = mul_val*o1.duration_used.toFullString().compareTo(o2.duration_used.toFullString());
                // Sort by total duration
                if (c == 0) {
                    c = mul_val*o1.duration.toFullString().compareTo(o2.duration.toFullString());
                }
                // Sort by start datetime
                if (c == 0) {
                    c = mul_val*o1.from.toString().compareTo(o2.from.toString());
                }
            case DURATION_LEFT:
                c = mul_val*o1.duration_left.toFullString().compareTo(o2.duration_left.toFullString());
                // Sort by total duration
                if (c == 0) {
                    c = mul_val*o1.duration.toFullString().compareTo(o2.duration.toFullString());
                }
                // Sort by start datetime
                if (c == 0) {
                    c = mul_val*o1.from.toString().compareTo(o2.from.toString());
                }
            case START_DATETIME:
                c = mul_val*o1.from.toString().compareTo(o2.from.toString());
                // Sort by total duration
                if (c == 0) {
                    c = mul_val*o1.duration.toFullString().compareTo(o2.duration.toFullString());
                }
                // Sort by duration left
                if (c == 0) {
                    c = mul_val*o1.duration_left.toFullString().compareTo(o2.duration_left.toFullString());
                }
            case END_DATETIME:
                c = mul_val*o1.to.toString().compareTo(o2.to.toString());
                // Sort by total duration
                if (c == 0) {
                    c = mul_val*o1.duration.toFullString().compareTo(o2.duration.toFullString());
                }
                // Sort by duration left
                if (c == 0) {
                    c = mul_val*o1.duration_left.toFullString().compareTo(o2.duration_left.toFullString());
                }
            default:
                // Default to total duration
                c = mul_val*o1.duration.toFullString().compareTo(o2.duration.toFullString());
                // Sort by duration left
                if (c == 0) {
                    c = mul_val*o1.duration_left.toFullString().compareTo(o2.duration_left.toFullString());
                }
                // Sort by start datetime
                if (c == 0) {
                    c = mul_val*o1.from.toString().compareTo(o2.from.toString());
                }
        }
        return c;
    }
}
