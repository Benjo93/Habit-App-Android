package com.example.habitapp.Utils;

import android.os.Parcelable;
import android.os.Parcel;

import java.util.ArrayList;

public class ToDoItem implements Parcelable {

    private boolean itemUpdated;

    private int category = 0;
    private int module = 0;

    private int dayOfYearDue;
    private int yearDue;
    private long dueDate;

    private ArrayList<Integer> daysDue = new ArrayList<>();

    // To Do Item Data.
    private boolean active;
    private boolean complete;
    private String name = "";
    private String details;
    private boolean repeat;
    private int repeatType = Habit.REPEAT_DAILY;
    private int daysLeft;
    private long timeDue;

    public ToDoItem() {
        itemUpdated = true;
    }

    private ToDoItem(Parcel in) {
        active = in.readByte() != 0;
        complete = in.readByte() != 0;
        name = in.readString();
        dayOfYearDue = in.readInt();
        yearDue = in.readInt();
        dueDate = in.readLong();
        details = in.readString();
        repeat = in.readByte() != 0;
        daysLeft = in.readInt();
    }

    public boolean hasBeenUpdated() { return itemUpdated; }

    public void updateItem() { itemUpdated = false; }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void toggleActive() {
        active = !active;
    }

    public boolean isCompleted() {
        return complete;
    }

    public void setCompleted(boolean complete) {
        this.complete = complete;
    }

    public void toggleCompleted() {
        complete = !complete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getDayOfYearDue() { return dayOfYearDue; }

    public void setDayOfYearDue(int dayOfYearDue) { this.dayOfYearDue = dayOfYearDue; }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public void setCurrentYear (int year) { this.yearDue = year; }

    public int getCurrentYear() { return yearDue; }

    public void setTimeDue(long millisecond) { }

    public long getTimeDue() { return timeDue; }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {

        this.repeat = repeat;
        if (repeat) setDaysDue(repeatType);
    }

    public void toggleRepeat() {
        repeat = !repeat;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
        setRepeat(true);
    }

    public int getRepeatType() { return repeatType; }

    public void setDaysLeft(int currentDayOfYear) {
        daysLeft = dayOfYearDue - currentDayOfYear;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public void setDaysDue(int repeatType) {

        switch (repeatType) {

            case Habit.REPEAT_DAILY:

                break;

            case Habit.REPEAT_WEEKLY:

                break;

            case Habit.REPEAT_MONTHLY:

                break;
        }

    }

    //
    //
    //
    //
    //
    //
    // Parcelable Methods

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(name);
        dest.writeString(details);
        dest.writeInt(dayOfYearDue);
        dest.writeInt(yearDue);
        dest.writeLong(dueDate);
        dest.writeByte((byte) (repeat ? 1 : 0));
        dest.writeByte((byte) (complete ? 1 : 0));
        dest.writeInt(daysLeft);
    }

    public static final Creator<ToDoItem> CREATOR = new Creator<ToDoItem>() {
        @Override
        public ToDoItem createFromParcel(Parcel in) {
            return new ToDoItem(in);
        }

        @Override
        public ToDoItem[] newArray(int size) {
            return new ToDoItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    //
    //
    //
    //
    //
    //
    //
}