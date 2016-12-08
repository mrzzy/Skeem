package sstinc.skeem;

/**
 * @deprecated Use JodaTime {@link org.joda.time.Period} instead.
 * @see org.joda.time.Period
 */
class Duration {
    private int years;
    private int months;
    private int days;

    private int hours;
    private int minutes;

    Duration() {
        this.years = 0;
        this.months = 0;
        this.days = 0;

        this.hours = 0;
        this.minutes = 0;
    }

    Duration(long minutes) {
        this.years = (int) minutes/525600;
        minutes -= this.years*525600;
        this.months = (int) minutes/43801;
        minutes -= this.months*43801;
        this.days = (int) minutes/1440;
        minutes -= this.days*1440;
        this.hours = (int) minutes/60;
        minutes -= this.hours*60;
        this.minutes = (int) minutes;
    }

    Duration(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    Duration(int years, int months, int days, int hours, int minutes) {
        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }

    Duration(String duration_string) {
        String[] split = duration_string.split(":");
        this.hours = Integer.parseInt(split[0]);
        this.minutes = Integer.parseInt(split[1]);
    }

    long toMinutes() {
        return this.years * 525600 + this.months * 43801 + this.days * 1440 +
                this.hours*60 + this.minutes;
    }

    Duration add(Duration duration) {
        int years = this.years + duration.years;
        int months = this.months + duration.months;
        int days = this.days + duration.days;
        int hours = this.hours + duration.hours;
        int minutes = this.minutes + duration.minutes;
        if (months > 12) {
            years += months/12;
            months = months%12;
        }
        return new Duration(years, months, days, hours, minutes);
    }

    Duration subtract(Duration duration) {
        int years = this.years - duration.years;
        int months = this.months - duration.months;
        int days = this.days - duration.days;
        int hours = this.hours - duration.hours;
        int minutes = this.minutes - duration.minutes;
        return new Duration(years, months, days, hours, minutes);
    }

    Duration divide(int divisor) {
        long divided_value = toMinutes()/divisor;
        return new Duration(divided_value);
    }


    @Override
    public String toString() {
        // Only give hours and minutes
        return this.hours + ":" + this.minutes;
    }

    public String toFullString() {
        return this.years + "/" + this.months + "/" + this.days + " " +
                this.hours + ":" + this.minutes;
    }

    // Getters and setters
    int getYears() { return this.years; }
    int getMonths() { return this.months; }
    int getDays() { return this.days; }
    int getHours() { return this.hours; }
    int getMinutes() { return this.minutes; }

    void setYears(int years) { this.years = years; }
    void setMonths(int months) { this.months = months; }
    void setDays(int days) { this.days = days; }
    void setHours(int hours) { this.hours = hours; }
    void setMinutes(int minutes) { this.minutes = minutes; }
}
