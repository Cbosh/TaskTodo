package com.mrbreak.todo.fragments;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mrbreak.todo.R;
import com.mrbreak.todo.databinding.OverDueItemBinding;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.util.Utils;
import com.mrbreak.todo.view.ToDoCallBack;
import com.mrbreak.todo.viewmodel.OverDueListViewModel;

import java.util.Date;

class OverDueListAdapter extends PagedListAdapter<ToDoModel, OverDueListAdapter.ViewHolder> {

    private ToDoCallBack onClickCallBack;
    private OverDueListViewModel overDueListViewModel;

    OverDueListAdapter(ToDoCallBack onClickCallBack, OverDueListViewModel overDueListViewModel) {
        super(DIFF_CALLBACK);
        this.onClickCallBack = onClickCallBack;
        this.overDueListViewModel = overDueListViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        OverDueItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.over_due_item, parent,
                false);

        binding.setCallback(onClickCallBack);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoModel item = getItem(position);
        if (item != null) {
            Date date = Utils.convertStringToDate(item.getDueDate());
            String days = String.valueOf(Utils.getDaysDifference(new Date(), date));
            int daysInt = Integer.parseInt(days);
            if (daysInt < 0) {
                return;
            }

            holder.binding.setTodomodel(item);
            holder.binding.setViewmodel(overDueListViewModel);
            switch (item.getPriority()) {
                case 0:
                    holder.binding.priorityLine.setBackgroundResource(R.drawable.high_priority_list);
                    break;
                case 1:
                    holder.binding.priorityLine.setBackgroundResource(R.drawable.medium_priority_list);
                    break;
                case 2:
                    holder.binding.priorityLine.setBackgroundResource(R.drawable.low_priority_list);
                    break;
                default:
            }
            holder.binding.doneToggleButton.setChecked(item.isDone());
        } else {
            holder.binding.invalidateAll();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public OverDueItemBinding binding;

        public ViewHolder(@NonNull OverDueItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static DiffUtil.ItemCallback<ToDoModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ToDoModel>() {

                @Override
                public boolean areItemsTheSame(ToDoModel oldItem, ToDoModel newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ToDoModel oldItem, @NonNull ToDoModel newItem) {
                    return oldItem.getId() == newItem.getId() && oldItem.getContent().equals(
                            newItem.getContent());
                }
            };
}
