package com.mrbreak.todo.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mrbreak.todo.repository.model.ToDoModel;

@Database(entities = {ToDoModel.class}, version = 1, exportSchema = false)
public abstract class ToDoDataBase extends RoomDatabase {
    public abstract ToDoDao toDoDao();

    private static volatile ToDoDataBase INSTANCE;

    static ToDoDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ToDoDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ToDoDataBase.class, "todo_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //TODO: upgrade DB for calendar eventID and also update the model change DB version to 2

}

