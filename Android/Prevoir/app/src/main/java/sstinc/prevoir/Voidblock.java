package sstinc.prevoir;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

class Voidblock implements Parcelable {
    boolean checked = false;

    private long id;
    String name;
    Datetime from;
    Datetime to;

    Voidblock(String name, Datetime from, Datetime to) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // Write to parcel
        out.writeString(this.name);
        out.writeString(this.from.toString());
        out.writeString(this.to.toString());
        out.writeLong(this.id);
    }

    public static final Parcelable.Creator<Voidblock> CREATOR =
            new Parcelable.Creator<Voidblock>() {
        public Voidblock createFromParcel(Parcel in) {
            return new Voidblock(in);
        }

        public Voidblock[] newArray(int size) {
            return new Voidblock[size];
        }
    };

    private Voidblock(Parcel in) {
        // Read from parcel
        this.name = in.readString();
        this.from = new Datetime(in.readString());
        this.to = new Datetime(in.readString());
        setId(in.readLong());
    }
}
