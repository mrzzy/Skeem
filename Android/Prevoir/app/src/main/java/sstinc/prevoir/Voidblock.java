package sstinc.prevoir;

class Voidblock {
    private long id;
    String name;
    Datetime from;
    Datetime to;

    public Voidblock(String name, Datetime from, Datetime to) {
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
