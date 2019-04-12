package com.mrbreak.todo.view.fragments;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.mrbreak.todo.R;
import com.mrbreak.todo.view.ToDoClickCallBack;
import com.mrbreak.todo.view.activities.MainActivity;
import com.mrbreak.todo.constants.Constants;
import com.mrbreak.todo.databinding.FragmentToDoDoneBinding;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.viewmodel.DoneListViewModel;

public class ToDoDoneFragment extends Fragment {
    private FragmentToDoDoneBinding binding;

    public ToDoDoneFragment() {
        // Required empty public constructor
    }

    public static ToDoDoneFragment newInstance(String param1, String param2) {
        ToDoDoneFragment fragment = new ToDoDoneFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_to_do_done, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        DoneListViewModel doneListViewModel = ViewModelProviders.of(this).get(DoneListViewModel.class);

        DoneListAdapter toDoListAdapter = new DoneListAdapter(toDoCallBack, doneListViewModel);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.doneRecyclerView.setLayoutManager(linearLayoutManager);
        binding.doneRecyclerView.setAdapter(toDoListAdapter);
        doneListViewModel.getPagedListLiveData().observe(this, toDoListAdapter::submitList);
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
