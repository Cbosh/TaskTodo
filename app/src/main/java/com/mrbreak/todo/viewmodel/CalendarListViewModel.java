package com.mrbreak.todo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.mrbreak.todo.repository.ToDoRepository;
import com.mrbreak.todo.repository.model.ToDoModel;
import java.util.List;

public class CalendarListViewModel extends AndroidViewModel {

    private ToDoRepository toDoRepository;

    private LiveData<List<ToDoModel>> toDoList;

    public CalendarListViewModel(Application application) {
        super(application);
        toDoRepository = new ToDoRepository(application);
        toDoList = toDoRepository.getToDoList();
    }

    public LiveData<List<ToDoModel>> getToDoList() {
        return toDoList;
    }

}
