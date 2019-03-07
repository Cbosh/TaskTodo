package com.mrbreak.todo.fragments;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.mrbreak.todo.R;
import com.mrbreak.todo.databinding.ToDoItemBinding;
import com.mrbreak.todo.repository.model.ToDoModel;
import com.mrbreak.todo.util.Utils;
import com.mrbreak.todo.view.ToDoCallBack;
import com.mrbreak.todo.viewmodel.ToDoListViewModel;

import java.util.ArrayList;
import java.util.List;

class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {

    private ToDoCallBack onClickCallBack;
    private ToDoListViewModel doListViewModel;
    private List<ToDoModel> list;

    ToDoListAdapter(ToDoCallBack onClickCallBack, ToDoListViewModel doListViewModel) {
        this.onClickCallBack = onClickCallBack;
        this.doListViewModel = doListViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ToDoItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.to_do_item, parent,
                false);

        binding.setCallback(onClickCallBack);

        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoModel item = list.get(position);
        if (item != null) {
            holder.binding.setViewmodel(doListViewModel);
            holder.binding.setTodomodel(item);
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

            holder.binding.doneToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ToDoModel toDo = list.get(holder.getAdapterPosition());
                    toDo.setDone(true);
                    toDo.setCompletedDate(Utils.getCompletedDateTime());
                    doListViewModel.update(toDo);
                    Utils.displaySnackBar(holder.binding.doneToggleButton.getContext().getString(
                            R.string.task_complete_message),
                            holder.binding.doneToggleButton).show();
                }
            });

            holder.binding.executePendingBindings();

            holder.binding.dueDate.setTextSize(12);
            holder.binding.category.setTextSize(20);
            holder.binding.endTime.setTextSize(12);

        } else {
            holder.binding.invalidateAll();
        }
    }

    public void setListItems(List<ToDoModel> newList) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list = newList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ToDoItemBinding binding;

        public ViewHolder(@NonNull ToDoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

//    public void setList(List<ToDoModel> list) {
//        if (this.list == null) {
//            this.list = list;
//            notifyItemRangeInserted(0, list.size());
//        } else {
//
//            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
//                @Override
//                public int getOldListSize() {
//                    return ToDoListAdapter.this.list.size();
//                }
//
//                @Override
//                public int getNewListSize() {
//                    return list.size();
//                }
//
//                @Override
//                public boolean areItemsTheSame(int old, int newOne) {
//                    return ToDoListAdapter.this.list.get(old).getId() ==
//                            list.get(newOne).getId();
//                }
//
//                @Override
//                public boolean areContentsTheSame(int old, int newOne) {
//                    if (old == list.size()) {
//                        return false;
//                    }
//
//                    ToDoModel newModel = list.get(newOne);
//                    ToDoModel oldModel = list.get(old);
//
//                    return newModel.getId() == oldModel.getId() && newModel.getContent().equalsIgnoreCase(
//                            oldModel.getContent());
//
//                }
//            });
//
//            this.list = list;
//            result.dispatchUpdatesTo(this);
//        }
//
//    }
}
