package com.mrbreak.todo.util;

import com.mrbreak.todo.repository.model.ToDoModel;

import java.util.Comparator;
import java.util.Date;

public class ToDoModelComparator implements Comparator<ToDoModel> {

    @Override
    public int compare(ToDoModel leftModel, ToDoModel rightModel) {
        Date leftDate = DateUtil.getDueDate(leftModel.getDueDate());
        Date rightDate = DateUtil.getDueDate(rightModel.getDueDate());

        if (leftDate == null || rightDate == null) {
            return 0;
        }

        return leftDate.compareTo(rightDate);
    }
}
