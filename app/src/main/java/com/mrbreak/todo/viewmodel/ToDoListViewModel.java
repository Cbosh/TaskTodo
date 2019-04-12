package com.mrbreak.todo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.repository.ToDoRepository;
import com.mrbreak.todo.util.DateUtil;

import java.util.Date;
import java.util.List;

public class ToDoListViewModel extends AndroidViewModel {

    private ToDoRepository toDoRepository;
    private LiveData<List<ToDoModel>> toDoList;

    public ToDoListViewModel(Application application) {
        super(application);
        toDoRepository = new ToDoRepository(application);
        toDoList = toDoRepository.getToDoList();
    }

    public LiveData<List<ToDoModel>> getToDoList() {
        return toDoList;
    }

    public void update(ToDoModel toDo) {
        toDoRepository.update(toDo);
    }

    public String getEndTime(ToDoModel toDo) {
        if (toDo == null || TextUtils.isEmpty(toDo.getEndTime())) {
            return "";
        }

        return "End time " + DateUtil.getTime(toDo.getEndTime());
    }

    public String getDueDate(ToDoModel toDo) {
        Date date = DateUtil.getDueDate(toDo.getStartTime());
        String days = String.valueOf(DateUtil.getDaysDifference(new Date(), date));
        int daysInt = Integer.parseInt(days);

        if (days.contentEquals("0") && date.equals(new Date())) {
            if (DateUtil.getCurrentHour() > DateUtil.getHour(toDo.getStartTime())
                    && date.equals(new Date())) {
                return "Task overdue!";
            }
        } else if (days.contentEquals("0") && date.before(new Date())) {
            return "Task overdue!";
        } else if (days.contentEquals("0") && date.after(new Date())) {
            if (DateUtils.isToday(date.getTime()) && DateUtils.isToday(new Date().getTime())) {
                return "Due in " + DateUtil.getTime(toDo.getStartTime());
            }
            return "Task due tomorrow";
        }

        if (days.contentEquals("0") && date.equals(new Date()) &&
                DateUtil.getCurrentHour() == DateUtil.getHour(toDo.getStartTime()) &&
                DateUtil.getCurrentMinutes() >= DateUtil.getMinutes(toDo.getStartTime())) {
            days = "Task overdue!";
        } else if (daysInt < 0) {
            days = "Task overdue!";
        } else if (daysInt >= 1) {
            if (daysInt == 1) {
                days = days + " day left";
            } else {
                days = days + " days left";
            }

        } else {
            days = "Due in " + DateUtil.getTime(toDo.getStartTime());
        }

        return days;
    }

}
