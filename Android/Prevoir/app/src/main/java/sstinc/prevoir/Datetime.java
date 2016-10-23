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
        if (datetime_string.isEmpty()) {
            this.year = -1;
            this.month = -1;
            this.day = -1;

            this.hour = -1;
            this.minute = -1;
            return;
        }
        String[] datetime = datetime_string.split(" ");
        String[] date = datetime[0].split("/");

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

    Datetime add(Duration duration) {
        int year = this.year + duration.getYears();
        int month = this.month + duration.getMonths();
        int day = this.day + duration.getDays();

        int hour = this.hour + duration.getHours();
        int minute = this.minute + duration.getMinutes();
        Datetime added_datetime = new Datetime();
        added_datetime.setYear(year);
        added_datetime.setMonth(month);
        added_datetime.setDay(day);

        added_datetime.setHour(hour);
        added_datetime.setMinute(minute);
        return added_datetime;
    }

    Datetime subtract(Duration duration) {
        int year = this.year - duration.getYears();
        int month = this.month - duration.getMonths();
        int day = this.day - duration.getDays();

        int hour = this.hour - duration.getHours();
        int minute = this.minute - duration.getMinutes();
        Datetime subtracted_datetime = new Datetime();
        subtracted_datetime.setYear(year);
        subtracted_datetime.setMonth(month);
        subtracted_datetime.setDay(day);

        subtracted_datetime.setHour(hour);
        subtracted_datetime.setMinute(minute);
        return subtracted_datetime;
    }

    @Override
    public String toString() {
        return this.year + "/" + this.month + "/" + this.day + " " + this.hour + ":" + this.minute;
    }

    private String value_format(String value, int length) {
        if (value.length() < length) {
            String padding = "";
            for (int i=0; i<length-value.length(); i++) {
                padding += "0";
            }
            return padding + value;
        } else {
            return value;
        }
    }

    public String toFormattedString() {
        String formattedString = "";
        if (this.hour != -1 && this.minute != -1) {
            formattedString += value_format(Integer.toString(this.hour), 2);
            formattedString += ":";
            formattedString += value_format(Integer.toString(this.minute), 2);
        }

        if (this.year != -1 && this.month != -1 && this.day != -1) {
            if (!formattedString.isEmpty()) {
                formattedString += " ";
            }
            formattedString += value_format(Integer.toString(this.day), 2);
            formattedString += "/";
            formattedString += value_format(Integer.toString(this.month), 2);
            formattedString += "/";
            formattedString += this.year;
        }
        return formattedString;
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
