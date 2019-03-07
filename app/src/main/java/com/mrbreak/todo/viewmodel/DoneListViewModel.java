package com.mrbreak.todo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.mrbreak.todo.repository.ToDoRepository;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.util.Utils;

import java.util.List;

public class DoneListViewModel extends AndroidViewModel {

    private ToDoRepository toDoRepository;

    private LiveData<List<ToDoModel>> toDoList;

    private LiveData<PagedList<ToDoModel>> pagedListLiveData;

    public DoneListViewModel(Application application) {
        super(application);
        toDoRepository = new ToDoRepository(application, true);
        toDoList = toDoRepository.getToDoList();
        pagedListLiveData = toDoRepository.getPagedListLiveData();
    }

    public LiveData<PagedList<ToDoModel>> getPagedListLiveData() {
        return pagedListLiveData;
    }

    public LiveData<List<ToDoModel>> getToDoList() {
        return toDoList;
    }

    public String getDueDate(ToDoModel toDo) {
        return "Completed on " + Utils.getCompletedDateTimeDisplay(toDo.getCompletedDate());
    }

    public String getCreatedDate(ToDoModel toDo) {
        return "Created on " + Utils.getOutputDateFormt(toDo.getCreatedDate());
    }

}
