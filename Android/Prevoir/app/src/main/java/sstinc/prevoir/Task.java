package sstinc.prevoir;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

class Task extends Schedulable implements Parcelable {
    boolean checked = false;

    Datetime scheduled_start;
    Datetime scheduled_end;

    String name = "";
    String subject = "";

    enum WeekDay {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY}
    ArrayList<WeekDay> weekDays;
    Deadline deadline;
    String description;
    Duration duration;
    Duration min_time_period;

    Task(Task task) {
        this.name = task.name;
        this.subject = task.subject;
        this.weekDays = task.weekDays;
        this.deadline = task.deadline;
        this.description = task.description;
        this.duration = task.duration;
        this.min_time_period = task.min_time_period;
        this.scheduled_start = task.scheduled_start;
        this.scheduled_end = task.scheduled_end;
    }

    Task(String name, String subject, ArrayList<WeekDay> weekdays,
                Deadline deadline, String description, Duration duration,
                Duration min_time_period) {
        this.name = name;
        this.subject = subject;
        this.weekDays = weekdays;
        this.deadline = deadline;
        this.description = description;
        this.duration = duration;
        this.min_time_period = min_time_period;
    }

    @Override
    public void setId(long newId) {
        this.id = newId;
        this.deadline.setId(newId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // Write to parcel
        out.writeString(name);
        out.writeString(subject);
        // Convert weekDays to string
        ArrayList<String> arrayList_string_weekDays = new ArrayList<>();
        for (WeekDay weekDay : weekDays) {
            arrayList_string_weekDays.add(weekDay.toString());
        }
        String[] string_weekDays = new String[arrayList_string_weekDays.size()];
        string_weekDays = arrayList_string_weekDays.toArray(string_weekDays);
        out.writeStringArray(string_weekDays);
        out.writeString(deadline.deadline.toString());
        out.writeString(description);
        out.writeString(duration.toString());
        out.writeString(min_time_period.toString());
        out.writeLong(this.getId());
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    private Task(Parcel in) {
        // Read from parcel
        this.name = in.readString();
        this.subject = in.readString();
        Log.w(this.getClass().getName(), "Subject: " + this.subject);
        this.weekDays = new ArrayList<>();
        String[] string_weekDays = in.createStringArray();
        for (String string_weekday : string_weekDays) {
            this.weekDays.add(WeekDay.valueOf(string_weekday));
        }
        this.deadline = new Deadline(new Datetime(in.readString()));
        this.description = in.readString();
        this.duration = new Duration(in.readString());
        this.min_time_period = new Duration(in.readString());
        setId(in.readLong());
    }
}
