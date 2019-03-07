package com.mrbreak.todo.fragments;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrbreak.todo.R;
import com.mrbreak.todo.activities.MainActivity;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.databinding.FragmentTodoOverdueBinding;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.view.ToDoCallBack;
import com.mrbreak.todo.viewmodel.OverDueListViewModel;

public class ToDoOverDueFragment extends Fragment {

    private OverDueListViewModel overDueListViewModel;
    private OverDueListAdapter overDueListAdapter;
    private FragmentTodoOverdueBinding binding;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_todo_overdue, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        overDueListViewModel = ViewModelProviders.of(this).get(OverDueListViewModel.class);

        overDueListAdapter = new OverDueListAdapter(toDoCallBack, overDueListViewModel);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.overDueRecyclerView.setLayoutManager(linearLayoutManager);
        binding.overDueRecyclerView.setAdapter(overDueListAdapter);
        overDueListViewModel.getPagedListLiveData().observe(this, overDueListAdapter::submitList);
    }

    private ToDoCallBack toDoCallBack = new ToDoCallBack() {
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
