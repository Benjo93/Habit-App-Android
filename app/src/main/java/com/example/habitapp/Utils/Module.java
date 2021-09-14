package com.example.habitapp.Utils;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Module implements Parcelable {

    public String dayInfo;
    public String name;
    public long dueDate;
    public List<ToDoItem> items = new ArrayList<>();

    public int currentProgress;

    private int dayOfYear;
    private int moduleID;

    public Module (String dayInfo, String name, int dayOfYear) {
        this.dayInfo = dayInfo;
        this.name = name;
        this.dayOfYear = dayOfYear;
    }

    Module(Parcel in) {
        name = in.readString();
        dayOfYear = in.readInt();
        moduleID = in.readInt();
        items = in.createTypedArrayList(ToDoItem.CREATOR);
    }

    public static final Creator<Module> CREATOR = new Creator<Module>() {
        @Override
        public Module createFromParcel(Parcel in) {
            return new Module(in);
        }

        @Override
        public Module[] newArray(int size) {
            return new Module[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(dayOfYear);
        dest.writeInt(moduleID);
        dest.writeTypedList(items);
    }

    public float getProgress() {

        float completed = 0f;

        for (ToDoItem item : items)

            if (item.isCompleted())

                completed++;

        return completed / items.size();
    }
}
