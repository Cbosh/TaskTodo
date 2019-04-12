package com.mrbreak.todo.view.fragments;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mrbreak.todo.R;
import com.mrbreak.todo.databinding.DoneItemBinding;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.view.ToDoClickCallBack;
import com.mrbreak.todo.viewmodel.DoneListViewModel;

class DoneListAdapter extends PagedListAdapter<ToDoModel, DoneListAdapter.ViewHolder> {

    private ToDoClickCallBack onClickCallBack;
    private DoneListViewModel doneListViewModel;

    DoneListAdapter(ToDoClickCallBack onClickCallBack, DoneListViewModel doneListViewModel) {
        super(DIFF_CALLBACK);
        this.onClickCallBack = onClickCallBack;
        this.doneListViewModel = doneListViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        DoneItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.done_item, parent,
                false);

        binding.setCallback(onClickCallBack);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoModel item = getItem(position);
        if (item != null) {
            holder.binding.setTodomodel(item);
            holder.binding.setViewmodel(doneListViewModel);
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
            holder.binding.doneToggleButton.setEnabled(false);


            holder.binding.dueDate.setTextSize(12);
            holder.binding.category.setTextSize(20);
            holder.binding.createdDate.setTextSize(12);

        } else {
            holder.binding.invalidateAll();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public DoneItemBinding binding;

        public ViewHolder(@NonNull DoneItemBinding binding) {
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
