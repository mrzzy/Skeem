package sstinc.prevoir;

import java.util.Calendar;

class Datetime {
    private boolean hasDate;
    private int year;
    private int month;
    private int day;
    // Hour is in 24 hour
    private boolean hasTime;
    private int hour;
    private int minute;

    private Calendar calendar;

    Datetime() {
        this.hasDate = false;
        this.year = 0;
        this.month = 0;
        this.day = 0;

        this.hasTime = false;
        this.hour = 0;
        this.minute = 0;

        this.calendar = Calendar.getInstance();
        this.calendar.set(this.year, this.month, this.day,
                this.hour, this.minute);
    }

    Datetime(Datetime datetime) {
        this.hasDate = datetime.hasDate();
        this.year = datetime.getYear();
        this.month = datetime.getMonth();
        this.day = datetime.getDay();

        this.hasTime = datetime.hasTime();
        this.hour = datetime.getHour();
        this.minute = datetime.getMinute();

        this.calendar = Calendar.getInstance();
        this.calendar.set(this.year, this.month, this.day,
                this.hour, this.minute);
    }

    Datetime(int year, int month, int day) {
        this.hasDate = true;
        this.year = year;
        this.month = month;
        this.day = day;

        this.hour = 0;
        this.minute = 0;

        this.calendar = Calendar.getInstance();
        this.calendar.set(this.year, this.month, this.day,
                this.hour, this.minute);
    }

    Datetime(int hour, int minute) {
        this.hasDate = false;
        this.year = 0;
        this.month = 0;
        this.day = 0;

        this.hasTime = true;
        this.hour = hour;
        this.minute = minute;

        this.calendar = Calendar.getInstance();
        this.calendar.set(this.year, this.month, this.day,
                this.hour, this.minute);
    }

    Datetime(int year, int month, int day, int hour, int minute) {
        this.hasDate = true;
        this.year = year;
        this.month = month;
        this.day = day;

        this.hasTime = true;
        this.hour = hour;
        this.minute = minute;

        this.calendar = Calendar.getInstance();
        this.calendar.set(this.year, this.month, this.day,
                this.hour, this.minute);
    }

    Datetime(String datetime_string) {
        if (datetime_string.isEmpty()) {
            this.hasDate = false;
            this.year = 0;
            this.month = 0;
            this.day = 0;

            this.hasTime = false;
            this.hour = 0;
            this.minute = 0;

            this.calendar = Calendar.getInstance();
            this.calendar.set(this.year, this.month, this.day,
                    this.hour, this.minute);
            return;
        }
        String[] datetime = datetime_string.split(" ");
        String[] date = datetime[0].split("/");
        String[] time = datetime[1].split(":");

        this.year = Integer.parseInt(date[0]);
        this.month = Integer.parseInt(date[1]);
        this.day = Integer.parseInt(date[2]);

        this.hour = Integer.parseInt(time[0]);
        this.minute = Integer.parseInt(time[1]);

        this.calendar = Calendar.getInstance();
        this.calendar.set(this.year, this.month, this.day,
                this.hour, this.minute);
    }

    boolean hasTime() { return hasTime; }

    boolean hasDate() { return hasDate; }

    Datetime add(Duration duration) {
        this.calendar.toString();
        this.calendar.add(Calendar.YEAR, duration.getYears());
        this.calendar.add(Calendar.MONTH, duration.getMonths());
        this.calendar.add(Calendar.DAY_OF_MONTH, duration.getDays());

        this.calendar.add(Calendar.HOUR_OF_DAY, duration.getHours());
        this.calendar.add(Calendar.MINUTE, duration.getMinutes());

        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);

        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);

        return new Datetime(this.year, this.month, this.day,
                this.hour, this.minute);
    }

    Datetime subtract(Duration duration) {
        this.calendar.add(Calendar.YEAR, -duration.getYears());
        this.calendar.add(Calendar.MONTH, -duration.getMonths());
        this.calendar.add(Calendar.DAY_OF_MONTH, -duration.getDays());

        this.calendar.add(Calendar.HOUR_OF_DAY, -duration.getHours());
        this.calendar.add(Calendar.MINUTE, -duration.getMinutes());

        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);

        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);

        return new Datetime(this.year, this.month, this.day,
                this.hour, this.minute);
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
        hasDate = true;
        this.year = year;
    }
    void setMonth(int month) {
        hasDate = true;
        this.month = month;
    }
    void setDay(int day) {
        hasDate = true;
        this.day = day;
    }
    void setHour(int hour) {
        this.hasTime = true;
        this.hour = hour;
    }
    void setMinute(int minute) {
        this.hasTime = true;
        this.minute = minute;
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
