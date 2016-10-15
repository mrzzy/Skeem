package sstinc.prevoir;

import java.util.Calendar;

public class Voidblock {
    long id;
    String name;
    Calendar from;
    Calendar to;

    public Voidblock(String name, Calendar from, Calendar to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

    public void setId(long newId) {
        this.id = newId;
    }

    public long getId() {
        return this.id;
    }
}
