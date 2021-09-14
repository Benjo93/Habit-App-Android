package com.example.habitapp.Utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Category implements Parcelable {

    public String name;
    public int category;
    public int categoryID = 0;
    public boolean active;
    public ArrayList<ToDoItem> items = new ArrayList<>();

    public Category (String name, int category, int categoryID) {
        this.name = name;
        this.category = category;
        this.categoryID = categoryID;
    }


    protected Category(Parcel in) {
        name = in.readString();
        category = in.readInt();
        categoryID = in.readInt();
        active = in.readByte() != 0;
        items = in.createTypedArrayList(ToDoItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(category);
        dest.writeInt(categoryID);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeTypedList(items);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getProgress () {

        float completed = 0;
        for (ToDoItem item : items)
            if (item.isCompleted()) completed++;

        return (int) ((completed / items.size()) * 100);
    }
}
