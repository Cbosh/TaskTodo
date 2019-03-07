package com.mrbreak.todo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mrbreak.todo.repository.ToDoRepository;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.util.Utils;

public class ToDoDetailViewModel extends AndroidViewModel {
    private ToDoRepository toDoRepository;
    private LiveData<ToDoModel> toDo;
    private ObservableField<ToDoModel> toDoModelObservableField = new ObservableField<>();

    public ToDoDetailViewModel(Application application, String toDoGuid) {
        super(application);
        toDoRepository = new ToDoRepository(application);
        toDo = toDoRepository.getToDoById(toDoGuid);
    }

    public LiveData<ToDoModel> getToDo() {
        return toDo;
    }

    public void setToDo(LiveData<ToDoModel> toDo) {
        this.toDo = toDo;
    }

    public void insert(ToDoModel toDo) {
        toDoRepository.insert(toDo);
    }

    public void update(ToDoModel toDo) {
        toDoRepository.update(toDo);
    }

    public void delete(ToDoModel toDo) {
        toDoRepository.delete(toDo);
    }

    public void setToDoModelObservableField(ToDoModel toDoModel) {
        this.toDoModelObservableField.set(toDoModel);
    }

    public String getStartTime(ToDoModel model) {
        if (model == null || TextUtils.isEmpty(model.getStartTime())) {
            return "";
        }

        return Utils.getTime(model.getStartTime());
    }

    public String getEndTime(ToDoModel model) {
        if (model == null || TextUtils.isEmpty(model.getEndTime())) {
            return "";
        }

        return Utils.getTime(model.getEndTime());
    }

    public String getDueDate(ToDoModel model) {
        if (model == null || TextUtils.isEmpty(model.getDueDate())) {
            return "";
        }

        return Utils.getDisplayDueDate(model.getDueDate());
    }

    /**
     * A creator is used to inject the guid into the ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application application;

        private final String toDoGuid;

        public Factory(@NonNull Application application, String toDoGuid) {
            this.application = application;
            this.toDoGuid = toDoGuid;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new ToDoDetailViewModel(application, toDoGuid);
        }
    }

}
