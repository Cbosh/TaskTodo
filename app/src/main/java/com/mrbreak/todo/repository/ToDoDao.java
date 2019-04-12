package com.mrbreak.todo.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mrbreak.todo.repository.model.ToDoModel;

import java.util.List;

@Dao
public interface ToDoDao {
    @Query("SELECT * from todo_table  WHERE done =:done")
    LiveData<List<ToDoModel>> getToDoList(boolean done);

    @Query("SELECT * from todo_table  WHERE done =:done")
    List<ToDoModel> getFilteredToDoList(boolean done);

    @Query("SELECT * from todo_table")
    LiveData<List<ToDoModel>> getAllToDos();

    @Query("SELECT * from todo_table WHERE done =:done  ORDER BY due_date ASC")
    DataSource.Factory<Integer, ToDoModel> getPagedList(boolean done);

    @Query("SELECT * from todo_table WHERE done =:done  ORDER BY completed_date DESC")
    DataSource.Factory<Integer, ToDoModel> getDonePagedList(boolean done);

    @Query("SELECT * from todo_table WHERE done =:isDone  ORDER BY due_date ASC")
    DataSource.Factory<Integer, ToDoModel> getOverDuePagedList(boolean isDone);

    @Query("SELECT * from todo_table WHERE todo_guid =:toDoGuid ORDER BY created_date ASC")
    LiveData<ToDoModel> getToDoByGuid(String toDoGuid);

    @Insert
    void insert(ToDoModel toDo);

    @Delete
    void delete(ToDoModel toDo);

    @Update
    void update(ToDoModel toDo);

    @Query("DELETE FROM todo_table")
    void deleteAll();
}
