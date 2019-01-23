package com.mrbreak.todo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.birbit.android.jobqueue.JobManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrbreak.todo.R;
import com.mrbreak.todo.activities.DetailActivity;
import com.mrbreak.todo.adapter.ToDoAdapter;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.events.GetToDosFinished;
import com.mrbreak.todo.events.GetToDosStarted;
import com.mrbreak.todo.jobmanager.ToDoJobManager;
import com.mrbreak.todo.jobs.GetToDoJob;
import com.mrbreak.todo.model.ToDo;
import com.mrbreak.todo.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ToDoOverDueFragment extends Fragment {

    private RecyclerView overDueRecyclerView;
    private List<ToDo> toDos;
    private JobManager jobManager;

    public ToDoOverDueFragment() {
    }

    public static ToDoOverDueFragment newInstance(String param1, String param2) {
        ToDoOverDueFragment fragment = new ToDoOverDueFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String stringList = getArguments().getString("List");
            toDos = new Gson().fromJson(stringList, new TypeToken<ArrayList<ToDo>>() {
            }.getType());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_overdue, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        overDueRecyclerView = view.findViewById(R.id.overDueRecyclerView);
        overDueRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        overDueRecyclerView.setLayoutManager(linearLayoutManager);

        ToDoJobManager toDoJobManager = new ToDoJobManager();
        jobManager = toDoJobManager.getJobManager(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new GetToDosStarted(EventBus.getDefault()));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetToDosFinished event) {
        if (event != null && event.getToDoList() != null && event.getToDoList().size() > 0) {
            initializeData(Utils.getOverDueList(event.getToDoList()));
        }
    }

    @Subscribe
    public void onEvent(GetToDosStarted event) {
        jobManager.addJobInBackground(new GetToDoJob(EventBus.getDefault()));
    }

    private void initializeData(final List<ToDo> toDos) {
        ToDoAdapter adapter = new ToDoAdapter(toDos, new ToDoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.ISEDIT, true);
                bundle.putParcelable(Constants.TODO, toDos.get(position));

                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(Constants.ISEDIT, bundle);
                startActivity(intent);
            }
        }, jobManager, EventBus.getDefault());
        overDueRecyclerView.setAdapter(adapter);
    }
}
