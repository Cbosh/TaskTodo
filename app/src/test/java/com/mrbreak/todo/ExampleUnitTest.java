package com.mrbreak.todo;

import android.app.Application;

import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.viewmodel.ToDoListViewModel;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void given_todo_check_if_its_over_due() {
        ToDoModel toDoModel = new ToDoModel();
        toDoModel.setDone(false);
        toDoModel.setStartTime("");
        toDoModel.setContent("Read");
        //toDoModel.setCreatedDate(DateUtil.getDueDate(new Date());
        ToDoListViewModel viewModel = new ToDoListViewModel(new Application());
        assertEquals(toDoModel.getContent(), "Read");
    }
}