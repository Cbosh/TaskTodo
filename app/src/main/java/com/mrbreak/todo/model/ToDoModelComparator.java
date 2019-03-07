package com.mrbreak.todo.model;

import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.util.Utils;

import java.util.Comparator;
import java.util.Date;

public class ToDoModelComparator implements Comparator<ToDoModel> {

    @Override
    public int compare(ToDoModel leftModel, ToDoModel rightModel) {
        Date leftDate = Utils.getDueDate(leftModel.getDueDate());
        Date rightDate = Utils.getDueDate(rightModel.getDueDate());

        if (leftDate == null || rightDate == null) {
            return 0;
        }

        return leftDate.compareTo(rightDate);
    }
}
