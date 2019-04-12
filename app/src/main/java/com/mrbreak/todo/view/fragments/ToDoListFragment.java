package com.mrbreak.todo.view.fragments;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.mrbreak.todo.R;
import com.mrbreak.todo.view.activities.MainActivity;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.databinding.FragmentListBinding;
import com.mrbreak.todo.util.ToDoModelComparator;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.view.ToDoClickCallBack;
import com.mrbreak.todo.viewmodel.ToDoListViewModel;

import java.util.Collections;
import java.util.List;


public class ToDoListFragment extends Fragment {

    private ToDoListViewModel doListViewModel;

    private ToDoListAdapter toDoListAdapter;
    private FragmentListBinding binding;

    public ToDoListFragment() {
    }

    public static ToDoListFragment newInstance() {
        return new ToDoListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        doListViewModel = ViewModelProviders.of(this).get(ToDoListViewModel.class);


        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        binding.addToDoFloatingActionButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));

        binding.addToDoFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDoDetailFragment detailFragment = new ToDoDetailFragment();
                ((MainActivity) getActivity()).setFragment(detailFragment);
            }
        });

        toDoListAdapter = new ToDoListAdapter(toDoCallBack, doListViewModel);

        binding.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.toDoRecyclerView.setLayoutManager(linearLayoutManager);
        binding.noTask.setTextSize(14);
        binding.pleaseAdd.setTextSize(14);
    }

    private void refreshData() {
        toDoListAdapter.setListItems(doListViewModel.getToDoList().getValue());
        toDoListAdapter.notifyDataSetChanged();
        binding.pullToRefresh.setRefreshing(false);
    }

    private void displayData() {
        doListViewModel.getToDoList().observe(this, new Observer<List<ToDoModel>>() {
            @Override
            public void onChanged(@Nullable List<ToDoModel> toDoModels) {
                if (toDoModels == null || toDoModels.size() == 0) {
                    binding.noTask.setVisibility(View.VISIBLE);
                    binding.pleaseAdd.setVisibility(View.VISIBLE);
                    binding.emptyListLayout.setVisibility(View.VISIBLE);
                } else {
                    Collections.sort(toDoModels, new ToDoModelComparator());
                    binding.noTask.setVisibility(View.INVISIBLE);
                    binding.pleaseAdd.setVisibility(View.INVISIBLE);
                    binding.emptyListLayout.setVisibility(View.INVISIBLE);
                }

                toDoListAdapter.setListItems(toDoModels);
                binding.toDoRecyclerView.setAdapter(toDoListAdapter);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        displayData();
    }

    private ToDoClickCallBack toDoCallBack = new ToDoClickCallBack() {
        @Override
        public void onClick(ToDoModel toDoModel) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                ToDoDetailFragment detailFragment = new ToDoDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.ISEDIT, true);
                bundle.putParcelable(Constants.TODO, toDoModel);
                detailFragment.setArguments(bundle);
                ((MainActivity) getActivity()).setFragment(detailFragment);
            }
        }
    };
}
