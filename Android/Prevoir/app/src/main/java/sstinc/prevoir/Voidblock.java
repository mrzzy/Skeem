package sstinc.prevoir;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

class Voidblock extends Schedulable implements Parcelable {
    //    boolean checked = false;
    private String name;

    /**
     * Default constructor. Sets the name to an empty string.
     */
    Voidblock() {
        this.name = "";
    }

    // Getters and Setters

    /**
     * Gets the voidblock's name.
     * @return voidblock's name
     */
    String getName() {
        return this.name;
    }

    /**
     * {@link #getName()}
     * @param name voidblock's name
     */
    void setName(String name) {
        this.name = name;
    }


    // Empty describe contents function for parcel
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Function to write the data into a parcelable object.
     *
     * @param out parcel to write to
     * @param flags other flags
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        // Write name as string
        out.writeString(this.name);
        // Convert datetime values to string and write them
        out.writeString(this.scheduled_start.toString());
        out.writeString(this.scheduled_stop.toString());
        // Write the current id of the task
        out.writeLong(this.id);
    }

    // Creator constant for parcel
    public static final Parcelable.Creator<Voidblock> CREATOR =
            new Parcelable.Creator<Voidblock>() {
        public Voidblock createFromParcel(Parcel in) {
            return new Voidblock(in);
        }

        public Voidblock[] newArray(int size) {
            return new Voidblock[size];
        }
    };

    /**
     * Constructor to create instance from data from parcel.
     *
     * @param in parcel to read from
     */
    private Voidblock(Parcel in) {
        // Read name as string
        this.name = in.readString();
        // Create datetime strings to datetime objects
        this.scheduled_start = new Datetime(in.readString());
        this.scheduled_stop = new Datetime(in.readString());
        // Read the id of the task
        this.id = in.readLong();
    }
}
