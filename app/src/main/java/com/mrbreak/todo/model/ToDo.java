package com.mrbreak.todo.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ToDo extends RealmObject implements Parcelable, Comparable<ToDo> {
    @PrimaryKey
    private int id;
    private String category;
    private String content;
    private String dueDate;
    private String createdDate;
    private String completedDate;
    private int isComplete;
    private String startTime;
    private String endTime;
    private int priority;
    private boolean done;
    private String categories;
    private boolean locked;
    private int remindMeBefore;

    public ToDo() {
    }

    protected ToDo(Parcel in) {
        id = in.readInt();
        category = in.readString();
        content = in.readString();
        dueDate = in.readString();
        createdDate = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        priority = in.readInt();
        categories = in.readString();
        done = in.readInt() == 1;
        locked = in.readInt() == 1;
        completedDate = in.readString();
        remindMeBefore = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(category);
        dest.writeString(content);
        dest.writeString(dueDate);
        dest.writeString(createdDate);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeInt(priority);
        dest.writeString(categories);
        dest.writeInt(done ? 1 : 0);
        dest.writeInt(locked ? 1 : 0);
        dest.writeString(completedDate);
        dest.writeInt(remindMeBefore);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ToDo> CREATOR = new Creator<ToDo>() {
        @Override
        public ToDo createFromParcel(Parcel in) {
            return new ToDo(in);
        }

        @Override
        public ToDo[] newArray(int size) {
            return new ToDo[size];
        }
    };

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(int isComplete) {
        this.isComplete = isComplete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getRemindMeBefore() {
        return remindMeBefore;
    }

    public void setRemindMeBefore(int remindMeBefore) {
        this.remindMeBefore = remindMeBefore;
    }

    @Override
    public int compareTo(@NonNull ToDo toDo) {
        return getEndTime().compareTo(toDo.getEndTime());
    }
}
