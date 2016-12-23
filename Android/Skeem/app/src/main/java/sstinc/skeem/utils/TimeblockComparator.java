package sstinc.skeem.utils;

import org.joda.time.format.PeriodFormat;

import java.util.Comparator;

import sstinc.skeem.models.Timeblock;

/**
 * This class helps compare Timeblocks using {@link Comparator}.
 *
 * @see Comparator
 */
public class TimeblockComparator implements Comparator<Timeblock> {
    // Set default order
    private TimeblockComparator.Order order = Order.TOTAL_PERIOD;
    private boolean isAscending = true;
    // Ways to order by
    public enum Order {TOTAL_PERIOD, PERIOD_USED, PERIOD_LEFT, SCHEDULED_START, SCHEDULED_STOP}

    /**
     * Sets the order to sort the voidblocks by based on the enum value given.
     * If it is ascending, the voidblocks are sorted by 0-9, A-z.
     *
     * @param order sort the voidblocks by this order
     * @param isAscending sort by ascending order
     */
    public void setSortBy(TimeblockComparator.Order order, boolean isAscending) {
        this.order = order;
        this.isAscending = isAscending;
    }

    /**
     * Implemented function from {@link Comparator} to compare two voidblocks.
     * @param o1 voidblock 1
     * @param o2 voidblock 2
     * @return comparison value, -1, 0 or 1.
     */
    @Override
    public int compare(Timeblock o1, Timeblock o2) {
        int mul_val = isAscending ? 1 : -1;
        int c;
        switch (this.order) {
            case TOTAL_PERIOD:
                c = mul_val* PeriodFormat.getDefault().print(o1.getScheduledPeriod()).compareTo(
                        PeriodFormat.getDefault().print(o2.getScheduledPeriod()));
                // Sort by period left
                if (c == 0) {
                    c = mul_val*PeriodFormat.getDefault().print(o1.getPeriodLeft()).compareTo(
                            PeriodFormat.getDefault().print(o2.getPeriodLeft()));
                }
                // Sort by scheduled start datetime
                if (c == 0) {
                    c = mul_val*o1.getScheduledStart().toString().compareTo(
                            o2.getScheduledStart().toString());
                }
            case PERIOD_USED:
                c = mul_val*PeriodFormat.getDefault().print(o1.getPeriodUsed()).compareTo(
                        PeriodFormat.getDefault().print(o2.getPeriodUsed()));
                // Sort by total period
                if (c == 0) {
                    c = mul_val*PeriodFormat.getDefault().print(o1.getScheduledPeriod()).compareTo(
                            PeriodFormat.getDefault().print(o2.getScheduledPeriod()));
                }
                // Sort by scheduled start datetime
                if (c == 0) {
                    c = mul_val*o1.getScheduledStart().toString().compareTo(
                            o2.getScheduledStart().toString());
                }
            case PERIOD_LEFT:
                c = mul_val*PeriodFormat.getDefault().print(o1.getPeriodLeft()).compareTo(
                        PeriodFormat.getDefault().print(o2.getPeriodLeft()));
                // Sort by total period
                if (c == 0) {
                    c = mul_val*PeriodFormat.getDefault().print(o1.getScheduledPeriod()).compareTo(
                            PeriodFormat.getDefault().print(o2.getScheduledPeriod()));
                }
                // Sort by scheduled start datetime
                if (c == 0) {
                    c = mul_val*o1.getScheduledStart().toString().compareTo(
                            o2.getScheduledStart().toString());
                }
            case SCHEDULED_START:
                c = mul_val*o1.getScheduledStart().toString().compareTo(
                        o2.getScheduledStart().toString());
                // Sort by total period
                if (c == 0) {
                    c = mul_val*PeriodFormat.getDefault().print(o1.getScheduledPeriod()).compareTo(
                            PeriodFormat.getDefault().print(o2.getScheduledPeriod()));
                }
                // Sort by period left
                if (c == 0) {
                    c = mul_val*PeriodFormat.getDefault().print(o1.getPeriodLeft()).compareTo(
                            PeriodFormat.getDefault().print(o2.getPeriodLeft()));
                }
            case SCHEDULED_STOP:
                c = mul_val*o1.getScheduledStop().toString().compareTo(
                        o2.getScheduledStop().toString());
                // Sort by total period
                if (c == 0) {
                    c = mul_val*PeriodFormat.getDefault().print(o1.getScheduledPeriod()).compareTo(
                            PeriodFormat.getDefault().print(o2.getScheduledPeriod()));
                }
                // Sort by period left
                if (c == 0) {
                    c = mul_val*PeriodFormat.getDefault().print(o1.getPeriodLeft()).compareTo(
                            PeriodFormat.getDefault().print(o2.getPeriodLeft()));
                }
            default:
                // Default to total period
                c = mul_val*PeriodFormat.getDefault().print(o1.getScheduledPeriod()).compareTo(
                        PeriodFormat.getDefault().print(o2.getScheduledPeriod()));
                // Sort by period left
                if (c == 0) {
                    c = mul_val*PeriodFormat.getDefault().print(o1.getPeriodLeft()).compareTo(
                            PeriodFormat.getDefault().print(o2.getPeriodLeft()));
                }
                // Sort by scheduled start datetime
                if (c == 0) {
                    c = mul_val*o1.getScheduledStart().toString().compareTo(
                            o2.getScheduledStart().toString());
                }
        }
        return c;
    }
}
