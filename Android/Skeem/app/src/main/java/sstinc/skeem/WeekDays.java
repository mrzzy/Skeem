package sstinc.skeem;

import java.util.ArrayList;

/**
 * This class handles the task's repeated week days.
 *
 * @see Task
 */
class WeekDays {
    enum WeekDay {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY}
    private ArrayList<WeekDay> weekDays_list = new ArrayList<>();

    // Empty constructor
    WeekDays() {}

    // Copy constructor
    WeekDays(WeekDays weekDays) {
        this.weekDays_list = new ArrayList<>();
        this.weekDays_list.addAll(weekDays.getWeekDays_list());
    }

    /**
     * String array constructor. Constructs the object with a string array.
     * String array can be obtained with {@link #toStringArray()}.
     *
     * @param weekDays string array to copy
     */
    WeekDays(String[] weekDays) {
        this.weekDays_list = new ArrayList<>();
        for (String weekDay : weekDays) {
            this.weekDays_list.add(WeekDay.valueOf(weekDay));
        }
    }

    // Getters and Setters
    // Get weekDays_list
    ArrayList<WeekDay> getWeekDays_list() {
        return this.weekDays_list;
    }

    /**
     * Adds a new weekday to the list.
     * @param weekDay new weekday to add
     */
    void add(WeekDay weekDay) {
        this.weekDays_list.add(weekDay);
    }

    /**
     * Returns a string array of all WeekDays stored in the class.
     * @return string array value of WeekDays
     */
    String[] toStringArray() {
        // New array of strings
        ArrayList<String> weekDays_string = new ArrayList<>();
        // Add the string values of the weekdays into the string array
        for (WeekDay weekDay : this.weekDays_list) {
            weekDays_string.add(weekDay.toString());
        }

        return weekDays_string.toArray(new String[0]);
    }

    /**
     * Returns a string containing the first three letters of each WeekDay in
     * WeekDays delimited by a comma. If there are no WeekDays, an empty
     * string will be returned.
     *
     * @return comma delimited string of WeekDays
     */
    @Override
    public String toString() {
        // New string to store the new WeekDay strings
        String return_string = "";
        // Add first three letters of each WeekDay to return_string
        for (WeekDay weekDay : this.weekDays_list) {
            return_string += weekDay.toString().substring(0, 3);
            return_string += ", ";
        }

        // Remove trailing comma delimiter
        if (!return_string.equals("")) {
            return_string = return_string.substring(0, return_string.length()-2);
        }

        return return_string;
    }
}
