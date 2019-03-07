package com.mrbreak.todo.repository.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = "todo_table")
public class ToDoModel implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "todo_guid")
    private String toDoGuid;

    @NonNull
    @ColumnInfo(name = "created_date")
    private String createdDate;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "due_date")
    private String dueDate;

    @ColumnInfo(name = "completed_date")
    private String completedDate;

    @ColumnInfo(name = "complete")
    private int complete;

    @ColumnInfo(name = "start_time")
    private String startTime;

    @ColumnInfo(name = "end_time")
    private String endTime;

    @ColumnInfo(name = "priority")
    private int priority;

    @ColumnInfo(name = "done")
    private boolean done;

    @ColumnInfo(name = "categories")
    private String categories;

    @ColumnInfo(name = "locked")
    private boolean locked;

    @ColumnInfo(name = "remind_me_before")
    private int remindMeBefore;

    public ToDoModel() {
    }

    protected ToDoModel(Parcel in) {
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
        toDoGuid = in.readString();
        remindMeBefore = in.readInt();
    }


    @NonNull
    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@NonNull String createdDate) {
        this.createdDate = createdDate;
    }

    @NonNull
    public String getToDoGuid() {
        if (toDoGuid == null) {
            return UUID.randomUUID().toString();
        }
        return toDoGuid;
    }

    public void setToDoGuid(@NonNull String toDoGuid) {
        this.toDoGuid = toDoGuid;
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

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(priority);
        dest.writeInt(done ? 1 : 0);
        dest.writeInt(locked ? 1 : 0);
        dest.writeInt(remindMeBefore);
        dest.writeInt(complete);
        dest.writeString(category);
        dest.writeString(content);
        dest.writeString(dueDate);
        dest.writeString(createdDate);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(toDoGuid);
        dest.writeString(completedDate);
        dest.writeString(categories);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ToDoModel> CREATOR = new Creator<ToDoModel>() {
        @Override
        public ToDoModel createFromParcel(Parcel in) {
            return new ToDoModel(in);
        }

        @Override
        public ToDoModel[] newArray(int size) {
            return new ToDoModel[size];
        }
    };
}
