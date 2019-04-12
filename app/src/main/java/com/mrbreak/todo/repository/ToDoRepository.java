package com.mrbreak.todo.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;

import com.mrbreak.todo.repository.model.ToDoModel;

import java.util.List;

public class ToDoRepository {

    private ToDoDao toDoDao;
    private LiveData<List<ToDoModel>> toDoList;
    private LiveData<PagedList<ToDoModel>> pagedListLiveData;

    private DataSource.Factory<Integer, ToDoModel> factory;
    private LivePagedListBuilder<Integer, ToDoModel> pagedListBuilder;
    private LiveData<List<ToDoModel>> allToDos;


    public ToDoRepository(Application application) {
        ToDoDataBase db = ToDoDataBase.getDatabase(application);
        toDoDao = db.toDoDao();
        toDoList = toDoDao.getToDoList(false);
        allToDos = toDoDao.getAllToDos();
    }

    public DataSource.Factory<Integer, ToDoModel> getFactory() {
        return factory;
    }

    public ToDoRepository(Application application, boolean filter) {
        ToDoDataBase db = ToDoDataBase.getDatabase(application);
        toDoDao = db.toDoDao();
        toDoList = toDoDao.getToDoList(filter);
        factory = toDoDao.getDonePagedList(filter);
        pagedListBuilder = new LivePagedListBuilder<>(factory, 50);
        pagedListLiveData = pagedListBuilder.build();
    }

    public LiveData<PagedList<ToDoModel>> getPagedListLiveData() {
        return pagedListLiveData;
    }

    public LiveData<List<ToDoModel>> getToDoList() {
        return toDoList;
    }

    public LiveData<ToDoModel> getToDoById(String toDoGuid) {
        return new LiveData<ToDoModel>() {
        };
    }

    public LiveData<List<ToDoModel>> getAllToDos() {
        return allToDos;
    }

    public LiveData<ToDoModel> getOverDueList() {
        return new LiveData<ToDoModel>() {
        };
    }

    public void insert(ToDoModel toDo) {
        new insertAsyncTask(toDoDao).execute(toDo);
    }

    public void update(ToDoModel toDo) {
        new updateAsyncTask(toDoDao).execute(toDo);
    }

    public void delete(ToDoModel toDo) {
        new deleteAsyncTask(toDoDao).execute(toDo);
    }

    private static class insertAsyncTask extends AsyncTask<ToDoModel, Void, Void> {

        private ToDoDao mAsyncTaskDao;

        insertAsyncTask(ToDoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ToDoModel... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<ToDoModel, Void, Void> {

        private ToDoDao mAsyncTaskDao;

        updateAsyncTask(ToDoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ToDoModel... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    public ToDoDao getToDoDao() {
        return toDoDao;
    }

    private static class deleteAsyncTask extends AsyncTask<ToDoModel, Void, Void> {

        private ToDoDao mAsyncTaskDao;

        deleteAsyncTask(ToDoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ToDoModel... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class getToDoByGuidAsyncTask extends AsyncTask<String, Void, Void> {

        private ToDoDao mAsyncTaskDao;

        getToDoByGuidAsyncTask(ToDoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            mAsyncTaskDao.getToDoByGuid(params[0]);
            return null;
        }
    }
}
