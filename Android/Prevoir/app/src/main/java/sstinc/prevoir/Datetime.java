package sstinc.prevoir;

class Datetime {
    private int year;
    private int month;
    private int day;
    // Hour is in 24 hour
    private int hour;
    private int minute;

    Datetime() {
        this.year = -1;
        this.month = -1;
        this.day = -1;

        this.hour = -1;
        this.minute = -1;
    }

    Datetime(String datetime_string) {
        String[] datetime = datetime_string.split(" ");
        String[] date = datetime[0].split("-");

        this.year = Integer.parseInt(date[0]);
        this.month = Integer.parseInt(date[1]);
        this.day = Integer.parseInt(date[2]);

        this.hour = -1;
        this.minute = -1;
        if (datetime.length > 1) {
            String[] time = datetime[1].split(":");
            this.hour = Integer.parseInt(time[0]);
            this.minute = Integer.parseInt(time[1]);
        }
    }

    boolean hasTime() {
        return this.hour != -1;
    }

    @Override
    public String toString() {
        return this.year + "-" + this.month + "-" + this.day + " " + this.hour + ":" + this.minute;
    }

    // Getters and setters
    void setYear(int year) {
        this.year = year;
    }
    void setMonth(int month) {
        this.month = month;
    }
    void setDay(int day) {
        this.day = day;
    }
    void setHour(int hour) {
        this.hour = hour;
        if (this.minute == -1) {
            this.minute = 0;
        }
    }
    void setMinute(int minute) {
        this.minute = minute;
        if (this.hour == -1) {
            this.hour = 0;
        }
    }

    int getYear() {
        return this.year;
    }
    int getMonth() {
        return this.month;
    }
    int getDay() {
        return this.day;
    }
    int getHour() {
        return this.hour;
    }
    int getMinute() {
        return this.minute;
    }
}
