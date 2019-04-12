package com.mrbreak.todo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.mrbreak.todo.model.DashBoardFilterModel;
import com.mrbreak.todo.repository.ToDoDao;
import com.mrbreak.todo.repository.ToDoRepository;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DashBoardListViewModel extends AndroidViewModel {

    private ToDoRepository toDoRepository;

    private LiveData<List<ToDoModel>> toDoList;

    public DashBoardListViewModel(Application application) {
        super(application);
        toDoRepository = new ToDoRepository(application);
        toDoList = toDoRepository.getAllToDos();
    }

    public LiveData<List<ToDoModel>> getToDoList() {
        return toDoList;
    }

    public List<ToDoModel> getLiveDataList(DashBoardFilterModel filtering) {
        List<ToDoModel> listLiveData = null;
        try {
            listLiveData = new
                    getUserDataAsyncTask(toDoRepository.getToDoDao()).execute(filtering).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return listLiveData;
    }

    private static class getUserDataAsyncTask extends AsyncTask<DashBoardFilterModel,
            Void, List<ToDoModel>> {

        private ToDoDao toDoDao;

        public getUserDataAsyncTask(ToDoDao toDoDao) {
            this.toDoDao = toDoDao;
        }

        @Override
        protected List<ToDoModel> doInBackground(DashBoardFilterModel... filterings) {
            List<ToDoModel> list = new ArrayList<>();
            List<ToDoModel> listLiveData = toDoDao.getFilteredToDoList(filterings[0].isDone());

            if (listLiveData != null && listLiveData.size() > 0) {
                for (ToDoModel toDoModel : listLiveData) {
                    Date startDateFilter = DateUtil.getDueDate(filterings[0].getStartDate());
                    Date endDateFilter = DateUtil.getDueDate(filterings[0].getStartDate());
                    Date startDate = DateUtil.getDueDate(toDoModel.getStartTime());
                    if (toDoModel.getStartTime() != null && startDateFilter != null && endDateFilter != null &&
                            (startDateFilter.before(startDate) || startDateFilter.equals(startDate) &&
                                    endDateFilter.before(startDate) || endDateFilter.equals(startDate))) {
                        list.add(toDoModel);
                    }
                }
            }

            return list;
        }

        @Override
        protected void onPostExecute(List<ToDoModel> listLiveData) {
            super.onPostExecute(listLiveData);
        }
    }
}
