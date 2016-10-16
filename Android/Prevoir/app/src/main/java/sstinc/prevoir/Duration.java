package sstinc.prevoir;

class Duration {
    private int hours;
    private int minutes;

    Duration() {
        this.hours = -1;
        this.minutes = -1;
    }

    Duration(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    Duration(String duration_string) {
        String[] split = duration_string.split(":");
        this.hours = Integer.parseInt(split[0]);
        this.minutes = Integer.parseInt(split[1]);
    }

    int toMinutes() {
        return (this.hours*60 + this.minutes);
    }

    @Override
    public String toString() {
        return this.hours + ":" + this.minutes;
    }

    // Getters and setters
    int getHours() {
        return this.hours;
    }
    int getMinutes() {
        return this.minutes;
    }
    void setHours(int hours) {
        this.hours = hours;
    }
    void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
