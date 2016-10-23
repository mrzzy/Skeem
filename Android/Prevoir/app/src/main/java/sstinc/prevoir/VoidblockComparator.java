package sstinc.prevoir;

import java.util.Comparator;

class VoidblockComparator implements Comparator<Voidblock> {

    private VoidblockComparator.Order orderBy = Order.START_DATETIME;
    private boolean isAscending = true;

    enum Order {NAME, START_DATETIME, END_DATETIME};

    // Ascending is A-Z 0-9
    void setSortBy(VoidblockComparator.Order order, boolean isAscending) {
        this.orderBy = order;
        this.isAscending = isAscending;
    }

    @Override
    public int compare(Voidblock o1, Voidblock o2) {
        int mul_val = isAscending ? 1 : -1;
        int c;
        switch (this.orderBy) {
            case NAME:
                c = mul_val*o1.name.compareTo(o2.name);
                // Order by START_DATETIME next
                if (c == 0) {
                    c = mul_val*o1.from.toString().compareTo(o2.from.toString());
                }
                // Order by END_DATETIME last
                if (c == 0) {
                    c = mul_val*o1.to.toString().compareTo(o2.to.toString());
                }
            case START_DATETIME:
                c = mul_val*o1.from.toString().compareTo(o2.from.toString());
                // Order by END_DATETIME next
                if (c == 0) {
                    c = mul_val*o1.to.toString().compareTo(o2.to.toString());
                }
                // Order by NAME last
                if (c == 0) {
                    c = mul_val*o1.name.compareTo(o2.name);
                }
            case END_DATETIME:
                c = mul_val*o1.to.toString().compareTo(o2.to.toString());
                // Order by START_DATETIME next
                if (c == 0) {
                    c = mul_val*o1.from.toString().compareTo(o2.from.toString());
                }
                // Order by NAME last
                if (c == 0) {
                    c = mul_val*o1.name.compareTo(o2.name);
                }
            default:
                // Default to name
                c = mul_val*o1.name.compareTo(o2.name);
                if (c == 0) {
                    c = mul_val*o1.from.toString().compareTo(o2.from.toString());
                }
                if (c == 0) {
                    c = mul_val*o1.to.toString().compareTo(o2.to.toString());
                }
        }
        return c;
    }
}
