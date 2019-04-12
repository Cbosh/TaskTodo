package com.mrbreak.todo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.mrbreak.todo.repository.ToDoRepository;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.util.DateUtil;

import java.util.Date;
import java.util.List;

public class OverDueListViewModel extends AndroidViewModel {

    private ToDoRepository toDoRepository;

    private LiveData<List<ToDoModel>> toDoList;

    private LiveData<PagedList<ToDoModel>> pagedListLiveData;

    public OverDueListViewModel(Application application) {
        super(application);
        String date = DateUtil.convertDateToString(new Date());
        toDoRepository = new ToDoRepository(application);
        toDoList = toDoRepository.getToDoList();
        pagedListLiveData = toDoRepository.getPagedListLiveData();
    }

    public LiveData<PagedList<ToDoModel>> getPagedListLiveData() {
        return pagedListLiveData;
    }

    public LiveData<List<ToDoModel>> getToDoList() {
        return toDoList;
    }

    public String getCreatedDate(ToDoModel toDo) {
        Date date = DateUtil.convertStringToDate(toDo.getCreatedDate());
        String createdDate = String.valueOf(DateUtil.getDaysDifference(new Date(), date));

        if (createdDate.contentEquals("0")) {
            createdDate = "Created Today";
        } else {
            createdDate = "Created " + createdDate + " days ago";
        }

        return createdDate;
    }

    public String getDueDate(ToDoModel toDo) {
        Date date = DateUtil.convertStringToDate(toDo.getDueDate());
        String days = String.valueOf(DateUtil.getDaysDifference(new Date(), date));
        int daysInt = Integer.parseInt(days);

        if (toDo.isDone()) {
            days = "Completed on " + toDo.getCompletedDate();
        } else {
            if (days.contentEquals("0") && DateUtil.checkTimings(toDo.getStartTime(),
                    toDo.getEndTime())) {
                days = "Due in " + toDo.getEndTime();
            } else if (daysInt < 0) {
                days = "Times up!!";
            } else {
                days = "Due in " + days + " Days";
            }
        }

        return days;
    }

}
