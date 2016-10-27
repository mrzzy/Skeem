package sstinc.prevoir;

import java.util.ArrayList;

//TODO: Change Task WeekDays to use this instead. Ensure code uses methods supplied by this class

/**
 * This class handles the task's repeated week days.
 *
 * @see Task
 */
public class WeekDays {
    enum WeekDay {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY}
    private ArrayList<WeekDay> weekDays = new ArrayList<>();

    // Empty constructor
    WeekDays() {}

    void add(WeekDay weekDay) {
        this.weekDays.add(weekDay);
    }

    /**
     * Returns a string array of all WeekDays stored in the class.
     * @return string array value of WeekDays
     */
    String[] toStringArray() {
        // New array of strings
        ArrayList<String> weekDays_string = new ArrayList<>();
        // Add the string values of the weekdays into the string array
        for (WeekDay weekDay : weekDays) {
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
        for (WeekDay weekDay : weekDays) {
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
