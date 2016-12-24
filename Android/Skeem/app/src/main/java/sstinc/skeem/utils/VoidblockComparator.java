package sstinc.skeem.utils;

import android.util.Log;

import java.util.Comparator;

import sstinc.skeem.models.Voidblock;

/**
 * This class helps compare Voidblocks using {@link Comparator}.
 *
 * @see Comparator
 */
public class VoidblockComparator implements Comparator<Voidblock> {
    private VoidblockComparator.Order order;
    private boolean isAscending;
    // Ways to order by
    public enum Order {NAME, SCHEDULED_START, SCHEDULED_STOP}

    // Default constructor
    public VoidblockComparator() {
        // Set default order
        this.order = Order.SCHEDULED_START;
        this.isAscending = true;
    }

    /**
     * Sets the order to sort the voidblocks by based on the enum value given.
     * If it is ascending, the voidblocks are sorted by 0-9, A-z.
     *
     * @param order sort the voidblocks by this order
     * @param isAscending sort by ascending order
     */
    public void setSortBy(VoidblockComparator.Order order, boolean isAscending) {
        this.order = order;
        this.isAscending = isAscending;
    }

    /**
     * Implemented function from {@link Comparator} to compare two
     * voidblocks.
     * @param o1 voidblock 1
     * @param o2 voidblock 2
     * @return comparison value, -1, 0 or 1.
     */
    @Override
    public int compare(Voidblock o1, Voidblock o2) {
        int mul_val = this.isAscending ? 1 : -1;
        int c;
        switch (this.order) {
            case NAME:
                c = mul_val*o1.getName().compareTo(o2.getName());
                // Order by SCHEDULED_START next
                if (c == 0) {
                    c = mul_val*o1.getScheduledStart().toString().compareTo(
                            o2.getScheduledStart().toString());
                }
                // Order by SCHEDULED_STOP last
                if (c == 0) {
                    c = mul_val*o1.getScheduledStop().toString().compareTo(
                            o2.getScheduledStop().toString());
                }
                break;
            case SCHEDULED_START:
                c = mul_val*o1.getScheduledStart().toString().compareTo(
                        o2.getScheduledStart().toString());
                // Order by SCHEDULED_STOP next
                if (c == 0) {
                    c = mul_val*o1.getScheduledStop().toString().compareTo(
                            o2.getScheduledStop().toString());
                }
                // Order by NAME last
                if (c == 0) {
                    c = mul_val*o1.getName().compareTo(o2.getName());
                }
                break;
            case SCHEDULED_STOP:
                c = mul_val*o1.getScheduledStop().toString().compareTo(
                        o2.getScheduledStop().toString());
                // Order by SCHEDULED_START next
                if (c == 0) {
                    c = mul_val*o1.getScheduledStart().toString().compareTo(
                            o2.getScheduledStart().toString());
                }
                // Order by NAME last
                if (c == 0) {
                    c = mul_val*o1.getName().compareTo(o2.getName());
                }
                break;
            default:
                // Default to name
                c = mul_val*o1.getName().compareTo(o2.getName());
                if (c == 0) {
                    c = mul_val*o1.getScheduledStart().toString().compareTo(
                            o2.getScheduledStart().toString());
                }
                if (c == 0) {
                    c = mul_val*o1.getScheduledStop().toString().compareTo(
                            o2.getScheduledStop().toString());
                }
        }

        Log.w(this.getClass().getName(), "KEY ASDF: inside l " + c);
        return c;
    }
}
